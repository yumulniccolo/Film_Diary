package com.example.finalexer3grp2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FilmsFragment();
            case 1:
                return new DiaryFragment();
            case 2:
                return new ListsFragment();
            default:
                return new FilmsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
