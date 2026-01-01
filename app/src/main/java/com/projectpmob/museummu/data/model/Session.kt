package com.projectpmob.museummu.data.model

data class Session(
    val id: String = "",
    val name: String = "", // misal: "Sesi Pagi"
    val timeRange: String = "",
    val maxCapacity: Int = 60, // Default kapasitas total
    var currentBooked: Int = 0
) {
    // Helper untuk hitung sisa
    fun getRemaining(): Int = maxCapacity - currentBooked
}