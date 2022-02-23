package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileRepository {
    private final static Genson GENSON = new Genson();

    private final ProfileDataSource mDataSource;
    private final MutableLiveData<List<Profile>> mProfiles;

    public ProfileRepository(ProfileDataSource dataSource) {
        mDataSource = dataSource;
        mProfiles = new MutableLiveData<>();
    }

    public static Profile deserialize(String json) {
        Profile partial = GENSON.deserialize(json, Profile.class);
        Profile.Builder builder = partial.asBuilder();
        if (partial.getId() == null) {
            builder.setId(UUID.randomUUID().toString());
        }
        return builder.build();
    }

    public static String serialize(Profile profile) {
        return GENSON.serialize(profile);
    }

    private List<Profile> copyOf(List<Profile> original) {
        return new ArrayList<>(original);
    }

    public LiveData<List<Profile>> getObservable() {
        mProfiles.setValue(copyOf(all()));
        return mProfiles;
    }

    public int size() {
        return mDataSource.size();
    }

    public Profile get(int position) {
        return mDataSource.get(position);
    }

    public Profile get(String id) {
        return mDataSource.get(id);
    }

    public int indexOf(String id) {
        return mDataSource.indexOf(id);
    }

    public List<Profile> all() {
        return mDataSource.all();
    }

    public void add(@NonNull Profile profile) {
        mDataSource.add(profile);
        mProfiles.setValue(copyOf(all()));
    }

    public void set(@NonNull Profile profile) {
        mDataSource.set(profile);
        mProfiles.setValue(copyOf(all()));
    }

    public void remove(@NonNull Profile profile) {
        mDataSource.remove(profile);
        mProfiles.setValue(copyOf(all()));
    }

    public Bitmap getScreenshot(@NonNull Profile profile) {
        return mDataSource.getScreenshot(profile);
    }

    public void putScreenshot(@NonNull Profile profile, @NonNull Bitmap screenshot) throws IOException {
        mDataSource.putScreenshot(profile, screenshot);
    }
}
