package com.example.composition.domain.usecase

import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository

class GenerateQuestionUseCase (private val repository: GameRepository) {

    operator fun invoke(maxSumValue: Int): Question {
        return repository.generateQuestion(maxSumValue, OPTIONS_COUNT)
    }

    companion object {
        private const val OPTIONS_COUNT = 6
    }
}