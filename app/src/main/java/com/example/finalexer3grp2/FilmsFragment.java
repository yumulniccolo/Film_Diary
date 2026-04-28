package com.example.finalexer3grp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FilmsFragment extends Fragment {

    private FilmAdapter adapter;

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_films, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFilms);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_film);
        FloatingActionButton fabDelete = view.findViewById(R.id.fab_delete_film);
        TextView selectedCountText = view.findViewById(R.id.tvSelectedCount);

        adapter = new FilmAdapter(
                new ArrayList<>(),
                film -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("filmId", film.getId());
                    Navigation.findNavController(view).navigate(R.id.filmDetailsFragment, bundle);
                },
                count -> {
                    if (count > 0) {
                        fabAdd.setVisibility(View.GONE);
                        fabDelete.setVisibility(View.VISIBLE);
                        selectedCountText.setVisibility(View.VISIBLE);
                        selectedCountText.setText(count + " selected");
                    } else {
                        fabAdd.setVisibility(View.VISIBLE);
                        fabDelete.setVisibility(View.GONE);
                        selectedCountText.setVisibility(View.GONE);
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.addFilmFragment));

        fabDelete.setOnClickListener(v -> {
            List<Integer> ids = new ArrayList<>(adapter.getSelectedIds());
            if (ids.isEmpty()) return;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Films")
                    .setMessage("Are you sure you want to delete " + ids.size() + " selected film(s)? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FilmDatabase.databaseWriteExecutor.execute(() -> {
                            FilmDatabase db = FilmDatabase.getDatabase(requireContext());
                            db.filmDao().deleteByIds(ids);
                            
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    adapter.clearSelection();
                                });
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // back press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (adapter.isSelectionMode()) {
                    adapter.clearSelection();
                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        requireActivity().addMenuProvider(new androidx.core.view.MenuProvider() {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_sort, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {

                FilmDao dao = FilmDatabase.getDatabase(requireContext()).filmDao();

                if (item.getItemId() == R.id.sort_az) {
                    dao.getFilmsAZ().observe(getViewLifecycleOwner(), films -> adapter.updateData(films));
                    return true;

                } else if (item.getItemId() == R.id.sort_recent) {
                    dao.getRecentlyModified().observe(getViewLifecycleOwner(), films -> adapter.updateData(films));
                    return true;

                } else if (item.getItemId() == R.id.sort_latest) {
                    dao.getLatestToOldest().observe(getViewLifecycleOwner(), films -> adapter.updateData(films));
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner());

        FilmDatabase.getDatabase(requireContext()).filmDao().getAllFilms().observe(getViewLifecycleOwner(), films -> {
            adapter.updateData(films);
        });
    }
}
