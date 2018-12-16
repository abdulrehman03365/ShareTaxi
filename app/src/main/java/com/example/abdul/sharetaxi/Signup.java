package com.example.abdul.sharetaxi;

import android.app.Dialog;
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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.jar.Attributes;

public class Signup extends AppCompatActivity {
Intent intent;
String Catagory;
Dialog Emailverpopup;
Button Proceedbt;
public  String email="",Password="", Cnicnumber="";
    private FirebaseAuth mAuth;
    private String TAG ="iamtag";
    FirebaseUser firebaseUser ;
    public DatabaseReference db;
    private String Tag="iamtag";
TextView PasswordrecoveryView;
    Button Signupbt;
   EditText  Emailtext ,Passwordtext,Nametext,Phonenumbertext;
   ProgressBar progressBar;
     public String Email="",Phonenumber="",Name="";
    public Boolean Emptytextfields;

    private String Uid="";
    private String Phnumformat="^\\+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {





        setContentView(R.layout.activity_signup);
        Log.e(Tag,"signup layout parsed");


           Bundle extras;
           extras = getIntent().getExtras();
           if (extras != null) {
               Catagory = extras.getString("Catagory");
               Log.e(Tag, String.valueOf(Catagory));
           }

      progressBar =  findViewById(R.id.progressBar11);
           progressBar.setVisibility(View.GONE);
     Emailtext = findViewById(R.id.EMAIL);
     Passwordtext = findViewById(R.id.PASS);
     Phonenumbertext = findViewById(R.id.phonenumber);
     Nametext = findViewById(R.id.Nametxt);
     Signupbt = findViewById(R.id.Signupbt);

    mAuth = FirebaseAuth.getInstance();


    Signupbt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkfornull();

            if (getEmptytextfields()) {
        Log.e(Tag, String.valueOf(Emptytextfields));
        Toast.makeText(Signup.this, " Fill up above fields", Toast.LENGTH_SHORT).show();


    } else {
                Toast.makeText(Signup.this, "Data Enterd", Toast.LENGTH_SHORT).show();
Log.e(Tag,"Data Enterd");
                SignupUser();

    }

}




    });
        super.onCreate(savedInstanceState);
    }

    private void PhoneAuth() {
intent = new Intent(Signup.this,LinkphonewithCredential.class);
intent.putExtra("Phonenum",Phonenumber);
startActivity(intent);
    }

    private void SignupUser() {
        Log.e(Tag, String.valueOf("Inside Signup user"));
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            //    FirebaseUser user = mAuth.getCurrentUser();


                          //  Toast.makeText(Signup.this, "Succesfully Signup.", Toast.LENGTH_SHORT).show();
                            //
                            Emailverification();

                            Uploadinguserdata();


                           // progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());

                            progressBar.setVisibility(View.INVISIBLE);


                            Toast.makeText(Signup.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();


                        }


                        // ...
                    }
                });
    }


    private void Emailverification() {

         mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful())
{
    Toast.makeText(Signup.this, "Verification Email send", Toast.LENGTH_SHORT).show();
Log.e(Tag,"var email sent");

}

    else
{
    Log.e(Tag,String.valueOf(task.getException()));
    Toast.makeText(Signup.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();

}
            }



        });

    }


    private void Uploadinguserdata() {
        Log.e(Tag, String.valueOf("Inside Uploadinguserdata"));
        db=FirebaseDatabase.getInstance().getReference(Catagory);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        Uid=firebaseUser.getUid();

        db.child(Uid).child("Phonenumber").setValue(Phonenumber);
        db.child(Uid).child("Email").setValue(Email);
        db.child(Uid).child("Password").setValue(Password);
        db.child(Uid).child("Name").setValue(Name);

     Log.e(Tag,"DATA UPLOADLOADED TO DATABASE");
        progressBar.setVisibility(View.INVISIBLE);
        Showdialogbox();

     //Toast.makeText(Signup.this, " Succesfully Registerd", Toast.LENGTH_LONG).show();






    }

    private void Showdialogbox() {
try {
    Emailverpopup = new Dialog(this);

    Emailverpopup.setContentView(R.layout.emailverpopup);

    Proceedbt =(Button) Emailverpopup.findViewById(R.id.Proceedbt);

    Proceedbt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Emailverpopup.dismiss();

            PhoneAuth();
        }
    });
    Emailverpopup.show();
}
catch (Exception e)
{
    Log.e(Tag,e.getMessage());
}
    }

    public void    checkfornull() {
        //Name = Nametext.getText().toString();

      Name = Nametext.getText().toString();
      Email = Emailtext.getText().toString();
      Password = Passwordtext.getText().toString();
      Phonenumber = Phonenumbertext.getText().toString();
      if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Name) || TextUtils.isEmpty(Phonenumber)) {
          setEmptytextfield(true);
      }
      //Log.e(Tag,String.valueOf(Name+Email+Password+Phonenumber));
 else
     {
        setEmptytextfield(false);
     }


      /* else
        {
            if(Phnumformatchk()==true)
                Log.e(TAG, "true format");
                Emptytextfields=false;
        }
            Log.e(TAG, "false  format");*/
           //return  Emptytextfields;

    }

    private boolean Phnumformatchk() {
    if(Phonenumber.matches(Phnumformat))

        return true;
        else
            return false;
    }

    public Boolean getEmptytextfields() {
        return Emptytextfields;
    }

    public void setEmptytextfield(boolean emptytextfield) {
        this.Emptytextfields = emptytextfield;
    }


}
