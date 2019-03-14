package com.example.abdul.sharetaxi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class RiderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView Ridersimage;
    TextView RidersName,Ridersrating;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    String Userid;
    private String imageurlstring;
    private URL myURL;
    private Uri profileimageuri;
    private String RidersNametxt="";
    private String Catagory= " ";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        Getpermission();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SHARE TAXI");
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view1);
        View headerview = navigationView.inflateHeaderView(R.layout.nav_header_map_screen);
        Ridersimage = headerview.findViewById(R.id.Riderimage);
        RidersName = headerview.findViewById(R.id.RiderNameid);
        Ridersrating = headerview.findViewById(R.id.Ratingid);
        getdatabaseref();
        loadiprofileimage();
        Log.i("iamtag", " Inside RiderActivity");
        getusernametxt();
        getprofileimgurl();
        loadiprofileimage();
        setnavigationitemlistner();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void getdatabaseref() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        Userid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

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

    private void setnavigationitemlistner() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Rentcarid: {
                        Toast.makeText(RiderActivity.this, "Rent car id  ", Toast.LENGTH_LONG).show();

                    }
                    break;
                    case R.id.Become_Lessorid: {
                        Toast.makeText(RiderActivity.this, "Become Leaser", Toast.LENGTH_LONG).show();

                    }
                    break;

                }
                return true;
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
           case R.id.home:
           {
               drawerLayout.openDrawer(GravityCompat.START);
        return  true;
           }
       }

           return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager)
                getSystemService(RiderActivity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));

        LatLng myLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        // Add a marker in Sydney and move the camera
  //      LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(myLatLng).title("Marker in Current Loc"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_screen, menu);
        return true;


    }


    private void Getpermission() {

        Dexter.withActivity(RiderActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {


                AlertDialog.Builder builder = new AlertDialog.Builder(RiderActivity.this);
                builder.setTitle("Permission Needed");
                builder.setMessage("You Denied Permission for map go to setting to enable them");
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

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        });

    }

    private void opensetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri =  Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }


}
