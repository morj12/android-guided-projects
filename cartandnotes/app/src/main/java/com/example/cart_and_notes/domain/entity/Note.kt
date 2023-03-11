package com.example.cart_and_notes.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val title: String,
    val content: String,
    val time: String,
    val category: String,
    val id: Int = 0,
) : Parcelable
