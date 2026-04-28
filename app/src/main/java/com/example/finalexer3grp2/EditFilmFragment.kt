package com.example.finalexer3grp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.finalexer3grp2.databinding.FragmentEditFilmBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class EditFilmFragment : Fragment() {

    private var _binding: FragmentEditFilmBinding? = null
    private val binding get() = _binding!!

    private var filmId = -1
    private var currentFilm: Film? = null
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.ivEditPoster.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        filmId = arguments?.getInt("filmId") ?: -1

        if (filmId != -1) loadFilm()

        binding.toolbarEditFilm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvEditPoster.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnUpdateFilm.setOnClickListener {
            updateFilm()
        }
    }

    private fun loadFilm() {
        lifecycleScope.launch {
            val db = FilmDatabase.getDatabase(requireContext())
            currentFilm = db.filmDao().getFilmById(filmId)

            currentFilm?.let { film ->
                binding.etEditTitle.setText(film.title)
                binding.etEditYear.setText(film.year)
                binding.etEditDirector.setText(film.director)
                binding.etEditDescription.setText(film.description)

                selectedImageUri = film.posterUri?.let { Uri.parse(it) }
                binding.ivEditPoster.load(selectedImageUri)

                val genres = film.genres.split(", ")
                for (i in 0 until binding.cgEditGenres.childCount) {
                    val chip = binding.cgEditGenres.getChildAt(i) as Chip
                    chip.isChecked = genres.contains(chip.text.toString())
                }
            }
        }
    }

    private fun updateFilm() {
        val title = binding.etEditTitle.text.toString()
        val year = binding.etEditYear.text.toString()

        if (title.isBlank() || year.isBlank()) {
            Toast.makeText(requireContext(), "Required fields missing", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenres = mutableListOf<String>()
        for (i in 0 until binding.cgEditGenres.childCount) {
            val chip = binding.cgEditGenres.getChildAt(i) as Chip
            if (chip.isChecked) selectedGenres.add(chip.text.toString())
        }

        val updated = currentFilm?.copy(
            title = title,
            year = year,
            director = binding.etEditDirector.text.toString(),
            description = binding.etEditDescription.text.toString(),
            genres = if (selectedGenres.isEmpty()) "Uncategorized"
            else selectedGenres.joinToString(", "),
            posterUri = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            updated?.let {
                FilmDatabase.getDatabase(requireContext())
                    .filmDao()
                    .updateFilm(it)

                Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}