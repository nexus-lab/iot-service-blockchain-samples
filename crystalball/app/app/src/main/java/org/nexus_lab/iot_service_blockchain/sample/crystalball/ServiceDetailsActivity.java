package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityServiceDetailsBinding;

public class ServiceDetailsActivity extends AppCompatActivity {
    private static final String ARG_ORGANIZATION_ID = "organization_id";
    private static final String ARG_DEVICE_ID = "device_id";
    private static final String ARG_SERVICE_NAME = "service_name";

    private String organizationId;
    private String deviceId;
    private String serviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityServiceDetailsBinding binding = ActivityServiceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.title_service_details);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.play.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.play, getString(R.string.transition_details_to_player));
            startActivity(new Intent(ServiceDetailsActivity.this, PlayerActivity.class), options.toBundle());
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
        }

        return super.onOptionsItemSelected(item);
    }
}