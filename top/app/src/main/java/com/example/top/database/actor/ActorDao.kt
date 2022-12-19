package com.example.top.database.actor

import androidx.room.*

@Dao
interface ActorDao {
    @Query("SELECT * FROM Actor ORDER BY Actor.`order`") fun getAll(): List<Actor>

    @Insert fun insertAll(actors: List<Actor>)

    @Insert fun insert(actor: Actor)

    @Delete fun delete(actor: Actor)

    @Query("SELECT * FROM Actor WHERE Actor.id = :id") fun get(id: Long): Actor

    @Update fun update(actor: Actor)
}