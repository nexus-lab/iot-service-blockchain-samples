package org.nexus_lab.iot_service_blockchain.sample.crystalball.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.BlockchainService;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.LiveDataWrapper;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityPlayerBinding;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.Profile;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.profile.ProfileRepository;
import org.nexus_lab.iot_service_blockchain.sdk.ServiceResponse;

import java.io.IOException;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity implements Player.Listener {
    public final static String TAG = PlayerActivity.class.getName();
    public final static String ARG_PROFILE_ID = "PROFILE_ID";

    private String mId;
    private ExoPlayer mPlayer;
    private ProfileRepository mRepository;
    private ActivityPlayerBinding mViewBinding;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BlockchainService.ServiceBinder binder = (BlockchainService.ServiceBinder) service;
            if (mViewBinding != null) {
                Snackbar.make(mViewBinding.getRoot(), R.string.alert_stream_requesting, Snackbar.LENGTH_LONG).show();
            }
            Observer<LiveDataWrapper<ServiceResponse>> observer = data -> {
                if (mViewBinding != null) {
                    if (data.getThrowable() != null) {
                        Snackbar.make(mViewBinding.getRoot(), R.string.alert_stream_request_failed, Snackbar.LENGTH_LONG).show();
                    } else if (data.getData() != null) {
                        play(data.getData().getReturnValue());
                    }
                }
            };
            LiveData<LiveDataWrapper<ServiceResponse>> observable = binder.request(mRepository.get(mId));
            observable.observe(PlayerActivity.this, observer);
            new Handler().postDelayed(() -> {
                observable.removeObserver(observer);
                if (mViewBinding != null) {
                    Snackbar.make(mViewBinding.getRoot(), R.string.alert_stream_request_failed, Snackbar.LENGTH_LONG).show();
                }
            }, 30 * 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = ((App) getApplication()).getProfileRepository();
        mViewBinding = ActivityPlayerBinding.inflate(getLayoutInflater());

        mId = getIntent().getStringExtra(ARG_PROFILE_ID);
        if (mId == null || mRepository.get(mId) == null) {
            Snackbar.make(mViewBinding.getRoot(), R.string.alert_profile_not_found, Snackbar.LENGTH_LONG).show();
            supportFinishAfterTransition();
            return;
        }

        setContentView(mViewBinding.getRoot());

        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }

        mViewBinding.close.setOnClickListener(v -> supportFinishAfterTransition());

        mPlayer = new ExoPlayer.Builder(this).build();
        mViewBinding.player.setPlayer(mPlayer);
        mViewBinding.player.setControllerVisibilityListener(mViewBinding.closeContainer::setVisibility);
        mViewBinding.closeContainer.setVisibility(mViewBinding.player.isControllerVisible() ? View.VISIBLE : View.INVISIBLE);
        mPlayer.addListener(this);

        bindService(new Intent(this, BlockchainService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mPlayer.release();
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            View view = mViewBinding.player.getVideoSurfaceView();
            if (view instanceof SurfaceView) {
                screenshot((SurfaceView) view);
            }
        }
    }

    private void play(@NonNull String uri) {
        Snackbar.make(mViewBinding.getRoot(), getString(R.string.alert_stream_playing, uri), Snackbar.LENGTH_LONG).show();
        mPlayer.setMediaItem(MediaItem.fromUri(uri));
        mPlayer.prepare();
        mPlayer.setPlayWhenReady(true);
    }

    private void screenshot(SurfaceView view) {
        Bitmap screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request(view, screenshot, (PixelCopy.OnPixelCopyFinishedListener) result -> {
            if (mRepository == null) {
                return;
            }
            Profile profile = mRepository.get(mId);
            if (profile == null || PixelCopy.SUCCESS != result) {
                return;
            }
            try {
                mRepository.putScreenshot(profile, screenshot);
                profile = profile.asBuilder().setScreenshotTime(new Date()).build();
                mRepository.set(profile);
            } catch (IOException e) {
                Log.w(TAG, "failed to save screenshot", e);
            }
        }, view.getHandler());
    }
}