package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.DataBase.Movie;
import com.example.popularmovies.DataBase.MoviesDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

public class DetailsActivity extends AppCompatActivity implements VideosAdapter.ListItemClickListener {
    public LinkedHashMap<Integer, String> videos = new LinkedHashMap<Integer, String>();
    public Movie movie;

    public String id;
    public String title;
    public String overView;
    public String releaseDate;
    public String posterPath;
    public Double ratings;
    private String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private String API_KEY = "API_KEY";
    private TextView title_tv;
    private TextView overview_tv;
    private TextView release_date_tv;
    private ImageView poster_iv;
    private TextView rating_tv;
    private RecyclerView videos_rv;
    private VideosAdapter videosAdapter;

    private MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title_tv = findViewById(R.id.movie_title_tv);
        overview_tv = findViewById(R.id.movie_plot_tv);
        release_date_tv = findViewById(R.id.movie_release_date_tv);
        rating_tv = findViewById(R.id.movie_rating_tv);
        poster_iv = findViewById(R.id.movie_poster_iv);

        moviesDatabase = MoviesDatabase.getInstance(getApplicationContext());
        movie = null;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            movie = intent.getParcelableExtra("movie");
        }

        id = movie.getId();
        title = movie.getTitle();
        releaseDate = movie.getReleaseDate();
        ratings = movie.getVoteAverage();
        overView = movie.getPlotSynopsis();
        posterPath = movie.getMoviePoster();

        //FetchMovie fetchMovie = new FetchMovie();
        //fetchMovie.execute(id);
        movieUI(movie);

        FetchMoviesVideos fetchMoviesVideos = new FetchMoviesVideos();
        fetchMoviesVideos.execute(movie.getId());


        videos_rv = findViewById(R.id.trailers_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);

        videos_rv.setLayoutManager(gridLayoutManager);
        videos_rv.setHasFixedSize(true);
        videosAdapter = new VideosAdapter(getBaseContext(), videos, this);
    }

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public void favoriteMovie(View view) {
        Movie movie = new Movie(id, title, posterPath, releaseDate, ratings, overView, "true");
        try {
            moviesDatabase.movieDao().insertMovie(movie);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();


        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onListItemClick(int clickedItemIndex) {
        watchYoutubeVideo(this, videos.get(clickedItemIndex));

    }

    private void movieUI(Movie movie) {


        title_tv.setText(title);
        overview_tv.setText(overView);
        release_date_tv.setText("Release date : " + releaseDate);
        rating_tv.setText("Rating : " + ratings);
        Picasso.with(getBaseContext()).load("https://image.tmdb.org/t/p/w185" + posterPath)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(poster_iv, new Callback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Toast.makeText(getBaseContext(), posterPath, Toast.LENGTH_LONG).show();
                    }
                });

    }

    protected class FetchMoviesVideos extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject jsonObject = null;
            String urlString = BASE_URL + strings[0] + "/videos?api_key=" + API_KEY;
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                InputStream is = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuffer stringBuffer = new StringBuffer();
                String ln;
                while ((ln = bufferedReader.readLine()) != null) {
                    stringBuffer.append(ln + "\n");
                }

                jsonObject = new JSONObject(stringBuffer.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                JSONObject video;
                for (int i = 0; i < jsonArray.length(); i++) {
                    video = jsonArray.getJSONObject(i);
                    videos.put(i, video.getString("key"));
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

        protected void onPostExecute(Void result) {
            videos_rv = findViewById(R.id.trailers_rv);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL, false);


            try {
                videos_rv.setAdapter(videosAdapter);

//
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }
    }

}
