package com.example.abdul.sharetaxi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseRecyclerAdapter adapter;
    FirebaseRecyclerOptions<HistoryModel> options;

    Query query;
    private View view;
    RecyclerView historyRecycler;
    private HistoryModel mHistoryModel;
    private View histDesView;
    private String TAG="iamtag";
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String Userid;
    private ArrayList<String> marraylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.Historytoolbar);
        toolbar.setTitle("Trip History");
        historyRecycler = findViewById(R.id.HistoryRecycler);
        marraylist = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



         getdatabaseref();


        query= FirebaseDatabase.getInstance()
                .getReference("TripHistory").child(Userid);



        options= new FirebaseRecyclerOptions.Builder<HistoryModel>()
                .setQuery(query,HistoryModel.class).build();


          adapter = new FirebaseRecyclerAdapter<HistoryModel,ViewHolder>
                  (options) {








            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                histDesView= LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.historydesign,viewGroup,false);

                Log.i(TAG,"Creating View Holder");
                return new ViewHolder(histDesView);

            }


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryModel model) {
                Log.i(TAG,"Binding the data");
                Log.i(TAG,"Key Of History "+String.valueOf( model.Key));

                holder.Dest.setText(model.Dest);
                holder.From.setText(model.From);
                holder.Time.setText(model.Time);
                holder.Date.setText(model.Date);
                holder.Cost.setText(model.Cost);



            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        historyRecycler.setLayoutManager(linearLayoutManager);
        historyRecycler.setAdapter(adapter);




    }



    private void getdatabaseref() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        Userid = firebaseUser.getUid();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

}

