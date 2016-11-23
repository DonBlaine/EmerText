package com.example.don.emertext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class LocationDetails extends AppCompatActivity {


    private boolean locationEnabled = false;
    private String gps = null;
    private String stringLocation = null;
    private boolean userEnteredLocation = false;
    private String buttonSelected;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Double lat = i.getExtras().getDouble("lat");
        Double lon = i.getExtras().getDouble("lon");
        buttonSelected = i.getExtras().getString("buttonselected");
        sharedPref = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        String fullAddress = sharedPref.getString(getString(R.string.address_1_key), "")
                + sharedPref.getString(getString(R.string.address_2_key), "")
                + sharedPref.getString(getString(R.string.county_key), "")
                + sharedPref.getString(getString(R.string.eircode_key), "");

        setContentView(R.layout.activity_location);
        String[] spinnerArray = getResources().getStringArray(R.array.emer_type);
        ((AutoCompleteTextView) findViewById(R.id.emertype_spinner)).setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                        spinnerArray));

        if (lat != null){
            populateLocation(lat, lon);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Nullable
    private String getLocation(Double latitude, Double longitude) {

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

    public void populateLocation(Double latitude, Double longitude) {
        if (!userEnteredLocation && isNetworkConnected()) {
            stringLocation = getLocation(latitude, longitude);

            if (stringLocation == null) {
                locationEnabled = false;
            } else {
                EditText locationTextBox = (EditText) findViewById(R.id.curLocation);
                locationTextBox.setText(stringLocation);
                locationEnabled = true;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void submitInfo(View view) {
        Intent i = new Intent(LocationDetails.this, MapPin.class);

        //Set the Data to pass
        EditText loc = (EditText) findViewById(R.id.curLocation);
        AutoCompleteTextView emt = (AutoCompleteTextView) findViewById(R.id.emertype_spinner);
        EditText pw = (EditText) findViewById(R.id.people_with_you);
        EditText ed = (EditText) findViewById(R.id.extra_details);
        String userLocation = loc.getText().toString();
        String emergencyType = emt.getText().toString();
        String peopleWith = pw.getText().toString();
        String extraDetails = ed.getText().toString();
        i.putExtra("userLocation", userLocation);
        i.putExtra("emergencyType", emergencyType);
        i.putExtra("peopleWith", peopleWith);
        i.putExtra("extraDetails", extraDetails);
        i.putExtra("buttonselected", buttonSelected);
        i.putExtra("gps", gps);

        startActivity(i);
    }
}
