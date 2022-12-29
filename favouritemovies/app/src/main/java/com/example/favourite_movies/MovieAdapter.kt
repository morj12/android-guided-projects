package com.example.favourite_movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.favourite_movies.databinding.MovieItemBinding
import com.example.favourite_movies.model.Movie

class MovieAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var itemsList = mutableListOf<Movie>()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MovieItemBinding.bind(itemView)

        fun setOnClickListener(movie: Movie) {
            binding.root.setOnClickListener { listener.onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = itemsList[position]
        holder.binding.movie = movie
        holder.setOnClickListener(movie)
    }

    override fun getItemCount() = itemsList.size

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }

    fun setMovieList(movieList: MutableList<Movie>) {
        this.itemsList = movieList
        notifyDataSetChanged()
    }
}