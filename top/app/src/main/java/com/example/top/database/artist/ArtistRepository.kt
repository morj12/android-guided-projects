package com.example.top.database.artist

object ArtistRepository {

    private lateinit var artistDao: ArtistDao

    fun setDao(dao: ArtistDao) {
        if (!ArtistRepository::artistDao.isInitialized) artistDao = dao
    }

    fun getArtist(id: Long) = artistDao.get(id)

    fun getAll() = artistDao.getAll()

    fun addArtist(artist: Artist) = artistDao.insert(artist)

    fun updateArtist(artist: Artist) = artistDao.update(artist)

    fun delete(artist: Artist) = artistDao.delete(artist)
}