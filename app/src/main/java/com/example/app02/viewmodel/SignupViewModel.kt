package com.example.app02.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.dto.SignupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel : ViewModel() {
    var signupState = mutableStateOf(SignupState())

    fun signup(name: String, email: String, phone: String, password: String) {
        signupState.value = SignupState(isLoading = true)

        val signupRequest = SignupRequest(name, email, phone, password)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.signup(signupRequest)
                }
                if (response.isSuccessful){
                    val msg = response.body()
                    signupState.value = SignupState(isLoading = false,
                        message =msg?.message )
                }else{
                     val errorBody = response.errorBody()?.string()
                    signupState.value = SignupState(isLoading = false, errorMessage = errorBody)
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "Lỗi đăng ký: ${e.message} ")
                signupState.value = SignupState(isLoading = false, errorMessage = "Lỗi kết nối")
            }
        }
    }
}

data class SignupState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)