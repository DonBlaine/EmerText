package com.example.don.emertext;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    TextView network_text;
    TextView sim_text;
    TextView network_match_text;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);


        network_text = (TextView) findViewById(R.id.net_status_text);
        network_match_text = (TextView) findViewById(R.id.network_match_text);
        sim_text = (TextView) findViewById(R.id.sim_status_text);
        boolean statusOK = checkMatch() && checkSim() && checkNetwork();
        if (statusOK
                && sharedPref.getBoolean(getString(R.string.details_initialised_key), false)
                ) {

            if (sharedPref.getBoolean(getString(R.string.useFingerprint_key), false)) {
                Intent intent = new Intent(this, FingerScannerActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
            }
        } else {
            if (!statusOK) {
                View statusBlock = findViewById(R.id.network_status_layout_block);
                statusBlock.setVisibility(View.VISIBLE);
            }

        }
        }

    public int operatorCode(String mnc) {
        int value = -1;
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

    public String operatorName(String mnc) {
        String name = "Unknown";
        switch (operatorCode(mnc)) {
            case 0:
                name = "Vodafone IE";
                break;
            case 1:
                name = "Three IE";
                break;
            case 2:
                name = "Meteor";
                break;
        }
        return name;
    }

    public void setDrawableForStatusTextView(TextView textView, boolean value) {
        if (value) {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.green_check), null, null, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.red_block), null, null, null);

        }
    }

    public void setDrawableForStatusTextView(TextView textView, boolean value, String networkCode) {
        if (networkCode == null) {
            networkCode = "";
        }
        String oldText = textView.getText().toString();
        String newText = oldText + " (" + networkCode + " — " + operatorName(networkCode) + ")";
        textView.setText(newText);
        if (value) {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.green_check), null, null, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.red_block), null, null, null);

        }
    }

    public boolean checkMatch() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String connected = tel.getNetworkOperator();
        String sim = tel.getSimOperator();

        boolean isSupported = false;
        if (operatorCode(connected) == operatorCode(sim) && operatorCode(connected) > 0) {
            isSupported = true;
        }

        setDrawableForStatusTextView(network_match_text, isSupported);
        return isSupported;
    }

    public boolean checkNetwork() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        boolean isSupported = false;
        if (operatorCode(networkOperator) >= 0) {
            isSupported = true;
        }

        setDrawableForStatusTextView(network_text, isSupported, networkOperator);
        return isSupported;
    }

    public boolean checkSim() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getSimOperator();
        boolean isSupported = false;
        if (operatorCode(networkOperator) >= 0) {
            isSupported = true;
        }
        setDrawableForStatusTextView(sim_text, isSupported, networkOperator);
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

    public void registerForService(View view) {
            if (requestSMS(view)){
                sendRegistrationText();
                ((Button) view).setText("Sent");
            }
        else {
                Button button = (Button) findViewById(R.id.register_button);
                button.setText("You haven't granted SMS permissions");
            }}

    public void sendRegistrationText() {

        SmsManager text = SmsManager.getDefault();
        String number = getString(R.string.default_emergency_number);
        String message = "register";

        text.sendTextMessage(number // Number to send to
                , null               // Message centre to send to (we'll never want to change this)
                , message            // Message to send
                , null               // The PendingIntent to perform when the message is successfully sent
                , null);           // The PendingIntent to perform when the message is successfully delivered
    }
    public void enterFragment(View view){
            Intent intent = new Intent(this, TabbedDetails.class);
            startActivity(intent);

    }

}
