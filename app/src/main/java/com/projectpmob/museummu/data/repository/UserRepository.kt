package com.projectpmob.museummu.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.projectpmob.museummu.data.model.User
import com.projectpmob.museummu.ui.profile.ResultState

// Import User model & ResultState

class UserRepository {

    private val database = FirebaseDatabase.getInstance("https://museum-b7fbf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")

    // Fungsi Ambil Data
    fun getUserProfile(uid: String, callback: (ResultState<User>) -> Unit) {
        callback(ResultState.Loading)

        database.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    callback(ResultState.Success(user))
                } else {
                    callback(ResultState.Error("Data user kosong"))
                }
            } else {
                callback(ResultState.Error("User tidak ditemukan"))
            }
        }.addOnFailureListener {
            callback(ResultState.Error(it.message ?: "Terjadi kesalahan"))
        }
    }

    // Fungsi Update Data
    fun updateUserProfile(uid: String, updates: Map<String, Any>, callback: (ResultState<Boolean>) -> Unit) {
        callback(ResultState.Loading)

        database.child(uid).updateChildren(updates).addOnSuccessListener {
            callback(ResultState.Success(true))
        }.addOnFailureListener {
            callback(ResultState.Error(it.message ?: "Gagal update data"))
        }
    }
}