package com.example.finalexer3grp2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import coil.Coil;
import coil.request.ImageRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private List<Film> films;
    private final OnItemClickListener onItemClickListener;
    private final OnSelectionChangedListener onSelectionChangedListener;

    private final Set<Integer> selectedIds = new HashSet<>();
    private boolean isSelectionMode = false;

    public interface OnItemClickListener {
        void onItemClick(Film film);
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public FilmAdapter(List<Film> films, OnItemClickListener onItemClickListener, OnSelectionChangedListener onSelectionChangedListener) {
        this.films = films;
        this.onItemClickListener = onItemClickListener;
        this.onSelectionChangedListener = onSelectionChangedListener;
    }

    public void clearSelection() {
        selectedIds.clear();
        isSelectionMode = false;
        notifyDataSetChanged();
        onSelectionChangedListener.onSelectionChanged(0);
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    public Film getFilmAt(int position) {
        if (position >= 0 && position < films.size()) {
            return films.get(position);
        }
        return null;
    }

    public void toggleSelection(int filmId) {
        if (selectedIds.contains(filmId)) {
            selectedIds.remove(filmId);
        } else {
            selectedIds.add(filmId);
        }

        if (selectedIds.isEmpty()) {
            isSelectionMode = false;
        }

        onSelectionChangedListener.onSelectionChanged(selectedIds.size());
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView info;
        final ImageView poster;

        public FilmViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.itemTitle);
            info = view.findViewById(R.id.itemYear);
            poster = view.findViewById(R.id.itemPoster);
        }
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {

        Film film = films.get(position);
        boolean isSelected = selectedIds.contains(film.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());

        String updatedDate = sdf.format(new Date(film.getUpdatedAt()));

        holder.title.setText(film.getTitle());
        holder.info.setText(film.getYear() + " • " + film.getDirector() + "\nUpdated: " + updatedDate);

        // Coil Java usage
        ImageRequest request = new ImageRequest.Builder(holder.poster.getContext())
                .data(film.getPosterUri())
                .crossfade(true)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .target(holder.poster)
                .build();
        Coil.imageLoader(holder.poster.getContext()).enqueue(request);

        // color when selected
        if (isSelected) {
            holder.itemView.setForeground(new ColorDrawable(Color.parseColor("#803F51B5")));
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setForeground(null);
            holder.itemView.setAlpha(isSelectionMode ? 0.6f : 1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleSelection(film.getId());
                notifyItemChanged(position);
            } else {
                onItemClickListener.onItemClick(film);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                selectedIds.add(film.getId());
                notifyItemChanged(position);
                onSelectionChangedListener.onSelectionChanged(selectedIds.size());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public void updateData(List<Film> newFilms) {
        this.films = newFilms;
        notifyDataSetChanged();
    }
}
