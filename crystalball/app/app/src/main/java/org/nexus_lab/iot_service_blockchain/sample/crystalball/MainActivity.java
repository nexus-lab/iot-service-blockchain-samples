package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.JsonStreamException;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityMainBinding;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.Profile;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileAdapter;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileDetailsActivity;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileEditorBuilder;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileRepository;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.settings.SettingsActivity;

import java.util.Objects;

import io.github.g00fy2.quickie.QRResult;
import io.github.g00fy2.quickie.ScanQRCode;

public class MainActivity extends AppCompatActivity implements ProfileAdapter.OnItemClickListener, ActivityResultCallback<QRResult>, ProfileEditorBuilder.Listener {
    @SuppressWarnings("unchecked")
    private final ActivityResultLauncher<Void> scanner = registerForActivityResult(new ScanQRCode(), this);
    private ProfileAdapter mAdapter;
    private ProfileRepository mRepository;
    private ActivityMainBinding mViewBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindService(new Intent(this, BlockchainService.class), mConnection, Context.BIND_AUTO_CREATE);

        mViewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        setSupportActionBar(mViewBinding.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.drawable.divider)));
        mViewBinding.serviceList.setLayoutManager(layoutManager);
        mViewBinding.serviceList.addItemDecoration(divider);

        mRepository = ((App) getApplication()).getProfileRepository();
        mAdapter = new ProfileAdapter(this, mRepository);
        mAdapter.addOnItemClickListener(this);
        mViewBinding.serviceList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_manually) {
            new ProfileEditorBuilder(this)
                    .setTitle(getString(R.string.title_service_add))
                    .setOnActionListener(this)
                    .create()
                    .show();
            return true;
        } else if (id == R.id.action_add_by_scanning) {
            scanner.launch(null);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mAdapter.removeOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, Profile profile) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.screenshot), getString(R.string.transition_main_to_details));
        Intent intent = new Intent(this, ProfileDetailsActivity.class);
        intent.putExtra(ProfileDetailsActivity.ARG_PROFILE_ID, profile.getId());
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onActivityResult(QRResult result) {
        if (result instanceof QRResult.QRSuccess) {
            String value = ((QRResult.QRSuccess) result).getContent().getRawValue();
            try {
                Profile profile = ProfileRepository.deserialize(value);
                onSave(profile);
            } catch (JsonBindingException | JsonStreamException e) {
                if (mViewBinding != null) {
                    Snackbar.make(mViewBinding.getRoot(), R.string.alert_qrcode_invalid, Snackbar.LENGTH_LONG).show();
                }
            }
        } else if (result instanceof QRResult.QRError) {
            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_qrcode_scanning_failed, Snackbar.LENGTH_LONG).show();
            }
        } else if (result instanceof QRResult.QRMissingPermission) {
            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_permission_denied, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSave(@NonNull Profile profile) {
        mRepository.add(profile);
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
}