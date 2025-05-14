package com.example.app02.network.models

data class Seat(
    val id: Int,
    val col: Int,
    val state: Int,
    val row: String,
    val type: SeatType,
    val price: Double
)
