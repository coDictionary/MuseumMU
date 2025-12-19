package com.projectpmob.museummu.data.model

data class User(
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val passwordHash: String = "" // Disimpan sesuai request (lihat catatan keamanan di bawah)
)