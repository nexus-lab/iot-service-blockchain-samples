package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivitySettingsBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SettingsActivity extends AppCompatActivity {
    private SettingsRepository mRepository;
    private ActivitySettingsBinding mViewBinding;

    private final ActivityResultLauncher<String[]> importFileLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri == null) {
            return;
        }

        try {
            InputStream input = getContentResolver().openInputStream(uri);
            mRepository.set(SettingsRepository.deserialize(input));

            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_settings_imported, Snackbar.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_file_not_found, Snackbar.LENGTH_LONG).show();
            }
        }
    });

    private final ActivityResultLauncher<String> exportFileLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {
        if (uri == null) {
            return;
        }

        try {
            Settings settings = mRepository.get();
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream output = new FileOutputStream(descriptor.getFileDescriptor());
            output.write(SettingsRepository.serialize(settings).getBytes(StandardCharsets.UTF_8));
            output.close();
            descriptor.close();

            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_settings_exported, Snackbar.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_settings_export_failed, Snackbar.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        mRepository = ((App) getApplication()).getSettingsRepository();

        setContentView(mViewBinding.getRoot());
        setTitle(R.string.title_settings);

        setSupportActionBar(mViewBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mViewBinding.saveSettings.setOnClickListener(v -> {
            Settings settings = getSettingsFromUi();
            mRepository.set(settings);

            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_settings_saved, Snackbar.LENGTH_LONG).show();
            }
        });

        mRepository.getObservable().observe(this, settings -> {
            if (settings != null) {
                updateUi(settings);
            }
        });

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
        mViewBinding = null;
    }

    private Settings getSettingsFromUi() {
        Settings.Builder settingsBuilder = new Settings.Builder();
        ClientSettings.Builder clientBuilder = new ClientSettings.Builder();
        GatewaySettings.Builder gatewayBuilder = new GatewaySettings.Builder();
        NetworkSettings.Builder networkBuilder = new NetworkSettings.Builder();

        if (mViewBinding != null) {
            if (mViewBinding.organizationId.getText() != null) {
                clientBuilder.setOrganizationId(mViewBinding.organizationId.getText().toString());
            }
            if (mViewBinding.clientCertificate.getText() != null) {
                clientBuilder.setCertificate(mViewBinding.clientCertificate.getText().toString());
            }
            if (mViewBinding.clientPrivateKey.getText() != null) {
                clientBuilder.setPrivateKey(mViewBinding.clientPrivateKey.getText().toString());
            }

            if (mViewBinding.gatewayEndpoint.getText() != null) {
                gatewayBuilder.setEndpoint(mViewBinding.gatewayEndpoint.getText().toString());
            }
            if (mViewBinding.gatewayServerName.getText() != null) {
                gatewayBuilder.setServerName(mViewBinding.gatewayServerName.getText().toString());
            }
            if (mViewBinding.gatewayTlsCertificate.getText() != null) {
                gatewayBuilder.setTlsCertificate(mViewBinding.gatewayTlsCertificate.getText().toString());
            }

            if (mViewBinding.networkName.getText() != null) {
                networkBuilder.setName(mViewBinding.networkName.getText().toString());
            }
            if (mViewBinding.networkChaincodeName.getText() != null) {
                networkBuilder.setChaincode(mViewBinding.networkChaincodeName.getText().toString());
            }
        }

        return settingsBuilder
                .setClient(clientBuilder)
                .setGateway(gatewayBuilder)
                .setNetwork(networkBuilder)
                .build();
    }

    private void updateUi(Settings settings) {
        if (mViewBinding != null) {
            if (settings.getClient() != null) {
                mViewBinding.organizationId.setText(settings.getClient().getOrganizationId());
                mViewBinding.clientCertificate.setText(settings.getClient().getCertificate());
                mViewBinding.clientPrivateKey.setText(settings.getClient().getPrivateKey());
            }
            if (settings.getGateway() != null) {
                mViewBinding.gatewayEndpoint.setText(settings.getGateway().getEndpoint());
                mViewBinding.gatewayServerName.setText(settings.getGateway().getServerName());
                mViewBinding.gatewayTlsCertificate.setText(settings.getGateway().getTlsCertificate());
            }
            if (settings.getNetwork() != null) {
                mViewBinding.networkName.setText(settings.getNetwork().getName());
                mViewBinding.networkChaincodeName.setText(settings.getNetwork().getChaincode());
            }
        }
    }
}