package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {
    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPlayerBinding binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemBars();

        binding.close.setOnClickListener(v -> supportFinishAfterTransition());

        player = new ExoPlayer.Builder(this).build();
        binding.player.setPlayer(player);
        binding.player.setControllerVisibilityListener(binding.closeContainer::setVisibility);
        binding.closeContainer.setVisibility(binding.player.isControllerVisible() ? View.VISIBLE : View.INVISIBLE);

        player.setMediaItem(MediaItem.fromUri("rtmp://192.168.1.5:1935"));
        player.prepare();
        player.setPlayWhenReady(true);
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
        if (player != null) {
            player.release();
        }
    }
}