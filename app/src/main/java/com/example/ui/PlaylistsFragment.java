package com.example.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.data.entity.Playlist;
import com.example.data.repository.MusicRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying user playlists.
 * Shows list of playlists and allows creation, editing, and deletion.
 */
public class PlaylistsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private View emptyView;
    private FloatingActionButton fabCreatePlaylist;
    
    private PlaylistAdapter adapter;
    private MusicRepository repository;
    private LiveData<List<Playlist>> playlistsLiveData;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.playlists_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        fabCreatePlaylist = view.findViewById(R.id.fab_create_playlist);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaylistAdapter(
            new ArrayList<>(),
            this::onPlaylistClick,
            this::onPlaylistMenuClick
        );
        recyclerView.setAdapter(adapter);
        
        // Initialize repository
        repository = new MusicRepository(requireContext());
        
        // Setup FAB
        fabCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
        
        // Load playlists
        loadPlaylists();
        
        return view;
    }
    
    /**
     * Load playlists from database.
     */
    private void loadPlaylists() {
        playlistsLiveData = repository.getAllPlaylists();
        playlistsLiveData.observe(getViewLifecycleOwner(), playlists -> {
            adapter.updatePlaylists(playlists);
            updateEmptyView(playlists.isEmpty());
        });
    }
    
    /**
     * Handle playlist item click.
     */
    private void onPlaylistClick(Playlist playlist) {
        // TODO: Navigate to playlist detail screen showing songs in the playlist
        Toast.makeText(getContext(), "Abrir: " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Handle playlist menu click.
     */
    private void onPlaylistMenuClick(Playlist playlist, View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.inflate(R.menu.playlist_menu);
        
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_rename_playlist) {
                showRenamePlaylistDialog(playlist);
                return true;
            } else if (itemId == R.id.action_delete_playlist) {
                showDeletePlaylistDialog(playlist);
                return true;
            }
            return false;
        });
        
        popup.show();
    }
    
    /**
     * Show dialog to create a new playlist.
     */
    private void showCreatePlaylistDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.enter_playlist_name);
        
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_playlist)
            .setView(input)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    createPlaylist(name);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
    
    /**
     * Show dialog to rename a playlist.
     */
    private void showRenamePlaylistDialog(Playlist playlist) {
        EditText input = new EditText(requireContext());
        input.setText(playlist.getName());
        input.setHint(R.string.enter_playlist_name);
        
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.rename_playlist)
            .setView(input)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    renamePlaylist(playlist, name);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
    
    /**
     * Show dialog to confirm playlist deletion.
     */
    private void showDeletePlaylistDialog(Playlist playlist) {
        String message = getString(R.string.delete_playlist_confirm, playlist.getName());
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(message)
            .setPositiveButton(R.string.ok, (dialog, which) -> deletePlaylist(playlist))
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
    
    /**
     * Create a new playlist.
     */
    private void createPlaylist(String name) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDateCreated(System.currentTimeMillis());
        
        repository.insertPlaylist(playlist);
        Toast.makeText(getContext(), R.string.playlist_created, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Rename an existing playlist.
     */
    private void renamePlaylist(Playlist playlist, String newName) {
        playlist.setName(newName);
        repository.updatePlaylist(playlist);
        Toast.makeText(getContext(), R.string.playlist_renamed, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Delete a playlist.
     */
    private void deletePlaylist(Playlist playlist) {
        repository.deletePlaylist(playlist);
        Toast.makeText(getContext(), R.string.playlist_deleted, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Update empty view visibility.
     */
    private void updateEmptyView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
