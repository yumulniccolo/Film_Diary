package com.example.finalexer3grp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finalexer3grp2.databinding.FragmentFilmDetailsBinding;

import coil.Coil;
import coil.request.ImageRequest;

public class FilmDetailsFragment extends Fragment {

    private FragmentFilmDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilmDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;
        int filmId = getArguments().getInt("filmId", -1);
        if (filmId == -1) return;

        binding.toolbarDetail.setNavigationOnClickListener(v -> 
            Navigation.findNavController(view).popBackStack());

        binding.fabEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("filmId", filmId);
            Navigation.findNavController(view).navigate(R.id.editFilmFragment, bundle);
        });

        loadFilm(filmId);
    }

    private void loadFilm(int id) {
        FilmDatabase.databaseWriteExecutor.execute(() -> {
            FilmDatabase db = FilmDatabase.getDatabase(requireContext());
            Film film = db.filmDao().getFilmById(id);

            if (film != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ImageRequest request = new ImageRequest.Builder(requireContext())
                            .data(film.getPosterUri())
                            .target(binding.ivPoster)
                            .build();
                    Coil.imageLoader(requireContext()).enqueue(request);

                    binding.tvTitle.setText(film.getTitle());
                    binding.tvYear.setText(film.getYear());
                    binding.tvDirector.setText(film.getDirector());
                    binding.tvGenres.setText(film.getGenres());
                    binding.tvDescription.setText(film.getDescription());
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
