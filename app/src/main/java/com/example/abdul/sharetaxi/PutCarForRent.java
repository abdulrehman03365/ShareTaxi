package com.example.abdul.sharetaxi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class PutCarForRent extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int SElect_FILE = 110;
    Toolbar toolbar;
    ImageView Carimage;
    TextView carmodel;
    Button putCarforRent;
    Boolean isimageupload=false;
    private LocationRequest mLocationRequest;
    CharSequence[] items;
    private int i;
    Uri URI;
    private String TAG = "iamtag";
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Uri downloadUri;
    private DatabaseReference databaseReference;
    Switch switchauto;
    EditText milage;
    private boolean automatic;
    private String milagest, Userid;
    private boolean iscarputonlocation;
    private Location mLastKnownLocation;
    private LocationCallback mlocationCallback;
    private FirebaseUser firebaseUser;
    private DatabaseReference driverReference;
    private Driver Driver;
    private String imageurlstring;
    private boolean carimageuploaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_car_for_rent);


        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        Userid=firebaseUser.getUid();




        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Put Car for Rent");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(PutCarForRent.this);
        Carimage = findViewById(R.id.carimage);
        switchauto = findViewById(R.id.SwitchAuto);
        milage = findViewById(R.id.Milagetxt);
        carmodel = findViewById(R.id.CarModeltitle);
        switchauto.setChecked(true);
        putCarforRent = findViewById(R.id.PutforRentbt);

      loadCarImage();
        fetchexistingcardetail();

        Carimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                douploadimage();
            }
        });


        putCarforRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putLocatointogeofire();

                milagest = milage.getText().toString();

                if (milagest.isEmpty()) {
                    milage.setError("Enter the Millage");

                } else if (isimageupload || carimageuploaded ) {

                    databaseReference = FirebaseDatabase.getInstance().getReference("RentaCar");
                    databaseReference.child(Userid).child("Millage").setValue(milagest);
                    databaseReference.child(Userid).child("Automatic").setValue(String.valueOf(automatic));
                    databaseReference.child(Userid).child("cardownurl").setValue(String.valueOf(downloadUri));
                    databaseReference.child(Userid).child("Key").setValue(String.valueOf(databaseReference.child(Userid).getKey()));

                    Log.i(TAG,"KAEY OF Rent a car Driver"+String.valueOf(databaseReference.child(Userid).getKey()));

                    databaseReference.child(Userid).child("key").setValue(String.valueOf(databaseReference.child(Userid).getKey()));

                    Toast toast = Toast.makeText(PutCarForRent.this, "Online", Toast.LENGTH_LONG);
                    toast.show();

                }
                else if(isimageupload!=true)
                {
                    Toast toast = Toast.makeText(PutCarForRent.this, "Fist Upload Car Image", Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });


        switchauto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    automatic = true;

                } else {
                    automatic = false;
                }


            }
        });

    }

    private void loadCarImage() {

        databaseReference = FirebaseDatabase.getInstance().getReference("RentaCar");
        databaseReference.child(Userid).child("CarImageUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists());
                {

                    imageurlstring = (String) dataSnapshot.getValue();

                    Picasso.get().load(imageurlstring).into(Carimage);
                     carimageuploaded = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchexistingcardetail() {
        driverReference = FirebaseDatabase.getInstance().getReference("Driver");
        driverReference.child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Driver=  dataSnapshot.getValue(Driver.class);
            carmodel.setText(Driver.Carmodel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });












    }

    private void douploadimage() {


        items = new CharSequence[]{"Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PutCarForRent.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {


                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.setType("images/*");
                    //           checkforpermission();
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {


            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    // Uri    URI=  data.getData();
                    Bundle bundle = data.getExtras();
                    //      Uri  imageURI= (Uri) bundle.get("data");


                    //
                    Bitmap bmp = (Bitmap) bundle.get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(),
                            bmp, "Temp", null);
                    URI = Uri.parse(path);

                    Log.e(TAG, String.valueOf(URI));

                    Glide.with(this).load(bmp).into(Carimage);
                    //Ridersimage.setImageBitmap(bmp);
                    uploadimage(URI);
                } else if (requestCode == SElect_FILE) {
                    Uri imageuri = data.getData();
                    Glide.with(this).load(imageuri).into(Carimage);
                    //Ridersimage.setImageURI(imageuri);


                    uploadimage(imageuri);
                }


            }


        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e.getMessage()));
        }
    }


    private void uploadimage(Uri URI) {

       // storageReference = FirebaseStorage.getInstance().getReference();
        this.URI = URI;
        Log.i(TAG, "image path uri" + " " + String.valueOf(this.URI));
        mAuth =FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        storageReference =  FirebaseStorage.getInstance().getReference().child(currentUser.getUid() + "/" + "Carpic");
          progressDialog.setMessage("Image Uploading");
        progressDialog.show();
        storageReference.putFile(URI).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Profile image UPLOADED");
                isimageupload = true;
                Log.i(TAG, String.valueOf(isimageupload));

                 storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                     @Override
                     public void onComplete(@NonNull Task<Uri> task) {
                        downloadUri= task.getResult();
                         databaseReference = FirebaseDatabase.getInstance().getReference("RentaCar").child(Userid).child("CarImageUrl");
                         databaseReference.setValue(String.valueOf(downloadUri));
                         Log.i(TAG, String.valueOf(downloadUri));


                     }
                 });

                progressDialog.dismiss();
                Toast toast = Toast.makeText(PutCarForRent.this, "Image uploaded", Toast.LENGTH_LONG);
                toast.show();

            }


        });

    }


    private void putLocatointogeofire() {

        createLocationRequest();

        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastKnownLocation = locationResult.getLastLocation();

                final DatabaseReference OnlineDriver = FirebaseDatabase.getInstance().getReference("RentaCarLoc");
                GeoFire geoFire = new GeoFire(OnlineDriver);
                GeoLocation geoLocation = new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());


                geoFire.setLocation(Userid, geoLocation, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.i(TAG, String.valueOf(error));

                          iscarputonlocation=true;
                            Log.i(TAG,"RentaCarLocation on map");
                        } else {
                            Log.i(TAG, String.valueOf(error));

                        }


                    }
                });

            }




        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mlocationCallback, null);


    }


    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(100);

    }



}

