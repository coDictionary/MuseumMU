package com.projectpmob.museummu.ui.order

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.projectpmob.museummu.data.model.Ticket
import com.projectpmob.museummu.data.repository.TicketRepository

class OrderViewModel : ViewModel() {

    private val repository = TicketRepository()

    // Data Sesi Museum (Statis)
    private val sessionMap = mapOf(
        "1" to "09:00 - 10:00",
        "2" to "10:00 - 11:00",
        "3" to "11:00 - 12:00",
        "4" to "12:00 - 13:00",
        "5" to "13:00 - 14:00",
        "6" to "14:00 - 15:00"
    )

    // LiveData untuk UI
    private val _availableSessions = MutableLiveData<List<String>>()
    val availableSessions: LiveData<List<String>> = _availableSessions

    private val _orderResult = MutableLiveData<Result<String>>()
    val orderResult: LiveData<Result<String>> = _orderResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var selectedDate: String = "" // Format YYYY-MM-DD

    // 1. Logic Cek Ketersediaan
    fun checkSessionAvailability(date: String, requestedPerson: Int) {
        selectedDate = date
        _isLoading.value = true

        repository.checkAvailability(date) { bookedMap ->
            _isLoading.value = false

            // Filter Sesi: Kapasitas Max (60) - Yang Sudah Booking >= Permintaan User
            val validList = mutableListOf<String>()

            sessionMap.forEach { (key, timeLabel) ->
                val currentBooked = bookedMap[key] ?: 0
                val remaining = 60 - currentBooked

                if (remaining >= requestedPerson) {
                    // Format tampilan di dropdown: "Sesi 1 (09:00-10:00) | Sisa: 45"
                    validList.add("Sesi $key ($timeLabel) | Sisa: $remaining")
                }
            }

            if (validList.isEmpty()) {
                validList.add("Semua sesi penuh/tidak cukup")
            }
            _availableSessions.value = validList
        }
    }

    // 2. Logic Submit Tiket
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitTicket(
        userId: String,
        type: String, // "individual" or "group"
        totalPerson: Int,
        sessionFullString: String, // String dari dropdown
        name: String,
        email: String,
        phone: String,
        origin: String,
        captchaInput: String,
        captchaReal: String
    ) {
        // --- VALIDASI INPUT ---
        if (selectedDate.isEmpty()) {
            _orderResult.value = Result.failure(Exception("Pilih tanggal dahulu"))
            return
        }
        if (sessionFullString.contains("penuh") || sessionFullString.isEmpty()) {
            _orderResult.value = Result.failure(Exception("Sesi tidak valid"))
            return
        }
        if (!name.matches(Regex("^[a-zA-Z\\s]+$"))) {
            _orderResult.value = Result.failure(Exception("Nama tidak boleh mengandung angka/simbol"))
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _orderResult.value = Result.failure(Exception("Format email salah"))
            return
        }
        if (captchaInput != captchaReal) {
            _orderResult.value = Result.failure(Exception("Captcha salah!"))
            return
        }

        val currentDateTime = java.util.Date()
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val formattedOrderTime = formatter.format(currentDateTime) // Hasil: "2025-01-22 14:30:05"


        // --- PROSES DATA ---
        // Ambil ID Sesi dari string dropdown (Contoh: "Sesi 1 (..." -> ambil "1")
        val sessionId = sessionFullString.split(" ")[1]
        val sessionTime = sessionMap[sessionId] ?: ""
        val price = 30000L * totalPerson

        val newTicket = Ticket(
            userId = userId,
            bookingDate = java.time.LocalDate.now().toString(),
            visitDate = selectedDate,
            session = sessionId,
            sessionLabel = sessionTime,
            type = type,
            name = name,
            email = email,
            phone = phone,
            originCity = origin,
            totalPerson = totalPerson,
            totalPrice = price,
            orderTime = formattedOrderTime
        )

        _isLoading.value = true
        repository.bookTicket(newTicket) { success, msg ->
            _isLoading.value = false
            if (success) _orderResult.value = Result.success(msg ?: "Berhasil")
            else _orderResult.value = Result.failure(Exception(msg))
        }
    }
}