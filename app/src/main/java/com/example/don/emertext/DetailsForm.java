package com.example.don.emertext;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.HttpURLConnection;


public class DetailsForm extends Activity {

    private boolean overrideNumber = false;
    private String EMERGENCY_NUMBER = "0831453528";
    private int overrideCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);

        //Set the contents for the county selector
        Spinner spinner = (Spinner) findViewById(R.id.county_select_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.county_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



    }




    public String composeMessage(View view) {
        EditText firstname = (EditText) findViewById(R.id.fnameInput);
        EditText lastname = (EditText) findViewById(R.id.lnamepinput);
        EditText address1 = (EditText) findViewById(R.id.address1);
        EditText address2 = (EditText) findViewById(R.id.address2);

        String message = firstname.getText().toString() + " " +
                lastname.getText().toString() + " " +
                address1.getText().toString() + " " +
                address2.getText().toString() + " " +
                "requires an ambulance.";
        return message;
    }


    public void checkNetwork(View view) {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        if (networkOperator.equals("27203")) {

        }
    }

    public void revealOverride(View view) {
        if (++overrideCounter >= 5) {
            (findViewById(R.id.overrideNumber)).setVisibility(View.VISIBLE);
            overrideNumber = true;
        }
    }

    public void sendSMS(View view) {

        SmsManager text = SmsManager.getDefault();
        String number;
        if (overrideNumber) {
            number = ((EditText) findViewById(R.id.overrideNumber)).getText().toString();
        } else {
            number = EMERGENCY_NUMBER;
        }
        String message = composeMessage(view);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent intent = PendingIntent.getActivity(this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        text.sendTextMessage(number // Number to send to
                , null               // Message centre to send to (we'll never want to change this)
                , message            // Message to send
                , null               // The PendingIntent to perform when the message is successfully sent
                , intent);           // The PendingIntent to perform when the message is successfully delivered
        TextView textView = (TextView) findViewById(R.id.return_message);
        textView.setVisibility(view.VISIBLE);
    }


}
