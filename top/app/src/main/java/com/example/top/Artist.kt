package com.example.top

data class Artist(
    val id: Long = 0,
    val name: String = "",
    val surname: String = "",
    val birthDate: Long = 0,
    val birthPlace: String= "",
    val height: Short = 0,
    val order: Int = 0,
    val notes: String = "",
    val photoUrl: String = ""
)
