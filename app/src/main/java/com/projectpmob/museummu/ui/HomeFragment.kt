package com.projectpmob.museummu.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        // Ambil username dari session
        val username = sessionManager.getUsername() ?: "Pengunjung"
        binding.tvWelcome.text = "Hai, $username"
    }
}