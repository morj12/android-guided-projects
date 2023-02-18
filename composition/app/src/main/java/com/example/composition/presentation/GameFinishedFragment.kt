package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding is null")

    private val args by navArgs<GameFinishedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        bindViews()
    }

    private fun setupClickListeners() {
        binding.buttonRetry.setOnClickListener { retryGame() }
    }

    private fun bindViews() {
        with(binding) {
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = String.format(
                getString(R.string.required_score),
                args.gameResult.gameSettings.minRightAnswers
            )
            tvScoreAnswers.text = String.format(
                getString(R.string.score_answers),
                args.gameResult.rightAnswersCount
            )
            tvRequiredPercentage.text = String.format(
                getString(R.string.required_percentage),
                args.gameResult.gameSettings.minPercentRightAnswers
            )
            tvScorePercentage.text = String.format(
                getString(R.string.score_percentage),
                getRightAnswersPercent()
            )
        }
    }

    private fun getRightAnswersPercent() = with(args.gameResult) {
        if (questionsCount == 0) 0
        else ((rightAnswersCount / questionsCount.toDouble()) * 100).toInt()
    }

    private fun getSmileResId() =
        if (args.gameResult.winner) R.drawable.ic_smile else R.drawable.ic_sad

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun retryGame() =
        findNavController().popBackStack()

}