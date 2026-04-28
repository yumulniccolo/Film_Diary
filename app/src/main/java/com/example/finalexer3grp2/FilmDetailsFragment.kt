package com.example.finalexer3grp2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.finalexer3grp2.databinding.FragmentFilmDetailsBinding
import kotlinx.coroutines.launch

class FilmDetailsFragment : Fragment(R.layout.fragment_film_details) {

    private var _binding: FragmentFilmDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentFilmDetailsBinding.bind(view)

        val filmId = arguments?.getInt("filmId") ?: return

        binding.toolbarDetail.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        loadFilm(filmId)
    }

    private fun loadFilm(id: Int) {
        lifecycleScope.launch {
            val db = FilmDatabase.getDatabase(requireContext())
            val film = db.filmDao().getFilmById(id)

            if (film != null) {
                binding.ivPoster.load(film.posterUri)
                binding.tvTitle.text = film.title
                binding.tvYear.text = "Year: ${film.year}"
                binding.tvDirector.text = "Director: ${film.director}"
                binding.tvGenres.text = film.genres
                binding.tvDescription.text = film.description
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}