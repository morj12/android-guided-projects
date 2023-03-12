package com.example.cart_and_notes.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.cart_and_notes.databinding.EditListItemDialogBinding
import com.example.cart_and_notes.domain.entity.CartItem

object EditCartItemDialog {

    fun showDialog(context: Context, cartItem: CartItem, callback: (CartItem) -> Unit) {
        var dialog: AlertDialog? = null
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }
        binding.apply {
            edCartName.setText(cartItem.name)
            edCartInfo.setText(cartItem.info)
            btUpdateCartItem.setOnClickListener {
                val cartName = edCartName.text.toString()
                val cartInfo = edCartInfo.text.toString()
                if (cartName.isNotEmpty()) {
                    callback(cartItem.copy(name = cartName, info = cartInfo))
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }

}