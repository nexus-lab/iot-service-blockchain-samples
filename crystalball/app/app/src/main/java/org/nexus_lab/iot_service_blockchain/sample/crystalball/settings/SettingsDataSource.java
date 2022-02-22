package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.owlike.genson.Genson;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;

public class SettingsDataSource {
    private final static Genson GENSON = new Genson();
    private final static String PREFERENCE_NAME = "settings";
    private final SharedPreferences mPreferences;
    @NonNull
    private Settings mSettings;

    public SettingsDataSource(Context context) {
        mPreferences = context.getSharedPreferences(App.PREFERENCE_DEFAULT_NAME, Context.MODE_PRIVATE);
        mSettings = GENSON.deserialize(mPreferences.getString(PREFERENCE_NAME, "{}"), Settings.class);
    }

    private void save() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCE_NAME, GENSON.serialize(mSettings));
        editor.apply();
    }

    public Settings get() {
        return mSettings;
    }

    public void set(@NonNull Settings settings) {
        mSettings = settings;
        save();
    }
}
