package com.projectpmob.museummu.ui.order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Jumlah Tab yang kita inginkan (Individu & Grup)
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                // Tab 0: Panggil Fragment Form dalam mode Individu (isGroup = false)
                OrderFormFragment.newInstance(isGroup = false)
            }
            1 -> {
                // Tab 1: Panggil Fragment Form dalam mode Grup (isGroup = true)
                OrderFormFragment.newInstance(isGroup = true)
            }
            else -> OrderFormFragment.newInstance(isGroup = false)
        }
    }
}