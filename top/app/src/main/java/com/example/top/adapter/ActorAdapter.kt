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
import com.example.top.database.actor.Actor
import com.example.top.databinding.ItemActorBinding

class ActorAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    private lateinit var context: Context
    var actorList = mutableListOf<Actor>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_actor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actor = actorList[position]
        holder.setListener(actor)
        holder.binding.tvName.text = "${actor.name} ${actor.surname}"
        holder.binding.tvOrder.text = actor.order.toString()

        if (actor.photoUrl != "") {
            val options = RequestOptions().also {
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.centerCrop()
                it.placeholder(R.drawable.ic_satisfied)
            }

            Glide.with(context)
                .load(actor.photoUrl)
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

    override fun getItemCount() = actorList.size

    fun add(actor: Actor) {
        if (!actorList.contains(actor)) {
            actorList.add(actor)
            notifyItemInserted(actorList.size - 1)
        }
    }

    fun remove(actor: Actor) {
        if (actorList.contains(actor)) {
            actorList.remove(actor)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemActorBinding.bind(view)

        fun setListener(actor: Actor) {
            binding.root.setOnClickListener { listener.onItemClick(actor) }
            binding.root.setOnLongClickListener {
                listener.onLongItemClick(actor)
                return@setOnLongClickListener true
            }
        }
    }
}