package com.example.abdul.sharetaxi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
   // private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private GoogleMap mMap;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView Driversimage;
    TextView Driversname, Driversrating;
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
    public    TextView Customername,CustomerRating;
    public    ImageView CustomerImage,CallCustomer;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mlocationCallback;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private boolean mLocationPermissionGranted;
    private String TAG = "iamtag";
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Boolean mRequestingLocationUpdates;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    GeoFire geoFire;
    private double destination_Long = 0;
    private double destination_Lat = 0;
    Marker marker;
    Switch goOnline;
    private View pickUprdview;
    private EditText pickUpradEdTxt;
    private EditText SeatsavailEdTxt,DestEdTxt;
    private String pickUpradSt="";
    private String SeatsavailSt="";
    private String DestSt="";
    private AlertDialog.Builder builder,builder1;
    private AlertDialog dialog,dialogExit;
    private boolean ischeckinrange;
    private int minradius=1,maxradius=6;
    private String Assignedcustomerid;
    public   RelativeLayout Customerrequestrel;
    private List<Polyline> polylines;
    private MediaPlayer mediaPlayer;
    private View customrequestview;
    private DatabaseReference requestref;
    private boolean requestaccepted;
    private double latitude;
    private double longitude;
    private Marker Ridermarker;
    private Marker Pickupmarker;
    private DatabaseReference Assigncustomer;
    private Rider Rider;
    private String customerNumber;
    private View Includelayout;
    LatLng start,end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);

        polylines = new ArrayList<>();

           Customerrequestrel=findViewById(R.id.Customer_detail);
           Customerrequestrel.setVisibility(View.GONE);
        //Setting Toolbar


        toolbar = (Toolbar) findViewById(R.id.toolbarDriver);
        toolbar.setTitle("Driver");
        setSupportActionBar(toolbar);


        //Getting header Reference
        navigationView = findViewById(R.id.DriverNavView);
        View headerview = navigationView.inflateHeaderView(R.layout.nav_header_map_screen);
        Driversimage = headerview.findViewById(R.id.Riderimage);
        Driversname = headerview.findViewById(R.id.RiderNameid);
        Driversrating = headerview.findViewById(R.id.Ratingid);


        Log.i("iamtag", " Inside DriverActivity");

        //Loadin Profile Details
        getdatabaseref();
        loadiprofileimage();
        getusernametxt();
        getprofileimgurl();
        loadiprofileimage();


        //Setting Nav Listner
        setnavigationitemlistner();


        //Setting Drawer Toogle

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DriverDrawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Ask Permission

        checkpermission();


        //mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.drivermap);
        mapFragment.getMapAsync(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //String apiKey = String.valueOf(R.string.Google_Places_api_key);
        String apiKey = "AIzaSyDgkY5iTRu-SPvkF3AHkJK_CLgDfhS3hcE";
        final String placeId = "INSERT_PLACE_ID_HERE";
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);


        //Initialize Places
        Places.initialize(getApplicationContext(), apiKey);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dest_autocomp_fragment_Driver);

        autocompleteFragment.setPlaceFields(placeFields);



        //Placesauto complete Fragment
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                String address = place.getAddress();
                place.getName();
                destination_Long=place.getLatLng().longitude;
                destination_Lat=place.getLatLng().latitude;
                Log.i(TAG,String.valueOf("Latitude " +destination_Lat + " Longitude" + destination_Long));
                Log.i(TAG,String.valueOf(place.getName()));
                Log.i(TAG,String.valueOf(place.getAddress()));
                Log.i(TAG,String.valueOf(place.getName()));
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        //goOnline
         goOnline = findViewById(R.id.switchOnline);
         goOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    askpickupradius();
                    putLocatointogeofire();
                    setRidefoundListner();


                    Log.i(TAG,String.valueOf(" last longitude" + mLastKnownLocation.getLatitude()));

                }
                else if(isChecked!=true)
                {
                    Toast.makeText(DriverActivity.this,"Offline",Toast.LENGTH_LONG).show();
                     Log.i(TAG,String.valueOf(" last longitude" + mLastKnownLocation.getLatitude()));
                 }

             }
         });

    }

    private void setRidefoundListner() {

        DatabaseReference  Assigncustomer =    FirebaseDatabase
                .getInstance()
                .getReference("Pickuprequest")
                .child(Userid).child("RiderId");

        Assigncustomer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean requestexist = dataSnapshot.exists();
                Log.i(TAG,String.valueOf(requestexist));

         if(requestexist)
{

        showalertdialog();

        Log.i(TAG, "Checking Assigned Customer" + String.valueOf(dataSnapshot.exists()));
        Assignedcustomerid = (String) dataSnapshot.getValue();

        Log.i(TAG, "Checking Assigned Customer" + String.valueOf(Assignedcustomerid));

}
else
         {

         }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showalertdialog() {

        builder1= new AlertDialog.Builder(DriverActivity.this);
        builder1.setTitle("You got Ride Request");


        builder1.setPositiveButton("Accept ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder1.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        LayoutInflater inflater = getLayoutInflater();
        customrequestview=inflater.inflate(R.layout.got_pickuprequest, null);
        builder1.setView(customrequestview);
        dialogExit = builder1.create();

        dialogExit.show();
       //mediaPlayer // mediaPlayer = MediaPlayer.create(this , RingtoneManager.getValidRingtoneUri(this));

        //mediaPlayer.start();
        dialogExit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG,"Accept Button clicked");
                requestaccepted =true;

                requestcceptedusecse();





          //      mediaPlayer.stop();
                dialogExit.dismiss();
            }
        });


        dialogExit.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Reject Button clicked");
                // removedrivervalues();
               requestaccepted =false;
               requestrejectedusecase();


            //    mediaPlayer.stop();
                dialogExit.dismiss();

            }
        });

