package com.example.abdul.sharetaxi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.abdul.sharetaxi.Signup.REQUEST_CAMERA;

public class RiderActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PICK_IMAGE = 111;
    private static boolean Driverfound = false;
    private GoogleMap mMap;
    Toolbar toolbar;
    private RippleBackground rippleBg;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView Ridersimage;
    TextView RidersName, Ridersrating;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    String Userid;
    private String imageurlstring;
    private URL myURL;
    private Uri profileimageuri;
    private String RidersNametxt = "";
    private String Catagory = " ";
    private CameraPosition mcameraPosition;
    private BottomSheetDialog bottomSheetDialog;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mlocationCallback;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private boolean mLocationPermissionGranted;
    private String TAG = "iamtag";
    private GoogleApiClient mgoogleApiClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Boolean mRequestingLocationUpdates;
    ProgressDialog progressDialog;
    public RelativeLayout Driverdetail_rel;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    GeoFire geoFire;
    private double destination_Long = 0;
    private double destination_Lat = 0;
    Marker Ridermarker;
    public String FoundDriveruid;
    private double longitude, latitude;
    private Marker Drivermarker;
    private Double pickupradius;
    private Double queryRadius = 6.0;
    public View bottomsheetview;
    private Driver Driverdata;
    public String Driverid;
    private boolean pickupradiusgreater = false;
    private TextView SeatsavailableBottom;
    private TextView CosttxtBottom;
    private EditText Chooseseatxt;
    private String ChoosedseatSt = "";
    private double totalcost;
    private float distanceinkm;
    private double Base_fare = 10.0;
    private double startlatitude;
    private double startlongitude;
    private double distance;
    private double ChoosSeatdouble;
    private Button FaraEstimate, Request_PickupBt;
    private float distanceinm;
    private Boolean costcalculated = false;
    private String Requeststatus;
    private Button calltexi;
    private View includeDriver;
    private TextView Customername, CustomerRating;
    private ImageView CallCustomer, CustomerImage, RiderProfileImage;
    private String customerNumber;
    private DatabaseReference
            driverReference;
    public Driver Driver;
    private String driverNumber;
    LatLng start, end;
    private List<Polyline> polylines;
    private DatabaseReference Requeststref;
    private int i = 0;
    private Uri URI;
    private Uri downloadUri;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private boolean imageupload;
    private AlertDialog.Builder builder1;
    private AlertDialog rateRideDialog;
    private View ratingLayout;
    private Button submitReviewBt;
    private EditText reviewtText;
    private RatingBar reviewRatingBar;
    private float ratingst = 0;
    private String reviewst = " ";
    private DatabaseReference driverReviewRef;
    private String F;
    private String TripUniqueKey;
    private Date currentTime;
    private String formattedDate;
    PlacesClient placesClient;
    TextView myplace;
    private String apiKey;
    private AutocompleteSupportFragment autocompleteFragment;
    private Polyline currentPolyline;
    private AlertDialog dialogExit;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider);


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        /**
         * Stores parameters for requests to the FusedLocationProviderApi.
         */
        polylines = new ArrayList<>();
        progressDialog = new ProgressDialog(RiderActivity.this);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomsheetview = getLayoutInflater().inflate(R.layout.fare_and_trip_details, null);
        bottomSheetDialog.setContentView(bottomsheetview);
        myplace = findViewById(R.id.Myplace);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SHARE TAXI");
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view1);
        rippleBg = findViewById(R.id.ripple_bg);
        View headerview = navigationView.inflateHeaderView(R.layout.nav_header_map_screen);
        Ridersimage = headerview.findViewById(R.id.Riderimage);
        RidersName = headerview.findViewById(R.id.RiderNameid);
        Ridersrating = headerview.findViewById(R.id.Ratingid);

        Driverdetail_rel = findViewById(R.id.Driver_detail);
        Driverdetail_rel.setVisibility(View.GONE);
        getdatabaseref();
        loadiprofileimage();
        Log.i("iamtag", " Inside RiderActivity");
        getusernametxt();
        getprofileimgurl();
        loadiprofileimage();
        setnavigationitemlistner();

        mAuth = FirebaseAuth.getInstance();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadRiderImage();
        Ridersimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(Userid).child("downloadimageurl").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            updateRiderPrImg();
                        } else {
                            uploadRiderPrImg();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        //mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //String apiKey = String.valueOf(R.string.Google_Places_api_key);
        apiKey = "AIzaSyDgkY5iTRu-SPvkF3AHkJK_CLgDfhS3hcE";
        final String placeId = "INSERT_PLACE_ID_HERE";
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);


        //Initialize Places
        Places.initialize(getApplicationContext(), apiKey);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dest_autocomp_fragment);

        autocompleteFragment.setPlaceFields(placeFields);


        //Placesauto complete Fragment
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                String address = place.getAddress();
                place.getName();
                destination_Long = place.getLatLng().longitude;
                destination_Lat = place.getLatLng().latitude;
                Log.i(TAG, String.valueOf("Latitude " + destination_Lat + " Longitude" + destination_Long));
                Log.i(TAG, String.valueOf(place.getName()));
                Log.i(TAG, String.valueOf(place.getAddress()));
                Log.i(TAG, String.valueOf(place.getName()));
            }

            @Override
            public void onError(@NonNull Status status) {

                Log.e(TAG, "Place not found: " + status.getStatusMessage());
                Log.i(TAG, "An error occurred: " + status);
            }

        });


        calltexi = findViewById(R.id.calltexi);
        calltexi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference pickupref = FirebaseDatabase.getInstance().getReference("Pickuprequest");
                GeoFire geoFire = new GeoFire(pickupref);
                GeoLocation geoLocation = new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                Log.i(TAG, String.valueOf(" last longitude" + mLastKnownLocation.getLatitude()));
                geoFire.setLocation(Userid, geoLocation, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {


                                if (error != null) {
                                    Log.i(TAG, String.valueOf(error));

                                } else {
                                    Log.i(TAG, String.valueOf(error));
                                }


                            }


                        }

                );


                if (Ridermarker != null) {
                    Ridermarker.remove();
                }

                Ridermarker = mMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                .title("Pickup herre")
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pickup_web", 100, 100)))
                                .draggable(true));
                Ridermarker.setTag(0);
                calltexi.setText("Searching Ride");
                rippleBg.startRippleAnimation();
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 findDriver();
                 rippleBg.stopRippleAnimation();
             }
         };
               new Handler().postDelayed(r, 5000);


               // progressDialog.setMessage("Searching");
                // progressDialog.show();



            }
        });


        //getCurentPlaceName();
    }

    public void onrideComplete() {


        builder1 = new AlertDialog.Builder(RiderActivity.this);
        builder1.setTitle("Rate The Ride");
        LayoutInflater inflater = getLayoutInflater();
        ratingLayout = inflater.inflate(R.layout.onridecmpletelayout, null);
        submitReviewBt = ratingLayout.findViewById(R.id.submitReviewbt);
        reviewtText = ratingLayout.findViewById(R.id.ReviewText);
        reviewRatingBar = ratingLayout.findViewById(R.id.reviewRatingBar);
        reviewRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.i(TAG, String.valueOf(ratingBar.getRating()));
                ratingst = ratingBar.getRating();

            }
        });


        builder1.setView(ratingLayout);
        rateRideDialog = builder1.create();

        submitReviewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewst = reviewtText.getText().toString();
                if (reviewst.isEmpty()) {
                    Toast toast = Toast.makeText(RiderActivity.this, "Kindly give your Review", Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    uploadreview();


                }

                uploadhistory();

            }
        });
        rateRideDialog.show();


    }

    @Override
    public void onBackPressed() {


        if (rippleBg.isRippleAnimationRunning()) {
            rippleBg.stopRippleAnimation();
        } else {
            openExitDialog();
        }


    }

    private void openExitDialog() {
        builder1 = new AlertDialog.Builder(RiderActivity.this);
        builder1.setTitle("Are You Sure You want to Exit");


        builder1.setPositiveButton("StayOnline ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder1.setNegativeButton("Offline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogExit = builder1.create();
        dialogExit.show();
        dialogExit.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "StayOnline button Clicked");
                dialogExit.dismiss();
            }
        });


        dialogExit.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Exitbutton button Clicked");
                // removedrivervalues();
                {
                    finish();
                    dialogExit.dismiss();
                }
            }
        });


    }

    private void getCurentPlaceName() {

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlacesClient placesClient = Places.createClient(this);
            placesClient.findCurrentPlace(request).addOnSuccessListener((new OnSuccessListener<FindCurrentPlaceResponse>() {
                private double minLikeLihood;

                @Override
                public void onSuccess(FindCurrentPlaceResponse response) {
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        minLikeLihood = placeLikelihood.getLikelihood();
                        if (placeLikelihood.getLikelihood() < minLikeLihood) {


                        }
//                        StringBuilder textView=null;
//                        myplace.append(String.format
//                                ("Place '%s' has likelihood: %f\n",
//                                placeLikelihood.getPlace().getName(),
//                                placeLikelihood.getLikelihood()));
                    }

                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        Log.e(TAG, "Place not found: " + apiException.getStatusMessage());
                        Log.e(TAG, "Place not found exc mess: " + exception.getMessage());
                        Log.e(TAG, "Stack Trace: " + exception.getStackTrace());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }
    }


    private void uploadhistory() {


        currentTime = Calendar.getInstance().getTime();
        Log.i(TAG, String.valueOf(currentTime));
        Date date = Calendar.getInstance().getTime();
        Log.i(TAG, "Current time => " + date);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(date);

        //  HistoryModel historyModel= new HistoryModel(costcalculated,formattedDate,dest,from,currentTime);

    }

    private void uploadreview() {
        Review review = new Review(reviewst, ratingst);
        driverReviewRef = FirebaseDatabase
                .getInstance()
                .getReference("DriverReview")
                .child(FoundDriveruid);
        TripUniqueKey = driverReviewRef.push().getKey();
        driverReviewRef.child(TripUniqueKey).setValue(review, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                Log.i(TAG, String.valueOf(task.getResult()));
                Toast toast = Toast.makeText(RiderActivity.this, "Review Uploaded", Toast.LENGTH_LONG);
                toast.show();
            }
        });


    }

    private void loadRiderImage() {
        databaseReference.child(Userid).child("downloadimageurl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    imageurlstring = (String) dataSnapshot.getValue();

                    Picasso.get().load(imageurlstring).into(Ridersimage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateRiderPrImg() {
        ShowImgdialog();

    }


    private void ShowImgdialog() {
        final CharSequence[] items = {"Camera", "Gallery"};


        AlertDialog.Builder builder = new AlertDialog.Builder(RiderActivity.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                    switch (which) {
                        case 0
                            :
                        {


                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //intent.setType("images/*");
                            //           checkforpermission();
                            startActivityForResult(intent, REQUEST_CAMERA);
break;
                        }
                        case 1:
{
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");

                            startActivityForResult(intent, PICK_IMAGE);

      break;
                        }
                    }
            }
        });
        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {


            if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();


                Bitmap bmp = (Bitmap) bundle.get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(RiderActivity.this.getContentResolver(),
                        bmp, "Temp", null);
                URI = Uri.parse(path);

                Log.e(TAG, String.valueOf(URI));

                Glide.with(this).load(bmp).into(Ridersimage);
                //Ridersimage.setImageBitmap(bmp);
                uploadimage(URI);
            }
            else if (requestCode == PICK_IMAGE &&resultCode==Activity.RESULT_OK)
            {
                Uri image = data.getData();
                Glide.with(this).load(image).into(Ridersimage);
                uploadimage(image);
            }


        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e.getMessage()));
        }
    }





    private void uploadRiderPrImg() {

        ShowImgdialog();


    }


    private void uploadimage(Uri URI)
    {

        storageReference= FirebaseStorage.getInstance().getReference();
        this.URI=URI;
        Log.d(TAG,"image path uri" + " " + String.valueOf(this.URI));
        FirebaseUser currentUser= mAuth.getCurrentUser();
        storageReference = storageReference.child(currentUser.getUid() + "/" + "Profilepic");
 /* DatabaseReference databaseRefere;

    storageRef=storageRef.child(currentUser.getUid() + "/" + "Profilepic").child(URI.getLastPathSegment());*/
        progressDialog.setMessage("Image Uploading");
        progressDialog.show();

        storageReference.putFile(URI).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"Profile image UPLOADED");
                imageupload=false;
                storageReference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.i("iamtag",e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        downloadUri = task.getResult();
                        databaseReference.child(Userid).child("downloadimageurl").setValue(String.valueOf(downloadUri));
                        Log.i("iamtag",String.valueOf("Urldowncomp" + downloadUri));


                    }
                });

                progressDialog.dismiss();
                Toast toast = Toast.makeText(RiderActivity.this,"Image uploaded",Toast.LENGTH_LONG);
                toast.show();

            }



        });


    }


    private void checkpickuprequest() {
        Log.i(TAG,String.valueOf("Checking Pickup Request" ));

         Requeststref = FirebaseDatabase
                .getInstance()
                .getReference("Pickuprequest")
                .child(FoundDriveruid);
        Requeststref.child("Requeststatus")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG,String.valueOf("Request Snapshot +" + String.valueOf(dataSnapshot.exists()) ));

                if(dataSnapshot.exists())
                {
                    Requeststatus=(String) dataSnapshot.getValue();

                    switch (Requeststatus)
                    {
                        case "accepted":
                        {
                            Log.i(TAG, String.valueOf("Request Status" + Requeststatus));
                            showdriverdetail();


                            break;
                        }

                        case "rejected":
                        {
                            Log.i(TAG,String.valueOf(Requeststatus));
                            Requeststref.child("Requeststatus").setValue(null);
                            Requeststref.child("RiderId").setValue(null);

                            break;
                        }
                        default:
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters + "&key=" +
                getString(R.string.google_maps_key);
        return url;
    }



    private void addroute() {
































 RoutingListener routingListener = new RoutingListener() {
    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(RiderActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(RiderActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (i = 0; i < arrayList.size(); i++) {

            //In case of more than 5 alternative routes


            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(R.color.light);
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(arrayList.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1)
                    + ": distance - " +
                    arrayList.get(i).getDistanceValue()
                    + ": duration - " +
                    arrayList.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    public void onRoutingCancelled() {

    }
};
     start = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
    end = new LatLng(destination_Lat,destination_Long);
        Routing routing = new Routing.Builder()

                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(routingListener)
                .alternativeRoutes(true)
                .waypoints(start, end).key(apiKey)
                .build();
        routing.execute();

    }

    private void showdriverdetail() {
        Log.i(TAG, String.valueOf("Showing Driver Details"));
        calltexi.setVisibility(View.GONE);
        bottomSheetDialog.dismiss();
        Driverdetail_rel.setVisibility(View.VISIBLE);
        driverReference = FirebaseDatabase.getInstance().getReference("Driver").child(FoundDriveruid);
        driverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Driver=dataSnapshot.getValue(Driver.class);
                Log.i(TAG, String.valueOf(String.valueOf(FoundDriveruid)));
                showdialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showdialog() {
        includeDriver = findViewById(R.id.include_Driver);

        Customername = includeDriver.findViewById(R.id.Customername);
        CustomerRating = includeDriver.findViewById(R.id.Customer_rating);
        CallCustomer = includeDriver.findViewById(R.id.Call_customer);
        CustomerImage = includeDriver.findViewById(R.id.Customer_image);
        if (FoundDriveruid != null)
        {
            Picasso.get().load(Driver.downloadimageurl).into(CustomerImage);


            Customername.setText(Driver.Name);
            CustomerRating.setText("4");
            Log.i(TAG, String.valueOf(Driver.Phonenumber));
            driverNumber = Driver.Phonenumber;

            Driverdetail_rel.setVisibility(View.VISIBLE);

            CallCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", driverNumber, null));
                    startActivity(intent);

                    startActivity(intent);
                }
            });


        }
    }

    private void findDriver() {

        DatabaseReference FindDriver  =FirebaseDatabase.getInstance().getReference("OnlineDrivers");
        GeoFire  drivergeofire = new GeoFire(FindDriver);
        GeoQuery findDriverque =drivergeofire
                .queryAtLocation (new GeoLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),3);
        findDriverque.addGeoQueryEventListener(new GeoQueryEventListener() {
          @Override
          public void onKeyEntered(String key, final GeoLocation location) {

              progressDialog.dismiss();
               //  Log.i(TAG,"Driver Found");
                 Driverfound=true;
                 FoundDriveruid=key;


                 // matchUserCriteria();
                 //OpenBottomDialogSheet
              /*    if(matchUserCriteria())
                {
                    showDriver();
                }

             */


              DatabaseReference  driverfoundref =    FirebaseDatabase
                      .getInstance()
                      .getReference("OnlineDrivers")
                      .child(FoundDriveruid).child("l");
              driverfoundref.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      List<Object> map=    (List<Object>) dataSnapshot.getValue();
                      longitude= Double.parseDouble(map.get(0).toString());
                      latitude= Double.parseDouble(map.get(1).toString());



                      if(Drivermarker!=null) {
                          Drivermarker.remove();
                      }

                      Drivermarker = mMap.addMarker
                              (new MarkerOptions()
                                      .position(new LatLng(longitude, latitude))
                                      .title("I am Driver")
                                      .draggable(true)
                                      .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_car_web", 100, 100))));




                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }

              });



              if(Drivermarker !=null)

              {
                  Drivermarker.remove();
              }


              Drivermarker =   mMap.addMarker(new MarkerOptions().position(new LatLng(longitude,latitude))
                      .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_car_web",100,100))).draggable(true));

 //Car Marker and Rider Marker added



               openBottomDialogueSheet();

          }



          @Override
          public void onKeyExited(String key) {
              rippleBg.stopRippleAnimation();
          }

          @Override
          public void onKeyMoved(String key, GeoLocation location) {
              rippleBg.stopRippleAnimation();
          }

          @Override
          public void onGeoQueryReady() {

          }

          @Override
          public void onGeoQueryError(DatabaseError error) {
              rippleBg.stopRippleAnimation();
          }
      });
        if(pickupradiusgreater!=true)
        {
            Log.i(TAG,String.valueOf("No matching Car Found"));

        }
    }

    private void showDriver() {





    }

    private boolean matchUserCriteria() {
        Log.i(TAG,"Matching UserCriteria");
        pickupradius=checkradius();
        Log.i(TAG,"Pickupradius =" +String.valueOf(pickupradius));
        Log.i(TAG,"Pickupradius =" +String.valueOf(queryRadius));

        if(pickupradius>=queryRadius )
        {
            Log.i(TAG,"Pickupradius =" +String.valueOf(pickupradius));
            Log.i(TAG,"Pickupradius =" +String.valueOf(queryRadius));
            pickupradiusgreater=true;

        }
        else
        {
            pickupradiusgreater=false;

            queryRadius++;
            findDriver();
        }
        return pickupradiusgreater;
    }

    private void openBottomDialogueSheet() {

        Log.i(TAG, "Opening BottomDialogsheet");


        SeatsavailableBottom = bottomsheetview.findViewById(R.id.seatsavialable);
        CosttxtBottom = bottomsheetview.findViewById(R.id.cost);
        Chooseseatxt = bottomsheetview.findViewById(R.id.choosetxt);
        FaraEstimate = bottomsheetview.findViewById(R.id.ConfirmPickupBt);
        Request_PickupBt = bottomsheetview.findViewById(R.id.RequestPickup);

        bottomSheetDialog.show();

        FaraEstimate.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                ChoosedseatSt = Chooseseatxt.getText().toString();

                if (ChoosedseatSt != null) {
                    CalculateCost();
                }
                else
                {
                    Chooseseatxt.setError("Choose Seats First");
                }


            }
        });

        //Request Pickup
        Request_PickupBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

     if(costcalculated)
                {

                    DatabaseReference Assigncustomer = FirebaseDatabase
                            .getInstance()
                            .getReference("Pickuprequest")
                            .child(FoundDriveruid);


                    Assigncustomer.child("RiderId").setValue(Userid).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Assigning user  eXCEP" + String.valueOf(e.getMessage()));

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "Assigning user task" + String.valueOf(task.getResult()));
                        }
                    });
                }

            else
            {
                CosttxtBottom.setError("Calculate Cost First");

            }

            }
        });










            //Getting Data

        databaseReference=FirebaseDatabase.getInstance()
                .getReference("Driver")
                .child(String.valueOf(FoundDriveruid));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Driverdata = dataSnapshot.getValue(Driver.class);
                SeatsavailableBottom.setText(Driverdata.SeatsAvailable);
                //
                //FareCalculation





                Log.i("iamtag", " Value of Seatsavailable = " + String.valueOf(Driverdata.SeatsAvailable));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








    }

    private void getbottomreferences() {

    }

    private void CalculateCost() {


           float[] resultdist = new float[10];
          Location startlocation = new Location("startingloc");
                startlocation.setLatitude(mLastKnownLocation.getLatitude());
                startlocation.setLongitude(mLastKnownLocation.getLongitude());

        Location endlocation = new Location("startingloc");

        endlocation.setLatitude(33.561588);

        endlocation.setLongitude(73.06374);

        //After AutoComplete
        Location endlocationbyauto = new Location("startingloc");
        endlocationbyauto.setLongitude(destination_Lat);
        endlocationbyauto.setLongitude(destination_Long);


        distanceinm=startlocation.distanceTo(endlocation);
             distanceinkm=(distanceinm/1000);
             double roudofdistance = Math.round(distanceinkm*100)/100;
           Log.i(TAG, String.valueOf(roudofdistance));

           totalcost = (Base_fare *roudofdistance );
           ChoosSeatdouble = Double.parseDouble(ChoosedseatSt);
           totalcost = totalcost / (ChoosSeatdouble);
        double roundoffcost = Math.round(totalcost*100)/100;
           CosttxtBottom.setText(String.valueOf(roundoffcost));
        Log.i(TAG, String.valueOf(roundoffcost + " Pkr"));
            costcalculated =true;
     }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    private void getdatabaseref() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        Userid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

    }



    private void setnavigationitemlistner()

    {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.FindRentcar: {
                        //Toast.makeText(RiderActivity.this, "Find Rent a car", Toast.LENGTH_LONG).show();

             Intent intent = new Intent(RiderActivity.this,RentaCar.class);
             startActivity(intent);


              break;


                    }

                    case R.id.History:
                    {
                        Intent intent = new Intent(RiderActivity.this,History.class);
                        startActivity(intent);



                     break;
                    }

                    case R.id.LogoutRider:
                    {

                     Log.i(TAG,"LOGOUT CLICKED");
                        try {
                            FirebaseAuth.getInstance().signOut();
                            Toast toast = Toast.makeText(RiderActivity.this,"Succefully Signout",Toast.LENGTH_LONG);
                            toast.show();
                            finish();




                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        break;
                    }
                    case R.id.ReviewsRider:
                    {

                        Intent intent = new Intent(RiderActivity.this,ReviewHistory.class);
                        startActivity(intent);




                        break;
                    }
                    case R.id.SettingRider:
                    {




                        break;
                    }

                }
                return true;
            }
        });

    }

    private void getusernametxt() {
        databaseReference.child(Userid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RidersNametxt = (String) dataSnapshot.getValue();
                RidersName.setText(RidersNametxt);
                Log.i("iamtag", " Value of RidersName is" + " " + RidersName );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getprofileimgurl() {
        databaseReference.child(Userid).child("downloadimageurl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageurlstring = (String) dataSnapshot.getValue();
                Picasso.get().load(imageurlstring).into(Ridersimage);
                Log.i("iamtag", " Value of imageuri" + " " + imageurlstring);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      //Need to Get the rating right here
    }

    private void loadiprofileimage() {
        Picasso.get().load(imageurlstring).into(Ridersimage);
     Log.i("iamtag", "image profile Loaded");
     return;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId())
       {

       }

           return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG,"onMapready");
        mMap = googleMap;
        //getLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        else
        {
            readylocationcallback();
            updateLocationUI();
            //getDeviceLocation();
            createLocationRequest();








            
        }
        mMap.setMyLocationEnabled(true);

    }

    private void loadavailabledriver() {

     Log.i(TAG,"Loading All avialable driver");

        final DatabaseReference FindDriverRef =FirebaseDatabase.getInstance().getReference("OnlineDrivers");
        GeoFire  drivergeofire = new GeoFire(FindDriverRef);
        Log.i(TAG,"Queriying at location with radius" + String.valueOf(queryRadius));
        startlatitude=mLastKnownLocation.getLatitude();
        startlongitude=mLastKnownLocation.getLongitude();




        GeoQuery findDriverque =drivergeofire
                .queryAtLocation (new GeoLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),queryRadius);
        findDriverque.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                    Driverfound=true;
                    FoundDriveruid=key;
                Log.i(TAG,"Checking the pickup request");


                DatabaseReference  alldriverref =    FirebaseDatabase
                        .getInstance()
                        .getReference("OnlineDrivers");
                alldriverref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });







                checkpickuprequest();


                DatabaseReference  driverfoundref =    FirebaseDatabase
                        .getInstance()
                        .getReference("OnlineDrivers")
                        .child(FoundDriveruid).child("l");
                driverfoundref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Object> map=    (List<Object>) dataSnapshot.getValue();
                        longitude= Double.parseDouble(map.get(0).toString());
                        latitude= Double.parseDouble(map.get(1).toString());

                        if(Drivermarker!=null) {
                            Drivermarker.remove();
                        }

                        Drivermarker = mMap.addMarker
                                (new MarkerOptions()
                                        .position(new LatLng(longitude, latitude))
                                        .title("I am Driver")
                                        .draggable(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_car_web", 100, 100))));

                  //      checkpickuprequest();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
              //  Drivermarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_car_web", 100, 100))).draggable(true));

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });





    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(100);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,mlocationCallback,null);


    }

    private void readylocationcallback() {


        mlocationCallback = new LocationCallback(){


    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        mLastKnownLocation=locationResult.getLastLocation();

        Log.i(TAG,"ON Location change");
        if(Ridermarker!=null)
        {
            Ridermarker.remove();
        }

        Ridermarker = mMap.addMarker(new MarkerOptions()
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pickup_web",100,100)))
                .position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())));






       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

       //addroute();
        start = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        end = new LatLng(destination_Lat,destination_Long);

        Geocoder geocoder = new Geocoder(RiderActivity.this, Locale.getDefault());
        try {

            String ad="";
           List<Address> addresses= geocoder.getFromLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),1);
             String address =addresses.get(0).getAddressLine(0);
          ad=addresses.get(0).getSubLocality();
             autocompleteFragment.setText(ad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mLastKnownLocation!=null)
        {
            loadavailabledriver();
        }

        //Add Drivermarker
// mMap.animateCamera(CameraUpdateFactory.zoomTo(10));




//        loadavailabledriver();
    }
};

    }

    private Double checkradius() {

        Log.i(TAG,"Checking Radius");
        Log.i(TAG,String.valueOf(FoundDriveruid));
        databaseReference=FirebaseDatabase.getInstance()
                .getReference("Driver")
                .child(String.valueOf(FoundDriveruid));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             try {

                 Driver Driverdata = dataSnapshot.getValue(Driver.class);
                 pickupradius= Double.valueOf(Driverdata.PickupRadius);

                 Log.i(TAG, "Pick up Radius ="+ String.valueOf(pickupradius));

             }
             catch (Exception e)
             {
                 Log.i(TAG, e.getMessage());

             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   return pickupradius;
       }

    private void updateUI() {

        Ridermarker.remove();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        Ridermarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())).draggable(true));

    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    private void getDeviceLocation() {


        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 100));
          //Add Drivermarker

                  //Drivermarker=          mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())).draggable(true));



                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }




    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);



            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_screen, menu);
        return true;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
                else
                {
                    opensetting();
                }
            }
        }
        updateLocationUI();

    }


    private void opensetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri =  Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
