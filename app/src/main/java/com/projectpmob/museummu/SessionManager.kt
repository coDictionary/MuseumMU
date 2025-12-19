package com.projectpmob.museummu

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val KEY_IS_LOGIN = "is_login"
        const val KEY_USERNAME = "username"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "full_name"
    }

    // Fungsi menyimpan sesi login
    fun saveSession(username: String, email: String, name: String) {
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_NAME, name)
        editor.apply() // Menggunakan apply() agar asinkron
    }

    // Cek apakah user sudah login
    fun isLogin(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGIN, false)
    }

    // Logout (Hapus data sesi)
    fun logout() {
        editor.clear()
        editor.apply()
    }

    // Ambil data user (opsional, jika butuh untuk ditampilkan di Profil)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
}