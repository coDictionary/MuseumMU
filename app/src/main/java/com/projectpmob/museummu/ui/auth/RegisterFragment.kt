package com.projectpmob.museummu.ui.auth


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.projectpmob.museummu.R
import com.projectpmob.museummu.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        setupObserver()
        setupListeners()
    }

    private fun setupListeners() {
        // Aksi Tombol Register
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            // Validasi: Pastikan semua kolom terisi
            if (username.isNotEmpty() && name.isNotEmpty() &&
                email.isNotEmpty() && phone.isNotEmpty() && pass.isNotEmpty()) {

                // Kirim data ke ViewModel (ViewModel yang akan urus Firebase Auth & RTDB)
                viewModel.register(username, name, email, phone, pass)

            } else {
                Toast.makeText(context, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi Text "Sudah punya akun? Login" (Opsional, jika ada tombol back di XML)
        // binding.tvBackToLogin.setOnClickListener {
        //     findNavController().popBackStack()
        // }
    }

    private fun setupObserver() {
        // 1. Observasi Loading (Putar loading bar saat proses firebase berjalan)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading // Matikan tombol saat loading
        }

        // 2. Observasi Hasil Register
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                // Registrasi Berhasil -> Kembali ke Halaman Login
                // Kita gunakan popBackStack() karena user datang dari halaman Login
                findNavController().popBackStack()
            }

            result.onFailure { error ->
                // Registrasi Gagal (misal: Email sudah terpakai, koneksi putus)
                Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}