dialogExit.setOnDismissListener(new DialogInterface.OnDismissListener() {
    @Override
    public void onDismiss(DialogInterface dialog) {
      //  mediaPlayer.stop();
    }
});




    }

    private void requestrejectedusecase() {



        DatabaseReference Assigncustomer = FirebaseDatabase
                .getInstance()
                .getReference("Pickuprequest")
                .child(Userid);
        Assigncustomer.child("Requeststatus").setValue("rejected");




        dialogExit.dismiss();
    }

    private void requestcceptedusecse() {
        //Set status
        dialogExit.dismiss();

        DatabaseReference pickupreqref = FirebaseDatabase
                .getInstance()
                .getReference("Pickuprequest")
                .child(Userid);

       pickupreqref.child("Requeststatus").setValue("accepted");



        Assigncustomer = FirebaseDatabase
                .getInstance()
                .getReference("Rider")
                .child(Assignedcustomerid);
          Assigncustomer.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  Rider = dataSnapshot.getValue(Rider.class);

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }




          });
      DatabaseReference  riderlocation =    FirebaseDatabase
                .getInstance()
                .getReference("Pickuprequest")
                .child(Assignedcustomerid).child("l");

              riderlocation.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        List<Object> map=    (List<Object>) dataSnapshot.getValue();
        longitude= Double.parseDouble(map.get(0).toString());
        latitude= Double.parseDouble(map.get(1).toString());
        Log.i(TAG,String.valueOf("customar latitude" + latitude));
        Pickupmarker = mMap.addMarker
                (new MarkerOptions()
                        .position(new LatLng(longitude, latitude))
                        .title("Pickup here")
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pickup_web", 100, 100))));

         showriderinfo();


    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});



    }


    private void addroute() {

        RoutingListener routingListener = new RoutingListener() {
            @Override
            public void onRoutingFailure(RouteException e) {

                if(e != null) {

                    Toast.makeText(DriverActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG,String.valueOf(e.getMessage()));
                }else {
                    Toast.makeText(DriverActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
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
                .waypoints(start, end)
                .build();
        routing.execute();
        Log.i(TAG,"adding Route");
    }

    private void showriderinfo() {
   //LayoutInflater inflater = getLayoutInflater();
   //View customerdetail=inflater.inflate(R.layout.customer_detail,Customerrequestrel,false);


            Includelayout= findViewById(R.id.include_id);
            Customername=Includelayout.findViewById(R.id.Customername);
   CustomerRating=Includelayout.findViewById(R.id.Customer_rating);
   CallCustomer=Includelayout.findViewById(R.id.Call_customer);
   CustomerImage=Includelayout.findViewById(R.id.Customer_image);
   Customername.setText(Rider.Name);
   CustomerRating.setText("4");
   Log.i(TAG,String.valueOf(Rider.Phonenumber));
    customerNumber = Rider.Phonenumber;
   if(Rider.downloadimageurl!=null) {
       Picasso.get().load(Rider.downloadimageurl).into(Driversimage);
   }
   Customerrequestrel.setVisibility(View.VISIBLE);

CallCustomer.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerNumber, null));
        startActivity(intent);

        startActivity(intent);
    }
});
       addroute();
    }

    private void askpickupradius()
    {
          builder = new AlertDialog.Builder(DriverActivity.this);
         pickUprdview=getLayoutInflater().inflate(R.layout.pickupradius,null);
         pickUpradEdTxt=   pickUprdview.findViewById(R.id.RadiusEdtxt);
         SeatsavailEdTxt =pickUprdview.findViewById(R.id.seatsavailEdtxt);
         DestEdTxt =pickUprdview.findViewById(R.id.DestinationEdtxt);

         builder.setView(pickUprdview);
          builder.setTitle("Enter Pickup Radius(Km)");
          builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {

              }
          });

          dialog =builder.create();
