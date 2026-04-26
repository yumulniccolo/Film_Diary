package com.example.finalexer3grp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalexer3grp2.databinding.FragmentAddFilmBinding
import kotlinx.coroutines.launch

class AddFilmFragment : Fragment() {

    private var _binding: FragmentAddFilmBinding? = null
    private val binding get() = _binding!!
    
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
            binding.ivPosterPicker.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.toolbarAddFilm.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.cvPosterPicker.setOnClickListener {
            pickImage.launch("image/*")
        }
        
        binding.btnSaveFilm.setOnClickListener {
            saveFilmToDatabase()
        }
    }

    private fun saveFilmToDatabase() {
        val title = binding.etTitle.text.toString()
        val year = binding.etYear.text.toString()
        val director = binding.etDirector.text.toString()
        val description = binding.etDescription.text.toString()
        
        val selectedGenres = binding.cgGenres.checkedChipIds.map { id ->
            binding.root.findViewById<com.google.android.material.chip.Chip>(id)?.text.toString()
        }.joinToString(", ")

        if (title.isEmpty() || year.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter title and year", Toast.LENGTH_SHORT).show()
            return
        }

        val film = Film(
            title = title,
            year = year,
            director = director,
            description = description,
            genres = if (selectedGenres.isNotEmpty()) selectedGenres else "Uncategorized",
            posterUri = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            val database = FilmDatabase.getDatabase(requireContext())
            database.filmDao().insertFilm(film)

            Toast.makeText(requireContext(), "Film Saved!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
