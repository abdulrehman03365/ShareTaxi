package com.example.abdul.sharetaxi;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    public String Code="";
    private FirebaseAuth mAuth;
  public  String  mverificationId="";
  Intent intent;
    EditText Authtext;

     EditText Phonenumbertxt;
     Bundle extras;
     Button Submitcode;
    private boolean Status=false;
    PhoneAuthCredential phoneAuthCredential;
    private String status;
    private String tag="iamlog";
    private String phonenumstring="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_authentication);


        final Button Verifyphonebt= (Button) findViewById(R.id.Verifybt);
        final Button Submitcode= (Button) findViewById(R.id.submit);
        Phonenumbertxt = findViewById(R.id.Phonenumlog);
        final EditText Authtext = findViewById(R.id.authtxt);

          mAuth =FirebaseAuth.getInstance();
          Authtext.setVisibility(View.GONE);

//checkforSignupscenerio();
  //        Log.d(tag,"scenariocheck");
            Verifyphonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  phonenumstring =Phonenumbertxt.getText().toString();
if(TextUtils.isEmpty(phonenumstring))
{
    Toast.makeText(PhoneAuthentication.this, "First Enter Phonenumber", Toast.LENGTH_SHORT).show();
}
else
    if(phonenumstring.length()<13)
    {
        Toast.makeText(PhoneAuthentication.this, "InvalidPhoneformat", Toast.LENGTH_SHORT).show();
    }
    else
PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumstring,60, TimeUnit.SECONDS,PhoneAuthentication.this,mcallback);



            }
        });
        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
               Toast.makeText(PhoneAuthentication.this,e.getMessage() , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mverificationId=verificationid;
                super.onCodeSent(verificationid, forceResendingToken);

                Phonenumbertxt.setVisibility(View.GONE);

                Verifyphonebt.setVisibility(View.GONE);
                Authtext.setVisibility(View.VISIBLE);
                 Submitcode.setVisibility(View.VISIBLE);

            }
        };
    Submitcode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Code=Authtext.getText().toString();
            if(Code!=" ") {

               phoneAuthCredential  = PhoneAuthProvider.getCredential(mverificationId, Code);
//check wheather its Signup authentication or Login Authentication

                IntentCheck();
            }







            }

    });
    }

    private void IntentCheck() {
        intent= new Intent();
        Bundle extras;


        extras = getIntent().getExtras();
        if(extras!=null) {
            //Login scenario
            status = extras.getString("Loginstatus");
            Log.e(tag, String.valueOf(status));
            signin();



        }
        else
        {
            //Signup sccenario

            //mAuth.getCurrentUser().linkWithCredential(phoneAuthCredential);
           // Intent intent = new Intent(PhoneAuthentication.this,MainActivity.class);

            //startActivity(intent);
        }
    }


    private void signin() {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(PhoneAuthentication.this, "Succesful Login", Toast.LENGTH_LONG).show();

                        //Toast.makeText(PhoneAuthentication.this, String.valueOf(j), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PhoneAuthentication.this,MapScreen.class);

                    startActivity(intent);

                    }
                    else
                        Log.e(tag,String.valueOf(task.getException()));
                Toast.makeText(PhoneAuthentication.this, String.valueOf(task.getException()), Toast.LENGTH_LONG).show();
            }
        });
    }

    void Checkstatus() {



    }


}
