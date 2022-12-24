package com.example.soccer_quiz

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.soccer_quiz.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private var quizItems = QuestionsProvider.provide()

    lateinit var currentQuizItem: QuizItem
    lateinit var answers: MutableList<String>
    private var quizItemIndex = 0
    private val numberOfQuestions = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false)

        binding.quizFragment = this

        binding.btAnswer.setOnClickListener(::onPassButtonClicked)

        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome_screen_fragment_name)

        setHasOptionsMenu(true)

        getRandomQuizItem()

        return binding.root
    }

    private fun onPassButtonClicked(view: View) {
        val selectedCbId = binding.rgQuiz.checkedRadioButtonId
        if (selectedCbId != -1) {
            var answerIndex = 0

            when (selectedCbId) {
                R.id.rbFirst -> answerIndex = 0
                R.id.rbSecond -> answerIndex = 1
                R.id.rbThird -> answerIndex = 2
            }

            if (answers[answerIndex] == currentQuizItem.answers[0]) {
                quizItemIndex++

                if (quizItemIndex < numberOfQuestions) {
                    setNextQuizItem()
                    binding.invalidateAll()
                } else view.findNavController().navigate(R.id.action_quizFragment_to_goalFragment)

            } else view.findNavController().navigate(R.id.action_quizFragment_to_missFragment)
        }
    }

    private fun getRandomQuizItem() {
        quizItems.shuffle()
        quizItemIndex = 0
        setNextQuizItem()
    }

    private fun setNextQuizItem() {
        currentQuizItem = quizItems[quizItemIndex]
        answers = currentQuizItem.answers.toMutableList()
        answers.shuffle()
    }
}