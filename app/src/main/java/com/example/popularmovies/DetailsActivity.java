package com.example.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DetailsActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title_tv = findViewById(R.id.movie_title_tv);
        overview_tv = findViewById(R.id.movie_plot_tv);
        release_date_tv = findViewById(R.id.movie_release_date_tv);
        rating_tv = findViewById(R.id.movie_rating_tv);
        poster_iv = findViewById(R.id.movie_poster_iv);


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            id = intent.getExtras().getString("id");
        }
        FetchMovie fetchMovie = new FetchMovie();
        fetchMovie.execute(id);


    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    protected class FetchMovie extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            String urlString = BASE_URL + strings[0] + "?api_key=" + API_KEY;
            JSONObject jsonObject = null;
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String ln;
                while ((ln = bufferedReader.readLine()) != null) {
                    stringBuffer.append(ln + "\n");
                }
                jsonObject = new JSONObject(stringBuffer.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                title = jsonObject.getString("original_title");
                releaseDate = jsonObject.getString("release_date");
                ratings = jsonObject.getDouble("vote_average");
                overView = jsonObject.getString("overview");
                posterPath = "https://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");

                title_tv.setText(title);
                overview_tv.setText(overView);
                release_date_tv.setText("Release date : " + releaseDate);
                rating_tv.setText("Rating : " + ratings);
                Picasso.with(getBaseContext()).load(posterPath)
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

                //poster_iv = findViewById(R.id.movie_poster_iv);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
