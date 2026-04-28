package com.example.finalexer3grp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalexer3grp2.databinding.FragmentMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getViewLifecycleOwner().getLifecycle());
        binding.ViewPager2.setAdapter(adapter);

        // Set up search trigger
        binding.searchTriggerCard.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.searchFragment);
        });

        new TabLayoutMediator(binding.tabLayout, binding.ViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Films");
                    break;
                case 1:
                    tab.setText("Diary");
                    break;
                case 2:
                    tab.setText("Lists");
                    break;
            }
        }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
