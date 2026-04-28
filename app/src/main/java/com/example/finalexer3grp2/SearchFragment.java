package com.example.finalexer3grp2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalexer3grp2.databinding.FragmentSearchBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchAdapter adapter;
    private FilmDao filmDao;
    private int currentSearchType = 0; // 0 for Films, 1 for Directors
    private LiveData<List<Film>> currentSearchResults;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filmDao = FilmDatabase.getDatabase(requireContext()).filmDao();
        adapter = new SearchAdapter();
        
        adapter.setOnItemClickListener(film -> {
            Bundle bundle = new Bundle();
            bundle.putInt("filmId", film.getId());
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.filmDetailsFragment, bundle);
        });

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchResults.setAdapter(adapter);

        // Restore tab selection
        TabLayout.Tab savedTab = binding.searchTabLayout.getTabAt(currentSearchType);
        if (savedTab != null) {
            savedTab.select();
        }

        // Back button
        binding.ivBackSearch.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Clear button
        binding.ivClearSearch.setOnClickListener(v -> binding.etSearchQuery.setText(""));

        // Tab selection listener
        binding.searchTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentSearchType = tab.getPosition();
                performSearch(binding.etSearchQuery.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Search text listener
        binding.etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Automatically show keyboard
        binding.etSearchQuery.requestFocus();
        binding.etSearchQuery.postDelayed(() -> {
            if (isAdded()) {
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) 
                        requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(binding.etSearchQuery, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 200);
    }

    private void performSearch(String query) {
        // Remove previous observers to avoid memory leaks or double triggers
        if (currentSearchResults != null) {
            currentSearchResults.removeObservers(getViewLifecycleOwner());
        }

        if (query.isEmpty()) {
            adapter.setData(new java.util.ArrayList<>(), currentSearchType);
            return;
        }

        if (currentSearchType == 0) {
            currentSearchResults = filmDao.searchFilms(query);
        } else {
            currentSearchResults = filmDao.searchByDirector(query);
        }

        currentSearchResults.observe(getViewLifecycleOwner(), films -> {
            adapter.setData(films, currentSearchType);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
