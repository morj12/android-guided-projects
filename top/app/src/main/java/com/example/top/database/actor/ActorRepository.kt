package com.example.top.database.actor

object ActorRepository {

    private lateinit var actorDao: ActorDao

    fun setDao(dao: ActorDao) {
        if (!ActorRepository::actorDao.isInitialized) actorDao = dao
    }

    fun getActor(id: Long) = actorDao.get(id)

    fun getAll() = actorDao.getAll()

    fun addActor(actor: Actor) = actorDao.insert(actor)

    fun addAll(actors: List<Actor>) = actorDao.insertAll(actors)

    fun updateActor(actor: Actor) = actorDao.update(actor)

    fun delete(actor: Actor) = actorDao.delete(actor)
}