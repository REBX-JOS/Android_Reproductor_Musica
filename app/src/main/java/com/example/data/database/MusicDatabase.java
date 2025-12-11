package com.example.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.data.dao.PlaylistDao;
import com.example.data.dao.PlaylistSongDao;
import com.example.data.dao.SongDao;
import com.example.data.entity.Playlist;
import com.example.data.entity.PlaylistSong;
import com.example.data.entity.Song;

/**
 * Room database for the music player application.
 * Manages songs, playlists, and their relationships.
 */
@Database(
    entities = {Song.class, Playlist.class, PlaylistSong.class},
    version = 1,
    exportSchema = false
)
public abstract class MusicDatabase extends RoomDatabase {
    
    private static volatile MusicDatabase INSTANCE;
    private static final String DATABASE_NAME = "music_database";
    
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistSongDao playlistSongDao();
    
    /**
     * Get singleton instance of the database.
     */
    public static MusicDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MusicDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        MusicDatabase.class,
                        DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
