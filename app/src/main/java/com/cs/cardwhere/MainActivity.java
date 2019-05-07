package com.cs.cardwhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static int REQUEST_LOCATION = 1;

    // android location
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;

    double mLatitude;
    double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Toolbar
        initToolbar();
        // Init Bottom Navigation View => set two fragment card and account
        initBottomNavigationView();
        // Init Current Location
        initLocation();
    }

    private void initToolbar(){
        // Action Bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initBottomNavigationView(){
        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Bottom Navigation
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // initial fragment is Card List Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_card:
                    selectedFragment = new CardFragment();
                    break;
                case R.id.nav_account:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, "MainFragmentsTag").commit();

            return true;
        }
    };


    // handle toolbar search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle toolbar item (add card) clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){
            case R.id.add_card:
                intent = new Intent(this, ScanCardActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                // initial fragment is Card List Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardFragment()).commit();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }


    // get current location
    public void initLocation(){
        // GoogleApiClient allows to connect to remote services, the two listeners are the first
        // two interfaces the current class implements
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // LocationReques sets how often etc the app receives location updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // start the connection then get the location
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // check if the current app has permission to access location of the device
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            // This ACCESS_COARSE_LOCATION corresponds to permission defined in manifest
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude=mLastLocation.getLongitude();

                //log the result
                Log.d("main", "onConnected: get the latitude" + mLatitude);
                Log.d("main", "onConnected: get the longitude" + mLongitude);

                // Local Storage
                SharedPreferences sharedPreferences;
                sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LOCATION_LATITUDE", String.valueOf(mLatitude));
                editor.putString("LOCATION_LONGITUDE", String.valueOf(mLongitude));
                editor.commit();
            }

            // the last parameter specify the onLocationChanged listener
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude=location.getLongitude();

        //log the result
        Log.d("main", "onLocationChanged: get the latitude" + mLatitude);
        Log.d("main", "onLocationChanged: get the longitude" + mLongitude);
    }
}
