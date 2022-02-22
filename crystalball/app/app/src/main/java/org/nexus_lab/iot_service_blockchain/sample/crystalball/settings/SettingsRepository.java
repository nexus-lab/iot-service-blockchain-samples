package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.owlike.genson.Genson;

import java.io.InputStream;

public class SettingsRepository {
    private final static Genson GENSON = new Genson();

    private final SettingsDataSource mDataSource;
    private final MutableLiveData<Settings> mSettings;

    public SettingsRepository(SettingsDataSource dataSource) {
        mDataSource = dataSource;
        mSettings = new MutableLiveData<>();
    }

    public static Settings deserialize(String json) {
        return GENSON.deserialize(json, Settings.class).asBuilder().build();
    }

    public static Settings deserialize(InputStream json) {
        return GENSON.deserialize(json, Settings.class).asBuilder().build();
    }

    public static String serialize(Settings settings) {
        return GENSON.serialize(settings);
    }

    private Settings copyOf(Settings original) {
        return original.asBuilder().build();
    }

    public LiveData<Settings> getObservable() {
        mSettings.setValue(copyOf(get()));
        return mSettings;
    }

    public Settings get() {
        return mDataSource.get();
    }

    public void set(Settings settings) {
        mDataSource.set(settings);
        mSettings.setValue(copyOf(get()));
    }
}
