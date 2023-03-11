package com.example.cart_and_notes.domain.entity

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(val name: String,
                val creationTime: String,
                val totalItems: Int,
                val checkedItems: Int,
                val itemIds: String,
                @PrimaryKey(autoGenerate = true)
                val id: Int = 0
) : Parcelable
