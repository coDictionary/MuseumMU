package com.projectpmob.museummu.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.projectpmob.museummu.data.model.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TicketRepository {

    private val db = FirebaseDatabase.getInstance("https://museum-b7fbf-default-rtdb.asia-southeast1.firebasedatabase.app").reference

    // Fungsi mengambil jumlah pengunjung yang sudah booking di tanggal & sesi tertentu
    fun checkAvailability(date: String, onResult: (Map<String, Int>) -> Unit) {
        // date format: YYYY-MM-DD
        db.child("daily_availability").child(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val availabilityMap = mutableMapOf<String, Int>()
                    // Inisialisasi sesi 1-6 dengan 0 jika belum ada data
                    for (i in 1..6) availabilityMap["$i"] = 0

                    snapshot.children.forEach { sessionSnapshot ->
                        val sessionKey = sessionSnapshot.key // "1", "2"...
                        val count = sessionSnapshot.getValue(Int::class.java) ?: 0
                        if (sessionKey != null) {
                            availabilityMap[sessionKey] = count
                        }
                    }
                    onResult(availabilityMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyMap())
                }
            })
    }

    // Fungsi Simpan Tiket (Sekaligus update availability)
    fun bookTicket(ticket: Ticket, onResult: (Boolean, String?) -> Unit) {
        val ticketRef = db.child("tickets").push()
        val ticketId = ticketRef.key ?: return
        val finalTicket = ticket.copy(ticketId = ticketId)
        val availabilityRef = db.child("daily_availability").child(ticket.visitDate).child(ticket.session)

        // Gunakan Transaction agar aman dari race condition (rebutan kuota)
        availabilityRef.runTransaction(object : com.google.firebase.database.Transaction.Handler {
            override fun doTransaction(currentData: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                val currentCount = currentData.getValue(Int::class.java) ?: 0

                // Cek lagi apakah setelah ditambah jumlah orang, melebihi 60?
                if (currentCount + ticket.totalPerson > 60) {
                    // Abort jika tiba-tiba penuh
                    return com.google.firebase.database.Transaction.abort()
                }

                // Jika aman, update angka
                currentData.value = currentCount + ticket.totalPerson
                return com.google.firebase.database.Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed) {
                    // Jika update kuota sukses, baru simpan data tiket detail
                    ticketRef.setValue(finalTicket)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) onResult(true, "Booking Berhasil!")
                            else onResult(false, "Gagal simpan tiket: ${task.exception?.message}")
                        }
                } else {
                    onResult(false, "Maaf, Sesi ini baru saja penuh.")
                }
            }
        })
    }

    // Fungsi Ambil History user
    fun getTicketsByUser(userId: String, onResult: (List<Ticket>) -> Unit) {
        db.child("tickets")
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ticketList = mutableListOf<Ticket>()
                    for (item in snapshot.children) {
                        val ticket = item.getValue(Ticket::class.java)
                        if (ticket != null) {
                            ticketList.add(ticket)
                        }
                    }
                    // Sort dari yang terbaru (berdasarkan timestamp descending)
                    ticketList.sortByDescending { it.timestamp }

                    onResult(ticketList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }
}