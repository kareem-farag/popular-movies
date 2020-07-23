package com.example.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.viewHolder> {

    final private ListItemClickListener mOnClickListener;
    private Map<Integer, String> moviesMap;
    private Context context;

    public MoviesAdapter(Context c, Map<Integer, String> movies, ListItemClickListener listener) {
        moviesMap = movies;
        context = c;
        mOnClickListener = listener;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int mhlayout = R.layout.movie_holder;

        View view = layoutInflater.inflate(mhlayout, viewGroup, false);
        return new viewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int position) {

        //   String posterPath = moviesMap.get(position);
        final String posterPath = (new ArrayList<String>(moviesMap.values())).get(position);

        Picasso.with(context).load(posterPath)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(viewHolder.movie_iv, new Callback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Toast.makeText(context, posterPath, Toast.LENGTH_LONG).show();
                    }
                });


        // Movie movie = JsonUtils.parseMovieJson(i);

    }

    @Override
    public int getItemCount() {
        if (moviesMap == null) {
            return 0;
        } else {
            return moviesMap.size();
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movie_iv;

        public viewHolder(@NonNull View itemView, ListItemClickListener listener) {
            super(itemView);
            movie_iv = itemView.findViewById(R.id.movie_iv);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);
        }
    }
}
