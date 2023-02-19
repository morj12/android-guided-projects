package com.example.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ItemShopDisabledBinding
import com.example.shoppinglist.databinding.ItemShopEnabledBinding
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter : ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }

        /**
         * DataBindingUtil is used when there is more than one layout for recycler view elements
         */

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return ShopItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.binding) {
            is ItemShopDisabledBinding -> {
                holder.binding.tvName.text = item.name
                holder.binding.tvCount.text = item.count.toString()
            }
            is ItemShopEnabledBinding -> {
                holder.binding.tvName.text = item.name
                holder.binding.tvCount.text = item.count.toString()
            }
        }
        holder.binding.root.setOnClickListener {
            onShopItemClickListener?.invoke(item)
        }
        holder.binding.root.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(item)
            true
        }
    }

    // Used when there is more than one layout for recycler view elements
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled) VIEW_TYPE_ENABLED else VIEW_TYPE_DISABLED
    }

    companion object {
        const val VIEW_TYPE_ENABLED = 0
        const val VIEW_TYPE_DISABLED = 1
        const val MAX_POOL_SIZE = 20
    }

}