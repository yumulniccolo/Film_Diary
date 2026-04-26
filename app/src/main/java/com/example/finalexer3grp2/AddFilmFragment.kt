package com.example.finalexer3grp2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalexer3grp2.databinding.FragmentAddFilmBinding

class AddFilmFragment : Fragment() {

    private var _binding: FragmentAddFilmBinding? = null
    private val binding get() = _binding!!

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
        
        binding.btnSaveFilm.setOnClickListener {
            saveFilm()
        }
    }

    private fun saveFilm() {
        // TODO: Implement database saving logic
        val title = binding.etMovieTitle.text.toString()
        val year = binding.etYear.text.toString()
        val director = binding.etDirector.text.toString()
        val description = binding.etDescription.text.toString()
        
        // Basic validation
        if (title.isBlank()) {
            binding.tilMovieTitle.error = "Title is required"
            return
        }
        binding.tilMovieTitle.error = null
        
        // After saving, navigate back or show success
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}