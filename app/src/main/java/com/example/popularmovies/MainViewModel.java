package com.example.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.popularmovies.DataBase.Movie;
import com.example.popularmovies.DataBase.MoviesDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {


    private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(this.getApplication());
        movies = moviesDatabase.movieDao().loadFavoriteMovies();

    }


    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

}
