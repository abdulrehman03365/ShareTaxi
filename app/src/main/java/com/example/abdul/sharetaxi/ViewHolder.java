package com.example.abdul.sharetaxi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.zip.Inflater;


public class ViewHolder extends RecyclerView.ViewHolder {
TextView From,Dest,Date,Time,Cost;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
           Date=itemView.findViewById(R.id.Datetxt);
            Time=itemView.findViewById(R.id.timetxt);
            Cost=itemView.findViewById(R.id.Costtxt);
            From=itemView.findViewById(R.id.fromtxt);
            Dest=itemView.findViewById(R.id.DestTxt);

    }

}
