package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivitySettingsBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    private final ActivityResultLauncher<String[]> importFileLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri == null) {
            return;
        }

        try {
            InputStream input = getContentResolver().openInputStream(uri);
            Settings settings = Settings.deserialize(input);
            settings.validate();
            settings.save(this);
            setUiWithSettings(settings);

            if (binding != null) {
                Snackbar.make(binding.getRoot(), R.string.alert_settings_imported, Snackbar.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            if (binding != null) {
                Snackbar.make(binding.getRoot(), R.string.alert_file_not_found, Snackbar.LENGTH_LONG).show();
            }
        } catch (Settings.InvalidSettingsException e) {
            if (binding != null) {
                Snackbar.make(binding.getRoot(), R.string.alert_settings_invalid, Snackbar.LENGTH_LONG).show();
            }
        }
    });

    private final ActivityResultLauncher<String> exportFileLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {
        if (uri == null) {
            return;
        }

        try {
            Settings settings = Settings.load(this);
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream output = new FileOutputStream(descriptor.getFileDescriptor());
            output.write(settings.serialize().getBytes(StandardCharsets.UTF_8));
            output.close();
            descriptor.close();

            if (binding != null) {
                Snackbar.make(binding.getRoot(), R.string.alert_settings_exported, Snackbar.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if (binding != null) {
                Snackbar.make(binding.getRoot(), R.string.alert_settings_export_failed, Snackbar.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.title_settings);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.saveSettings.setOnClickListener(v -> {
            try {
                Settings settings = getSettingsFromUi();
                settings.validate();
                settings.save(this);

                if (binding != null) {
                    Snackbar.make(binding.getRoot(), R.string.alert_settings_saved, Snackbar.LENGTH_LONG).show();
                }
            } catch (Settings.InvalidSettingsException e) {
                if (binding != null) {
                    Snackbar.make(binding.getRoot(), R.string.alert_settings_invalid, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        setUiWithSettings(Settings.load(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_import) {
            importFileLauncher.launch(new String[]{getString(R.string.mime_type_json)});
            return true;
        } else if (id == R.id.action_export) {
            exportFileLauncher.launch(getString(R.string.file_settings_default));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private Settings getSettingsFromUi() {
        Settings settings = new Settings();
        Settings.ClientSettings client = new Settings.ClientSettings();
        Settings.GatewaySettings gateway = new Settings.GatewaySettings();
        Settings.NetworkSettings network = new Settings.NetworkSettings();
        settings.setGateway(gateway);
        settings.setNetwork(network);
        settings.setClient(client);

        if (binding != null) {
            client.setOrganizationId(
                    binding.organizationId.getText() == null
                            ? ""
                            : binding.organizationId.getText().toString()
            );
            client.setCertificate(
                    binding.clientCertificate.getText() == null
                            ? ""
                            : binding.clientCertificate.getText().toString()
            );
            client.setPrivateKey(
                    binding.clientPrivateKey.getText() == null
                            ? ""
                            : binding.clientPrivateKey.getText().toString()
            );

            gateway.setEndpoint(
                    binding.gatewayEndpoint.getText() == null
                            ? ""
                            : binding.gatewayEndpoint.getText().toString()
            );
            gateway.setServerName(
                    binding.gatewayServerName.getText() == null
                            ? ""
                            : binding.gatewayServerName.getText().toString()
            );
            gateway.setTlsCertificate(
                    binding.gatewayTlsCertificate.getText() == null
                            ? ""
                            : binding.gatewayTlsCertificate.getText().toString()
            );

            network.setName(
                    binding.networkName.getText() == null
                            ? ""
                            : binding.networkName.getText().toString()
            );
            network.setChaincode(
                    binding.networkChaincodeName.getText() == null
                            ? ""
                            : binding.networkChaincodeName.getText().toString()
            );
        }

        return settings;
    }

    private void setUiWithSettings(Settings settings) {
        if (binding != null) {
            if (settings.getClient() != null) {
                binding.organizationId.setText(settings.getClient().getOrganizationId());
                binding.clientCertificate.setText(settings.getClient().getCertificate());
                binding.clientPrivateKey.setText(settings.getClient().getPrivateKey());
            }
            if (settings.getGateway() != null) {
                binding.gatewayEndpoint.setText(settings.getGateway().getEndpoint());
                binding.gatewayServerName.setText(settings.getGateway().getServerName());
                binding.gatewayTlsCertificate.setText(settings.getGateway().getTlsCertificate());
            }
            if (settings.getNetwork() != null) {
                binding.networkName.setText(settings.getNetwork().getName());
                binding.networkChaincodeName.setText(settings.getNetwork().getChaincode());
            }
        }
    }
}