package com.example.favourite_movies.model

import android.content.Context

internal object BasicData {

    fun load(context: Context) {
        val genreDao = MoviesDatabase.getDatabase(context).genreDao()
        val movieDao = MoviesDatabase.getDatabase(context).movieDao()

        genreDao.insert(Genre(_name = "Comedy"))
        genreDao.insert(Genre(_name = "Romance"))
        genreDao.insert(Genre(_name = "Drama"))

        movieDao.insert(Movie(_name = "Bad Boys for Life", _description = "The Bad Boys Mike Lowrey and Marcus Burnett are back together for one last ride in the highly anticipated Bad Boys for Life.", _genreId = 1))
        movieDao.insert(Movie(_name = "Parasite", _description = "All unemployed, Ki-taek and his family take peculiar interest in the wealthy and glamorous Parks, as they ingratiate themselves into their lives and get entangled in an unexpected incident.", _genreId =  1))
        movieDao.insert(Movie(_name = "Once Upon a Time... in Hollywood", _description = "A faded television actor and his stunt double strive to achieve fame and success in the film industry during the final years of Hollywood's Golden Age in 1969 Los Angeles.", _genreId = 1))
        movieDao.insert(Movie(_name = "You", _description = "A dangerously charming, intensely obsessive young man goes to extreme measures to insert himself into the lives of those he is transfixed by.", _genreId = 2))
        movieDao.insert(Movie(_name = "Little Women", _description = "Jo March reflects back and forth on her life, telling the beloved story of the March sisters - four young women each determined to live life on their own terms.", _genreId = 2))
        movieDao.insert(Movie(_name = "Vikings", _description = "Vikings transports us to the brutal and mysterious world of Ragnar Lothbrok, a Viking warrior and farmer who yearns to explore - and raid - the distant shores across the ocean.", _genreId = 2))
        movieDao.insert(Movie(_name = "1917", _description = "Two young British soldiers during the First World War are given an impossible mission: deliver a message deep in enemy territory that will stop 1,600 men, and one of the soldiers' brothers, from walking straight into a deadly trap.", _genreId = 3))
        movieDao.insert(Movie(_name = "The Witcher", _description = "Geralt of Rivia, a solitary monster hunter, struggles to find his place in a world where people often prove more wicked than beasts.", _genreId = 3))
        movieDao.insert(Movie(_name = "The Outsider", _description = "Investigators are confounded over an unspeakable crime that's been committed.", _genreId = 3))
    }

}