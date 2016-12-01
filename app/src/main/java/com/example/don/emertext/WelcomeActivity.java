package com.example.don.emertext;
//created by Donovan Blaine

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission_group.LOCATION;


public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //creating variables used in activity
    public static final String TAG = WelcomeActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private String buttonSelected = "";
    private Double lat = null;
    private Double lon = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean smsGranted = false;
    private boolean nopermissions = true;


    @Override
    //setup initial page layout and create location/googleapi client
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        enterDetails(null);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000);

        defineButtons();
    }

    @Override
    //start resources on resume.
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    //stop resources on pause
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    //define buttons for clickability
    public void defineButtons(){
        findViewById(R.id.details_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.talk_stranger_btn).setOnClickListener(buttonClickListener);

        findViewById(R.id.fire_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.ambu_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.garda_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.coast_btn).setOnClickListener(buttonClickListener);

        findViewById(R.id.fire_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.ambu_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.garda_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.coast_btn).setOnLongClickListener(buttonLongClickListener);
    }

    //set up button click listener with a actions on click
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.details_btn:
                    //change to point to user details page
                    launchDetails(view);
                    break;
                case R.id.talk_stranger_btn:
                    //change to point to the talk to stranger page
                    launchPasserby(view);
                    break;
                case R.id.fire_btn:
                    buttonSelected = "Fire";
                    LaunchNext();
                    break;
                case R.id.ambu_btn:
                    buttonSelected = "Ambulance";
                    LaunchNext();
                    break;
                case R.id.garda_btn:
                    buttonSelected = "Garda";
                    LaunchNext();
                    break;
                case R.id.coast_btn:
                    buttonSelected = "Coast Guard";
                    LaunchNext();
                    break;
            }
        }
    };

    //set up button click listner for long click functionality
    private View.OnLongClickListener buttonLongClickListener = new View.OnLongClickListener() {
        // if a button is long clicked go straight to the final text screen.
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.fire_btn:
                    buttonSelected = "Fire";
                    LaunchFinal();
                    break;
                case R.id.ambu_btn:
                    buttonSelected = "Ambulance";
                    LaunchFinal();
                    break;
                case R.id.garda_btn:
                    buttonSelected = "Garda";
                    LaunchFinal();
                    break;
                case R.id.coast_btn:
                    buttonSelected = "Coast Guard";
                    LaunchFinal();
                    break;
            }
            return true;
        }
    };

    //define method to start intent on button short click to location details activity
    //passes in user's lat/lon coordinates and the button they selected
    private void LaunchNext() {
        if (smsGranted){
            Intent intent3 = new Intent(WelcomeActivity.this, LocationDetails.class);
            intent3.putExtra("buttonselected", buttonSelected);
            intent3.putExtra("lat", lat);
            intent3.putExtra("lon", lon);
            startActivity(intent3);
        }
    }

    //define method to start intent on button long click to messagescreeninteraction activity
    private void LaunchFinal() {
        if (smsGranted){
            Intent intent3 = new Intent(WelcomeActivity.this, MessageScreenInteraction.class);
            intent3.putExtra("buttonselected", buttonSelected);
            String gps = "Lat: " + Double.toString(lat) + ", Lon: " + Double.toString(lon);
            intent3.putExtra("gps", gps);
            startActivity(intent3);
        }
    }

    @Override
    //method called when checking location connected
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        getCoord();
    }

    //checks permissions and returns updates lat/lon variables
    //checks what permissions user has granted and network connectivity
    private void getCoord(){
        nopermissions = (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);

        if (Build.VERSION.SDK_INT >= 23) {
            if (nopermissions) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION},
                        Utilities.REQUEST_LOCATION);
            }
        }
        if (isNetworkConnected() && !nopermissions) {
            Location mlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mlocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                mlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            if (mlocation!=null){
                lat = mlocation.getLatitude();
                lon = mlocation.getLongitude();
            }
        } else {
            Toast.makeText(this, "Error, No Network Connection Detected", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    //method called when connection is suspended.
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    //method called when connection has failed.
    //resolve connection if possible
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
    //method called if user did not grant permissions and api >= 23
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        // Make sure it's our original READ_CONTACTS request
        if (requestCode == Utilities.REQUEST_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                nopermissions = false;
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!showRationale) {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    //method called when user's location changes, updates lat/lon variables
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    //checks if user is connected to a network and returns boolean
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //start tabbed details activity if user clicks on button
    public void launchDetails(View view){
        Intent intent = new Intent(WelcomeActivity.this, TabbedDetails.class);
        startActivity(intent);
    }

    //start textspeech activity if user clicks on button
    public void launchPasserby(View view){
        if (smsGranted){
            Intent intent = new Intent(WelcomeActivity.this, TextSpeech.class);
            startActivity(intent);
        }
    }

    //  Function to just check if we have SEND_SMS permission
    public boolean checkSendSMS(){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestSendSMS(){
        if (checkSendSMS()){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    6);
            return checkSendSMS();
        }
    }

    //  Function to just check if we have Receive sms permission
    public boolean checkReceiveSMS(){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Function to check if we have receive sms and request it if we don't.
    public boolean requestReceiveSMS(){
        if (checkReceiveSMS()){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECEIVE_SMS},
                    6);
            return checkReceiveSMS();
        }
    }

    //method to check if user has granted send and receive sms functionality
    public void enterDetails(View view){
        if (requestSendSMS() & requestReceiveSMS()) {
            smsGranted = true;
        }else {
            if (!requestSendSMS()) {
                Toast.makeText(this, "You did not grant send sms permission", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "You did not grant receive sms permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}