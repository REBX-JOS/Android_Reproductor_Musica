package com.example.reproductordemsica.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.example.R;
import com.example.data.entity.Song;
import com.example.data.repository.MusicRepository;
import com.example.ui.MainActivity;
import com.example.utils.QueueManager;

import java.util.List;

/**
 * Foreground service for music playback.
 * Uses ExoPlayer for media playback and MediaSession for media controls.
 * Displays a persistent notification with playback controls.
 */
public class PlayerService extends MediaSessionService {
    
    private static final String TAG = "PlayerService";
    private static final String CHANNEL_ID = "music_playback_channel";
    private static final int NOTIFICATION_ID = 1;
    
    private ExoPlayer player;
    private MediaSession mediaSession;
    private QueueManager queueManager;
    private MusicRepository repository;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        // Initialize components
        queueManager = new QueueManager(this);
        repository = new MusicRepository(this);
        
        // Create notification channel
        createNotificationChannel();
        
        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        
        // Setup player listeners
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    handleTrackEnded();
                }
                updateNotification();
            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updateNotification();
                
                // Save playback position periodically
                if (isPlaying) {
                    savePlaybackPosition();
                }
            }
            
            @Override
            public void onPositionDiscontinuity(
                    Player.PositionInfo oldPosition,
                    Player.PositionInfo newPosition,
                    int reason) {
                savePlaybackPosition();
            }
        });
        
        // Create MediaSession
        mediaSession = new MediaSession.Builder(this, player)
                .setCallback(new MediaSessionCallback())
                .build();
        
        // Restore queue and start playback if there was a song playing
        restoreState();
    }
    
    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification());
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        // Save current state
        savePlaybackPosition();
        
        // Release resources
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        
        if (player != null) {
            player.release();
            player = null;
        }
        
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }
    
    /**
     * Create notification channel for Android O and above.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.notification_channel_description));
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Create the notification for foreground service.
     */
    private Notification createNotification() {
        Song currentSong = queueManager.getCurrentSong();
        
        // Create intent to open the app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(currentSong != null ? currentSong.getTitle() : getString(R.string.app_name))
                .setContentText(currentSong != null ? currentSong.getArtist() : getString(R.string.no_songs_found))
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        
        // Add media style with MediaSession
        if (mediaSession != null) {
            androidx.media3.session.MediaNotification.ActionFactory actionFactory =
                    (mediaSession1, customActions) -> {
                        // Use default actions
                        return null;
                    };
        }
        
        return builder.build();
    }
    
    /**
     * Update the notification with current playback state.
     */
    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification());
        }
    }
    
    /**
     * Handle when a track ends.
     */
    private void handleTrackEnded() {
        Song nextSong = queueManager.next();
        if (nextSong != null) {
            playSong(nextSong);
        } else {
            // Queue ended
            player.pause();
        }
    }
    
    /**
     * Play a specific song.
     */
    private void playSong(Song song) {
        if (song == null || song.getPath() == null) {
            Log.e(TAG, "Cannot play null song");
            return;
        }
        
        try {
            MediaItem mediaItem = MediaItem.fromUri(song.getPath());
            player.setMediaItem(mediaItem);
            player.prepare();
            
            // Restore playback position if available
            if (song.getPlaybackPosition() > 0) {
                player.seekTo(song.getPlaybackPosition());
            }
            
            player.play();
            updateNotification();
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing song: " + e.getMessage());
        }
    }
    
    /**
     * Save current playback position to database.
     */
    private void savePlaybackPosition() {
        Song currentSong = queueManager.getCurrentSong();
        if (currentSong != null && player != null) {
            long position = player.getCurrentPosition();
            long timestamp = System.currentTimeMillis();
            repository.updatePlaybackPosition(currentSong.getId(), position, timestamp);
        }
    }
    
    /**
     * Restore previous playback state.
     */
    private void restoreState() {
        // Queue is automatically restored by QueueManager
        Song currentSong = queueManager.getCurrentSong();
        if (currentSong != null) {
            playSong(currentSong);
            // Don't auto-play, just prepare
            player.pause();
        }
    }
    
    /**
     * MediaSession callback for handling media button events.
     */
    private class MediaSessionCallback implements MediaSession.Callback {
        
        @Override
        public MediaSession.ConnectionResult onConnect(
                MediaSession session,
                MediaSession.ControllerInfo controller) {
            return MediaSession.Callback.super.onConnect(session, controller);
        }
        
        @Override
        public void onPlaybackResumption(
                MediaSession mediaSession,
                MediaSession.ControllerInfo controller) {
            // Handle playback resumption
        }
    }
    
    /**
     * Public API for controlling playback from activities.
     */
    public static class PlaybackController {
        // Methods to control playback will be called via service binding
        // For now, using broadcasts or LiveData for communication
    }
}
