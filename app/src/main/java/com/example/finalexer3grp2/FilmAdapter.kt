package com.example.finalexer3grp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class FilmAdapter(
    private var films: List<Film>,
    private val onEditClick: (Film) -> Unit // New callback for editing
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val info: TextView = view.findViewById(R.id.itemYear)
        val poster: ImageView = view.findViewById(R.id.itemPoster)

        fun bind(film: Film, onEditClick: (Film) -> Unit) {
            title.text = film.title
            info.text = "${film.year} • Dir. ${film.director}"
            
            poster.load(film.posterUri) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }

            // Set up Long Click Listener for Context Menu
            itemView.setOnLongClickListener {
                val popup = android.widget.PopupMenu(itemView.context, itemView)
                popup.menu.add("Edit")
                popup.setOnMenuItemClickListener { item ->
                    if (item.title == "Edit") {
                        onEditClick(film)
                    }
                    true
                }
                popup.show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(films[position], onEditClick)
    }

    override fun getItemCount() = films.size

    fun updateData(newFilms: List<Film>) {
        this.films = newFilms
        notifyDataSetChanged()
    }
}