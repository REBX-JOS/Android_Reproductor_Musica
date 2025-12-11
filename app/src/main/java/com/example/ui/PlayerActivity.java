package com.example.reproductordemsica.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reproductordemsica.R;

/**
 * Activity for playing music with full playback controls.
 * Displays album art, song info, playback controls, and progress bar.
 */
public class PlayerActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        // Get song ID from intent
        long songId = getIntent().getLongExtra("song_id", -1);
        
        // TODO: Initialize player UI and controls
        // TODO: Connect to PlayerService
        // TODO: Load song data and start playback
        
        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
