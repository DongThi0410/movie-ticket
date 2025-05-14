package com.example.app02.network.models

import java.time.LocalDate

data class Movie(
    val id: Int? = null,
    val title: String,
    val des: String,
    val trailer: String,
    val genre: Genre,
    val duration: Int,
    val deleted: Boolean? = false,
    val poster: String,
    val cast: String,
    val rating: Float,
    val director: String,
    val startDate: String,
    val endDate: String,
    val category: Cate
)
