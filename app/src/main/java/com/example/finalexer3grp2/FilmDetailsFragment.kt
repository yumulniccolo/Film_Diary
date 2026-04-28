package com.example.finalexer3grp2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.finalexer3grp2.databinding.FragmentFilmDetailsBinding
import kotlinx.coroutines.launch

class FilmDetailsFragment : Fragment(R.layout.fragment_film_details) {

    private var _binding: FragmentFilmDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = FragmentFilmDetailsBinding.bind(view)

        val filmId = arguments?.getInt("filmId") ?: return

        val db = FilmDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val film = db.filmDao().getFilmById(filmId)

            film?.let {
                binding.ivPoster.load(it.posterUri)
                binding.tvTitle.text = it.title
                binding.tvYear.text = it.year
                binding.tvDirector.text = it.director
                binding.tvGenres.text = it.genres
                binding.tvDescription.text = it.description
            }
        }

        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // edit button
        binding.fabEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("filmId", filmId)
            }
            findNavController().navigate(R.id.editFilmFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}