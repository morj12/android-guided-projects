package com.example.composition.domain.entity

data class GameResult(
    val winner: Boolean,
    val rightAnswersCount: Int,
    val questionsCount: Int,
    val gameSettings: GameSettings
)
