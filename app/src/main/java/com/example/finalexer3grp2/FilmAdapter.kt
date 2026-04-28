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
    private val onItemClick: (Film) -> Unit,
    private val onSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    val selectedIds = mutableSetOf<Int>()
    var isSelectionMode = false

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val info: TextView = view.findViewById(R.id.itemYear)
        val poster: ImageView = view.findViewById(R.id.itemPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {

        val film = films[position]
        val isSelected = selectedIds.contains(film.id)

        holder.title.text = film.title
        holder.info.text = "${film.year} • ${film.director}"

        holder.poster.load(film.posterUri)

        //color when selected
        if (isSelected) {

            holder.itemView.foreground = android.graphics.drawable.ColorDrawable(
                android.graphics.Color.parseColor("#803F51B5")
            )

            holder.itemView.alpha = 1f

        } else {

            holder.itemView.foreground = null

            holder.itemView.alpha = if (isSelectionMode) 0.6f else 1f
        }


        holder.itemView.setOnClickListener {

            if (isSelectionMode) {

                toggleSelection(film.id)
                notifyItemChanged(position)

            } else {
                onItemClick(film)
            }
        }

        //long press - start of selection
        holder.itemView.setOnLongClickListener {

            if (!isSelectionMode) {
                isSelectionMode = true
                selectedIds.add(film.id)
                notifyItemChanged(position)
                onSelectionChanged(selectedIds.size)
            }

            true
        }
    }

    override fun getItemCount() = films.size

    fun updateData(newFilms: List<Film>) {
        films = newFilms
        notifyDataSetChanged()
    }

    fun toggleSelection(id: Int) {
        if (selectedIds.contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)

        if (selectedIds.isEmpty()) isSelectionMode = false

        onSelectionChanged(selectedIds.size)
    }

    fun clearSelection() {
        selectedIds.clear()
        isSelectionMode = false
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    fun getFilmAt(position: Int) = films[position]
}