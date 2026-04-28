package com.example.finalexer3grp2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FilmsFragment : Fragment() {

    private lateinit var adapter: FilmAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_films, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFilms)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_film)
        val fabDelete = view.findViewById<FloatingActionButton>(R.id.fab_delete_film)

        adapter = FilmAdapter(
            films = emptyList(),
            onEditClick = { film ->
                val bundle = Bundle().apply { putInt("filmId", film.id) }
                findNavController().navigate(R.id.editFilmFragment, bundle)
            },
            onSelectionChanged = { count ->
                if (count > 0) {
                    fabAdd.visibility = View.GONE
                    fabDelete.visibility = View.VISIBLE
                } else {
                    fabAdd.visibility = View.VISIBLE
                    fabDelete.visibility = View.GONE
                }
            }
        )
        recyclerView.adapter = adapter

        // --- Existing add button ---
        fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFilmFragment)
        }

        // --- New delete button ---
        fabDelete.setOnClickListener {
            val ids = adapter.selectedIds.toList()
            lifecycleScope.launch {
                FilmDatabase.getDatabase(requireContext())
                    .filmDao()
                    .deleteByIds(ids)
                adapter.clearSelection()
            }
        }

        // Back press exits selection mode instead of navigating away
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (adapter.isSelectionMode) {
                adapter.clearSelection()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // --- Existing DB observer ---
        val database = FilmDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            database.filmDao().getAllFilms().collect { films ->
                adapter.updateData(films)
            }
        }
    }
}