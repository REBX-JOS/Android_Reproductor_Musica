package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing a playlist in the music library.
 */
@Entity(tableName = "playlists")
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private long dateCreated;
    private long dateModified;

    public Playlist() {
        this.dateCreated = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getDateCreated() { return dateCreated; }
    public void setDateCreated(long dateCreated) { this.dateCreated = dateCreated; }

    public long getDateModified() { return dateModified; }
    public void setDateModified(long dateModified) { this.dateModified = dateModified; }
}
