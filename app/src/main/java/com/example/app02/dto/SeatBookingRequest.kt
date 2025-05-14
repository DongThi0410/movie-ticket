package com.example.app02.dto

data class SeatBookingRequest (
    val seatId: Int,
    val userId: Int,
    val showtimeId: Int
)