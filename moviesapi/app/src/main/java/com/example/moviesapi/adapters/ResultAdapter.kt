package com.example.moviesapi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesapi.R
import com.example.moviesapi.databinding.ResultItemBinding
import com.example.moviesapi.model.Results

class ResultAdapter(val listener: OnClickListener) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var items = mutableListOf<Results>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ResultItemBinding.bind(view)

        fun setListener(movie: Results) {
            binding.root.setOnClickListener { listener.onItemClicked(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = items[position]

        holder.binding.tvTitle.text = result.title
        holder.binding.tvPopularity.text = result.popularity.toString()
        holder.setListener(result)
        val imgPath = context.getString(R.string.base_img_path) + result.poster_path
        Glide.with(context)
            .load(imgPath)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.binding.ivMovie)
    }

    override fun getItemCount() = items.size

    fun setList(list: MutableList<Results>) {
        items = list
    }

    interface OnClickListener {
        fun onItemClicked(item: Results)
    }

}