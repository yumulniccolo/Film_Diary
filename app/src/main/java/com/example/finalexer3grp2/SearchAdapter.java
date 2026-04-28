package com.example.finalexer3grp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FILM = 0;
    private static final int TYPE_DIRECTOR = 1;

    public interface OnItemClickListener {
        void onItemClick(Film film);
    }

    private OnItemClickListener listener;
    private List<Film> items = new ArrayList<>();
    private int currentType = TYPE_FILM;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Film> newItems, int type) {
        this.items = newItems;
        this.currentType = type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return currentType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FILM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_film, parent, false);
            return new FilmViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_director, parent, false);
            return new DirectorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Film film = items.get(position);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(film);
            }
        });

        if (holder instanceof FilmViewHolder) {
            FilmViewHolder fvh = (FilmViewHolder) holder;
            fvh.title.setText(film.getTitle());
            fvh.info.setText(film.getYear() + ", directed by " + film.getDirector());
            
            ImageRequest request = new ImageRequest.Builder(fvh.poster.getContext())
                    .data(film.getPosterUri())
                    .target(fvh.poster)
                    .build();
            Coil.imageLoader(fvh.poster.getContext()).enqueue(request);
            
        } else if (holder instanceof DirectorViewHolder) {
            DirectorViewHolder dvh = (DirectorViewHolder) holder;
            dvh.name.setText(film.getDirector());
            dvh.subtitle.setText("Directed the film: " + film.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, info;

        FilmViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.ivSearchPoster);
            title = itemView.findViewById(R.id.tvSearchTitle);
            info = itemView.findViewById(R.id.tvSearchInfo);
        }
    }

    static class DirectorViewHolder extends RecyclerView.ViewHolder {
        TextView name, subtitle;

        DirectorViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvDirectorName);
            subtitle = itemView.findViewById(R.id.tvDirectorSubtitle);
        }
    }
}
