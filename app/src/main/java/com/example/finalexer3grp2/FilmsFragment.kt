package com.example.finalexer3grp2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        adapter = FilmAdapter(emptyList()) { film ->
            // This is the callback triggered by the long-press "Edit" option
            // When we create the EditFilmFragment, we will navigate here
            android.widget.Toast.makeText(requireContext(), "Edit: ${film.title}", android.widget.Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fab_add_film).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFilmFragment)
        }

        // Observe the database
        val database = FilmDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            database.filmDao().getAllFilms().collect { films ->
                adapter.updateData(films)
            }
        }
    }
}
