package com.example.top.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.R
import com.example.top.database.artist.Artist
import com.example.top.databinding.ItemArtistBinding

class ArtistAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    private lateinit var context: Context
    var artistList = mutableListOf<Artist>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artistList[position]
        holder.setListener(artist)
        holder.binding.tvName.text = "${artist.name} ${artist.surname}"
        holder.binding.tvOrder.text = artist.order.toString()

        if (artist.photoUrl != "") {
            val options = RequestOptions().also {
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.centerCrop()
                it.placeholder(R.drawable.ic_satisfied)
            }

            Glide.with(context)
                .load(artist.photoUrl)
                .apply(options)
                .into(holder.binding.imgIcon)
        } else {
            holder.binding.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_account_box
                )
            )
        }
    }

    override fun getItemCount() = artistList.size

    fun add(artist: Artist) {
        if (!artistList.contains(artist)) {
            artistList.add(artist)
            notifyItemInserted(artistList.size - 1)
        }
    }

    fun remove(artist: Artist) {
        if (artistList.contains(artist)) {
            artistList.remove(artist)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemArtistBinding.bind(view)

        fun setListener(artist: Artist) {
            binding.root.setOnClickListener { listener.onItemClick(artist) }
            binding.root.setOnLongClickListener {
                listener.onLongItemClick(artist)
                return@setOnLongClickListener true
            }
        }
    }
}