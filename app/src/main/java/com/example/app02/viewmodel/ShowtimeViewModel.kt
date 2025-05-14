package com.example.app02.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.models.Showtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ShowtimeViewModel() : ViewModel() {

    private val _showtimes = MutableStateFlow<List<Showtime>>(emptyList())
    val showtimes = _showtimes.asStateFlow()
    private val _showtime = MutableStateFlow<Showtime?>(null)

    val showtime = _showtime.asStateFlow()

    fun fetchShowtimes(movieId: Int, date: String) {
        viewModelScope.launch {
            try {

                val response = RetrofitInstance.apiService.getShowtime(movieId, date)
                _showtimes.value = response

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun fetchShowtimeById(showtimeId: Int) {
        viewModelScope.launch {
            try {
                val fetchedShowtime = RetrofitInstance.apiService.getShowtimeById(showtimeId)
                _showtime.value = fetchedShowtime
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
