package com.example.geofence.db

import androidx.room.*


@Dao
abstract class BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(obj: T): Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: List<T>?): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract fun update(obj: T)

    /**
     * Update an array of objects from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract fun update(obj: List<T>?)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract fun delete(obj: T)

    @Transaction
    open suspend fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

}