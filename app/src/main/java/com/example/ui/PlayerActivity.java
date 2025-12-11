package com.example.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.bumptech.glide.Glide;
import com.example.R;
import com.example.data.entity.Song;
import com.example.data.repository.MusicRepository;
import com.example.service.PlayerService;
import com.example.utils.TimeUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

/**
 * Activity for playing music with full playback controls.
 * Displays album art, song info, playback controls, and progress bar.
 */
public class PlayerActivity extends AppCompatActivity {
    
    private static final String TAG = "PlayerActivity";
    
    private ImageView albumArt;
    private TextView songTitle;
    private TextView songArtist;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalTime;
    private ImageButton btnPlayPause;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnShuffle;
    private ImageButton btnRepeat;
    private ImageButton btnFavorite;
    
    private MusicRepository repository;
    private Song currentSong;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    
    private Handler handler;
    private Runnable updateProgressRunnable;
    private boolean isUserSeeking = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        // Initialize repository
        repository = new MusicRepository(this);
        handler = new Handler(Looper.getMainLooper());
        
        // Initialize views
        initializeViews();
        
        // Get song ID from intent
        long songId = getIntent().getLongExtra("song_id", -1);
        
        // Load song data
        if (songId != -1) {
            loadSongData(songId);
        }
        
        // Start PlayerService
        Intent serviceIntent = new Intent(this, PlayerService.class);
        startService(serviceIntent);
        
        // Connect to MediaController
        connectToMediaController();
        
        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeViews() {
        albumArt = findViewById(R.id.album_art);
        songTitle = findViewById(R.id.song_title);
        songArtist = findViewById(R.id.song_artist);
        seekBar = findViewById(R.id.seek_bar);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnShuffle = findViewById(R.id.btn_shuffle);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnFavorite = findViewById(R.id.btn_favorite);
        
        // Setup click listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrevious.setOnClickListener(v -> skipToPrevious());
        btnNext.setOnClickListener(v -> skipToNext());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        
        // Setup SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(TimeUtils.formatDuration(progress));
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaController != null) {
                    mediaController.seekTo(seekBar.getProgress());
                }
                isUserSeeking = false;
            }
        });
        
        // Setup progress update runnable
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                handler.postDelayed(this, 1000);
            }
        };
    }
    
    private void connectToMediaController() {
        SessionToken sessionToken = new SessionToken(this, 
            new ComponentName(this, PlayerService.class));
        
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                
                // Add listener for playback state changes
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        runOnUiThread(() -> updatePlayPauseButton(isPlaying));
                    }
                    
                    @Override
                    public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                        // Handle track changes if needed
                    }
                });
                
                // Play the current song
                if (currentSong != null) {
                    playSong(currentSong);
                }
                
                // Start progress updates
                handler.post(updateProgressRunnable);
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error connecting to MediaController", e);
            }
        }, Runnable::run);
    }
    
    private void loadSongData(long songId) {
        new Thread(() -> {
            currentSong = repository.getSongById(songId);
            
            runOnUiThread(() -> {
                if (currentSong != null) {
                    updateUI(currentSong);
                }
            });
        }).start();
    }
    
    private void updateUI(Song song) {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
        
        // Load album art
        if (song.getAlbumArtPath() != null) {
            Glide.with(this)
                .load(song.getAlbumArtPath())
                .placeholder(R.drawable.ic_album)
                .error(R.drawable.ic_album)
                .into(albumArt);
        } else {
            albumArt.setImageResource(R.drawable.ic_album);
        }
        
        // Set duration
        seekBar.setMax((int) song.getDuration());
        totalTime.setText(TimeUtils.formatDuration(song.getDuration()));
        
        // Update favorite button
        updateFavoriteButton(song.isFavorite());
    }
    
    private void playSong(Song song) {
        if (mediaController == null || song == null || song.getPath() == null) {
            Log.e(TAG, "Cannot play song: mediaController or song is null");
            return;
        }
        
        try {
            MediaItem mediaItem = MediaItem.fromUri(song.getPath());
            mediaController.setMediaItem(mediaItem);
            mediaController.prepare();
            mediaController.play();
            
            updatePlayPauseButton(true);
        } catch (Exception e) {
            Log.e(TAG, "Error playing song", e);
        }
    }
    
    private void togglePlayPause() {
        if (mediaController == null) return;
        
        if (mediaController.isPlaying()) {
            mediaController.pause();
        } else {
            mediaController.play();
        }
    }
    
    private void skipToNext() {
        if (mediaController != null && mediaController.hasNextMediaItem()) {
            mediaController.seekToNext();
        }
    }
    
    private void skipToPrevious() {
        if (mediaController != null && mediaController.hasPreviousMediaItem()) {
            mediaController.seekToPrevious();
        }
    }
    
    private void toggleFavorite() {
        if (currentSong != null) {
            boolean newState = !currentSong.isFavorite();
            currentSong.setFavorite(newState);
            repository.updateFavoriteStatus(currentSong.getId(), newState);
            updateFavoriteButton(newState);
        }
    }
    
    private void updatePlayPauseButton(boolean isPlaying) {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }
    
    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }
    
    private void updateProgress() {
        if (mediaController != null && !isUserSeeking) {
            long position = mediaController.getCurrentPosition();
            seekBar.setProgress((int) position);
            currentTime.setText(TimeUtils.formatDuration(position));
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Stop progress updates
        handler.removeCallbacks(updateProgressRunnable);
        
        // Release MediaController
        if (controllerFuture != null) {
            MediaController.releaseFuture(controllerFuture);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
