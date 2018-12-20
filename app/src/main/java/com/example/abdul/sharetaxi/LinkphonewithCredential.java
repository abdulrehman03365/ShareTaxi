package com.example.abdul.sharetaxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LinkphonewithCredential extends AppCompatActivity {
Intent intent;

String tag="iamtag",Phonenumstring="",Code="";
EditText Authtext;
Button Verifybutton,Submit;
FirebaseAuth mAuth;
PhoneAuthCredential phoneAuthCredential;
ProgressBar Progressbar;
PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
String mverificationId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_linkphonewith_credential);
        super.onCreate(savedInstanceState);
    Authtext= findViewById(R.id.authtxt);
    Verifybutton = findViewById(R.id.submitbt);
    Progressbar = findViewById(R.id.progressBar11);
    Submit= findViewById(R.id.submitbt);
    checkforSignupscenerio();
    initvercallback();
    Progressbar.setVisibility(View.VISIBLE);

    PhoneAuthProvider.getInstance().verifyPhoneNumber(Phonenumstring,60, TimeUnit.SECONDS,LinkphonewithCredential.this,mcallback);

Submit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mAuth = FirebaseAuth.getInstance();
        Code=Authtext.getText().toString();
        if(Code!=" ") {
            phoneAuthCredential = PhoneAuthProvider.getCredential(mverificationId, Code);
        }
            mAuth.getCurrentUser().linkWithCredential(phoneAuthCredential);
        Log.e(tag,"Succesfuly linked with credential");
        Toast.makeText(LinkphonewithCredential.this, "Succesfully Signup.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LinkphonewithCredential.this,MainActivity.class);
        startActivity(intent);
    }
});
    }

    private void initvercallback() {
    mcallback= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
        Log.e(tag,e.getMessage());
            Toast.makeText(LinkphonewithCredential.this, String.valueOf(e.getMessage()), Toast.LENGTH_SHORT).show();
            Progressbar.setVisibility(View.GONE);

        }

        @Override
        public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            mverificationId=verificationid;
            super.onCodeSent(verificationid, forceResendingToken);
            Verifybutton.setVisibility(View.GONE);
            Submit.setVisibility(View.VISIBLE);
        }
    };
    }


    private void checkforSignupscenerio() {
        intent= new Intent();
        Bundle extras;


        extras = getIntent().getExtras();
        if(extras!=null) {
            Phonenumstring = extras.getString("Phonenum");
            Log.e(tag, String.valueOf(Phonenumstring));







        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
