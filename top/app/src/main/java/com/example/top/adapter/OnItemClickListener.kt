package com.example.top.adapter

import com.example.top.database.actor.Actor

interface OnItemClickListener {
    fun onItemClick(actor: Actor)
    fun onLongItemClick(actor: Actor)
}