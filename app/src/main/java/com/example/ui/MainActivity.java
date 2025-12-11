package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.R;
import com.example.utils.PermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity that hosts the primary navigation and fragments.
 * Displays library, playlists, and settings via bottom navigation.
 */
public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Setup bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(this::onNavigationItemSelected);
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new LibraryFragment());
        }
        
        // Check permissions
        checkPermissions();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Setup search view
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint(getString(R.string.search));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        performSearch(query);
                        return true;
                    }
                    
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        performSearch(newText);
                        return true;
                    }
                });
            }
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Handle bottom navigation item selection.
     */
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        
        if (id == R.id.nav_library) {
            fragment = new LibraryFragment();
        } else if (id == R.id.nav_playlists) {
            fragment = new PlaylistsFragment();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        
        if (fragment != null) {
            return loadFragment(fragment);
        }
        
        return false;
    }
    
    /**
     * Load a fragment into the container.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    
    /**
     * Perform search across the current fragment.
     */
    private void performSearch(String query) {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        
        if (currentFragment instanceof LibraryFragment) {
            ((LibraryFragment) currentFragment).performSearch(query);
        }
    }
    
    /**
     * Check and request necessary permissions.
     */
    private void checkPermissions() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this);
        }
        
        if (!PermissionHelper.hasNotificationPermission(this)) {
            PermissionHelper.requestNotificationPermission(this);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PermissionHelper.REQUEST_STORAGE_PERMISSION) {
            // Refresh library if permission was granted
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof LibraryFragment) {
                ((LibraryFragment) currentFragment).refreshLibrary();
            }
        }
    }
}
