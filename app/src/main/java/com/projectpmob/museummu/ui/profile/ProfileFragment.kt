package com.projectpmob.museummu.ui.profile

import EditProfileViewModel
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentProfileBinding
import kotlin.getValue

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sessionManager: SessionManager

    private val viewModel: EditProfileViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        // Set Data dummy/session (Pastikan di SessionManager ada fun getEmail dll)
        val uid = auth.currentUser?.uid
        if (uid != null){
            viewModel.loadUser(uid)
        }

        observeData()

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            // Arahkan kembali ke Login dan hapus backstack
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun observeData() {
        // Observer untuk Load Data User
        viewModel.userState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    val user = result.data
                    binding.tvusername.text = user.username
                    binding.tvFullName.text = user.fullName
                    binding.tvEmail.text = user.email
                    binding.tvPhone.text = user.phone
                    binding.tvDisplayName.text = user.username
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observer untuk Proses Update
        viewModel.updateState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp() // Kembali ke halaman sebelumnya
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnLogout.isEnabled = !isLoading
    }
}