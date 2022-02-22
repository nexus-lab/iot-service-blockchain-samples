package org.nexus_lab.iot_service_blockchain.sample.crystalball.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.snackbar.Snackbar;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {
    public final static String ARG_ORGANIZATION_ID = "ORGANIZATION_ID";
    public final static String ARG_DEVICE_ID = "DEVICE_ID";
    public final static String ARG_SERVICE_NAME = "SERVICE_NAME";

    private String mDeviceId;
    private String mServiceName;
    private String mOrganizationId;
    private ExoPlayer mPlayer;
    private ActivityPlayerBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityPlayerBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        mOrganizationId = intent.getStringExtra(ARG_ORGANIZATION_ID);
        mDeviceId = intent.getStringExtra(ARG_DEVICE_ID);
        mServiceName = intent.getStringExtra(ARG_SERVICE_NAME);
        if (mOrganizationId == null || mDeviceId == null || mServiceName == null) {
            Snackbar.make(mViewBinding.getRoot(), R.string.alert_player_arguments_invalid, Snackbar.LENGTH_LONG).show();
            supportFinishAfterTransition();
            return;
        }

        setContentView(mViewBinding.getRoot());

        hideSystemBars();

        mViewBinding.close.setOnClickListener(v -> supportFinishAfterTransition());

        mPlayer = new ExoPlayer.Builder(this).build();
        mViewBinding.player.setPlayer(mPlayer);
        mViewBinding.player.setControllerVisibilityListener(mViewBinding.closeContainer::setVisibility);
        mViewBinding.closeContainer.setVisibility(mViewBinding.player.isControllerVisible() ? View.VISIBLE : View.INVISIBLE);
    }

    private void play() {
        mPlayer.setMediaItem(MediaItem.fromUri("rtmp://192.168.1.5:1935"));
        mPlayer.prepare();
        mPlayer.setPlayWhenReady(true);
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }
}