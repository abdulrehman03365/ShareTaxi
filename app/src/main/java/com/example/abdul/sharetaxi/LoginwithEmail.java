package com.example.abdul.sharetaxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginwithEmail extends AppCompatActivity  {
    public String Catagory,email ;
    public String password;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    DatabaseReference db;
    ProgressDialog progressDialog;
    FirebaseAuth mauth;
    Button  Riderbt,LoginDriverbt;
    private String TAG = "iamtag";
    public String description ="";
    private String Uid;
    private boolean IsRider;
    private  boolean IsDriver;
    private EditText Emailtext1,passwordtext1;
    private boolean LoginSuccess =false;
    private boolean EmailVerified=false;
    private String DbCatagory ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginwithemail);

        progressDialog = new ProgressDialog(LoginwithEmail.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        this.Riderbt= findViewById(R.id.Rider);
        LoginDriverbt = findViewById(R.id.LoginDriverbt);

        Emailtext1 =(EditText) findViewById(R.id.EMAIL);
        passwordtext1 =(EditText) findViewById(R.id.PASS);
        TextView Passwordrecovery = findViewById(R.id.PasswordRecoveryView);
        mauth=FirebaseAuth.getInstance();

        this.Riderbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginRider();

            }
        });


        LoginDriverbt.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                LoginDriver();
            }
        });



        Passwordrecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Emailtext1.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginwithEmail.this, "First  Email", Toast.LENGTH_LONG).show();
                    if(email.isEmpty())
                    {
                        Emailtext1.setError("Enter Valid Email");
                    }
                    if(password.isEmpty())
                    {
                        passwordtext1.setError("Enter Your Password");
                    }




                } else {

                    sendverificationEmail();

                }
            }
        });






    }

    private void LoginRider() {
        email = Emailtext1.getText().toString();
        password = passwordtext1.getText().toString();
        Catagory = "Rider";
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            if (email.isEmpty()) {
                Emailtext1.setError("Enater valid Email");
            }
            if (password.isEmpty()) {
                passwordtext1.setError("Enater your Password");
            }

            Toast.makeText(LoginwithEmail.this, "Enter you email and Password", Toast.LENGTH_LONG).show();

        }

        else


            {

            progressDialog.setMessage("Loging In");
            progressDialog.show();


                SigninRider();







        }
    }

    private void SigninRider() {



        mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginwithEmail.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // progressDialog.dismiss();
                    LoginSuccess=true;
                    Log.i(TAG,"Is Login success ="+ String.valueOf(LoginSuccess) );
                    Checkemailverification();

                }


                else

                {
                    progressDialog.dismiss();
                    Log.e(TAG, String.valueOf(task.getException()));

                    Toast.makeText(LoginwithEmail.this, String.valueOf(task.getException()), Toast.LENGTH_LONG).show();




                }
            }
        });

    }


    private void CheckIsRider() {



        Log.i("iamtag"," Inside Checking + Is " + Catagory);

        firebaseUser=mauth.getCurrentUser();
        Uid=firebaseUser.getUid();

        db = FirebaseDatabase.getInstance().getReference("Rider");
        db.child(Uid).child("Catagory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             //  DbCatagory =(String) dataSnapshot.getValue();
                Log.i("iamtag"," Inside Checking + Is " + DbCatagory);

                if ( dataSnapshot.getValue()==null)
                {

                    Log.i("iamtag"," Retrieved value of Is" + Catagory + String.valueOf(dataSnapshot.getValue()));
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialog = new  AlertDialog.Builder(LoginwithEmail.this).setTitle("Sorry")
                            .setMessage("You are Not Registerd as " + Catagory);
                    alertDialog.show();

                }
                else if( dataSnapshot.getValue()!=null)
                {
                    IsRider=true;
                    Log.i("iamtag"," Retrieved value of Is" + Catagory + String.valueOf(dataSnapshot.getValue()));
                    Log.i(TAG,"Is rider"+ String.valueOf(IsRider));

                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginwithEmail.this, RiderActivity.class);
                    //intent.putExtra("Catagory",Catagory);
                    startActivity(intent);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }


    private void LoginDriver() {
        Log.e(TAG,"Signing in Driver");


        email = Emailtext1.getText().toString();
        password = passwordtext1.getText().toString();
        Catagory="Driver";
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginwithEmail.this, "First Enter Email and password", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else {
            SigninDriver();

            progressDialog.setMessage("Login In");
            progressDialog.show();



        }
    }

    private void sendverificationEmail() {
        mauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(LoginwithEmail.this, "Recovery Email sent", Toast.LENGTH_LONG).show();
                progressDialog.setMessage("Sending Recovery Email");
                progressDialog.show();


            }
        });


    }

    private void SigninDriver() {

        mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginwithEmail.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // progressDialog.dismiss();

                    // progressDialog.dismiss();
                   LoginSuccess=true;
                   Checkemailverification();
                  /*  if(IsDriver!=false) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginwithEmail.this, DriverActivity.class);
                        intent.putExtra("catagory","Driver");
                        startActivity(intent);


                    }
                    else
                    {

                        mauth.signOut();
                    }*/

                    //Check Weather User tring to login  IS Driver


                    // Toast.makeText(LoginwithEmail.this, "Driver Logedin", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog.dismiss();
                    Log.e(TAG, String.valueOf(task.getException()));

                    Toast.makeText(LoginwithEmail.this, String.valueOf(task.getException()), Toast.LENGTH_LONG).show();




                }
            }
        });


    }




    private void Checkemailverification() {
        if (mauth.getCurrentUser().isEmailVerified()==true)
        {

            Log.e(TAG, "Email Verified");
            EmailVerified=true;
            Log.i(TAG,"Email Verified ="+ String.valueOf(EmailVerified) );
         if(Catagory=="Rider") {
             CheckIsRider();
         }
         else
         {
             CheckIsDriver();
         }
        }
        else if (mauth.getCurrentUser().isEmailVerified()==true)
        {
            EmailVerified=false;
            Log.e(TAG, "Email not verified");
            AlertDialog.Builder alertDialog = new  AlertDialog.Builder(LoginwithEmail.this).setTitle("Email verification Error")
                    .setMessage("First Verify Your Email " );
            alertDialog.show();

            Deletedata();



        }
    }

    private void CheckIsDriver() {




        Log.i("iamtag"," Inside Checking + Is " + Catagory);

        firebaseUser=mauth.getCurrentUser();
        Uid=firebaseUser.getUid();

        db = FirebaseDatabase.getInstance().getReference("Driver");
        db.child(Uid).child("Catagory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //DbCatagory= (String) dataSnapshot.getValue();
                if ( dataSnapshot.getValue()==null)
                {
                    IsDriver=false;
                    Log.i("iamtag"," Retrieved value of Is" + Catagory + String.valueOf(dataSnapshot.getValue()));
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialog = new  AlertDialog.Builder(LoginwithEmail.this).setTitle("Sorry")
                            .setMessage("You are Not Registerd as " + Catagory);
                    alertDialog.show();

                }
                else if(dataSnapshot.getValue()!=null)
                {
                    IsDriver=true;
                    Log.i("iamtag"," Retrieved value of Is" + Catagory + String.valueOf(dataSnapshot.getValue()));
                    Log.i(TAG,"Is rider"+ String.valueOf(IsRider));

                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginwithEmail.this, DriverActivity.class);
                    //intent.putExtra("Catagory",Catagory);
                    startActivity(intent);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void Deletedata() {
        db=FirebaseDatabase.getInstance().getReference(Catagory);
        mauth=FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();
        Uid=firebaseUser.getUid();

        db.child(Uid).child("Phonenumber").setValue(null);
        db.child(Uid).child("Email").setValue(null);
        db.child(Uid).child("Password").setValue(null);
        db.child(Uid).child("Name").setValue(null);
        mauth.signOut();

    }

}

