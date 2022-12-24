package com.example.soccer_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.soccer_quiz.databinding.FragmentWelcomeScreenBinding

class WelcomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeScreenBinding.inflate(inflater, container, false)

        binding.startQuizButton.setOnClickListener(::onStartQuizButtonClicked)

        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome_screen_fragment_name)

        return binding.root
    }

    private fun onStartQuizButtonClicked(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_welcomeScreenFragment_to_quizFragment)
    }
}