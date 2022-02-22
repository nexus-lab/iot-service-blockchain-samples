package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.owlike.genson.Genson;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {
    private final static Genson GENSON = new Genson();

    private final ProfileDataSource mDataSource;
    private final MutableLiveData<List<Profile>> mProfiles;

    public ProfileRepository(ProfileDataSource dataSource) {
        mDataSource = dataSource;
        mProfiles = new MutableLiveData<>();
    }

    public static Profile deserialize(String json) {
        return GENSON.deserialize(json, Profile.class).asBuilder().build();
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

    public void add(Profile profile) {
        mDataSource.add(profile);
        mProfiles.setValue(copyOf(all()));
    }

    public void set(Profile profile) {
        mDataSource.set(profile);
        mProfiles.setValue(copyOf(all()));
    }

    public void remove(Profile profile) {
        mDataSource.remove(profile);
        mProfiles.setValue(copyOf(all()));
    }
}