dialog.show();

dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        pickUpradSt=pickUpradEdTxt.getText().toString();
        SeatsavailSt=SeatsavailEdTxt.getText().toString();
        DestSt=DestEdTxt.getText().toString();
        checkfieldstoprocess();

    }


});



    }

    private void checkfieldstoprocess() {

        if(pickUpradSt.isEmpty()||SeatsavailSt.isEmpty()||DestSt.isEmpty())
        {
            if(pickUpradSt.isEmpty())
            {
                pickUpradEdTxt.setError("Enter Pickup radius");

            }

            if(SeatsavailSt.isEmpty())
            {
                SeatsavailEdTxt.setError("Enter Available Seats");

            }
            if(DestSt.isEmpty() )
            {
                DestEdTxt.setError("Enter Destination");

            }
        }
        else if(checkinRange())
        {
            Log.i(TAG, "CheckInRange ="  + String.valueOf(String.valueOf(checkinRange())));


            Log.i(TAG, String.valueOf(" pick up radius" + pickUpradEdTxt.getText().toString()));
            //   String pickupradiusSt = pickUpradEdTxt.getText().toString();
            databaseReference.child(Userid).child("PickupRadius").setValue(pickUpradSt);
            databaseReference.child(Userid).child("SeatsAvailable").setValue(SeatsavailSt);
            databaseReference.child(Userid).child("SeatsAvailable").setValue(SeatsavailSt);
            dialog.dismiss();
        }
        else if(false==checkinRange())
        {
            Log.i(TAG, "CheckInRange ="  + String.valueOf(String.valueOf(checkinRange())));
         pickUpradEdTxt.setError("Enter Radius in Between "
                 + String.valueOf(minradius) +
                 "-" +String.valueOf( maxradius));
        }

    }

    private boolean checkinRange() {



    return ischeckinrange=true;

    }


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void putLocatointogeofire() {
        final DatabaseReference OnlineDriver  =FirebaseDatabase.getInstance().getReference("OnlineDrivers");
        GeoFire geoFire = new GeoFire(OnlineDriver);
        GeoLocation geoLocation = new GeoLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());


        geoFire.setLocation(Userid, geoLocation, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error!=null)
                {
                    Log.i(TAG,String.valueOf(error));

                }
                else
                {
                    Log.i(TAG,String.valueOf(error));

                }


            }
        });
    }

    private void checkpermission() {


        if (ContextCompat.checkSelfPermission(DriverActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            {

                ActivityCompat.requestPermissions(DriverActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                        );

            }



/*
        Dexter.withActivity(DriverActivity.this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()) {
                    opensetting();
                }



            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        });*/


        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        {
            Log.i(TAG, "Received response for ACCESS COARSE PERMISSION request.");
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Coarse permission granted");
            }
      else if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED);

            {

                opensetting();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);





    }

    private void getdatabaseref() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        Userid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapready");
        mMap = googleMap;

        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // TODO: Consider calling

         ActivityCompat
                 .requestPermissions(DriverActivity.this,
                         new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                         PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        } else {
            readylocatiocallback();
            updateLocationUI();
            createLocationRequest();
        }
        mMap.setMyLocationEnabled(true);
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(100);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            checkpermission();
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
                checkpermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }




    private void getusernametxt() {
        databaseReference.child(Userid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RidersNametxt = (String) dataSnapshot.getValue();
                Driversname.setText(RidersNametxt);
                Log.i("iamtag", " Value of DriverName is" + " " + Driversname );
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
                Picasso.get().load(imageurlstring).into(Driversimage);
                Log.i("iamtag", " Value of imageuri" + " " + imageurlstring);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Need to Get the rating right here
    }

    private void loadiprofileimage() {
        Picasso.get().load(imageurlstring).into(Driversimage);
        Log.i("iamtag", "image profile Loaded");
        return;
    }

    private void readylocatiocallback() {


        mlocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastKnownLocation=locationResult.getLastLocation();
                Log.i(TAG,"Location change");



                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));


                //Add Drivermarker
// mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                if(marker!=null)
                {
                    marker.remove();
                }

                marker= mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_car_web",100,100))).draggable(true));




            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_screen, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

        }

        return super.onOptionsItemSelected(item);
    }


    private void setnavigationitemlistner() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Rentcarid: {
                        Toast.makeText(DriverActivity.this, "Rent car id  ", Toast.LENGTH_LONG).show();
                  Intent intent = new Intent(DriverActivity.this,PutCarForRent.class);
                  startActivity(intent);


                    }

                    break;
                    case R.id.LogoutDriver: {
                        try {
                            FirebaseAuth.getInstance().signOut();
                            Toast toast = Toast.makeText(DriverActivity.this,"Succefully Signout",Toast.LENGTH_LONG);
                            toast.show();
                            finish();




                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                    break;

                    case R.id.SettingRider: {


                    }

                    break;
                    case R.id.ReviewsDriver: {

                        Intent intent = new Intent(DriverActivity.this,ReviewHistory.class);
                        startActivity(intent);

                    }

                    break;
                    case R.id.HistoryDriver: {


                    }

                    break;
                }
                return true;
            }
        });

    }


    private void opensetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri =  Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {



        builder1= new AlertDialog.Builder(DriverActivity.this);
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
dialogExit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Log.i(TAG,"StayOnline button Clicked");
        dialogExit.dismiss();
    }
});


    dialogExit.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"Exitbutton button Clicked");
         // removedrivervalues();
            finish();
            dialogExit.dismiss();

        }
    });





    }

    private void removedrivervalues() {
        final DatabaseReference OnlineDriver  =FirebaseDatabase.getInstance().getReference("OnlineDrivers");
        OnlineDriver.removeValue();
        databaseReference.child(Userid).child("PickupRadius").removeValue();
        databaseReference.child(Userid).child("SeatsAvailable").removeValue();
        databaseReference.child(Userid).child("SeatsAvailable").removeValue();


    }


}
