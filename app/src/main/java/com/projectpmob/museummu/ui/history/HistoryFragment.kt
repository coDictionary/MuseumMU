package com.projectpmob.museummu.ui.history

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            Log.d("HistoryFragment", "User ID ditemukan: $userId") // Cek di Logcat
            viewModel.getHistory(userId)
        } else {
            Toast.makeText(context, "User tidak terdeteksi, silakan login ulang", Toast.LENGTH_SHORT).show()
        }

        observeData()
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
    }

    private fun observeData() {
        viewModel.tickets.observe(viewLifecycleOwner) { list ->
            // Jika kosong tampilkan text kosong, jika ada tampilkan list
            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE // Pastikan ada TextView kosong di XML
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE

                val adapter = HistoryAdapter { ticket ->
                    // Klik item -> Ke Detail
                    val action = HistoryFragmentDirections.actionHistoryFragmentToDetailHistoryFragment(ticket)
                    findNavController().navigate(action)
                }
                adapter.submitList(list)
                binding.rvHistory.adapter = adapter
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }
    }
}