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
    private val onEditClick: (Film) -> Unit,
    private val onSelectionChanged: (Int) -> Unit = {}
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    val selectedIds = mutableSetOf<Int>()
    var isSelectionMode = false

    fun clearSelection() {
        selectedIds.clear()
        isSelectionMode = false
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val info: TextView = view.findViewById(R.id.itemYear)
        val poster: ImageView = view.findViewById(R.id.itemPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        val isSelected = selectedIds.contains(film.id)

        holder.title.text = film.title
        holder.info.text = "${film.year} • Dir. ${film.director}"
        holder.poster.load(film.posterUri) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }

        // Visual feedback
        holder.itemView.alpha = if (isSelectionMode && !isSelected) 0.5f else 1.0f
        holder.itemView.isActivated = isSelected // use activated state in your item bg selector

        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                // Enter selection mode
                isSelectionMode = true
                selectedIds.add(film.id)
                notifyDataSetChanged()
                onSelectionChanged(selectedIds.size)
            } else {
                // Already in selection mode — show popup like before
                val popup = android.widget.PopupMenu(holder.itemView.context, holder.itemView)
                popup.menu.add("Edit")
                popup.setOnMenuItemClickListener { item ->
                    if (item.title == "Edit") onEditClick(film)
                    true
                }
                popup.show()
            }
            true
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                if (selectedIds.contains(film.id)) {
                    selectedIds.remove(film.id)
                } else {
                    selectedIds.add(film.id)
                }
                if (selectedIds.isEmpty()) {
                    isSelectionMode = false
                }
                notifyItemChanged(position)
                onSelectionChanged(selectedIds.size)
            }
            // Normal click does nothing here since edit is via long press popup
        }
    }

    override fun getItemCount() = films.size

    fun updateData(newFilms: List<Film>) {
        this.films = newFilms
        notifyDataSetChanged()
    }

    fun toggleSelection(filmId: Int) {
        if (selectedIds.contains(filmId)) {
            selectedIds.remove(filmId)
        } else {
            selectedIds.add(filmId)
        }
        if (selectedIds.isEmpty()) isSelectionMode = false
        onSelectionChanged(selectedIds.size)
    }

    fun getFilmAt(position: Int): Film? {
        return films.getOrNull(position)
    }
}