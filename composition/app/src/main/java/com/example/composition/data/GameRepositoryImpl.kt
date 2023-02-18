package com.example.composition.data

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val MIN_SUM_VALUE = 2
    private const val MIN_ANSWER_VALUE = 1

    override fun generateQuestion(maxSumValue: Int, optionsCount: Int): Question {
        val sum = Random.nextInt(MIN_SUM_VALUE, maxSumValue + 1)
        val visibleNumber = Random.nextInt(MIN_ANSWER_VALUE, sum)
        val options = HashSet<Int>()
        val rightAnswer = sum - visibleNumber

        options.add(rightAnswer)

        val from = max(rightAnswer - optionsCount, MIN_ANSWER_VALUE)
        val to = min(maxSumValue, rightAnswer + optionsCount)

        while (options.size < optionsCount)
            options.add(Random.nextInt(from, to))

        return Question(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
        return when (level) {
            Level.TEST -> GameSettings(
                10,
                3,
                50,
                8)
            Level.EASY -> GameSettings(
                10,
                10,
                70,
                50)
            Level.MEDIUM -> GameSettings(
                20,
                20,
                80,
                40)
            Level.HARD -> GameSettings(
                30,
                30,
                90,
                30)
        }
    }
}