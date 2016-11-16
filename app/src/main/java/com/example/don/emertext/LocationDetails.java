package com.example.don.emertext;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.location.Location;
import static android.Manifest.permission_group.LOCATION;

public class LocationDetails extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = LocationDetails.class.getSimpleName();
    private static final int REQUEST_LOCATION = 1;
    private boolean locationEnabled = false;
    private Location mlocation;
    private String gps = null;
    private String stringLocation = null;
    private boolean userEnteredLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent i = getIntent();
        //Boolean longPress = i.getExtras().getBoolean("longpress");

        setContentView(R.layout.activity_location);
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

        String[] spinnerArray = getResources().getStringArray(R.array.emer_type);
        ((AutoCompleteTextView) findViewById(R.id.emertype_spinner)).setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                        spinnerArray));

//            Intent intent = new Intent(LocationDetails.this, finalscreen.class);
//            intent.putExtra("location",stringLocation);
//            intent.putExtra("gps",gps);
//            startActivityForResult(intent,1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void startMap(View view) {
        if (locationEnabled){
            Intent intent = new Intent(LocationDetails.this, MapPin.class);
            startActivityForResult(intent,1);
        }else{
            Toast.makeText(this, "Error, location data is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                stringLocation = data.getStringExtra("locationaddress");
                EditText loc = (EditText)findViewById(R.id.curLocation);
                gps = data.getStringExtra("gps");
                loc.setText(stringLocation);
                userEnteredLocation = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Error! You did not select a location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,LOCATION},
                        REQUEST_LOCATION);
            }
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mlocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            locationEnabled = true;
            populateLocation(mlocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationEnabled = false;
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            locationEnabled = false;
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        // Make sure it's our original READ_CONTACTS request
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale( this, android.Manifest.permission.ACCESS_FINE_LOCATION);
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
    public void onLocationChanged(Location location) {
        mlocation = location;
    }

    @Nullable
    private String getLocation(Location curLocation){

        double latitude = curLocation.getLatitude();
        double longitude = curLocation.getLongitude();
        gps = "Lat: " + Double.toString(latitude) + ", Lon: " + Double.toString(longitude);
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<android.location.Address> addresses;

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return addresses.get(0).getAddressLine(0);
    }

    public void populateLocation(Location curLocation){
        if (!userEnteredLocation && isNetworkConnected()){
            stringLocation = getLocation(curLocation);

            if (stringLocation == null){
                locationEnabled = false;
            }else{
                EditText locationTextBox = (EditText) findViewById(R.id.curLocation);
                locationTextBox.setText(stringLocation);
            }
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void submitInfo(View view){
        Intent i = new Intent(LocationDetails.this, MapPin.class);

        //Set the Data to pass
        EditText loc = (EditText)findViewById(R.id.curLocation);
        AutoCompleteTextView emt = (AutoCompleteTextView) findViewById(R.id.emertype_spinner);
        EditText pw = (EditText)findViewById(R.id.people_with_you);
        EditText ed = (EditText)findViewById(R.id.extra_details);
        String userLocation = loc.getText().toString();
        String emergencyType = emt.getText().toString();
        String peopleWith = pw.getText().toString();
        String extraDetails = ed.getText().toString();
        i.putExtra("userLocation", userLocation);
        i.putExtra("emergencyType", emergencyType);
        i.putExtra("peopleWith", peopleWith);
        i.putExtra("extraDetails", extraDetails);
        i.putExtra("gps",gps);

        startActivity(i);
    }
}
