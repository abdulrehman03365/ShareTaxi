package com.example.abdul.sharetaxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

public class LoginwithPhone extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    public String Code="";
    private FirebaseAuth mAuth;
    public  String  mverificationId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwith_phone);

    }
}
