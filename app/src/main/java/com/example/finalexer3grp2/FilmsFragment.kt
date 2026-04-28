package com.example.finalexer3grp2

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FilmsFragment : Fragment() {

    private lateinit var adapter: FilmAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_films, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewFilms)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_film)
        val fabDelete = view.findViewById<FloatingActionButton>(R.id.fab_delete_film)
        val selectedCountText = view.findViewById<TextView>(R.id.tvSelectedCount)

        adapter = FilmAdapter(
            films = emptyList(),
            onItemClick = { film ->
                val bundle = Bundle().apply {
                    putInt("filmId", film.id)
                }
                findNavController().navigate(R.id.filmDetailsFragment, bundle)
            },
            onSelectionChanged = { count ->

                if (count > 0) {
                    fabAdd.visibility = View.GONE
                    fabDelete.visibility = View.VISIBLE
                    selectedCountText.visibility = View.VISIBLE
                    selectedCountText.text = "$count selected"
                } else {
                    fabAdd.visibility = View.VISIBLE
                    fabDelete.visibility = View.GONE
                    selectedCountText.visibility = View.GONE
                }
            }
        )

        rv.adapter = adapter

        fabAdd.setOnClickListener {
            findNavController().navigate(R.id.addFilmFragment)
        }

        fabDelete.setOnClickListener {

            val ids = adapter.selectedIds.toList()

            if (ids.isEmpty()) return@setOnClickListener

            AlertDialog.Builder(requireContext())
                .setTitle("Delete Films")
                .setMessage("Are you sure you want to delete ${ids.size} selected film(s)? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->

                    lifecycleScope.launch {
                        FilmDatabase.getDatabase(requireContext())
                            .filmDao()
                            .deleteByIds(ids)

                        adapter.clearSelection()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val db = FilmDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            db.filmDao().getAllFilms().collect {
                adapter.updateData(it)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (adapter.isSelectionMode) {
                adapter.clearSelection()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}