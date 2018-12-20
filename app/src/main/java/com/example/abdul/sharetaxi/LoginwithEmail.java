package com.example.abdul.sharetaxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.FileObserver;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginwithemail);

        progressDialog = new ProgressDialog(LoginwithEmail.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        this.Riderbt= findViewById(R.id.Rider);
        LoginDriverbt = findViewById(R.id.LoginDriverbt);

        final EditText        Emailtext1 =(EditText) findViewById(R.id.EMAIL);
        final EditText    passwordtext1 =(EditText) findViewById(R.id.PASS);
        TextView Passwordrecovery = findViewById(R.id.PasswordRecoveryView);
        mauth=FirebaseAuth.getInstance();



        LoginDriverbt.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Log.e(TAG,"CALLING SIGNIN");


                email = Emailtext1.getText().toString();
                password = passwordtext1.getText().toString();
                Catagory="Driver";
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginwithEmail.this, "First Enter Email and password", Toast.LENGTH_LONG).show();
                }
                else {
                    Signin();
                    progressDialog.setMessage("Login In");
                    progressDialog.show();



                }

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




        try {
            this.Riderbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = Emailtext1.getText().toString();
                    password = passwordtext1.getText().toString();
                    Catagory="Rider";
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
if(email.isEmpty())
{
    Emailtext1.setError("Enater valid Email");
}
                        if(password.isEmpty())
                        {
                            passwordtext1.setError("Enater your Password");
                        }

                        Toast.makeText(LoginwithEmail.this, "Enter you email and Password", Toast.LENGTH_LONG).show();

                    } else {

                        progressDialog.setMessage("Loging In");
                        progressDialog.show();

                        Signin();
                    }

                }
            });


        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
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

    private void Signin() {

        mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginwithEmail.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Checkemailverification();
                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginwithEmail.this, MapScreen.class);
                    intent.putExtra("desc", Catagory);
                    startActivity(intent);

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
        if (mauth.getCurrentUser().isEmailVerified()) {

            Log.e(TAG, "Email Verified");
            Log.e(TAG, "Driver SUCCESFULY  LoginwithEmail");


        } else {
            Log.e(TAG, "Email not verified");
            Deletedata();



        }
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




