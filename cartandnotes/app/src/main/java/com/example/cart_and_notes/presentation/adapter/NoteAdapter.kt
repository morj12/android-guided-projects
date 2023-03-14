package com.example.cart_and_notes.presentation.adapter

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.databinding.NoteItemBinding
import com.example.cart_and_notes.domain.entity.Note
import com.example.cart_and_notes.util.PrefsConsts
import com.example.cart_and_notes.util.TimeHelper
import com.example.cart_and_notes.util.setTextSize

class NoteAdapter(private val prefs: SharedPreferences) :
    ListAdapter<Note, NoteAdapter.ViewHolder>(NoteCallback()) {

    var onNoteClickListener: ((Note) -> Unit)? = null

    class ViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root)

    class NoteCallback : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note) =
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
        val textSize = prefs.getString(
            PrefsConsts.Keys.NOTES_TEXT_SIZE_KEY,
            PrefsConsts.Values.DEFAULT_TEXT_SIZE
        )
        with(holder) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            textSize.let {
                binding.tvTitle.setTextSize((textSize!!.toInt() + 2).toString())
                binding.tvContent.setTextSize(textSize)
            }
            binding.tvTime.text = TimeHelper.getTimeFormat(item.time, prefs)
            binding.root.setOnClickListener {
                onNoteClickListener?.invoke(item)
            }
        }
    }

}