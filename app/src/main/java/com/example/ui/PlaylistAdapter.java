package com.example.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.data.entity.Playlist;

import java.util.List;

/**
 * RecyclerView adapter for displaying playlists.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    
    private List<Playlist> playlists;
    private final OnPlaylistClickListener clickListener;
    private final OnPlaylistMenuClickListener menuClickListener;
    
    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }
    
    public interface OnPlaylistMenuClickListener {
        void onPlaylistMenuClick(Playlist playlist, View anchor);
    }
    
    public PlaylistAdapter(List<Playlist> playlists, 
                          OnPlaylistClickListener clickListener,
                          OnPlaylistMenuClickListener menuClickListener) {
        this.playlists = playlists;
        this.clickListener = clickListener;
        this.menuClickListener = menuClickListener;
    }
    
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }
    
    @Override
    public int getItemCount() {
        return playlists.size();
    }
    
    /**
     * Update the playlist list.
     */
    public void updatePlaylists(List<Playlist> newPlaylists) {
        this.playlists = newPlaylists;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for playlist items.
     */
    class PlaylistViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView iconImage;
        private final TextView nameText;
        private final TextView songCountText;
        private final ImageButton menuButton;
        
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.playlist_icon);
            nameText = itemView.findViewById(R.id.playlist_name);
            songCountText = itemView.findViewById(R.id.playlist_song_count);
            menuButton = itemView.findViewById(R.id.playlist_menu);
        }
        
        public void bind(Playlist playlist) {
            // Set playlist name
            nameText.setText(playlist.getName());
            
            // Set song count - for now show 0, will be updated later
            // In a real app, you'd query the database for the song count
            songCountText.setText(itemView.getContext().getString(R.string.songs_count, 0));
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onPlaylistClick(playlist);
                }
            });
            
            // Set menu button listener
            menuButton.setOnClickListener(v -> {
                if (menuClickListener != null) {
                    menuClickListener.onPlaylistMenuClick(playlist, v);
                }
            });
        }
    }
}
