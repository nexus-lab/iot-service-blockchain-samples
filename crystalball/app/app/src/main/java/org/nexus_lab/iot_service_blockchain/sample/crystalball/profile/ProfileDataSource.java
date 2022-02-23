package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sdk.OffsetDateTimeConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProfileDataSource {
    private final static int MAX_SCREENSHOT_WIDTH = 1280;
    private final static String PREFERENCE_NAME = "profiles";
    private final static String SCREENSHOT_DIR_NAME = "screenshot";
    private final static Genson GENSON = new GensonBuilder().withConverters(new OffsetDateTimeConverter()).create();

    private final File mScreenshotDir;
    private final List<Profile> mProfiles;
    private final SharedPreferences mPreferences;

    public ProfileDataSource(Context context) {
        mScreenshotDir = new File(context.getCacheDir(), SCREENSHOT_DIR_NAME);
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
            if (Objects.equals(id, profile.getId())) {
                return profile;
            }
        }
        return null;
    }

    public int indexOf(@NonNull String id) {
        for (int i = 0; i < mProfiles.size(); i++) {
            if (Objects.equals(id, mProfiles.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public List<Profile> all() {
        return mProfiles;
    }

    public void add(@NonNull Profile profile) {
        mProfiles.add(profile);
        save();
    }

    public void set(@NonNull Profile profile) {
        int index = profile.getId() == null ? -1 : indexOf(profile.getId());
        if (index > -1) {
            mProfiles.set(index, profile);
            save();
        }
    }

    public void remove(@NonNull Profile profile) {
        mProfiles.remove(profile);
        save();
    }

    public void putScreenshot(@NonNull Profile profile, @NonNull Bitmap screenshot) throws IOException {
        if (profile.getId() == null) {
            return;
        }

        if (!mScreenshotDir.exists() && !mScreenshotDir.mkdir()) {
            throw new IOException("failed to create screenshot directory " + mScreenshotDir.getAbsolutePath());
        }

        File f = new File(mScreenshotDir, profile.getId());
        try {
            //noinspection ResultOfMethodCallIgnored
            f.createNewFile();
            FileOutputStream output = new FileOutputStream(f);

            if (screenshot.getWidth() * 9 != screenshot.getHeight() * 16) {
                // center crop bitmap to 16:9
                double ratio = (screenshot.getWidth() * 9.0) / (screenshot.getHeight() * 16.0);
                int width = ratio < 1 ? screenshot.getWidth() : screenshot.getHeight() * 16 / 9;
                int height = ratio > 1 ? screenshot.getHeight() : screenshot.getWidth() * 9 / 16;
                if (width > MAX_SCREENSHOT_WIDTH) {
                    width = MAX_SCREENSHOT_WIDTH;
                    height = MAX_SCREENSHOT_WIDTH * 9 / 16;
                }
                int x = (screenshot.getWidth() - width) / 2;
                int y = (screenshot.getHeight() - height) / 2;
                screenshot = Bitmap.createBitmap(screenshot, x, y, width, height);
            }
            screenshot.compress(Bitmap.CompressFormat.PNG, 85, output);

            output.flush();
            output.close();
        } catch (IOException e) {
            throw new IOException("failed to create screenshot file " + f.getAbsolutePath(), e);
        }
    }

    public Bitmap getScreenshot(@NonNull Profile profile) {
        if (profile.getId() == null) {
            return null;
        }

        File f = new File(mScreenshotDir, profile.getId());
        if (!f.exists()) {
            return null;
        }

        return BitmapFactory.decodeFile(f.getAbsolutePath());
    }
}
