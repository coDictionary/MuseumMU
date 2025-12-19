package com.projectpmob.museummu.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projectpmob.museummu.data.model.User
import java.security.MessageDigest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance("https://museum-b7fbf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")

    // Fungsi Hash Password sederhana (SHA-256)
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun registerUser(user: User, passOriginal: String, onResult: (Boolean, String?) -> Unit) {
        // 1. Create User di Firebase Authentication
        auth.createUserWithEmailAndPassword(user.email, passOriginal)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("STATUS REPORT", "SUCSESS REGISTER DI REPOSITORY")
                    val uid = task.result?.user?.uid
                    if (uid != null) {
                        // 2. Hash Password sebelum simpan ke DB
                        val finalUser = user.copy(passwordHash = hashPassword(passOriginal))

                        // 3. Simpan data detail ke Realtime Database
                        db.child(uid).setValue(finalUser)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Log.d("STATUS REPORT", "SUCSESS REGISTER DI REPOSITORY RTDB")
                                    onResult(true, "Registrasi Berhasil")
                                } else {
                                    onResult(false, dbTask.exception?.message)
                                    Log.d("STATUS REPORT", "FAIL REGISTER DI REPOSITORY RTDB")
                                }
                            }
                    }
                } else {
                    Log.d("STATUS REPORT", "FAILURE REGISTER DI REPOSITORY")
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, pass: String, onResult: (Boolean, User?, String?) -> Unit) {
        // 1. Login ke Firebase Auth
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    if (uid != null) {
                        // 2. Ambil data detail user dari Realtime Database
                        db.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                if (user != null) {
                                    onResult(true, user, "Login Berhasil")
                                } else {
                                    onResult(false, null, "Data user tidak ditemukan di database")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                onResult(false, null, error.message)
                            }
                        })
                    }
                } else {
                    onResult(false, null, task.exception?.message)
                }
            }
    }
}