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
import com.example.finalexer3grp2.databinding.FragmentAddFilmBinding
import kotlinx.coroutines.launch

class AddFilmFragment : Fragment() {

    private var _binding: FragmentAddFilmBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}

                selectedImageUri = it
                binding.ivPosterPicker.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.toolbarAddFilm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvPosterPicker.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveFilm.setOnClickListener {
            saveFilm()
        }
    }

    private fun saveFilm() {
        val title = binding.etTitle.text.toString()
        val year = binding.etYear.text.toString()
        val director = binding.etDirector.text.toString()
        val description = binding.etDescription.text.toString()

        if (title.isBlank() || year.isBlank()) {
            Toast.makeText(requireContext(), "Title and Year required", Toast.LENGTH_SHORT).show()
            return
        }

        val genres = binding.cgGenres.checkedChipIds.map {
            binding.root.findViewById<com.google.android.material.chip.Chip>(it).text.toString()
        }.joinToString(", ")

        val film = Film(
            title = title,
            year = year,
            director = director,
            description = description,
            genres = if (genres.isBlank()) "Uncategorized" else genres,
            posterUri = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            FilmDatabase.getDatabase(requireContext())
                .filmDao()
                .insertFilm(film)

            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}