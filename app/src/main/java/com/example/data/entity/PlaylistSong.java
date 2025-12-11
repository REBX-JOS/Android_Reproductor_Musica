package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

/**
 * Entity representing the many-to-many relationship between playlists and songs.
 */
@Entity(
    tableName = "playlist_songs",
    primaryKeys = {"playlistId", "songId", "position"},
    foreignKeys = {
        @ForeignKey(entity = Playlist.class, 
                    parentColumns = "id", 
                    childColumns = "playlistId",
                    onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Song.class,
                    parentColumns = "id",
                    childColumns = "songId",
                    onDelete = ForeignKey.CASCADE)
    },
    indices = {@Index("playlistId"), @Index("songId")}
)
public class PlaylistSong {
    private long playlistId;
    private long songId;
    private int position;  // Order within playlist

    @Ignore
    public PlaylistSong() {}

    public PlaylistSong(long playlistId, long songId, int position) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.position = position;
    }

    // Getters and Setters
    public long getPlaylistId() { return playlistId; }
    public void setPlaylistId(long playlistId) { this.playlistId = playlistId; }

    public long getSongId() { return songId; }
    public void setSongId(long songId) { this.songId = songId; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
