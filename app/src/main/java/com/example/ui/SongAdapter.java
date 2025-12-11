package com.example.reproductordemsica.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reproductordemsica.R;
import com.example.reproductordemsica.data.entity.Song;
import com.example.reproductordemsica.utils.TimeUtils;

import java.io.File;
import java.util.List;

/**
 * RecyclerView adapter for displaying songs in a list.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    
    private List<Song> songs;
    private final OnSongClickListener clickListener;
    private final OnFavoriteClickListener favoriteListener;
    
    public interface OnSongClickListener {
        void onSongClick(Song song, int position);
    }
    
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Song song);
    }
    
    public SongAdapter(List<Song> songs, OnSongClickListener clickListener,
                      OnFavoriteClickListener favoriteListener) {
        this.songs = songs;
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
    }
    
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position);
    }
    
    @Override
    public int getItemCount() {
        return songs.size();
    }
    
    /**
     * Update the song list.
     */
    public void updateSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for song items.
     */
    class SongViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView coverImage;
        private final TextView titleText;
        private final TextView artistText;
        private final TextView durationText;
        private final ImageButton favoriteButton;
        
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.song_cover);
            titleText = itemView.findViewById(R.id.song_title);
            artistText = itemView.findViewById(R.id.song_artist);
            durationText = itemView.findViewById(R.id.song_duration);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
        }
        
        public void bind(Song song, int position) {
            // Set text fields
            titleText.setText(song.getTitle());
            artistText.setText(song.getArtist());
            durationText.setText(TimeUtils.formatDuration(song.getDuration()));
            
            // Load album art
            if (song.getAlbumArtPath() != null && new File(song.getAlbumArtPath()).exists()) {
                Glide.with(itemView.getContext())
                        .load(song.getAlbumArtPath())
                        .placeholder(R.drawable.ic_album)
                        .error(R.drawable.ic_album)
                        .into(coverImage);
            } else {
                coverImage.setImageResource(R.drawable.ic_album);
            }
            
            // Set favorite button
            if (song.isFavorite()) {
                favoriteButton.setImageResource(R.drawable.ic_favorite);
            } else {
                favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            }
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onSongClick(song, position);
                }
            });
            
            favoriteButton.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(song);
                }
            });
        }
    }
}
