package com.example.don.emertext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view=getCurrentFocus();
        checkNetwork(view);

        }


//  Function to just check if we have SEND_SMS permission
    public boolean checkSMS(View view){
        boolean permissionGranted = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            return true;
        }
        else
        {  return false;
        }
    }

    public void checkNetwork(View view){
        String[][] SUPPORTED_OPERATORS = {{"27201","Vodafone IE"},{"27202","3 IE"},{"27203", "Meteor"},{"27205","Three IE"}};
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        String sim = tel.getSimOperator();
        boolean isSupported=false;
        int location=-1;
        for (int i=0;i<SUPPORTED_OPERATORS.length;i++){
            if (SUPPORTED_OPERATORS[i][0].equals(networkOperator)) {
                isSupported=true;
                location=i;}
        }
        if (!sim.equals(networkOperator)){
            if (!(sim.equals("27202") || sim.equals("27205")) && (networkOperator.equals("27202") || networkOperator.equals("27205"))){
                isSupported=false;
            }

        }
        if (isSupported){
        Context context = getApplicationContext();
        CharSequence text = "You appear to be using " + SUPPORTED_OPERATORS[location][1] + ". This service should be supported";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    else {
            Intent intent = new Intent(this, UnsupportedNetwork.class);
            startActivity(intent);
        }}

// Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestSMS(View view){
        if (checkSMS(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    6);
            if (checkSMS(view)) {
                return true;
            }
            else{
                return false;
            }
        }
    }

    public void enterDetails(View view){
            if (requestSMS(view)){
            Intent intent = new Intent(this, DetailsForm.class);
            startActivity(intent);}
        else {
                Button button = (Button) findViewById(R.id.button);
                button.setText("You haven't granted SMS permissions");
            }}

    public void enterFragment(View view){
            Intent intent = new Intent(this, TabbedDetails.class);
            startActivity(intent);

    }}
