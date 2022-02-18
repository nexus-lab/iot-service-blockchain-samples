package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityMainBinding;

import java.util.Objects;

import io.github.g00fy2.quickie.QRResult;
import io.github.g00fy2.quickie.ScanQRCode;

public class MainActivity extends AppCompatActivity implements ActivityResultCallback<QRResult> {
    @SuppressWarnings("unchecked")
    ActivityResultLauncher<Void> scanner = registerForActivityResult(new ScanQRCode(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.drawable.divider)));
        binding.serviceList.setAdapter(new ServiceProfileListAdapter((view, item) -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.screenshot), getString(R.string.transition_main_to_details));
            startActivity(new Intent(this, ServiceDetailsActivity.class), options.toBundle());
        }));
        binding.serviceList.setLayoutManager(layoutManager);
        binding.serviceList.addItemDecoration(divider);
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
            new ServiceEditDialogBuilder(this).setListener(new ServiceEditDialogBuilder.Listener() {
                @Override
                public void onSave(ServiceEditDialogBuilder.Result result) {
                    Toast.makeText(MainActivity.this, String.format("saved %s, %s, %s", result.organizationId, result.deviceId, result.serviceName), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(MainActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                }
            }).create().show();
            return true;
        }

        if (id == R.id.action_add_by_scanning) {
            scanner.launch(null);
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(QRResult result) {
        if (result instanceof QRResult.QRSuccess) {
            Toast.makeText(this, ((QRResult.QRSuccess) result).getContent().getRawValue(), Toast.LENGTH_SHORT).show();
        } else if (result instanceof QRResult.QRError) {
            Toast.makeText(this, ((QRResult.QRError) result).getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } else if (result instanceof QRResult.QRMissingPermission) {
            Toast.makeText(this, "missing permission", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "user cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}