package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.hyperledger.fabric.client.CloseableIterator;
import org.hyperledger.fabric.client.GatewayRuntimeException;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.Profile;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.settings.Settings;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.settings.SettingsRepository;
import org.nexus_lab.iot_service_blockchain.sdk.Device;
import org.nexus_lab.iot_service_blockchain.sdk.Sdk;
import org.nexus_lab.iot_service_blockchain.sdk.SdkOptions;
import org.nexus_lab.iot_service_blockchain.sdk.Service;
import org.nexus_lab.iot_service_blockchain.sdk.ServiceRequest;
import org.nexus_lab.iot_service_blockchain.sdk.ServiceRequestEvent;
import org.nexus_lab.iot_service_blockchain.sdk.ServiceResponse;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BlockchainService extends LifecycleService {
    private final static String TAG = BlockchainService.class.getName();
    private final IBinder mBinder = new ServiceBinder();
    private Handler mMainHandler;
    private Settings mCurrentSettings;
    private HandlerThread mServiceThread;
    private ServiceHandler mServiceHandler;

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMainHandler = new Handler(getMainLooper());
        mServiceThread = new HandlerThread("BlockchainService.HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mServiceThread.start();
        mServiceHandler = new ServiceHandler(mServiceThread.getLooper());

        SettingsRepository repository = ((App) getApplication()).getSettingsRepository();
        repository.getObservable().observe(this, (Observer<Settings>) settings -> {
            if (Objects.equals(mCurrentSettings, settings)) {
                return;
            }
            mCurrentSettings = settings;
            mServiceHandler.setNewConfiguration(settings, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.i(TAG, "blockchain connection configuration updated");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "failed to update blockchain connection configuration", e);
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceHandler != null && mServiceHandler.mEventStream != null) {
            mServiceHandler.mEventStream.close();
        }
        mServiceThread.quit();
        mCurrentSettings = null;
    }

    public interface ResultCallback<T> {
        default void onSuccess(T result) {
        }

        default void onError(Exception e) {
        }
    }

    private final static class EventHandlerThread extends Thread {
        private final Map<String, ResultCallback<?>> mCallbackRegistry;
        private final CloseableIterator<ServiceRequestEvent> mEventStream;

        public EventHandlerThread(String name, CloseableIterator<ServiceRequestEvent> eventStream, Map<String, ResultCallback<?>> callbackRegistry) {
            super(name);
            mEventStream = eventStream;
            mCallbackRegistry = callbackRegistry;
        }

        @Override
        public void run() {
            if (mEventStream != null) {
                try {
                    while (true) {
                        try {
                            if (!mEventStream.hasNext()) {
                                break;
                            }
                        } catch (GatewayRuntimeException ignored) {
                            break;
                        }
                        ServiceRequestEvent event = mEventStream.next();
                        if (!"respond".equals(event.getAction())) {
                            continue;
                        }
                        String requestId = event.getRequestId();
                        if (mCallbackRegistry != null && mCallbackRegistry.containsKey(requestId)) {
                            ResultCallback<?> callback = mCallbackRegistry.remove(requestId);
                            if (callback != null) {
                                //noinspection unchecked
                                ((ResultCallback<ServiceResponse>) callback).onSuccess((ServiceResponse) event.getPayload());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "failed to receive smart contract events", e);
                }
            }
        }
    }

    private final static class ServiceHandler extends Handler {
        private Sdk mBlockchainSdk;
        private Map<String, ResultCallback<?>> mCallbackRegistry;
        private CloseableIterator<ServiceRequestEvent> mEventStream;

        public ServiceHandler(@NonNull Looper looper) {
            super(looper);
        }

        public void setNewConfiguration(Settings settings, @Nullable ResultCallback<Void> callback) {
            post(() -> {
                try {
                    if (mBlockchainSdk != null) {
                        mBlockchainSdk.close();
                    }
                    SdkOptions options = new SdkOptions();
                    if (settings.getClient() != null) {
                        options.setOrganizationId(settings.getClient().getOrganizationId());
                        options.setCertificate(settings.getClient().getCertificate());
                        options.setPrivateKey(settings.getClient().getPrivateKey());
                    }
                    if (settings.getGateway() != null) {
                        options.setGatewayPeerEndpoint(settings.getGateway().getEndpoint());
                        options.setGatewayPeerServerName(settings.getGateway().getServerName());
                        options.setGatewayPeerTlsCertificate(settings.getGateway().getTlsCertificate());
                    }
                    if (settings.getNetwork() != null) {
                        options.setNetworkName(settings.getNetwork().getName());
                        options.setChaincodeId(settings.getNetwork().getChaincode());
                    }

                    if (mEventStream != null) {
                        mEventStream.close();
                    }
                    mBlockchainSdk = new Sdk(options);
                    mCallbackRegistry = new HashMap<>();
                    mEventStream = mBlockchainSdk.getServiceBroker().registerEvent();
                    EventHandlerThread eventHandlerThread = new EventHandlerThread("BlockchainService.EventHandlerThread", mEventStream, mCallbackRegistry);
                    eventHandlerThread.start();

                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            });
        }

        public void getDevice(String organizationId, String deviceId, @Nullable ResultCallback<Device> callback) {
            post(() -> {
                try {
                    Device device = Objects.requireNonNull(mBlockchainSdk).getDeviceRegistry().get(organizationId, deviceId);
                    if (callback != null) {
                        callback.onSuccess(device);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            });
        }

        public void getService(String organizationId, String deviceId, String serviceName, @Nullable ResultCallback<Service> callback) {
            post(() -> {
                try {
                    Service service = Objects.requireNonNull(mBlockchainSdk).getServiceRegistry().get(organizationId, deviceId, serviceName);
                    if (callback != null) {
                        callback.onSuccess(service);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            });
        }

        public void requestService(String organizationId, String deviceId, String serviceName, @Nullable ResultCallback<ServiceResponse> callback) {
            post(() -> {
                try {
                    Service service = new Service();
                    service.setOrganizationId(organizationId);
                    service.setDeviceId(deviceId);
                    service.setName(serviceName);

                    ServiceRequest request = new ServiceRequest();
                    request.setId(UUID.randomUUID().toString());
                    request.setService(service);
                    request.setMethod("GET");
                    request.setTime(OffsetDateTime.now());
                    request.setArguments(new String[0]);

                    mCallbackRegistry.put(request.getId(), callback);
                    Objects.requireNonNull(mBlockchainSdk).getServiceBroker().request(request);
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            });
        }
    }

    public final class ServiceBinder extends Binder {
        public LiveData<LiveDataWrapper<Profile>> refresh(@NonNull Profile profile) {
            MutableLiveData<LiveDataWrapper<Profile>> data = new MutableLiveData<>(new LiveDataWrapper<>(profile, null));
            mServiceHandler.getDevice(profile.getOrganizationId(), profile.getDeviceId(), new ResultCallback<Device>() {
                @Override
                public void onSuccess(Device device) {
                    mMainHandler.post(() -> {
                        LiveDataWrapper<Profile> current = data.getValue();
                        Profile.Builder builder = current != null && current.getData() != null ? current.getData().asBuilder() : new Profile.Builder();
                        Profile next = builder.setDeviceName(device.getName())
                                .setDeviceDescription(device.getDescription())
                                .setDeviceLastUpdateTime(device.getLastUpdateTime())
                                .build();
                        data.setValue(new LiveDataWrapper<>(next, null));
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "failed to fetch device information", e);
                    mMainHandler.post(() -> data.setValue(new LiveDataWrapper<>(null, e)));
                }
            });
            mServiceHandler.getService(profile.getOrganizationId(), profile.getDeviceId(), profile.getServiceName(), new ResultCallback<Service>() {
                @Override
                public void onSuccess(Service service) {
                    mMainHandler.post(() -> {
                        LiveDataWrapper<Profile> current = data.getValue();
                        Profile.Builder builder = current != null && current.getData() != null ? current.getData().asBuilder() : new Profile.Builder();
                        Profile next = builder.setServiceName(service.getName())
                                .setServiceVersion(service.getVersion())
                                .setServiceDescription(service.getDescription())
                                .setServiceLastUpdateTime(service.getLastUpdateTime())
                                .build();
                        data.setValue(new LiveDataWrapper<>(next, null));
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "failed to fetch service information", e);
                    mMainHandler.post(() -> data.setValue(new LiveDataWrapper<>(null, e)));
                }
            });
            return data;
        }

        public LiveData<LiveDataWrapper<ServiceResponse>> request(@NonNull Profile profile) {
            MutableLiveData<LiveDataWrapper<ServiceResponse>> data = new MutableLiveData<>();
            mServiceHandler.requestService(profile.getOrganizationId(), profile.getDeviceId(), profile.getServiceName(), new ResultCallback<ServiceResponse>() {
                @Override
                public void onSuccess(ServiceResponse response) {
                    mMainHandler.post(() -> data.setValue(new LiveDataWrapper<>(response, null)));
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "failed to send service request", e);
                    mMainHandler.post(() -> data.setValue(new LiveDataWrapper<>(null, e)));
                }
            });
            return data;
        }
    }
}