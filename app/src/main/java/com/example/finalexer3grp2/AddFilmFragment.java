package com.example.finalexer3grp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finalexer3grp2.databinding.FragmentAddFilmBinding;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.stream.Collectors;

public class AddFilmFragment extends Fragment {

    private FragmentAddFilmBinding binding;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                    selectedImageUri = uri;
                    binding.ivPosterPicker.setImageURI(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddFilmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarAddFilm.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        binding.cvPosterPicker.setOnClickListener(v -> pickImage.launch("image/*"));

        binding.btnSaveFilm.setOnClickListener(v -> saveFilm());
    }

    private void saveFilm() {
        String title = binding.etTitle.getText().toString();
        String year = binding.etYear.getText().toString();
        String director = binding.etDirector.getText().toString();
        String description = binding.etDescription.getText().toString();

        if (title.trim().isEmpty() || year.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Title and Year required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> checkedIds = binding.cgGenres.getCheckedChipIds();
        String genres = checkedIds.stream()
                .map(id -> ((Chip) binding.cgGenres.findViewById(id)).getText().toString())
                .collect(Collectors.joining(", "));

        Film film = new Film();
        film.setTitle(title);
        film.setYear(year);
        film.setDirector(director);
        film.setDescription(description);
        film.setGenres(genres.isEmpty() ? "Uncategorized" : genres);
        film.setPosterUri(selectedImageUri != null ? selectedImageUri.toString() : null);

        long currentTime = System.currentTimeMillis();
        film.setCreatedAt(currentTime);
        film.setUpdatedAt(currentTime);

        FilmDatabase.databaseWriteExecutor.execute(() -> {
            FilmDatabase.getDatabase(requireContext()).filmDao().insertFilm(film);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
