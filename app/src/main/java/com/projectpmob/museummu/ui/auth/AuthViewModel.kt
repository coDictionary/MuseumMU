package com.projectpmob.museummu.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.projectpmob.museummu.data.model.User
import com.projectpmob.museummu.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(username: String, name: String, email: String, phone: String, pass: String) {
        _isLoading.value = true

        val newUser = User(username = username, fullName = name, email = email, phone = phone)

        repository.registerUser(newUser, pass) { success, message ->
            _isLoading.value = false
            if (success) {
                Log.d("STATUS REPORT", "SUCSESS REGISTER DI VIEWMODEL")
                _registerResult.value = Result.success(message ?: "Success")
            } else {
                Log.d("STATUS REPORT", "FAILLURE REGISTER DI VIEWMODEL")
                _registerResult.value = Result.failure(Exception(message ?: "Error"))
            }
        }
    }

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    fun login(email: String, pass: String) {
        _isLoading.value = true

        repository.loginUser(email, pass) { success, user, message ->
            _isLoading.value = false
            if (success && user != null) {
                _loginResult.value = Result.success(user)
            } else {
                _loginResult.value = Result.failure(Exception(message ?: "Login Gagal"))
            }
        }
    }
}