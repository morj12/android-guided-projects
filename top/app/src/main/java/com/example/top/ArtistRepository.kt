package com.example.top

object ArtistRepository {

    private lateinit var artistDao: ArtistDao

    fun setDao(dao: ArtistDao) {
        if (!::artistDao.isInitialized) artistDao = dao
    }

    fun getArtist(id: Long) = artistDao.get(id)

    fun getAll() = artistDao.getAll()

    fun addArtist(artist: Artist) = artistDao.insert(artist)

    fun updateArtist(artist: Artist) = artistDao.update(artist)

    fun delete(artist: Artist) = artistDao.delete(artist)
}