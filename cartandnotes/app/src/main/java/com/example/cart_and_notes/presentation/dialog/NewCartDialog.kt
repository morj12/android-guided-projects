package com.example.cart_and_notes.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.NewCartDialogBinding

object NewCartDialog {

    fun showDialog(context: Context, name: String, callback: (String) -> Unit) {
        var dialog: AlertDialog? = null
        val binding = NewCartDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }
        binding.apply {
            edNewCartName.setText(name)
            if (name.isNotEmpty()) {
                binding.bCreateCart.text =
                    context.getString(R.string.update_cart)
                binding.newCartTitle.text =
                    context.getString(R.string.update_cart_title)
            }
            bCreateCart.setOnClickListener {
                val cartName = this.edNewCartName.text.toString()
                if (cartName.isNotEmpty()) {
                    callback(cartName)
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }
}