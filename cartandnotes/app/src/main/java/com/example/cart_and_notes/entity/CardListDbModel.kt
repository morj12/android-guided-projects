package com.example.cart_and_notes.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "card_list")
data class CardListDbModel(
    val name: String,
    val creationTime: String,
    val totalItems: Int,
    val checkedItems: Int,
    val itemIds: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable
