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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageException;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity {
    private static String Uid = "" ;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    public String Code="";
    private FirebaseAuth mAuth;
    public  String  mverificationId="";
    Intent intent;
    EditText Authtext;
    String Catagory="";

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
    FirebaseUser firebaseUser;
    RadioButton RadioDriver,RadioRider;
    RadioGroup radioGroup;
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
         radioGroup= findViewById(R.id.RadioGroup);


       /* RadioDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        RadioRider.setOnClickListener(new);*/
        EnterText = findViewById(R.id.EnterText);
        mAuth =FirebaseAuth.getInstance();
        Authtext.setVisibility(View.GONE);
        Submitcode.setVisibility(View.GONE);



        Verifyphonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumstring=ccp.getFormattedFullNumber();


              /*phonenumstring="+923022448152";
              String testvercode ="123456";*/
                if(TextUtils.isEmpty(phonenumstring))
                {
                    Log.d(tag,"scenariocheck");
                    Toast.makeText(PhoneAuthentication.this, "First Enter Phonenumber", Toast.LENGTH_SHORT).show();
                }
                else if(editTextCarrierNumber.length()<11) {
                    Toast.makeText(PhoneAuthentication.this, "PhoneNum Must be 11dig", Toast.LENGTH_SHORT).show();

                }
                else
                {
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
                EnterText.setText("Enter the code sent to you");
                Authtext.setVisibility(View.VISIBLE);
                Submitcode.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.GONE);

            }
        };
        Submitcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Code=Authtext.getText().toString();
  if(Code==null)
  {

      Toast.makeText(PhoneAuthentication.this, String.valueOf("First Enter the Code"), Toast.LENGTH_LONG).show();

  }

                if(Code!=" ") {


                    phoneAuthCredential  = PhoneAuthProvider.getCredential(mverificationId, Code);
     //check wheather its Signup authentication or Login Authentication

                    IntentCheck();
                    progressDialog.setMessage("Logging In");
                    progressDialog.show();
                    signin();
                }







            }

        });
    }



    public void onRadioButtonClicked(View view) {
        RadioDriver= findViewById(R.id.radiodriver);
        RadioRider = findViewById(R.id.radiorider);
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radiodriver:
                if (checked) {
                    Catagory = "Driver";
                    Log.i(tag, "OnRadioButtonClick " + " " + Catagory);

                }
                break;
            case R.id.radiorider:
                if (checked) {
                    Catagory = "Rider";
                    Log.i(tag, "OnRadioButtonClick " + " " + Catagory);
                }
                break;
        }
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
        mAuth.signInWithCredential(phoneAuthCredential).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int errorCode = ((StorageException) e).getErrorCode();
                if(errorCode== StorageException.ERROR_UNKNOWN)
                {
                    Toast.makeText(PhoneAuthentication.this, String.valueOf("Unknown Error"), Toast.LENGTH_LONG).show();
                    }
                 if(errorCode==StorageException.ERROR_CANCELED);
                {
                    Toast.makeText(PhoneAuthentication.this, String.valueOf("User canceld the operation"), Toast.LENGTH_LONG).show();

                }
                if(errorCode==StorageException.ERROR_NOT_AUTHENTICATED);
                {
                    Toast.makeText(PhoneAuthentication.this, String.valueOf("User Not Authenticated"), Toast.LENGTH_LONG).show();

                }
                if(errorCode==StorageException.ERROR_OBJECT_NOT_FOUND)
                {
                    Toast.makeText(PhoneAuthentication.this, String.valueOf("Object Not Found"), Toast.LENGTH_LONG).show();

                }
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                 firebaseUser=mAuth.getCurrentUser();
                  Uid=firebaseUser.getUid();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference(Catagory);
                  db.child(Uid).child("Catagory").addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if( dataSnapshot.getValue()!=null)
                          {
                              Log.i(tag,"checking categry of login user cat ="+ String.valueOf(dataSnapshot.getValue()));
                              //Toast.makeText(PhoneAuthentication.this, String.valueOf(dataSnapshot.getValue()), Toast.LENGTH_LONG).show();

                              if("Driver"==String.valueOf(dataSnapshot.getValue()))
                              {
                                  progressDialog.dismiss();
                                  Intent intent = new Intent(PhoneAuthentication.this, DriverActivity.class);
                                  startActivity(intent);
                              }
                              else if("Rider"==String.valueOf(dataSnapshot.getValue()))
                              {
                                  progressDialog.dismiss();
                                  Intent intent = new Intent(PhoneAuthentication.this, RiderActivity.class);
                                  startActivity(intent);
                              }


                          }
                          else{
                              progressDialog.dismiss();
                              Toast.makeText(PhoneAuthentication.this, "SignUp you are not registerd", Toast.LENGTH_LONG).show();
                              Intent intent = new Intent(PhoneAuthentication.this,MainActivity.class);
                              startActivity(intent);

                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });


                  //  Toast.makeText(PhoneAuthentication.this, "Succesful Login", Toast.LENGTH_LONG).show();
                    // Log.e(tag,"Signin succesful");
                   // progressDialog.dismiss();

                 /*   Toast.makeText(PhoneAuthentication.this, String.valueOf(task.getException()), Toast.LENGTH_LONG).show();

                    */

                    //Toast.makeText(PhoneAuthentication.this, String.valueOf(j), Toast.LENGTH_LONG).show();



                }






            }
        });

    }

    void Checkstatus() {



    }


}
