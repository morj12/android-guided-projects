package com.example.retrofitdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitdemo.R
import com.example.retrofitdemo.databinding.AlbumItemBinding
import com.example.retrofitdemo.model.Album

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var items = mutableListOf<Album>()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = AlbumItemBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = items[position]
        holder.binding.userId.text = album.userId.toString()
        holder.binding.albumId.text = album.id.toString()
        holder.binding.albumTitle.text = album.title
    }

    fun setList(list: MutableList<Album>) {
        this.items = list
    }

    override fun getItemCount() = items.size
}