package com.example.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.DataBase.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    List<Review> reviews = new ArrayList<Review>();
    Context context;

    public ReviewsAdapter(Context c, List<Review> r) {
        context = c;
        reviews = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context c = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);
        int reviewLayout = R.layout.review_holder;

        View view = inflater.inflate(reviewLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Review review = reviews.get(i);
        viewHolder.reviewAuthor.setText(review.getAuther());
        viewHolder.reviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        } else {
            return reviews.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.review_auther_tv);
            reviewContent = itemView.findViewById(R.id.review_content_tv);
        }
    }


}
