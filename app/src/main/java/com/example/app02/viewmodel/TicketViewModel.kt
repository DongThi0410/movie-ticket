package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.api.TicketDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(application: Application) : AndroidViewModel(application) {
    val dataStore = DataStore(application)
    private val _ticket = MutableStateFlow<TicketDTO?>(null)
    val ticket: StateFlow<TicketDTO?> = _ticket

    private val _tickets = MutableStateFlow<List<TicketDTO>>(emptyList())
    val tickets = _tickets.asStateFlow()


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    fun getTicket(bookingId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {


                val response = RetrofitInstance.apiService.getTicket(bookingId)
                if (response.isSuccessful) {
                    _ticket.value = response.body()
                } else {
                    Log.e(
                        "TicketViewModel",
                        "API error: ${response.code()} - ${response.message()}"
                    )
                    _ticket.value = null
                }
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Network error", e)
                _ticket.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun getTicketsByUser(id:Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val response = RetrofitInstance.apiService.getTicketsByUser(userId)
                    _tickets.value = response
                    onResult(true)
                } catch (e: Exception) {
                    onResult(false)
                    Log.e("TicketViewModel", "${e.message}")

                }
            }
            onResult(false)

        }
    }
}
