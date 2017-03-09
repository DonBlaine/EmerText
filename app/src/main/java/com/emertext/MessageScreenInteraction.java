package com.emertext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

// The activity to exchange messages with the emergency services or the number that the
// user sets manually
public class MessageScreenInteraction extends AppCompatActivity {

    EditText msgText;
    ScrollView scroll;
    String number;
    private BroadcastReceiver mIntentReceiver;

    // It creates a predefined message that we send to the number in our database
    // which is sent on the first instance that you enter the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.emergency_service_number_key), getString(R.string.default_emergency_number));
        number = number.replaceAll("\\s","");
        if(number.equals("")){
            number = getString(R.string.default_emergency_number);
        }
        setContentView(R.layout.activity_message_screen_interaction);
        msgText = (EditText) findViewById(R.id.messageText);
        scroll = (ScrollView) findViewById(R.id.scroller);


        //get data from previous activities
        Intent i = getIntent();
        String gps = i.getExtras().getString("gps");
        String buttonSelected = i.getExtras().getString("buttonselected");
        String userLocation = i.getExtras().getString("userLocation");
        if (userLocation == null) userLocation = "n/a";
        String emergencyType = i.getExtras().getString("emergencyType");
        String peopleWith = i.getExtras().getString("peopleWith");
        String extraDetails = i.getExtras().getString("extraDetails");

        // Constructing the inittial message
        String message =
                "Please send: " + buttonSelected + ". \n" +
                "Location: " + userLocation + ". \n";

        if (gps != null && !gps.equals(""))
            message = message + "GPS: " + gps + ". \n";
        if (peopleWith != null && !peopleWith.equals(""))
            message = message + "People with me: " + peopleWith + ". \n";
        if (extraDetails != null && !extraDetails.equals(""))
            message = message + "Additional details: " + extraDetails + ". \n";
        if (emergencyType != null && !emergencyType.equals(""))
            message = message + "Emergency Type: " + emergencyType + ". \n";

        if (i.getBooleanExtra("include_medical", false)) {
            message += " Medical Information: " + getSharedPreferences(
                    getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.medical_conditions_key), " ")
                    + " " + getSharedPreferences(
                    getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.current_meds_key), " ")
                    + " " + getSharedPreferences(
                    getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.allergies_key), " ") +
                    ". \n";
        }

        if (i.getBooleanExtra("include_ice", false)) {
            message += " Emergency contact: " + getSharedPreferences(
                    getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.emergency_contact_name_key), " ")
                    + " at " + getSharedPreferences(
                    getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.emergency_contact_number_key), " ") +
                    ". \n"
            ;
        }


        // setting this as text in edit text since future implementation is set up based
        // on text in edit text
        msgText.setText(message);
        sendSMS(findViewById(android.R.id.content).getRootView());


    }

    // Receiving the message from the Broadcast receiver ans displaying it in the screen.
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.EMERGENCY");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");
                showReceiverMessage(msg);
            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
    }


    // unregistering receiver on pausing the activity since we do not want
    // to receive messages inside the screen once the screen loses focus.
    @Override
    protected void onPause() {

        super.onPause();
        this.unregisterReceiver(this.mIntentReceiver);
    }

    // function to send sms
    public void sendSMS(View view) {
        if (number != null){

            SmsManager text = SmsManager.getDefault();
            String message;
            TextView q = (TextView) findViewById(R.id.messageText);
            // converting text in textview to string
            message = q.getText().toString();
            String currentmessage;
            // Dividing messages into subpart if the length of message is > 160
            for (int part = 0; part <= message.length() / 160; part++) {
                if (part == message.length() / 160) {
                    currentmessage = message.substring(160 * part);
                } else {
                    currentmessage = message.substring(160 * part, 160 * part + 160);
                }
                text.sendTextMessage(number // Number to send to
                        , null               // Message centre to send to (we'll never want to change this)
                        , currentmessage            // Message to send
                        , null               // The PendingIntent to perform when the message is successfully sent
                        , null);           // The PendingIntent to perform when the message is successfully delivered
            }
            message = message.trim();
            // displaying the message if it is not empty but allowing the user to send
            // blank message in case the user is in extreme emergency.
            // putting this check before sending SMS will stop the user from sending empty messages though.
            if (!message.equals("")) {
                showSenderMessage(message);
            }
        }
    }

    // Displaying sender message on screen
    public void showSenderMessage(String message){


        LinearLayout ll = (LinearLayout)findViewById(R.id.messageHolder);

        TextView nmsg = new TextView(this);
        nmsg.setText(message);
        nmsg.setBackgroundResource(R.drawable.message_wrap);
        nmsg.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prop.setMargins(0,0,0,8);
        prop.gravity = Gravity.START;

        nmsg.setLayoutParams(prop);
        nmsg.setPadding(15,8,15,8);
        nmsg.setTextSize(18);

        ll.addView(nmsg);
        scroll.fullScroll(View.FOCUS_DOWN);
        msgText.setText("");

    }


    // Displaying receiver message on the screen
    public void showReceiverMessage(String message){

            LinearLayout ll1 = (LinearLayout) findViewById(R.id.messageHolder);

            TextView nmsg = new TextView(this);
            nmsg.setText(message);
            nmsg.setBackgroundResource(R.drawable.message_received_wrap);
            nmsg.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            prop.setMargins(0, 0, 0, 8);
            prop.gravity = Gravity.END;

            nmsg.setLayoutParams(prop);
            nmsg.setPadding(15, 8, 15, 8);
            nmsg.setTextSize(18);

            ll1.addView(nmsg);

    }

}
