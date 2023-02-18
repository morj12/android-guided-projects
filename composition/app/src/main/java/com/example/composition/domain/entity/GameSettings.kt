package com.example.composition.domain.entity

data class GameSettings(
    val maxSumValue: Int,
    val minRightAnswers: Int,
    val minPercentRightAnswers: Int,
    val timeInSeconds: Int
)
