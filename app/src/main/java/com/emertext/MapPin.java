package com.emertext;
//Created by Donovan Blaine
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import static android.Manifest.permission_group.LOCATION;

//This code uses a Google Map API key which only works for one Android studio license
//therefore the map may be blank if you use it from another Android Studio copy.
//for further info see:
// https://stackoverflow.com/questions/29878593/android-studio-using-google-maps-apis-key-by-multiple-developers

// This code was inspired by the tutorial http://blog.teamtreehouse.com/beginners-guide-location-android
// which deals with creating google map with the user's current location.

public class MapPin extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener {

    //create variables used in activity
    public static final String TAG = MapPin.class.getSimpleName();
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private TextView markerButton;
    private String locationAddress = null;
    private String gps = null;
    boolean nopermissions = true;


    @Override
    //create initial layout of map, start location and google api client
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pin);
        setUpMapIfNeeded();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    @Override
    //connect google api client if not connected
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        setUpMapIfNeeded();
    }

    @Override
    //disconnect google api client if connected
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    //define map if map is not set up
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    //when map is ready, added functionality to map and have map check location
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This check is handled in the onConnected method.
            return;
        }
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    //create marker on map with clickable label that repositions on camera move and displays address
    //in textview
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        markerButton = (TextView) findViewById(R.id.markerbutton);

        if (mMap != null){
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    markerButton.setVisibility(View.VISIBLE);
                    if (isNetworkConnected()) {
                        locationAddress = getLocation(mMap.getCameraPosition().target);
                    }
                    gps = getGPS(mMap.getCameraPosition().target);
                    if (locationAddress == null) {
                        Toast.makeText(getApplicationContext(), "Error, address data is unavailable",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        TextView locationTextBox = (TextView) findViewById(R.id.user_address);
                        locationTextBox.setText(locationAddress);
                    }
                }
            });
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);

        }else{
            Toast.makeText(this, "Error, unable to display map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //remove marker when map moves
    public void onCameraMove() {
        markerButton.setVisibility(View.INVISIBLE);
    }

    @Override
    //make marker visible and get address when user stops moving map
    public void onCameraIdle() {
        markerButton.setVisibility(View.VISIBLE);
        if (isNetworkConnected()) {
            locationAddress = getLocation(mMap.getCameraPosition().target);
        }
        gps = getGPS(mMap.getCameraPosition().target);
        if (locationAddress == null) {
            Toast.makeText(getApplicationContext(), "Error, address data is unavailable",
                    Toast.LENGTH_SHORT).show();
        } else {
            TextView locationTextBox = (TextView) findViewById(R.id.user_address);
            locationTextBox.setText(locationAddress);
        }
    }

    //when user clicks button set the result of this activity to be the selected
    //gps and address
    public void submitAddress(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("locationaddress", locationAddress);
        returnIntent.putExtra("gps", gps);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    //method called if user has not given permissions for location
    //this is called in previous methods however user could decide to change permissions mid-way
    //if their api is >= 23
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                nopermissions=false;
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!showRationale) {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        }

    @Override
    //method called when connected to location services
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        getCoord();
    }

    //checks permissions and starts handlenewlocation method which updates map
    private void getCoord(){
        nopermissions = (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);

        if (Build.VERSION.SDK_INT >= 23) {
            if(nopermissions){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,LOCATION},
                        REQUEST_LOCATION);
            }
        }
        if (!nopermissions && isNetworkConnected()){
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            if (location != null) {
                handleNewLocation(location);
            }
        } else {
            Toast.makeText(this, "Error, No Network Connection Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //method called when connection suspended, try to connect again
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    //method called when connection failed, try to resolve
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    //method called when location changes. After initial map creation this is not needed
    public void onLocationChanged(Location location) {
        //no override necessary
    }

    @Nullable
    //gets the address from a latlng object
    private String getLocation(LatLng curLocation){

        double latitude = curLocation.latitude;
        double longitude = curLocation.longitude;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return addresses.get(0).getAddressLine(0);
    }

    //turns latlng into the gps string that is passed to location details activity
    private String getGPS(LatLng curLocation) {
        double latitude = curLocation.latitude;
        double longitude = curLocation.longitude;

        return "Lat: " + Double.toString(latitude) + ", Lon: " + Double.toString(longitude);
    }

    //method checks if network is connected
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
