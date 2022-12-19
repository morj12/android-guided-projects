package com.example.top.adapter

import com.example.top.database.artist.Artist

interface OnItemClickListener {
    fun onItemClick(artist: Artist)
    fun onLongItemClick(artist: Artist)
}