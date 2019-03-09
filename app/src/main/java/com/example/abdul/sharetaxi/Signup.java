package com.example.abdul.sharetaxi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Signup extends AppCompatActivity {
    Intent intent;
    static String Catagory;
    Dialog Emailverpopup;
    Button DialogProceedbt,Uploaddata;
    public  String email="",Password="", Cnicnumber="",Carnumber="",Carcolour="",
            Carmodel="";
    private FirebaseAuth mAuth;
    LinearLayout imageuperlayout;
    Uri URI;
    private String TAG ="iamtag";
    FirebaseUser firebaseUser ;
    public DatabaseReference db;
    private String Tag="iamtag";
    TextView PasswordrecoveryView;
    Button Signupbt, Proceeddriverbt;
    EditText  Emailtext ,Passwordtext,Nametext,Phonenumbertext,
            Carnumbertxt,Carcolourtxt,Carmodeltxt,Cninumbertxt;
    ProgressBar progressBar;
    public String Email="",Phonenumber="",Name="";
    public Boolean Emptytextfields;
    ImageView Driversimage;
    private String Uid="";
    private String Phnumformat="^\\+";
    private boolean DriverEmptyfields;
    //StorageReference storageReference;
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    CharSequence[] items;
    private int i;
    ProgressDialog progressDialog;
    private int REQUEST_CAMERA=0;
    private int SElect_FILE=1;
    private int REQUEST_External=11;
    private int PERMISSION_CODE=2;
    private boolean imageupload=true;
    private boolean checkemptydriverfields;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        Proceeddriverbt = findViewById(R.id.proceeddrbt);
        Emailtext = findViewById(R.id.EMAIL);
        Passwordtext = findViewById(R.id.PASS);
        Phonenumbertext = findViewById(R.id.phonenumber);
        Nametext = findViewById(R.id.Nametxt);
        Signupbt = findViewById(R.id.Signupbt);
        Cninumbertxt =findViewById(R.id.cnicno);
        Carcolourtxt = findViewById(R.id.carcolour);
        Carmodeltxt =findViewById(R.id.carmodel);
        Carnumbertxt=findViewById(R.id.carnumber);
        Driversimage =findViewById(R.id.driverimage);
        mAuth = FirebaseAuth.getInstance();
        Carnumbertxt.setVisibility(View.INVISIBLE);
        Carcolourtxt.setVisibility(View.INVISIBLE);
        Carmodeltxt.setVisibility(View.INVISIBLE);
        Cninumbertxt.setVisibility(View.INVISIBLE);
        Proceeddriverbt.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(Signup.this);
        imageuperlayout= findViewById(R.id.imageuperlayout);

         Driversimage.setVisibility(View.INVISIBLE);
         imageuperlayout.setVisibility(View.INVISIBLE);
        Driversimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowImgdialog();
            }
        });
        ccp = (CountryCodePicker) findViewById(R.id.ccp1);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber1);

        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        Signupbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkfornull();

                if (getEmptytextfields()) {
                    Log.e(Tag, String.valueOf(Emptytextfields));
                    Toast.makeText(Signup.this, " Fill up above fields", Toast.LENGTH_SHORT).show();


                } else {
                    Log.e(Tag,"Data Enterd");
                    SignupUser();

                }

            }




        });
        super.onCreate(savedInstanceState);



        Proceeddriverbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Tag,String.valueOf(imageupload));
                if(checkemptydriverfields()==true || imageupload==true)
                {

                    Toast toast = Toast.makeText(Signup.this, "Image and Data must be filled", Toast.LENGTH_LONG);
                    toast.show();

                }
                else
                {
                    Cardetailsupload();
                    End();
                }
            }
        });
    }

    private void End() {
        PhoneAuth();
    }

    private void Cardetailsupload() {

        db=FirebaseDatabase.getInstance().getReference(Catagory);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        Uid=firebaseUser.getUid();

        progressDialog.setMessage("Car Details Uploading");
        progressDialog.show();
        db.child(Uid).child("Carnumber").setValue(Carnumber);
        db.child(Uid).child("Carmodel").setValue(Carmodel);
        db.child(Uid).child("Carcolour").setValue(Carcolour);
        db.child(Uid).child("DriverCnincno").setValue(Cnicnumber);
        db.child(Uid).child("Catagory").setValue(Catagory);
        Log.e(Tag,"Car Detalis uploaded");
        Toast toast = Toast.makeText(Signup.this,"Driver Succesfuly Signup",Toast.LENGTH_LONG);
        toast.show();
        progressDialog.dismiss();



    }

    private boolean Checkemptydriversimage() {
        return  true;
    }


    private void PhoneAuth() {
        intent = new Intent(Signup.this,LinkphonewithCredential.class);
        intent.putExtra("Phonenum",Phonenumber);
        startActivity(intent);
    }

    private void SignupUser() {
        Log.e(Tag, String.valueOf("Inside Signup user"));
        progressDialog.setMessage("Signing Up");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            //    FirebaseUser user = mAuth.getCurrentUser();
                           progressDialog.dismiss();

                            //  Toast.makeText(Signup.this, "Succesfully Signup.", Toast.LENGTH_SHORT).show();
                            //
                            Emailverification();

                            Uploadinguserdata();

                             progressDialog.dismiss();
                          //  progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());

                           // progressBar.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();

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
        progressDialog.setMessage("Uploading Data");
        db.child(Uid).child("Phonenumber").setValue(Phonenumber);
        db.child(Uid).child("Email").setValue(Email);
        db.child(Uid).child("Password").setValue(Password);
        db.child(Uid).child("Name").setValue(Name);
        db.child(Uid).child("Catagory").setValue(Catagory);

        Log.e(Tag,"DATA UPLOADLOADED TO DATABASE");
       progressDialog.dismiss();
        //progressBar.setVisibility(View.INVISIBLE);
        Showdialogbox();

        //Toast.makeText(Signup.this, " Succesfully Registerd", Toast.LENGTH_LONG).show();






    }

    private void Showdialogbox() {
        try {
            Emailverpopup = new Dialog(this);











            Emailverpopup.setContentView(R.layout.emailverpopup);

            DialogProceedbt =(Button) Emailverpopup.findViewById(R.id.dialogProceedbt);

            DialogProceedbt.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {

                    if(Catagory.equals("Driver"))
                    {
                        Log.e(Tag,String.valueOf(Catagory));
                        Uploaddriverdata();
                    }
                    else {
                        PhoneAuth();

                    }
                    Emailverpopup.dismiss();
                }
            });
            Emailverpopup.show();
        }
        catch (Exception e)
        {
            Log.e(Tag,e.getMessage());
        }
    }


    @SuppressLint("NewApi")
    private void Uploaddriverdata() {

        Handlevisibility();

       /* if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_External);
        }*/
        DialogProceedbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkemptydriverfields())
                    Toast.makeText(Signup.this, "Enter Drivers Data First", Toast.LENGTH_SHORT).show();

                else
                    Log.e(Tag,"Drivers data Enterd");

                progressBar.setVisibility(View.VISIBLE);
                //ShowImgdialog();n

            }
        });

        Driversimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowImgdialog();
            }
        });



    }

    private void ShowImgdialog() {
        final CharSequence[] items={"Camera"};
        Log.e(Tag,"Drivers Fields"+String.valueOf(DriverEmptyfields));

        AlertDialog.Builder  builder = new AlertDialog.Builder(Signup.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[i].equals("Camera"))
                {


                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.setType("images/*");
                    //           checkforpermission();
                    startActivityForResult(intent,REQUEST_CAMERA);

                }
                else if(items[i].equals("Cancel"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }




    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try{



            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA ) {
                    // Uri    URI=  data.getData();
                    Bundle bundle = data.getExtras();
                    //      Uri  imageURI= (Uri) bundle.get("data");


                    //
                    Bitmap bmp = (Bitmap) bundle.get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(Signup.this.getContentResolver(),
                            bmp, "Temp", null);
                    URI=Uri.parse(path);

                    Log.e(TAG,String.valueOf(URI));

                    Glide.with(this).load(bmp).into(Driversimage);
                    //Driversimage.setImageBitmap(bmp);
                    uploadimage(URI);
                } else if (requestCode == SElect_FILE) {
                    Uri imageuri = data.getData();
                    Glide.with(this).load(imageuri).into(Driversimage);
                    //Driversimage.setImageURI(imageuri);


                    uploadimage(imageuri);
                }



            }


        }
        catch (Exception e)
        {
            Log.e(Tag,String.valueOf(e.getMessage()));
        }
    }




    private void uploadimage(Uri URI)
    {
        StorageReference storage;
        storage= FirebaseStorage.getInstance().getReference();
        this.URI=URI;
        Log.d(Tag,String.valueOf(this.URI));
        FirebaseUser currentUser= mAuth.getCurrentUser();
        StorageReference storageRef = storage.child(currentUser.getUid() + "/" + "Profilepic").child(this.URI.getLastPathSegment());
 /* DatabaseReference databaseRefere;

    storageRef=storageRef.child(currentUser.getUid() + "/" + "Profilepic").child(URI.getLastPathSegment());*/
 progressDialog.setMessage("Image Uploading");
 progressDialog.show();

        storageRef.putFile(URI).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"Profile image UPLOADED");
                imageupload=false;

               progressDialog.dismiss();
                Toast toast = Toast.makeText(Signup.this,"Image uploaded",Toast.LENGTH_LONG);
                toast.show();

            }



        });


        //StorageReference storageRef =;
        //Intent intent = new Intent(Intent.ACTION_PICK,)

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Handlevisibility() {


        progressDialog.dismiss();
        Carnumbertxt.setVisibility(View.VISIBLE);
        Carcolourtxt.setVisibility(View.VISIBLE);
        Carmodeltxt.setVisibility(View.VISIBLE);
        Cninumbertxt.setVisibility(View.VISIBLE);
        Driversimage.setVisibility(View.VISIBLE);
        imageuperlayout.setVisibility(View.VISIBLE);
        DialogProceedbt.setVisibility(View.VISIBLE);
        Proceeddriverbt.setVisibility(View.VISIBLE);
        Signupbt.setVisibility(View.GONE);
        ccp.setVisibility(View.INVISIBLE);
        editTextCarrierNumber.setVisibility(View.INVISIBLE);
        Nametext.setVisibility(View.GONE);
        Emailtext.setVisibility(View.GONE);
        Passwordtext.setVisibility(View.GONE);
        checkforpermission();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkforpermission() {
        Log.e(TAG,"Asking for permissions");

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.isAnyPermissionPermanentlyDenied())
                {

                }
                if(report.areAllPermissionsGranted()!=true)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    builder.setTitle("Permission Needed");
                    builder.setMessage("You Denied Camera And storage Permission which are necessory now go to setting to enable them");
                    builder.show();
                    builder.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            opensetting();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }

        }).check();


    /*

        String[]  permissions ={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(getApplicationContext().checkSelfPermission(
                permissions[0])!=PackageManager.PERMISSION_GRANTED
                &&(ContextCompat.checkSelfPermission(Signup.this,
                permissions[1])!=PackageManager.PERMISSION_GRANTED
        ))
        {
            ActivityCompat.requestPermissions(Signup.this,permissions,PERMISSION_CODE);
        }
        else
        {
            Log.e(TAG,"PERMISSION GRANTED");
        }

*/


    }

    private void opensetting() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri =  Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        boolean cam = grantResults[0]!=PackageManager.PERMISSION_GRANTED;

        boolean store = grantResults[1]!=PackageManager.PERMISSION_GRANTED;
        if(store==false || cam ==false)
        {
            checkforpermission();
        }

    }

    private boolean checkemptydriverfields() {
        Carnumber = Carnumbertxt.getText().toString();
        Carcolour = Carcolourtxt.getText().toString();
        Carmodel = Carmodeltxt.getText().toString();
        Cnicnumber = Cninumbertxt.getText().toString();
        if(TextUtils.isEmpty(Carmodel)||TextUtils.isEmpty(Carcolour)
                ||TextUtils.isEmpty(Carnumber)||TextUtils.isEmpty(Cnicnumber))

        {
            DriverEmptyfields = true;

            if(Cnicnumber.isEmpty() )
            {
                Cninumbertxt.setError("Enter the CNICnumber");


            }

            if(Carnumber.isEmpty() )
            {
                Carnumbertxt.setError("Enter the Carnumber");


            }
            if(Carmodel.isEmpty() )
            {
                Carmodeltxt.setError("Enter the Car Model");


            }
            if(Carcolour.isEmpty() )
            {
                Carcolourtxt.setError("Enter the CarColour");


            }


        }
        else

            DriverEmptyfields = false;

        return DriverEmptyfields;
    }

    public void    checkfornull() {
        //Name = Nametext.getText().toString();
        Phonenumber=ccp.getFormattedFullNumber();
        Name = Nametext.getText().toString();
        Email = Emailtext.getText().toString();
        Password = Passwordtext.getText().toString();
        //Phonenumber = Phonenumbertext.getText().toString();
        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Name) || TextUtils.isEmpty(Phonenumber)) {
            setEmptytextfield(true);
            if(Name.isEmpty() )
            {
                Nametext.setError("Enter the name ");

            }
            if(Email.isEmpty() )
            {
                Emailtext.setError("Enter the Email");

            }
            if(Password.isEmpty() )
            {
                Passwordtext.setError("Enter the Password");


            }





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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
   /*     if(mAuth.getCurrentUser()!=null)
        {
           if(Catagory=="Driver")
           {
              Uploaddriverdata();

           }
        }
        else
        {

        }*/
    }
}
