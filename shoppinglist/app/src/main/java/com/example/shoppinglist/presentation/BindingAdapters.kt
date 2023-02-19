package com.example.shoppinglist.presentation

import androidx.databinding.BindingAdapter
import com.example.shoppinglist.R
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorInputName")
fun bindErrorInputName(tvl: TextInputLayout, isError: Boolean) {
    val message = if (isError) tvl.context.getString(R.string.error_input_name) else null
    tvl.error = message
}

@BindingAdapter("errorInputCount")
fun bindErrorInputCount(tvl: TextInputLayout, isError: Boolean) {
    val message = if (isError) tvl.context.getString(R.string.error_input_count) else null
    tvl.error = message
}