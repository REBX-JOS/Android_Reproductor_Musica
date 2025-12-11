package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a song in the music library.
 * Stores metadata extracted from audio files including ID3 tags.
 */
@Entity(tableName = "songs", indices = {@Index(value = "path", unique = true)})
public class Song {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String path;           // File path on device
    private String title;          // Song title
    private String artist;         // Artist name
    private String album;          // Album name
    private long duration;         // Duration in milliseconds
    private String albumArtPath;   // Path to extracted album art
    private boolean isFavorite;    // Favorite flag
    private long dateAdded;        // Timestamp when added to library
    private long lastPlayed;       // Timestamp of last playback
    private long playbackPosition; // Last playback position in ms

    public Song() {
        this.isFavorite = false;
        this.dateAdded = System.currentTimeMillis();
        this.lastPlayed = 0;
        this.playbackPosition = 0;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public String getAlbumArtPath() { return albumArtPath; }
    public void setAlbumArtPath(String albumArtPath) { this.albumArtPath = albumArtPath; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public long getLastPlayed() { return lastPlayed; }
    public void setLastPlayed(long lastPlayed) { this.lastPlayed = lastPlayed; }

    public long getPlaybackPosition() { return playbackPosition; }
    public void setPlaybackPosition(long playbackPosition) { this.playbackPosition = playbackPosition; }
}
