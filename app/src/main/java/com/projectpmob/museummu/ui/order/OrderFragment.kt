package com.projectpmob.museummu.ui.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.projectpmob.museummu.R
import com.projectpmob.museummu.databinding.FragmentOrderBinding

class OrderFragment : Fragment(R.layout.fragment_order) {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderBinding.bind(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        // 1. Pasang Adapter ke ViewPager
        val adapter = OrderPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // 2. Hubungkan TabLayout dengan ViewPager menggunakan Mediator
        // Disini kita memberi judul pada masing-masing Tab
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Individu"
                1 -> tab.text = "Rombongan (Grup)"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}