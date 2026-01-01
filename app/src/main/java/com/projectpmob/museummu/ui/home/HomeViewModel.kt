package com.projectpmob.museummu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.projectpmob.museummu.data.model.Session
import com.projectpmob.museummu.ui.profile.ResultState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    private val _sessionList = MutableLiveData<ResultState<List<Session>>>()
    val sessionList: LiveData<ResultState<List<Session>>> = _sessionList

    // 1. Tambahkan LiveData untuk Nama User
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    // Definisi Template Sesi (Karena di DB hanya angka, kita butuh detail jam nya)
    private val defaultSessions = listOf(
        Session("1", "Sesi 1", "09:00 - 10:00"),
        Session("2", "Sesi 2", "10:00 - 11:00"),
        Session("3", "Sesi 3", "11:00 - 12:00"),
        Session("4", "Sesi 4", "12:00 - 13:00"),
        Session("5", "Sesi 4", "13:00 - 14:00"),
        Session("6", "Sesi 4", "14:00 - 15:00")
    )

    fun loadUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val ref = FirebaseDatabase.getInstance("https://museum-b7fbf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(uid)
            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Ambil value dari child "name" (sesuaikan dengan struktur DB kamu)
                    val name = snapshot.child("fullName").getValue(String::class.java)
                    _userName.value = name ?: "Pengunjung" // Default jika null
                }
            }.addOnFailureListener {
                _userName.value = "Pengunjung" // Default jika gagal
            }
        } else {
            _userName.value = "Pengunjung" // Default jika belum login
        }
    }

    fun getTodaySessions() {
        _sessionList.value = ResultState.Loading

        // 1. Dapatkan Tanggal Hari Ini (Format harus sama persis dengan di DB: YYYY-MM-DD)
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 2. Reference ke path hari ini
        val ref = FirebaseDatabase.getInstance("https://museum-b7fbf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("daily_availability").child(todayDate)

        // 3. Pasang Listener Realtime
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Clone list default agar kita bisa update datanya
                    val updatedList = defaultSessions.map { it.copy() }

                    // Loop data dari Firebase
                    // Struktur Snapshot: Key="1", Value=11
                    for (sessionData in snapshot.children) {
                        val sessionId = sessionData.key
                        val bookedCount = sessionData.getValue(Int::class.java) ?: 0

                        // Cari sesi di list yang ID nya cocok, lalu update currentBooked
                        updatedList.find { it.id == sessionId }?.currentBooked = bookedCount
                    }

                    _sessionList.value = ResultState.Success(updatedList)

                } catch (e: Exception) {
                    _sessionList.value = ResultState.Error("Gagal memparsing data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _sessionList.value = ResultState.Error(error.message)
            }
        })
    }
}