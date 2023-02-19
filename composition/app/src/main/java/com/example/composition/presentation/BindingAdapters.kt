package com.example.composition.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult

@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}

@BindingAdapter("correctAnswers")
fun bindCorrectAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        count
    )
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        count
    )
}

@BindingAdapter("correctPercentage")
fun bindCorrectPercentage(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getRightAnswersPercent(gameResult)
    )
}

private fun getRightAnswersPercent(gameResult: GameResult) = with(gameResult) {
    if (questionsCount == 0) 0
    else ((rightAnswersCount / questionsCount.toDouble()) * 100).toInt()
}

@BindingAdapter("resultEmoji")
fun bindResultEmoji(imageView: ImageView, winner: Boolean) {
    imageView.setImageResource(getSmileResId(winner))
}

private fun getSmileResId(winner: Boolean) =
    if (winner) R.drawable.ic_smile else R.drawable.ic_sad

@BindingAdapter("rightAnswersPercent")
fun bindRightAnswersPercent(progressBar: ProgressBar, progress: Int) {
    progressBar.setProgress(progress, true)
}

@BindingAdapter("enoughRightAnswers")
fun bindEnoughRightAnswers(textView: TextView, enough: Boolean) {
    textView.setTextColor(getColorByState(textView.context, enough))
}

private fun getColorByState(context: Context, goodState: Boolean): Int {
    val colorId = if (goodState) android.R.color.holo_green_light
    else android.R.color.holo_red_light
    return ContextCompat.getColor(context, colorId)
}

@BindingAdapter("enoughRightAnswersPercent")
fun bindEnoughRightAnswers(progressBar: ProgressBar, enough: Boolean) {
    val color = getColorByState(progressBar.context, enough)
    progressBar.progressTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("numberAsText")
fun bindNumberAsText(textView: TextView, number: Int) {
    textView.text = number.toString()
}

interface OnOptionClickListener {
    fun onOptionClick(option: Int)
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClickListener(textView: TextView, listener: OnOptionClickListener) {
    textView.setOnClickListener {
        listener.onOptionClick(textView.text.toString().toInt())
    }
}