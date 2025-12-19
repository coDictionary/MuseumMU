package com.projectpmob.museummu.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        // Set Data dummy/session (Pastikan di SessionManager ada fun getEmail dll)
        binding.tvName.text = sessionManager.prefs.getString(SessionManager.KEY_NAME, "User")
        binding.tvEmail.text = sessionManager.prefs.getString(SessionManager.KEY_EMAIL, "-")

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            // Arahkan kembali ke Login dan hapus backstack
            findNavController().navigate(R.id.loginFragment)
        }
    }
}