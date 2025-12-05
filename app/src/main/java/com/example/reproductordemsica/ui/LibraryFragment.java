package com.example.reproductordemsica.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android:widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.reproductordemsica.R;
import com.example.reproductordemsica.data.entity.Song;
import com.example.reproductordemsica.data.repository.MusicRepository;
import com.example.reproductordemsica.utils.LibraryScanner;
import com.example.reproductordemsica.utils.PermissionHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying the music library.
 * Shows all songs, favorites, and recent tracks with tab navigation.
 */
public class LibraryFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyView;
    private TabLayout tabLayout;
    
    private SongAdapter adapter;
    private MusicRepository repository;
    private LibraryScanner scanner;
    private LiveData<List<Song>> currentSongsLiveData;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        emptyView = view.findViewById(R.id.empty_view);
        tabLayout = view.findViewById(R.id.tab_layout);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongAdapter(new ArrayList<>(), this::onSongClick, this::onFavoriteClick);
        recyclerView.setAdapter(adapter);
        
        // Initialize repository and scanner
        repository = new MusicRepository(requireContext());
        scanner = new LibraryScanner(requireContext());
        
        // Setup swipe refresh
        swipeRefresh.setOnRefreshListener(this::refreshLibrary);
        
        // Setup tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadSongsForTab(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // Load initial data
        loadSongsForTab(0);
        
        return view;
    }
    
    /**
     * Load songs based on selected tab.
     */
    private void loadSongsForTab(int position) {
        // Remove previous observer if any
        if (currentSongsLiveData != null) {
            currentSongsLiveData.removeObservers(getViewLifecycleOwner());
        }
        
        // Load appropriate data based on tab
        switch (position) {
            case 0: // All songs
                currentSongsLiveData = repository.getAllSongs();
                break;
            case 1: // Favorites
                currentSongsLiveData = repository.getFavoriteSongs();
                break;
            case 2: // Recent
                currentSongsLiveData = repository.getRecentSongs();
                break;
        }
        
        // Observe new data
        if (currentSongsLiveData != null) {
            currentSongsLiveData.observe(getViewLifecycleOwner(), songs -> {
                adapter.updateSongs(songs);
                updateEmptyView(songs.isEmpty());
            });
        }
    }
    
    /**
     * Refresh the music library by scanning storage.
     */
    public void refreshLibrary() {
        if (!PermissionHelper.hasStoragePermission(requireContext())) {
            Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }
        
        scanner.setScanListener(new LibraryScanner.ScanListener() {
            @Override
            public void onScanStarted() {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), R.string.scanning_library, Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onScanProgress(int current, int total) {
                // Update progress if needed
            }
            
            @Override
            public void onScanCompleted(int songsFound) {
                requireActivity().runOnUiThread(() -> {
                    swipeRefresh.setRefreshing(false);
                    String message = songsFound + " songs found";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onScanError(String error) {
                requireActivity().runOnUiThread(() -> {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        scanner.startScan();
    }
    
    /**
     * Handle song item click.
     */
    private void onSongClick(Song song, int position) {
        // Start player activity
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("song_id", song.getId());
        startActivity(intent);
    }
    
    /**
     * Handle favorite button click.
     */
    private void onFavoriteClick(Song song) {
        boolean newState = !song.isFavorite();
        repository.updateFavoriteStatus(song.getId(), newState);
        song.setFavorite(newState);
        adapter.notifyDataSetChanged();
    }
    
    /**
     * Perform search on songs.
     */
    public void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadSongsForTab(tabLayout.getSelectedTabPosition());
        } else {
            if (currentSongsLiveData != null) {
                currentSongsLiveData.removeObservers(getViewLifecycleOwner());
            }
            
            currentSongsLiveData = repository.searchSongs(query);
            currentSongsLiveData.observe(getViewLifecycleOwner(), songs -> {
                adapter.updateSongs(songs);
                updateEmptyView(songs.isEmpty());
            });
        }
    }
    
    /**
     * Update empty view visibility.
     */
    private void updateEmptyView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.shutdown();
        }
    }
}
