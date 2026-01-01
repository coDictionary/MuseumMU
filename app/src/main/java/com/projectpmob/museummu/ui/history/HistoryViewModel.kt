package com.projectpmob.museummu.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.projectpmob.museummu.data.model.Ticket
import com.projectpmob.museummu.data.repository.TicketRepository

class HistoryViewModel : ViewModel() {
    private val repository = TicketRepository()

    private val _tickets = MutableLiveData<List<Ticket>>()
    val tickets: LiveData<List<Ticket>> = _tickets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getHistory(userId: String) {
        _isLoading.value = true
        repository.getTicketsByUser(userId) { list ->
            _isLoading.value = false
            _tickets.value = list
        }
    }
}