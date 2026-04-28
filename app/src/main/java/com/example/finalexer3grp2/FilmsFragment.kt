package com.example.finalexer3grp2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

        // --- Slide-through multi-select ---
        var isDragging = false
        var lastDraggedPosition = -1

        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (!adapter.isSelectionMode) return false

                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragging = true
                        lastDraggedPosition = -1
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            val child = rv.findChildViewUnder(e.x, e.y)
                            if (child != null) {
                                val pos = rv.getChildAdapterPosition(child)
                                if (pos != RecyclerView.NO_ID.toInt() && pos != lastDraggedPosition) {
                                    lastDraggedPosition = pos
                                    val film = adapter.getFilmAt(pos)
                                    film?.let {
                                        adapter.toggleSelection(it.id)
                                        adapter.notifyItemChanged(pos)
                                    }
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isDragging = false
                        lastDraggedPosition = -1
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        // --- Existing add button ---
        fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFilmFragment)
        }

        // --- New delete button with confirmation ---
        fabDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Films")
                .setMessage("Are you sure you want to delete ${adapter.selectedIds.size} selected film(s)?")
                .setPositiveButton("Delete") { _, _ ->
                    val ids = adapter.selectedIds.toList()
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