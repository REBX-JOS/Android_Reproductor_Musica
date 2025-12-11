package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.data.entity.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the playback queue.
 * Handles queue operations like add, remove, shuffle, and persistence.
 */
public class QueueManager {
    
    private static final String PREFS_NAME = "queue_prefs";
    private static final String KEY_QUEUE = "queue";
    private static final String KEY_CURRENT_INDEX = "current_index";
    private static final String KEY_SHUFFLE_ENABLED = "shuffle_enabled";
    private static final String KEY_REPEAT_MODE = "repeat_mode";
    
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ALL = 1;
    public static final int REPEAT_ONE = 2;
    
    private final SharedPreferences prefs;
    private final Gson gson;
    private List<Song> queue;
    private List<Song> originalQueue;  // For restoring after shuffle
    private int currentIndex;
    private boolean shuffleEnabled;
    private int repeatMode;
    
    public QueueManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        queue = new ArrayList<>();
        originalQueue = new ArrayList<>();
        currentIndex = 0;
        shuffleEnabled = false;
        repeatMode = REPEAT_OFF;
        loadQueue();
    }
    
    /**
     * Set the entire queue with a list of songs.
     */
    public void setQueue(List<Song> songs, int startIndex) {
        queue.clear();
        queue.addAll(songs);
        originalQueue.clear();
        originalQueue.addAll(songs);
        currentIndex = startIndex;
        saveQueue();
    }
    
    /**
     * Add a song to the end of the queue.
     */
    public void addToQueue(Song song) {
        queue.add(song);
        if (!shuffleEnabled) {
            originalQueue.add(song);
        }
        saveQueue();
    }
    
    /**
     * Insert a song at a specific position.
     */
    public void insertAt(int position, Song song) {
        if (position >= 0 && position <= queue.size()) {
            queue.add(position, song);
            if (!shuffleEnabled) {
                originalQueue.add(position, song);
            }
            if (position <= currentIndex) {
                currentIndex++;
            }
            saveQueue();
        }
    }
    
    /**
     * Remove a song at a specific position.
     */
    public void removeAt(int position) {
        if (position >= 0 && position < queue.size()) {
            Song removed = queue.remove(position);
            if (!shuffleEnabled) {
                originalQueue.remove(removed);
            }
            if (position < currentIndex) {
                currentIndex--;
            } else if (position == currentIndex && currentIndex >= queue.size()) {
                currentIndex = Math.max(0, queue.size() - 1);
            }
            saveQueue();
        }
    }
    
    /**
     * Move a song from one position to another.
     */
    public void moveSong(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && fromPosition < queue.size() &&
            toPosition >= 0 && toPosition < queue.size()) {
            Song song = queue.remove(fromPosition);
            queue.add(toPosition, song);
            
            // Update current index if needed
            if (fromPosition == currentIndex) {
                currentIndex = toPosition;
            } else if (fromPosition < currentIndex && toPosition >= currentIndex) {
                currentIndex--;
            } else if (fromPosition > currentIndex && toPosition <= currentIndex) {
                currentIndex++;
            }
            saveQueue();
        }
    }
    
    /**
     * Clear the entire queue.
     */
    public void clearQueue() {
        queue.clear();
        originalQueue.clear();
        currentIndex = 0;
        saveQueue();
    }
    
    /**
     * Get the current song in the queue.
     */
    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return queue.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Move to the next song in the queue.
     */
    public Song next() {
        if (queue.isEmpty()) return null;
        
        if (repeatMode == REPEAT_ONE) {
            return getCurrentSong();
        }
        
        currentIndex++;
        if (currentIndex >= queue.size()) {
            if (repeatMode == REPEAT_ALL) {
                currentIndex = 0;
            } else {
                currentIndex = queue.size() - 1;
                return null;  // End of queue
            }
        }
        saveQueue();
        return getCurrentSong();
    }
    
    /**
     * Move to the previous song in the queue.
     */
    public Song previous() {
        if (queue.isEmpty()) return null;
        
        if (repeatMode == REPEAT_ONE) {
            return getCurrentSong();
        }
        
        currentIndex--;
        if (currentIndex < 0) {
            if (repeatMode == REPEAT_ALL) {
                currentIndex = queue.size() - 1;
            } else {
                currentIndex = 0;
            }
        }
        saveQueue();
        return getCurrentSong();
    }
    
    /**
     * Skip to a specific song in the queue.
     */
    public Song skipTo(int index) {
        if (index >= 0 && index < queue.size()) {
            currentIndex = index;
            saveQueue();
            return getCurrentSong();
        }
        return null;
    }
    
    /**
     * Toggle shuffle mode.
     */
    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
        
        if (shuffleEnabled) {
            // Save current song
            Song currentSong = getCurrentSong();
            
            // Shuffle the queue
            Collections.shuffle(queue);
            
            // Make sure current song is at the current position
            if (currentSong != null) {
                int newIndex = queue.indexOf(currentSong);
                if (newIndex != -1 && newIndex != currentIndex) {
                    Collections.swap(queue, currentIndex, newIndex);
                }
            }
        } else {
            // Restore original order
            Song currentSong = getCurrentSong();
            queue.clear();
            queue.addAll(originalQueue);
            
            // Find the current song in the restored queue
            if (currentSong != null) {
                currentIndex = queue.indexOf(currentSong);
                if (currentIndex == -1) {
                    currentIndex = 0;
                }
            }
        }
        
        prefs.edit().putBoolean(KEY_SHUFFLE_ENABLED, shuffleEnabled).apply();
        saveQueue();
    }
    
    /**
     * Cycle through repeat modes.
     */
    public void cycleRepeatMode() {
        repeatMode = (repeatMode + 1) % 3;
        prefs.edit().putInt(KEY_REPEAT_MODE, repeatMode).apply();
    }
    
    /**
     * Set repeat mode directly.
     */
    public void setRepeatMode(int mode) {
        if (mode >= REPEAT_OFF && mode <= REPEAT_ONE) {
            repeatMode = mode;
            prefs.edit().putInt(KEY_REPEAT_MODE, repeatMode).apply();
        }
    }
    
    // Getters
    public List<Song> getQueue() { return new ArrayList<>(queue); }
    public int getCurrentIndex() { return currentIndex; }
    public boolean isShuffleEnabled() { return shuffleEnabled; }
    public int getRepeatMode() { return repeatMode; }
    public int getQueueSize() { return queue.size(); }
    
    /**
     * Save queue state to SharedPreferences.
     */
    private void saveQueue() {
        String queueJson = gson.toJson(queue);
        prefs.edit()
            .putString(KEY_QUEUE, queueJson)
            .putInt(KEY_CURRENT_INDEX, currentIndex)
            .apply();
    }
    
    /**
     * Load queue state from SharedPreferences.
     */
    private void loadQueue() {
        String queueJson = prefs.getString(KEY_QUEUE, null);
        if (queueJson != null) {
            Type type = new TypeToken<List<Song>>(){}.getType();
            List<Song> loadedQueue = gson.fromJson(queueJson, type);
            if (loadedQueue != null) {
                queue.clear();
                queue.addAll(loadedQueue);
                originalQueue.clear();
                originalQueue.addAll(loadedQueue);
            }
        }
        currentIndex = prefs.getInt(KEY_CURRENT_INDEX, 0);
        shuffleEnabled = prefs.getBoolean(KEY_SHUFFLE_ENABLED, false);
        repeatMode = prefs.getInt(KEY_REPEAT_MODE, REPEAT_OFF);
    }
}
