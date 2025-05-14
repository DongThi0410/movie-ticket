package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.RatingDTO
import com.example.app02.network.api.RatingResponseDTO
import com.example.app02.network.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RatingViewModel(application: Application) : AndroidViewModel(application) {
    val dataStore = DataStore(application)

    private val _resultRating = MutableStateFlow<List<RatingResponseDTO>>(emptyList())
    val result = _resultRating.asStateFlow()

    private val _sendRating = MutableStateFlow<Boolean>(false)
    val sendRating = _sendRating.value

    fun getRating(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getRate(movieId)
                _resultRating.value = response
            }catch (e: Exception){

                Log.e("RatingViewModel", "${e.message}")
            }
        }
    }

    fun rating(movieId: Int, score: Int, cmt: String, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let{
                val ratingDTO = RatingDTO(userId, movieId, score, cmt)
                val response = RetrofitInstance.apiService.rating(ratingDTO)
                if (response.isSuccessful) {
                    val msg = response.body()?.toString()
                    Log.d("Rating", "Server message: $msg")
                    onResult(true)
                } else {
                    Log.e("RatingError", "Server lỗi: ${response.errorBody()?.string()}")
                    onResult(false)
                }

            }
            _sendRating.value = false
        }
    }
}