package com.example.app02.network.models

data class User(
    val id: Int ,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",
    val password: String = ""
)
