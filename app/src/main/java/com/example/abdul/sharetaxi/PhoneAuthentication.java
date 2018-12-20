package com.example.abdul.sharetaxi;

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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    public String Code="";
    private FirebaseAuth mAuth;
    public  String  mverificationId="";
    Intent intent;
    EditText Authtext;

    EditText editTextCarrierNumber;
    Bundle extras;
    Button Submitcode;
    private boolean Status=false;
    PhoneAuthCredential phoneAuthCredential;
    private String status;
    private String tag="iamtag";
    private String phonenumstring="";
    CountryCodePicker ccp;
    ProgressDialog progressDialog;
    TextView EnterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_authentication);

 initialzation();
        final Button Verifyphonebt= (Button) findViewById(R.id.Verifybt);
        final Button Submitcode= (Button) findViewById(R.id.submit);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        final EditText Authtext = findViewById(R.id.authtxt);
        initProgressDialogBox();
 EnterText = findViewById(R.id.EnterText);
        mAuth =FirebaseAuth.getInstance();
        Authtext.setVisibility(View.GONE);
        Submitcode.setVisibility(View.GONE);

        Verifyphonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumstring=ccp.getFormattedFullNumber();


                if(TextUtils.isEmpty(phonenumstring))
                {
                    Log.d(tag,"scenariocheck");
                    Toast.makeText(PhoneAuthentication.this, "First Enter Phonenumber", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(tag, "scenariocheck");
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumstring, 60, TimeUnit.SECONDS, PhoneAuthentication.this, mcallback);

                }


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

                editTextCarrierNumber.setVisibility(View.GONE);
                ccp.setVisibility(View.GONE);
                Verifyphonebt.setVisibility(View.GONE);
                progressDialog.dismiss();
                EnterText.setText("Enter the OTP sended to you");
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

    private void initProgressDialogBox() {
   progressDialog= new ProgressDialog(PhoneAuthentication.this);
   progressDialog.setMessage("Sending Otp Code");
    }

    private void initialzation() {
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
