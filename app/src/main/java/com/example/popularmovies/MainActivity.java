package com.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.popularmovies.DataBase.Movie;
import com.example.popularmovies.DataBase.MoviesDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public LinkedHashMap<Integer, String> movies = new LinkedHashMap<Integer, String>();
    private MoviesAdapter.ListItemClickListener listener;
    private String BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private String POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    private String API_KEY = "fa000fe6accc8dfec66fd512859b4b60";
    private RecyclerView movies_rv;
    private MoviesAdapter movies_Adapter;
    MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        movies_rv = findViewById(R.id.movies_rv);

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreference.registerOnSharedPreferenceChangeListener(this);
        changeUrlBasedOnSharedPreference(sharedPreference);
        moviesDatabase = MoviesDatabase.getInstance(getApplicationContext());

        if (BASE_URL.equals(getString(R.string.sort_favorite)) && moviesDatabase.movieDao().loadFavoriteMovies() != null) {
            List favoriteMovies = moviesDatabase.movieDao().loadFavoriteMovies();

            for (int i = 0; i < favoriteMovies.size(); i++) {
                Movie movie = (Movie) favoriteMovies.get(i);
                movies.put(Integer.valueOf(movie.getId()), movie.getMoviePoster());
            }


        } else {
            GetMovies getMovies = new GetMovies();
            getMovies.execute(BASE_URL + "?api_key=" + API_KEY);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        movies_rv.setLayoutManager(gridLayoutManager);

        movies_rv.setHasFixedSize(true);

        movies_Adapter = new MoviesAdapter(getBaseContext(), movies, this);

        if (BASE_URL.equals(getString(R.string.sort_favorite)) && moviesDatabase.movieDao().loadFavoriteMovies() != null) {
            movies_rv.setAdapter(movies_Adapter);
        }
    }

    private void changeUrlBasedOnSharedPreference(SharedPreferences sharedPreference) {
        if (sharedPreference.getString("sort_list", getString(R.string.sort_popularity)).equals(getString(R.string.sort_popularity))) {
            BASE_URL = "https://api.themoviedb.org/3/movie/popular";
        }
        if (sharedPreference.getString("sort_list", getString(R.string.sort_popularity)).equals(getString(R.string.sort_rating))) {
            BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
        }
        if (sharedPreference.getString("sort_list", getString(R.string.sort_popularity)).equals(getString(R.string.sort_favorite))) {
            BASE_URL = getString(R.string.sort_favorite);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort) {
            Intent startSettingActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, DetailsActivity.class);
        String id = String.valueOf(movies.keySet().toArray()[clickedItemIndex]);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.finish();
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }

    public class GetMovies extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            URL url = null;
            URLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                url = new URL(urls[0]);
                urlConnection = url.openConnection();
                inputStream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String ln;
                StringBuffer buffer = new StringBuffer();

                while ((ln = bufferedReader.readLine()) != null) {
                    buffer.append(ln + "\n");
                }

                JSONObject jsonObject = new JSONObject(buffer.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject movie;
                for (int i = 0; i < jsonArray.length(); i++) {
                    movie = jsonArray.getJSONObject(i);
                    movies.put(movie.getInt("id"), POSTER_BASE_URL + "/w220_and_h330_face" + movie.getString("poster_path"));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            movies_rv = findViewById(R.id.movies_rv);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL, false);


            try {
                movies_rv.setAdapter(movies_Adapter);

//
            } catch (NullPointerException e) {

            }
        }

    }
}

