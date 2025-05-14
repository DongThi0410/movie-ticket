package com.example.app02.viewmodel

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.api.RevenueDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class RevenueViewModel : ViewModel() {

    private val _revenues = MutableStateFlow<List<RevenueDTO>>(emptyList())
    val revenues = _revenues.asStateFlow()

    fun fetchRevenues() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getRevenueDaily(LocalDate.now().minusDays(7).toString(), LocalDate.now().toString())
                _revenues.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
