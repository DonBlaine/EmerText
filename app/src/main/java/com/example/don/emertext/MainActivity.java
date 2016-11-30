package com.example.don.emertext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

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


        network_text = (TextView) findViewById(R.id.net_status_text);
        network_match_text = (TextView) findViewById(R.id.network_match_text);
        sim_text = (TextView) findViewById(R.id.sim_status_text);
        boolean statusOK = (checkMatch() && checkSim() && checkNetwork()) || Utilities.SKIP_NETWORK_CHECK;
        statusOK = true;

        if (statusOK && sharedPref.getBoolean(getString(R.string.setup_complete_key), false)) {

            if (sharedPref.getBoolean(getString(R.string.useFingerprint_key), false)) {
                Intent intent = new Intent(this, SecurityActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
            }
        } else {
            if (statusOK) {
                Intent intent = new Intent(this, Initial.class);
                startActivity(intent);
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





}
