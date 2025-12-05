package com.example.reproductordemsica.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.reproductordemsica.data.entity.Song;
import com.example.reproductordemsica.data.repository.MusicRepository;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scans device storage for audio files and extracts metadata.
 * Uses MediaStore for fast scanning and mp3agic for detailed ID3 tag reading.
 */
public class LibraryScanner {
    
    private static final String TAG = "LibraryScanner";
    
    private final Context context;
    private final MusicRepository repository;
    private final ExecutorService executorService;
    private ScanListener scanListener;
    
    public interface ScanListener {
        void onScanStarted();
        void onScanProgress(int current, int total);
        void onScanCompleted(int songsFound);
        void onScanError(String error);
    }
    
    public LibraryScanner(Context context) {
        this.context = context;
        this.repository = new MusicRepository(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public void setScanListener(ScanListener listener) {
        this.scanListener = listener;
    }
    
    /**
     * Start scanning the device for audio files.
     */
    public void startScan() {
        executorService.execute(this::scanMediaStore);
    }
    
    /**
     * Scan audio files using MediaStore API.
     * This is the primary method for discovering audio files on the device.
     */
    private void scanMediaStore() {
        if (scanListener != null) {
            scanListener.onScanStarted();
        }
        
        List<Song> songs = new ArrayList<>();
        
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        ContentResolver contentResolver = context.getContentResolver();
        
        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
        )) {
            if (cursor == null) {
                if (scanListener != null) {
                    scanListener.onScanError("Unable to access media store");
                }
                return;
            }
            
            int total = cursor.getCount();
            int current = 0;
            
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
            
            while (cursor.moveToNext()) {
                current++;
                
                try {
                    String path = cursor.getString(dataColumn);
                    File file = new File(path);
                    
                    if (!file.exists()) {
                        continue;
                    }
                    
                    Song song = new Song();
                    song.setPath(path);
                    song.setTitle(cursor.getString(titleColumn));
                    song.setArtist(cursor.getString(artistColumn));
                    song.setAlbum(cursor.getString(albumColumn));
                    song.setDuration(cursor.getLong(durationColumn));
                    song.setDateAdded(cursor.getLong(dateAddedColumn) * 1000);
                    
                    // Try to extract additional metadata from ID3 tags
                    enrichMetadataFromId3(song);
                    
                    // Set defaults for missing data
                    if (song.getTitle() == null || song.getTitle().isEmpty()) {
                        song.setTitle(file.getName());
                    }
                    if (song.getArtist() == null || song.getArtist().isEmpty()) {
                        song.setArtist("Unknown Artist");
                    }
                    if (song.getAlbum() == null || song.getAlbum().isEmpty()) {
                        song.setAlbum("Unknown Album");
                    }
                    
                    songs.add(song);
                    
                    if (scanListener != null && current % 10 == 0) {
                        final int prog = current;
                        scanListener.onScanProgress(prog, total);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error processing audio file: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning media store: " + e.getMessage());
            if (scanListener != null) {
                scanListener.onScanError(e.getMessage());
            }
            return;
        }
        
        // Save songs to database
        if (!songs.isEmpty()) {
            repository.insertSongs(songs);
        }
        
        if (scanListener != null) {
            scanListener.onScanCompleted(songs.size());
        }
        
        Log.i(TAG, "Scan completed. Found " + songs.size() + " songs.");
    }
    
    /**
     * Extract additional metadata from ID3 tags using mp3agic library.
     * This provides more detailed information than MediaStore alone.
     */
    private void enrichMetadataFromId3(Song song) {
        try {
            String path = song.getPath();
            if (path == null || !path.toLowerCase().endsWith(".mp3")) {
                return;  // mp3agic only works with MP3 files
            }
            
            Mp3File mp3File = new Mp3File(path);
            
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                
                // Override with ID3 data if available and more complete
                if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isEmpty()) {
                    song.setTitle(id3v2Tag.getTitle());
                }
                if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isEmpty()) {
                    song.setArtist(id3v2Tag.getArtist());
                }
                if (id3v2Tag.getAlbum() != null && !id3v2Tag.getAlbum().isEmpty()) {
                    song.setAlbum(id3v2Tag.getAlbum());
                }
                
                // Extract album art if available
                byte[] imageData = id3v2Tag.getAlbumImage();
                if (imageData != null && imageData.length > 0) {
                    // Save album art to cache directory
                    String albumArtPath = saveAlbumArt(song.getPath(), imageData);
                    song.setAlbumArtPath(albumArtPath);
                }
            }
        } catch (Exception e) {
            // If ID3 extraction fails, just use MediaStore data
            Log.w(TAG, "Could not read ID3 tags for: " + song.getPath());
        }
    }
    
    /**
     * Save album art image to app cache directory.
     */
    private String saveAlbumArt(String songPath, byte[] imageData) {
        try {
            File cacheDir = new File(context.getCacheDir(), "album_art");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            
            // Create unique filename based on song path hash
            String filename = "cover_" + Math.abs(songPath.hashCode()) + ".jpg";
            File artFile = new File(cacheDir, filename);
            
            java.io.FileOutputStream fos = new java.io.FileOutputStream(artFile);
            fos.write(imageData);
            fos.close();
            
            return artFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error saving album art: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Shutdown the scanner executor.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
