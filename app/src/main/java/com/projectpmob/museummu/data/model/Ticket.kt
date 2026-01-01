package com.projectpmob.museummu.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ticket(
    val ticketId: String = "",
    val userId: String = "",
    val bookingDate: String = "", // Tanggal User melakukan pemesanan (Hari H)
    val visitDate: String = "",   // Tanggal Kunjungan
    val session: String = "",
    val sessionLabel: String = "",
    val type: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val originCity: String = "",
    val totalPerson: Int = 1,
    val totalPrice: Long = 0,
    val status: String = "active",

    // --- TAMBAHAN BARU ---
    // 1. Timestamp (Long): Berguna untuk sorting codingan (terbaru ke terlama)
    val timestamp: Long = System.currentTimeMillis(),

    // 2. Readable Time (String): Berguna agar admin bisa baca jam pesannya di Firebase
    val orderTime: String = ""
): Parcelable