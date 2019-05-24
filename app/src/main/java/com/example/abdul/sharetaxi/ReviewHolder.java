package com.example.abdul.sharetaxi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewHolder extends RecyclerView.ViewHolder {
    RatingBar ratingBar;
    TextView reviewText;

    public ReviewHolder(@NonNull View itemView) {
        super(itemView);
        ratingBar = itemView.findViewById(R.id.reviewRatingBar);
        reviewText=itemView.findViewById(R.id.ReviewText);


    }
}
