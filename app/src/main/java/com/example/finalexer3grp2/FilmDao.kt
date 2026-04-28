package com.example.finalexer3grp2

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: Film)

    @Query("SELECT * FROM films_table ORDER BY title ASC")
    fun getAllFilms(): Flow<List<Film>>

    @Delete
    suspend fun deleteFilm(film: Film)

    @Update
    suspend fun updateFilm(film: Film)

    @Query("SELECT * FROM films_table WHERE id = :id")
    suspend fun getFilmById(id: Int): Film?

    @Query("DELETE FROM films_table WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}