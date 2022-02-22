package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.PlayerActivity;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityServiceDetailsBinding;

public class ProfileDetailsActivity extends AppCompatActivity {
    public final static String ARG_PROFILE_ID = "PROFILE_ID";

    private String mId;
    private ProfileRepository mRepository;
    private ActivityServiceDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityServiceDetailsBinding.inflate(getLayoutInflater());
        mRepository = ((App) getApplication()).getProfileRepository();

        Intent intent = getIntent();
        mId = intent.getStringExtra(ARG_PROFILE_ID);
        if (mId == null || mRepository.get(mId) == null) {
            Snackbar.make(mBinding.getRoot(), R.string.alert_profile_not_found, Snackbar.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(mBinding.getRoot());

        setTitle(R.string.title_service_details);

        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mBinding.play.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mBinding.play, getString(R.string.transition_details_to_player));
            startActivity(new Intent(ProfileDetailsActivity.this, PlayerActivity.class), options.toBundle());
        });

        mRepository.getObservable().observe(this, profiles -> {
            if (mRepository.get(mId) != null) {
                updateUi(mRepository.get(mId));
            }
        });
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
        } else if (id == R.id.action_edit) {
            Profile profile = mRepository.get(mId);
            ProfileEditorBuilder dialogBuilder = new ProfileEditorBuilder(this)
                    .setTitle(getString(R.string.title_service_edit))
                    .setDeviceId(profile.getDeviceId())
                    .setOrganizationId(profile.getOrganizationId())
                    .setServiceName(profile.getServiceName())
                    .setOnActionListener(update -> {
                        Profile.Builder builder = mRepository.get(mId).asBuilder();
                        builder.setDeviceId(update.getDeviceId());
                        builder.setOrganizationId(update.getOrganizationId());
                        builder.setServiceName(update.getServiceName());
                        mRepository.set(builder.build());
                    });
            dialogBuilder.create().show();
            return true;
        } else if (id == R.id.action_delete) {
            supportFinishAfterTransition();
            mRepository.remove(mRepository.get(mId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUi(Profile profile) {
        mBinding.serviceName.setText(profile.getServiceName());
        if (profile.getServiceVersion() != null) {
            mBinding.serviceVersion.setText(String.valueOf(profile.getServiceVersion()));
        }
        mBinding.serviceDescription.setText(profile.getServiceDescription());
        if (profile.getServiceLastUpdateTime() != null) {
            mBinding.serviceLastUpdateTime.setText(profile.getServiceLastUpdateTime().toString());
        }
        mBinding.organizationId.setText(profile.getOrganizationId());
        mBinding.deviceId.setText(profile.getDeviceId());
        mBinding.deviceName.setText(profile.getDeviceName());
        mBinding.deviceDescription.setText(profile.getDeviceDescription());
        if (profile.getDeviceLastUpdateTime() != null) {
            mBinding.deviceLastUpdateTime.setText(profile.getDeviceLastUpdateTime().toString());
        }
    }
}