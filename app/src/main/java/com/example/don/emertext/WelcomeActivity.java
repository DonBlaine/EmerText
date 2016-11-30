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


        // help gotten from https://www.youtube.com/watch?v=gh4nX-m6BEo
        // define all the buttons on the screen
        defineButtons();
    } // end onCreate

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
    }; // end OnClickListener
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
    }; // end OnLongClickListener

    private void LaunchNext() {
        if (smsGranted){
            Intent intent3 = new Intent(WelcomeActivity.this, LocationDetails.class);
            intent3.putExtra("buttonselected", buttonSelected);
            intent3.putExtra("lat", lat);
            intent3.putExtra("lon", lon);
            startActivity(intent3);
        }
    }

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
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        getCoord();
    }

    //checks permissions and returns user's lat/lon coordinates
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
    public void onConnectionSuspended(int i) {
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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
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
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void launchDetails(View view){
        Intent intent = new Intent(WelcomeActivity.this, TabbedDetails.class);
        startActivity(intent);
    }

    public void launchPasserby(View view){
        if (smsGranted){
            Intent intent = new Intent(WelcomeActivity.this, TextSpeech.class);
            startActivity(intent);
        }
    }

    //  Function to just check if we have SEND_SMS permission
    public boolean checkSendSMS(View view){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestSendSMS(View view){
        if (checkSendSMS(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    6);
            return checkSendSMS(view);
        }
    }

    //  Function to just check if we have Receive sms permission
    public boolean checkReceiveSMS(View view){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Function to check if we have receive sms and request it if we don't.
    public boolean requestReceiveSMS(View view){
        if (checkReceiveSMS(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECEIVE_SMS},
                    6);
            return checkReceiveSMS(view);
        }
    }

    public void enterDetails(View view){
        if (requestSendSMS(view) & requestReceiveSMS(view)) {
            smsGranted = true;
        }else {
            if (!requestSendSMS(view)) {
                Toast.makeText(this, "You did not grant send sms permission", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "You did not grant receive sms permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}// end main activity