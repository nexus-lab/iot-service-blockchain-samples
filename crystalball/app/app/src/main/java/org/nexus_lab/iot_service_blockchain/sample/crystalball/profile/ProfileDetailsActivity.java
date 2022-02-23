package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.BlockchainService;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityServiceDetailsBinding;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.player.PlayerActivity;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ProfileDetailsActivity extends AppCompatActivity implements ProfileEditorBuilder.Listener {
    public final static String ARG_PROFILE_ID = "PROFILE_ID";

    private String mId;
    private ProfileRepository mRepository;
    private ActivityServiceDetailsBinding mViewBinding;
    private BlockchainService.ServiceBinder mBinder;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (BlockchainService.ServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    };
    private final View.OnClickListener mOnPlayClickListener = v -> {
        Profile profile = mRepository.get(mId);
        if (profile == null) {
            Snackbar.make(mViewBinding.getRoot(), R.string.alert_profile_not_found, Snackbar.LENGTH_LONG).show();
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mViewBinding.play, getString(R.string.transition_details_to_player));
            Intent intent = new Intent(ProfileDetailsActivity.this, PlayerActivity.class);
            intent.putExtra(PlayerActivity.ARG_PROFILE_ID, profile.getId());
            startActivity(intent, options.toBundle());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindService(new Intent(this, BlockchainService.class), mConnection, Context.BIND_AUTO_CREATE);

        mRepository = ((App) getApplication()).getProfileRepository();
        mViewBinding = ActivityServiceDetailsBinding.inflate(getLayoutInflater());

        mId = getIntent().getStringExtra(ARG_PROFILE_ID);
        if (mId == null || mRepository.get(mId) == null) {
            Snackbar.make(mViewBinding.getRoot(), R.string.alert_profile_not_found, Snackbar.LENGTH_LONG).show();
            supportFinishAfterTransition();
            return;
        }

        setContentView(mViewBinding.getRoot());
        setTitle(R.string.title_service_details);

        setSupportActionBar(mViewBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mViewBinding.play.setOnClickListener(mOnPlayClickListener);

        mRepository.getObservable().observe(this, profiles -> {
            if (mRepository.get(mId) != null) {
                updateUi(mRepository.get(mId));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        } else if (id == R.id.action_refresh) {
            Profile profile = mRepository.get(mId);
            if (profile != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_profile_retrieving, Snackbar.LENGTH_LONG).show();
                refreshProfile(profile);
            }
            return true;
        } else if (id == R.id.action_edit) {
            Profile profile = mRepository.get(mId);
            ProfileEditorBuilder dialogBuilder = new ProfileEditorBuilder(this)
                    .setTitle(getString(R.string.title_service_edit))
                    .setDeviceId(profile.getDeviceId())
                    .setOrganizationId(profile.getOrganizationId())
                    .setServiceName(profile.getServiceName())
                    .setOnActionListener(this);
            dialogBuilder.create().show();
            return true;
        } else if (id == R.id.action_delete) {
            supportFinishAfterTransition();
            mRepository.remove(mRepository.get(mId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSave(@NonNull Profile profile) {
        Profile.Builder builder = mRepository.get(mId).asBuilder();
        builder.setDeviceId(profile.getDeviceId());
        builder.setOrganizationId(profile.getOrganizationId());
        builder.setServiceName(profile.getServiceName());
        mRepository.set(builder.build());
        refreshProfile(profile);
    }

    private void refreshProfile(@NonNull Profile profile) {
        if (mBinder != null) {
            mBinder.refresh(profile).observe(this, data -> {
                if (data.getThrowable() != null) {
                    Snackbar.make(mViewBinding.getRoot(), R.string.alert_profile_retrieve_failed, Snackbar.LENGTH_LONG).show();
                } else if (data.getData() != null) {
                    mRepository.set(data.getData());
                }
            });
        }
    }

    private void updateUi(Profile profile) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

        mViewBinding.serviceName.setText(profile.getServiceName());
        if (profile.getServiceVersion() != null) {
            mViewBinding.serviceVersion.setText(String.valueOf(profile.getServiceVersion()));
        }
        mViewBinding.serviceDescription.setText(profile.getServiceDescription());
        if (profile.getServiceLastUpdateTime() != null) {
            mViewBinding.serviceLastUpdateTime.setText(profile.getServiceLastUpdateTime().format(formatter));
        }
        mViewBinding.organizationId.setText(profile.getOrganizationId());
        mViewBinding.deviceId.setText(profile.getDeviceId());
        mViewBinding.deviceName.setText(profile.getDeviceName());
        mViewBinding.deviceDescription.setText(profile.getDeviceDescription());
        if (profile.getDeviceLastUpdateTime() != null) {
            mViewBinding.deviceLastUpdateTime.setText(profile.getDeviceLastUpdateTime().format(formatter));
        }
    }
}