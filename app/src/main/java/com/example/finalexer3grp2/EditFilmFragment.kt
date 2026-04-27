package com.example.finalexer3grp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var filmId: Int = -1
    private var currentFilm: Film? = null
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = requireContext().contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedImageUri = it
            binding.ivEditPoster.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filmId = arguments?.getInt("filmId") ?: -1

        if (filmId != -1) {
            loadFilmData()
        }

        binding.toolbarEditFilm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvEditPoster.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnUpdateFilm.setOnClickListener {
            updateFilmInDatabase()
        }
    }

    private fun loadFilmData() {
        lifecycleScope.launch {
            val database = FilmDatabase.getDatabase(requireContext())
            currentFilm = database.filmDao().getFilmById(filmId)

            currentFilm?.let { film ->
                // Pre-fill fields
                binding.etEditTitle.setText(film.title)
                binding.etEditYear.setText(film.year)
                binding.etEditDirector.setText(film.director)
                binding.etEditDescription.setText(film.description)

                // Load Poster
                if (!film.posterUri.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(film.posterUri)
                    binding.ivEditPoster.load(selectedImageUri)
                }

                // Pre-select Chips
                val genres = film.genres.split(", ")
                for (i in 0 until binding.cgEditGenres.childCount) {
                    val chip = binding.cgEditGenres.getChildAt(i) as? Chip
                    if (chip != null && genres.contains(chip.text.toString())) {
                        chip.isChecked = true
                    }
                }
            }
        }
    }

    private fun updateFilmInDatabase() {
        val title = binding.etEditTitle.text.toString()
        val year = binding.etEditYear.text.toString()
        val director = binding.etEditDirector.text.toString()
        val description = binding.etEditDescription.text.toString()

        val selectedGenres = mutableListOf<String>()
        for (i in 0 until binding.cgEditGenres.childCount) {
            val chip = binding.cgEditGenres.getChildAt(i) as? Chip
            if (chip != null && chip.isChecked) {
                selectedGenres.add(chip.text.toString())
            }
        }

        if (title.isEmpty() || year.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter title and year", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedFilm = currentFilm?.copy(
            title = title,
            year = year,
            director = director,
            description = description,
            genres = if (selectedGenres.isNotEmpty()) selectedGenres.joinToString(", ") else "Uncategorized",
            posterUri = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            updatedFilm?.let {
                val database = FilmDatabase.getDatabase(requireContext())
                database.filmDao().updateFilm(it)
                Toast.makeText(requireContext(), "Film Updated!", Toast.LENGTH_SHORT).show()
                
                // Return to MainFragment (MainActivity's start destination)
                findNavController().popBackStack(R.id.mainFragment, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
