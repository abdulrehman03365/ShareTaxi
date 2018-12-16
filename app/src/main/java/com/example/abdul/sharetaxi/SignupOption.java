package com.example.abdul.sharetaxi;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SignupOption extends AppCompatActivity {
    String Tag="iamtag",Catagory="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_option);
       try{
        Button Riderbt = findViewById(R.id.Riderbt);
        Button Driverbt = findViewById(R.id.Driverbt);

           Riderbt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 try {
                     Catagory = "Rider";
                     Intent intent = new Intent(SignupOption.this, Signup.class);
                     intent.putExtra("Catagory", Catagory);
                     startActivity(intent);

                 }
                 catch (Exception e)
                 {

                 }
               }
           });

           Driverbt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Catagory = "Driver";
                   Intent intent = new Intent(SignupOption.this, Signup.class);
                   intent.putExtra("Catagory", Catagory);
                   startActivity(intent);

               }
           });
       }
       catch(Exception e)
        {
          Log.e(Tag,e.getMessage());
        }
    }
}
