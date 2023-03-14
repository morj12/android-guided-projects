package com.example.cart_and_notes.presentation.adapter

import android.content.SharedPreferences
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.CartItemItemBinding
import com.example.cart_and_notes.domain.entity.CartItem
import com.example.cart_and_notes.util.PrefsConsts.Keys.NOTES_TEXT_SIZE_KEY
import com.example.cart_and_notes.util.PrefsConsts.Values.DEFAULT_TEXT_SIZE
import com.example.cart_and_notes.util.setTextSize

class CartItemAdapter(private val prefs: SharedPreferences) :
    ListAdapter<CartItem, CartItemAdapter.ViewHolder>(CartItemCallback()) {

    var onCartItemCheckBoxClickListener: ((CartItem) -> Unit)? = null
    var onCartItemEditClickListener: ((CartItem) -> Unit)? = null

    class ViewHolder(val binding: CartItemItemBinding) : RecyclerView.ViewHolder(binding.root)

    class CartItemCallback : DiffUtil.ItemCallback<CartItem>() {

        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val textSize = prefs.getString(NOTES_TEXT_SIZE_KEY, DEFAULT_TEXT_SIZE)
        holder.binding.apply {
            tvCartItemName.text = item.name
            tvCartItemInfo.text = item.info
            tvCartItemInfo.visibility = checkVisibility(item)
            cbCartItem.isChecked = item.checked
            textSize.let {
                tvCartItemName.setTextSize((textSize!!.toInt() + 2).toString())
                tvCartItemInfo.setTextSize(textSize)
            }
            setPaintFlagAndColor(this)
            cbCartItem.setOnClickListener {
                onCartItemCheckBoxClickListener?.invoke(item.copy(checked = cbCartItem.isChecked))
            }
            imEditCartItem.setOnClickListener {
                onCartItemEditClickListener?.invoke(item)
            }
        }
    }

    private fun setPaintFlagAndColor(binding: CartItemItemBinding) {
        binding.apply {
            if (cbCartItem.isChecked) {
                tvCartItemName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvCartItemInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvCartItemName.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.light_grey
                    )
                )
            } else {
                tvCartItemName.paintFlags = Paint.ANTI_ALIAS_FLAG
                tvCartItemInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                tvCartItemName.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.black
                    )
                )
            }
        }
    }

    private fun checkVisibility(item: CartItem): Int {
        return if (item.info.isEmpty()) View.GONE else View.VISIBLE
    }
}