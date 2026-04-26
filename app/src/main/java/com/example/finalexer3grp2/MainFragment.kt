package com.example.finalexer3grp2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.finalexer3grp2.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.ViewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.ViewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Films"
                1 -> "Diary"
                2 -> "Lists"
                else -> null
            }
        }.attach()

        val navController = findNavController()
        binding.bottomNavigation.setupWithNavController(navController)
    }
}