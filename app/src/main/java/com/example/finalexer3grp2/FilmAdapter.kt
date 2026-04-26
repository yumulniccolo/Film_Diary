package com.example.finalexer3grp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class FilmAdapter(private var films: List<Film>) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val info: TextView = view.findViewById(R.id.itemYear)
        val poster: ImageView = view.findViewById(R.id.itemPoster)
        // Add other views here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        holder.title.text = film.title
        holder.info.text = "${film.year} • Dir. ${film.director}"
        
        holder.poster.load(film.posterUri) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
    }

    override fun getItemCount() = films.size

    fun updateData(newFilms: List<Film>) {
        this.films = newFilms
        notifyDataSetChanged()
    }
}