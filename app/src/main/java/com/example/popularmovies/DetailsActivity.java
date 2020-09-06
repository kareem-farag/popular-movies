package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.DataBase.Movie;
import com.example.popularmovies.DataBase.MoviesDatabase;
import com.example.popularmovies.DataBase.Review;
import com.example.popularmovies.DataBase.Trailer;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    private String API_KEY = "fa000fe6accc8dfec66fd512859b4b60";
    private TextView title_tv;
    private TextView overview_tv;
    private TextView release_date_tv;
    private ImageView poster_iv;
    private TextView rating_tv;
    private RecyclerView videos_rv;
    private RecyclerView reviews_rv;
    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;
    private Button favoritButton;
    private MoviesDatabase moviesDatabase;

    private List<Trailer> trailers = new ArrayList<Trailer>();
    private List<Review> reviews = new ArrayList<Review>();


    public static void watchYoutubeVideo(Context context, Trailer trailer) {
        String id = trailer.getYoutubeId();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        title_tv = findViewById(R.id.movie_title_tv);
        overview_tv = findViewById(R.id.movie_plot_tv);
        release_date_tv = findViewById(R.id.movie_release_date_tv);
        rating_tv = findViewById(R.id.movie_rating_tv);
        poster_iv = findViewById(R.id.movie_poster_iv);
        favoritButton = findViewById(R.id.favorite_button_bv);

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


        int isFavorite = moviesDatabase.movieDao().checkMovieIsFavorite(id);
        if (isFavorite > 0) {
            favoritButton.setText("Unfavorite");
        }

        movieUI(movie);

        FetchMoviesVideos fetchMoviesVideos = new FetchMoviesVideos();
        fetchMoviesVideos.execute(movie.getId());

        FetchMoviesReviews fetchMoviesReviews = new FetchMoviesReviews();
        fetchMoviesReviews.execute(movie.getId());

        videos_rv = findViewById(R.id.trailers_rv);
        reviews_rv = findViewById(R.id.reviews_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        videos_rv.setLayoutManager(linearLayoutManager);
        videos_rv.setHasFixedSize(true);
        videosAdapter = new VideosAdapter(getBaseContext(), trailers, this);

        GridLayoutManager reviewGridLayoutManager = new GridLayoutManager(this, 1);
        reviews_rv.setLayoutManager(reviewGridLayoutManager);
        reviews_rv.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter(getBaseContext(), reviews);
    }

    public void favoriteMovie(View view) {
        int isFavorite = moviesDatabase.movieDao().checkMovieIsFavorite(id);
        if (isFavorite == 0) {
            favoritButton.setText("Unfavorite");
            Movie movie = new Movie(id, title, posterPath, releaseDate, ratings, overView, "true");

            try {
                moviesDatabase.movieDao().insertMovie(movie);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            favoritButton.setText("Favorite");
            moviesDatabase.movieDao().deleteById(id);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onListItemClick(int clickedItemIndex) {
        watchYoutubeVideo(this, trailers.get(clickedItemIndex));

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

                    Trailer trailer = new Trailer(video.getString("id"), video.getString("key"), video.getString("name"));
                    trailers.add(trailer);
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

    protected class FetchMoviesReviews extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject jsonObject = null;
            String urlString = BASE_URL + strings[0] + "/reviews?api_key=" + API_KEY;
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
                JSONObject reviewObject;

                for (int i = 0; i < jsonArray.length(); i++) {
                    reviewObject = jsonArray.getJSONObject(i);
                    Review review = new Review(reviewObject.getString("id"), reviewObject.getString("author"), reviewObject.getString("content"));
                    reviews.add(review);


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
            reviews_rv = findViewById(R.id.reviews_rv);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL, false);
            try {
                reviews_rv.setAdapter(reviewsAdapter);
//
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }
    }

}
