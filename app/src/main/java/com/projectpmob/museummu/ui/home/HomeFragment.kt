package com.projectpmob.museummu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.projectpmob.museummu.R
import com.projectpmob.museummu.SessionManager
import com.projectpmob.museummu.databinding.FragmentHomeBinding
import com.projectpmob.museummu.ui.profile.ResultState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment() ,OnMapReadyCallback{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Gunakan ViewModel
    private val viewModel: HomeViewModel by viewModels() // Pastikan ada dependency fragment-ktx
    private lateinit var sessionAdapter: SessionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Header
        setupHeader()

        // Setup RecyclerView
        sessionAdapter = SessionAdapter()
        binding.rvSessions.apply {
            // Ubah orientasi ke HORIZONTAL
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = sessionAdapter
        }

        val snapHelper = androidx.recyclerview.widget.LinearSnapHelper()
        // Pastikan attach hanya sekali (cek jika belum terpasang)
        binding.rvSessions.onFlingListener = null
        snapHelper.attachToRecyclerView(binding.rvSessions)

        // Panggil Data
        viewModel.getTodaySessions()

        // 2. Inisialisasi Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Observasi Data Realtime
        observeData()
    }

    // 3. Callback saat Map Siap
    override fun onMapReady(googleMap: GoogleMap) {

        // Atau Koordinat UAD Kampus 4 (Museum Muhammadiyah)
        val museumMuhammadiyah = LatLng(-7.834160, 110.383890)

        // Setting Tampilan
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID // Saya sarankan HYBRID (Satelit + Nama Jalan) agar lebih informatif
        // Jika ingin murni satelit tanpa teks jalan, gunakan: GoogleMap.MAP_TYPE_SATELLITE

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

        // Tambah Marker
        googleMap.addMarker(
            MarkerOptions()
                .position(museumMuhammadiyah)
                .title("Museum Muhammadiyah")
                .snippet("Universitas Ahmad Dahlan Kampus 4")
        )

        // Arahkan Kamera (Zoom level 15f agar terlihat detail gedung)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(museumMuhammadiyah, 17f))
    }

    private fun setupHeader() {
        // Set Tanggal di UI agar user tahu ini data tanggal berapa
        val dateNow = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        binding.tvCurrentDate.text = dateNow

        // Load user name dari Firebase Auth/Database seperti sebelumnya...
        // ... kode load user profil ...
    }

    private fun observeData() {
        // 1. Observer untuk Nama User (Dinamis)
        viewModel.loadUserName()
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            // Update teks tvUsername dengan nama dari database
            binding.tvUsername.text = name
        }

        viewModel.sessionList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBarSession.visibility = View.VISIBLE
                    binding.rvSessions.visibility = View.GONE
                }
                is ResultState.Success -> {
                    binding.progressBarSession.visibility = View.GONE
                    binding.rvSessions.visibility = View.VISIBLE
                    sessionAdapter.setSessions(result.data)
                }
                is ResultState.Error -> {
                    binding.progressBarSession.visibility = View.GONE
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}