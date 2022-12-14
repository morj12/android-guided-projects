package com.example.wildrunning.utils

import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout

object Animator {

    fun animateViewOfInt(view: View, attr: String, value: Int, time: Long) {
        ObjectAnimator.ofInt(view, attr, value)
            .apply {
                duration = time
                start()
            }
    }

    fun animateViewOfFloat(view: View, attr: String, value: Float, time: Long) {
        ObjectAnimator.ofFloat(view, attr, value)
            .apply {
                duration = time
                start()
            }
    }
}