package com.example.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.DataBase.Trailer;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    //    final private MoviesAdapter.ListItemClickListener mOnClickListener;
    final private ListItemClickListener vOnClickListener;
    private List<Trailer> trailers;
    private Context context;

    public VideosAdapter(Context c, List<Trailer> trailersList, ListItemClickListener listener) {
        trailers = trailersList;
        context = c;
        vOnClickListener = listener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int videoLayout = R.layout.trailer_holder;

        View view = layoutInflater.inflate(videoLayout, viewGroup, false);

        return new ViewHolder(view, vOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Trailer trailer = trailers.get(i);
        viewHolder.trailer_title_tv.setText(trailer.getTitle());

    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        } else {
            return trailers.size();
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView trailer_title_tv;

        public ViewHolder(@NonNull View itemView, ListItemClickListener listener) {
            super(itemView);
            trailer_title_tv = itemView.findViewById(R.id.trailer_title_tv);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            vOnClickListener.onListItemClick(position);
        }
    }
}
