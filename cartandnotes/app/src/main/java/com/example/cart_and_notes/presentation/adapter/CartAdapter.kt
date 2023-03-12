package com.example.cart_and_notes.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.CartItemBinding
import com.example.cart_and_notes.domain.entity.Cart

class CartAdapter : ListAdapter<Cart, CartAdapter.ViewHolder>(CartCallback()) {

    var onCartClickListener: ((Cart) -> Unit)? = null
    var onEditCartClickListener: ((Cart) -> Unit)? = null

    class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)

    class CartCallback : DiffUtil.ItemCallback<Cart>() {

        override fun areItemsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvCartName.text = item.name
            tvCartTime.text = item.creationTime
            tvCartCounter.text = root.context.getString(
                R.string.cart_items_counter,
                item.checkedItems,
                item.totalItems
            )
            pbarCart.max = item.totalItems
            pbarCart.progress = item.checkedItems
            pbarCart.progressTintList =
                ColorStateList.valueOf(getProgressBarColor(item, root.context))
            root.setOnClickListener {
                onCartClickListener?.invoke(item)
            }
            btEdit.setOnClickListener {
                onEditCartClickListener?.invoke(item)
            }
        }
    }

    private fun getProgressBarColor(item: Cart, context: Context) =
        if (item.checkedItems == item.totalItems)
            ContextCompat.getColor(context, R.color.green_main)
        else
            ContextCompat.getColor(context, R.color.red)

}
