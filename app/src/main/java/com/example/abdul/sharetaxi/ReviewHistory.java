package com.example.abdul.sharetaxi;

import android.app.DownloadManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ReviewHistory extends AppCompatActivity {
    RecyclerView reviewRecycler;
    FirebaseRecyclerAdapter adapter;
    FirebaseRecyclerOptions<Review> options;
    Query query;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String Userid;
    private View ReviewItemView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_history);

        reviewRecycler = findViewById(R.id.Reviewrecycler);
        getdatabaseref();

        query = FirebaseDatabase.getInstance()
                .getReference("Review").child(Userid);


        options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class).build();








adapter= new FirebaseRecyclerAdapter<Review, ReviewHolder>(options) {
    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ReviewItemView= LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.reviewlayout,viewGroup,false);

        return new ReviewHolder(ReviewItemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder,
                                    int position, @NonNull Review model) {

        holder.reviewText.setText(model.reviewst);
        holder.ratingBar.setRating(Float.parseFloat(model.ratingst));

    }
}       ;





    }

        private void getdatabaseref ()
        {
            auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();
            Userid = firebaseUser.getUid();

        }

    }

