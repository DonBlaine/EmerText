package com.example.don.emertext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

//Setting Utilities.skip_network_check to true allowing the app to be used outside of Ireland's
//supported networks
public class MainActivity extends AppCompatActivity {
    private TextView network_text;
    private TextView sim_text;
    private TextView network_match_text;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        findViewById(R.id.ignore_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });

        // Find EditTexts for the network status in case they need to be shown
        network_text = (TextView) findViewById(R.id.net_status_text);
        network_match_text = (TextView) findViewById(R.id.network_match_text);
        sim_text = (TextView) findViewById(R.id.sim_status_text);

        boolean statusOK = (checkMatch() && checkSim() && checkNetwork()) || Utilities.SKIP_NETWORK_CHECK;
        //Utilities.Skip_network_check  is set to true for app use in other countries.

        if (statusOK && sharedPref.getBoolean(getString(R.string.setup_complete_key), false)) {
            //If status is OK and is set to lock app, send to security screen
            if (sharedPref.getBoolean(getString(R.string.useFingerprint_key), false)) {
                Intent intent = new Intent(this, SecurityActivity.class);
                startActivity(intent);
            }
            // Otherwise to the welcome screen
            else {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
            }
        }
        // Otherwise send to the initialisation screen
        else {
            if (statusOK) {
                Intent intent = new Intent(this, Initial.class);
                startActivity(intent);
            }

            }

        }


    public int operatorCode(String mnc) {
        //Simple function to get the corresponding network code (specific to our implementation)
        // for a given MNC code
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
        //Function to get the network name for an MNC, Unknown if not one of the 3 supported ones
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
        //Function to set the drawable to a tick or a red circle depending on whether or not the check passed
        if (value) {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.green_check), null, null, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.red_block), null, null, null);

        }
    }

    public void setDrawableForStatusTextView(TextView textView, boolean value, String networkCode) {
        //Function to set the drawable to a tick or a red circle depending on whether or not the check passed
        // and append the detected values
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
        //Function to verify the connected network is the same as the SIM
        //A Meteor SIM roaming on Vodafone Ireland (both of which are supported on their own networks) is not guaranteed to work
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
        //Function to check the network is supported
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
        //Function to check the SIM is supported
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getSimOperator();
        boolean isSupported = false;
        if (operatorCode(networkOperator) >= 0) {
            isSupported = true;
        }
        setDrawableForStatusTextView(sim_text, isSupported, networkOperator);
        return isSupported;
    }





}
