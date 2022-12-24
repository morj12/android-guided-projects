package com.example.soccer_quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.soccer_quiz.databinding.FragmentMissBinding

class MissFragment : Fragment() {

    private lateinit var binding: FragmentMissBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {binding = DataBindingUtil.inflate(inflater, R.layout.fragment_miss, container, false)

        binding.btOneMoreTime.setOnClickListener(::onOneMoreTimeClicked)

        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome_screen_fragment_name)

        return binding.root
    }

    private fun onOneMoreTimeClicked(view: View) {
        view.findNavController().navigate(R.id.action_missFragment_to_quizFragment)
    }
}