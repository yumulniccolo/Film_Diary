package com.example.finalexer3grp2;

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

import com.example.finalexer3grp2.databinding.FragmentEditFilmBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;

public class EditFilmFragment extends Fragment {

    private FragmentEditFilmBinding binding;
    private int filmId = -1;
    private Film currentFilm;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivEditPoster.setImageURI(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditFilmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            filmId = getArguments().getInt("filmId", -1);
        }

        if (filmId != -1) loadFilm();

        binding.toolbarEditFilm.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        binding.cvEditPoster.setOnClickListener(v -> pickImage.launch("image/*"));

        binding.btnUpdateFilm.setOnClickListener(v -> updateFilm());
    }

    private void loadFilm() {
        FilmDatabase.databaseWriteExecutor.execute(() -> {
            FilmDatabase db = FilmDatabase.getDatabase(requireContext());
            currentFilm = db.filmDao().getFilmById(filmId);

            if (currentFilm != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.etEditTitle.setText(currentFilm.getTitle());
                    binding.etEditYear.setText(currentFilm.getYear());
                    binding.etEditDirector.setText(currentFilm.getDirector());
                    binding.etEditDescription.setText(currentFilm.getDescription());

                    if (currentFilm.getPosterUri() != null) {
                        selectedImageUri = Uri.parse(currentFilm.getPosterUri());
                        ImageRequest request = new ImageRequest.Builder(requireContext())
                                .data(selectedImageUri)
                                .target(binding.ivEditPoster)
                                .build();
                        Coil.imageLoader(requireContext()).enqueue(request);
                    }

                    List<String> genres = Arrays.asList(currentFilm.getGenres().split(", "));
                    for (int i = 0; i < binding.cgEditGenres.getChildCount(); i++) {
                        Chip chip = (Chip) binding.cgEditGenres.getChildAt(i);
                        chip.setChecked(genres.contains(chip.getText().toString()));
                    }
                });
            }
        });
    }

    private void updateFilm() {
        String title = binding.etEditTitle.getText().toString();
        String year = binding.etEditYear.getText().toString();

        if (title.trim().isEmpty() || year.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Required fields missing", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < binding.cgEditGenres.getChildCount(); i++) {
            Chip chip = (Chip) binding.cgEditGenres.getChildAt(i);
            if (chip.isChecked()) selectedGenres.add(chip.getText().toString());
        }

        if (currentFilm != null) {
            currentFilm.setTitle(title);
            currentFilm.setYear(year);
            currentFilm.setDirector(binding.etEditDirector.getText().toString());
            currentFilm.setDescription(binding.etEditDescription.getText().toString());
            currentFilm.setGenres(selectedGenres.isEmpty() ? "Uncategorized" : String.join(", ", selectedGenres));
            currentFilm.setPosterUri(selectedImageUri != null ? selectedImageUri.toString() : null);

            currentFilm.setUpdatedAt(System.currentTimeMillis());

            FilmDatabase.databaseWriteExecutor.execute(() -> {
                FilmDatabase.getDatabase(requireContext()).filmDao().updateFilm(currentFilm);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
