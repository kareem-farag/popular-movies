package com.example.popularmovies.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadAllMovies();

    @Query("SELECT * FROM movie where favorite = 'true' ")
    List<Movie> loadFavoriteMovies();

    @Query("SELECT * FROM movie WHERE id=:id")
    Movie getMovieById(String id);

    @Query("SELECT COUNT(id) FROM movie WHERE id=:id")
    int checkMovieIsFavorite(String id);


    @Insert
    void insertMovie(Movie movie);

    @Update
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("DELETE FROM movie WHERE id = :id")
    void deleteById(String id);

}
