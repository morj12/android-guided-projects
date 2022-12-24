package com.example.soccer_quiz

object QuestionsProvider {

    fun provide(): MutableList<QuizItem> {
        return mutableListOf(
            QuizItem(
                "How many players does each team have on the pitch when a soccer match starts?",
                listOf("11", "8", "12")
            ),
            QuizItem(
                "What should be the circumference of a Size 5 (adult) football?",
                listOf("27\" to 28\"", "24\" to 25\"", "23\" to 24\"")
            ),
            QuizItem(
                "What is given to a player for a very serious personal foul on an opponent?",
                listOf("Red Card", "Green Card", "Yellow Card")
            ),
            QuizItem(
                "To most places in the world, soccer is known as what?",
                listOf("Football", "Footgame", "Legball")
            ),
            QuizItem(
                "Offside. If a player is offside, what action does the referee take?",
                listOf(
                    "Awards an indirect free kick to the opposing team",
                    "Awards a penalty to the opposing team",
                    "Awards a yellow card  to the player"
                )
            ),
            QuizItem(
                "How many laws of Association Football are there?",
                listOf("17", "11", "23")
            ),
            QuizItem(
                "Excluding the goalkeeper, what part of the body cannot touch the ball?",
                listOf("Arm", "Head", "Shoulder")
            ),
            QuizItem(
                "How big is a regulation official soccer goal?",
                listOf("2.44m high, 7.32m wide", "2.55m high, 7.62m wide", "2.33m high, 8.15m wide")
            ),
            QuizItem(
                "What is it called when a player, without the ball on the offensive team is behind the last defender, or fullback?",
                listOf("Offside", "Outside", "Field-side")
            ),
            QuizItem(
                "The Ball. The circumference of the ball should not be greater than what?",
                listOf("70", "80", "90")
            ),
            QuizItem(
                "How many minutes are played in a regular game (without injury time or extra time)?",
                listOf("90", "95", "100")
            ),
            QuizItem(
                "What statement describes a proper throw-in?",
                listOf(
                    "Both hands must be on the ball behind the head, both feet must be on the ground",
                    "Both hands must be on the ball behind the head",
                    "Both feet must be on the ground"
                )
            ),
        )
    }
}