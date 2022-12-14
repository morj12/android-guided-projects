package com.example.wildrunning.utils

import android.widget.LinearLayout

object MyLayoutManager {

    fun setLinearLayoutHeight(linearLayout: LinearLayout, height: Int) {
        val params = linearLayout.layoutParams as LinearLayout.LayoutParams
        params.height = height
        linearLayout.layoutParams = params
    }
}