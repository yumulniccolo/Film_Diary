package com.example.finalexer3grp2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFilm(Film film);

    @Query("SELECT * FROM films_table ORDER BY createdAt DESC")
    LiveData<List<Film>> getAllFilms();

    @Query("SELECT * FROM films_table ORDER BY title ASC")
    LiveData<List<Film>> getFilmsAZ();

    @Query("SELECT * FROM films_table ORDER BY updatedAt DESC")
    LiveData<List<Film>> getRecentlyModified();

    @Query("SELECT * FROM films_table ORDER BY createdAt DESC")
    LiveData<List<Film>> getLatestToOldest();

    @Delete
    void deleteFilm(Film film);

    @Update
    void updateFilm(Film film);

    @Query("SELECT * FROM films_table WHERE id = :id")
    Film getFilmById(int id);

    @Query("SELECT * FROM films_table WHERE title LIKE '%' || :query || '%'")
    LiveData<List<Film>> searchFilms(String query);

    @Query("SELECT * FROM films_table WHERE director LIKE '%' || :query || '%'")
    LiveData<List<Film>> searchByDirector(String query);

    @Query("DELETE FROM films_table WHERE id IN (:ids)")
    void deleteByIds(List<Integer> ids);
}
