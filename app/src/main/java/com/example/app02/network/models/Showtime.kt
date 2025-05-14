package com.example.app02.network.models

import java.time.LocalDateTime

data class Showtime(
    val id: Int,
    val movie: Movie,
    val auditorium: Auditorium,
    val startTime: List<Int>,
    val endTime: List<Int>
)
