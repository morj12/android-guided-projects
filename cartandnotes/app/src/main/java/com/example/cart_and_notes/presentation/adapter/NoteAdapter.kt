package com.example.cart_and_notes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.databinding.NoteItemBinding
import com.example.cart_and_notes.entity.NoteDbModel

// TODO: use entity from domain layer
class NoteAdapter : ListAdapter<NoteDbModel, NoteAdapter.ViewHolder>(NoteCallback()) {

    var onNoteClickListener: ((NoteDbModel) -> Unit)? = null

    class ViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root)

    class NoteCallback : DiffUtil.ItemCallback<NoteDbModel>() {

        override fun areItemsTheSame(oldItem: NoteDbModel, newItem: NoteDbModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NoteDbModel, newItem: NoteDbModel) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            binding.tvTime.text = item.time
            binding.root.setOnClickListener {
                onNoteClickListener?.invoke(item)
            }
        }
    }

}