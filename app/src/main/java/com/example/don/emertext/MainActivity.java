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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    TextView network_text;
    ImageView network_image;
    TextView sim_text;
    ImageView sim_image;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);


        network_text = (TextView) findViewById(R.id.net_status_text);
        network_image = (ImageView) findViewById(R.id.network_icon);
        sim_text = (TextView) findViewById(R.id.sim_status_text);
        sim_image = (ImageView) findViewById(R.id.sim_icon);
        checkNetwork();
        checkSim();


        if (checkNetwork() && checkSim() && false) {

            if (sharedPref.getBoolean(getString(R.string.useFingerprint_key), false)) {
                String finger = "Trying to access fingerprint authentication";
                Toast toast = Toast.makeText(this, finger, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
            }
        }
        }


    public int operatorCode(String mnc) {
        int value = 0;
        String[] operators = {"27201", "27202", "27203", "27205"};
        for (int index = 0; index < operators.length; index++) {
            if (mnc.equals(operators[index])) {
                value = index;
            }
        }
        //Network codes 27205 and 27202 are both for Three IE, so return the same value
        if (value == 3) {
            value = 1;
        }
        return value;
    }

    public boolean checkNetwork() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        // String sim = tel.getSimOperator();
        boolean isSupported = false;
        if (operatorCode(networkOperator) > 0) {
            isSupported = true;
        }
        // if (!sim.equals(networkOperator)){
        //      if (!(sim.equals("27202") || sim.equals("27205")) && (networkOperator.equals("27202") || networkOperator.equals("27205"))){
        //         isSupported=false;
        //     }


        if (isSupported) {
            network_text.setText("Connected network should be supported. (" + networkOperator + ")");
            network_image.setImageDrawable(getDrawable(R.drawable.green_check));
        } else {
            network_text.setText("Connected network is not supported. (" + networkOperator + ")");
            network_image.setImageDrawable(getDrawable(R.drawable.red_block));

        }
        return isSupported;
    }

    public boolean checkSim() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getSimOperator();
        // String sim = tel.getSimOperator();
        boolean isSupported = false;
        if (operatorCode(networkOperator) > 0) {
            isSupported = true;
        }
        // if (!sim.equals(networkOperator)){
        //      if (!(sim.equals("27202") || sim.equals("27205")) && (networkOperator.equals("27202") || networkOperator.equals("27205"))){
        //         isSupported=false;
        //     }


        if (isSupported) {
            sim_text.setText("SIM network should be supported. (" + networkOperator + ")");
            sim_image.setImageDrawable(getDrawable(R.drawable.green_check));
        } else {
            sim_text.setText("SIM network is not supported. (" + networkOperator + ")");
            sim_image.setImageDrawable(getDrawable(R.drawable.red_block));

        }
        return isSupported;
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
                Intent intent = new Intent(this, TabbedDetails.class);
            startActivity(intent);}
        else {
                Button button = (Button) findViewById(R.id.button);
                button.setText("You haven't granted SMS permissions");
            }}

    public void enterFragment(View view){
            Intent intent = new Intent(this, TabbedDetails.class);
            startActivity(intent);

    }}
