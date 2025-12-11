package com.example.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
    version = 2,
    exportSchema = false
)
public abstract class MusicDatabase extends RoomDatabase {
    
    private static volatile MusicDatabase INSTANCE;
    private static final String DATABASE_NAME = "music_database";
    
    /**
     * Migration from version 1 to version 2.
     * Adds unique index on the path column in songs table to prevent duplicates.
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create a unique index on the path column
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_songs_path ON songs(path)");
        }
    };
    
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
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
