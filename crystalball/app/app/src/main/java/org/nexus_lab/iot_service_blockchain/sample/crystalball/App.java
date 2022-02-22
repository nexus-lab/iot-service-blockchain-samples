package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.app.Application;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileDataSource;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileRepository;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.settings.SettingsDataSource;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.settings.SettingsRepository;

public class App extends Application {
    public final static String PREFERENCE_DEFAULT_NAME = "default";

    private ProfileRepository mProfileRepository;
    private SettingsRepository mSettingsRepository;

    public ProfileRepository getProfileRepository() {
        if (mProfileRepository == null) {
            mProfileRepository = new ProfileRepository(new ProfileDataSource(this));
        }
        return mProfileRepository;
    }

    public SettingsRepository getSettingsRepository() {
        if (mSettingsRepository == null) {
            mSettingsRepository = new SettingsRepository(new SettingsDataSource(this));
        }
        return mSettingsRepository;
    }
}
