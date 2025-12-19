package com.projectpmob.museummu

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.projectpmob.museummu.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Setup Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Hubungkan BottomNav dengan NavController
        binding.bottomNav.setupWithNavController(navController)

        // LOGIC 1: Cek Session Login
        if (!sessionManager.isLogin()) {
            // Jika belum login, paksa navigasi ke LoginFragment
            // Kita gunakan popUpTo agar user tidak bisa back ke Home
            navController.navigate(R.id.loginFragment)
        }

        // LOGIC 2: Atur Visibilitas Bottom Nav
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Sembunyikan Bottom Nav & Toolbar di halaman Auth
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    // Tampilkan di Home, History, Profile
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}