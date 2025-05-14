package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.app02.data.DataStore
import com.example.app02.dto.LoginRequest
import com.example.app02.dto.LoginResponse
import com.example.app02.network.api.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Response
import okio.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
//

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val dataStore = DataStore(application)
    val isLogged: Flow<Boolean> = dataStore.token.map { token -> token != null }
    fun login(email: String, password: String, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitInstance.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful){
                    response.body()?.let {
                        dataStore.saveToken(it.token)
                        dataStore.extractAndSave()
                        Log.d("Login", "Token đã lưu: ${it.token}")
                        _loginState.value = LoginState.Success(it.token)
                        onResult(true)
                    } ?: run {
                        _loginState.value = LoginState.Error("Empty")
                        onResult(false)
                    }
                }else{
                    _loginState.value = LoginState.Error("invalid")
                    onResult(false)
                }
            }catch (E: IOException){
                _loginState.value = LoginState.Error("Network error")
                onResult(false)
            }catch (e: HttpException){
                _loginState.value = LoginState.Error("SERVER ERROR")
                onResult(false)
            }
        }
    }

    fun logout(onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {

                dataStore.clearToken()
                onResult(true)
            }catch (e:Exception){
                onResult(false)
            }
        }
    }
    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                    onResult(true, " Đã gửi")
                else
                    onResult(false, task.exception?.message ?: "Loii")
            }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
