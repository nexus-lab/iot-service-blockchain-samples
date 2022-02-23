package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sdk.OffsetDateTimeConverter;

import java.util.List;

public class ProfileDataSource {
    private final static String PREFERENCE_NAME = "profiles";
    private final static Genson GENSON = new GensonBuilder().withConverters(new OffsetDateTimeConverter()).create();

    private final List<Profile> mProfiles;
    private final SharedPreferences mPreferences;

    public ProfileDataSource(Context context) {
        mPreferences = context.getSharedPreferences(App.PREFERENCE_DEFAULT_NAME, Context.MODE_PRIVATE);
        mProfiles = GENSON.deserialize(mPreferences.getString(PREFERENCE_NAME, "[]"), new GenericType<List<Profile>>() {
        });
    }

    private void save() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCE_NAME, GENSON.serialize(mProfiles));
        editor.apply();
    }

    public int size() {
        return mProfiles.size();
    }

    public Profile get(int position) {
        return mProfiles.get(position);
    }

    public Profile get(@NonNull String id) {
        for (Profile profile : mProfiles) {
            if (id.equals(profile.getId())) {
                return profile;
            }
        }
        return null;
    }

    public int indexOf(@NonNull String id) {
        for (int i = 0; i < mProfiles.size(); i++) {
            if (id.equals(mProfiles.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public List<Profile> all() {
        return mProfiles;
    }

    public void add(Profile profile) {
        mProfiles.add(profile);
        save();
    }

    public void set(Profile profile) {
        int index = profile.getId() == null ? -1 : indexOf(profile.getId());
        if (index > -1) {
            mProfiles.set(index, profile);
            save();
        }
    }

    public void remove(Profile profile) {
        mProfiles.remove(profile);
        save();
    }
}
