package com.example.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val maxSumValue: Int,
    val minRightAnswers: Int,
    val minPercentRightAnswers: Int,
    val timeInSeconds: Int
) : Parcelable
