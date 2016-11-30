package com.example.don.emertext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageScreenInteraction extends AppCompatActivity {

    EditText msgText;
    ScrollView scroll;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.emergency_service_number_key), getString(R.string.default_emergency_number));
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

        String message =
                "Please send: " + buttonSelected + ". \n" +
                "Location: " + userLocation + ". \n" +
                        "GPS: " + gps + ". \n";


        if (peopleWith != null && !peopleWith.equals(""))
            message = message + "People with me: " + peopleWith + ". \n";
        if (peopleWith != null && !peopleWith.equals(""))
            message = message + "Additional details: " + extraDetails + ". \n";
        if (emergencyType != null && !emergencyType.equals(""))
            message = message + "Emergency Type: " + emergencyType + ". \n";



        msgText.setText(message);
        sendSMS(findViewById(android.R.id.content).getRootView());


    }

    public void sendSMS(View view) {
        SmsManager text = SmsManager.getDefault();
        String message = "";
        TextView q = (TextView) findViewById(R.id.messageText);

        message = q.getText().toString();
        String currentmessage;
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
        }/*
        else
        {
            text.sendTextMessage(number // Number to send to
                    , null               // Message centre to send to (we'll never want to change this)
                    , message            // Message to send
                    , null               // The PendingIntent to perform when the message is successfully sent
                    , null);           // The PendingIntent to perform when the message is successfully delivered
        }*/
        message = message.trim();
        if (!message.equals("")) {
            showSenderMessage(message);
        }
    }

    public void showSenderMessage(String message){


        LinearLayout ll = (LinearLayout)findViewById(R.id.messageHolder);

        TextView nmsg = new TextView(this);
        nmsg.setText(message);
        nmsg.setBackgroundResource(R.drawable.message_wrap);
        nmsg.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prop.setMargins(0,0,0,8);
        prop.gravity = Gravity.LEFT;

        nmsg.setLayoutParams(prop);
        nmsg.setPadding(5,5,5,5);
        nmsg.setTextSize(18);

        ll.addView(nmsg);
        scroll.fullScroll(View.FOCUS_DOWN);
        msgText.setText("");

    }

    // taken from mobiforge

    private BroadcastReceiver SmsReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //---get the SMS message passed in---
            Bundle bundle = intent.getExtras();
            android.telephony.SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null)
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new android.telephony.SmsMessage[pdus.length];
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = android.telephony.SmsMessage.createFromPdu((byte[])pdus[i]);
                    str += "SMS from " + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "n";
                }
                //---display the new SMS message---
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void showReceiverMessage(String message){

        LinearLayout ll1 = (LinearLayout)findViewById(R.id.messageHolder);

        TextView nmsg = new TextView(this);
        nmsg.setText(message);
        nmsg.setBackgroundResource(R.drawable.message_received_wrap);
        nmsg.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prop.setMargins(0,0,0,8);
        prop.gravity = Gravity.RIGHT;

        nmsg.setLayoutParams(prop);
        nmsg.setPadding(5,5,5,5);
        nmsg.setTextSize(18);

        ll1.addView(nmsg);
    }

    // Code for timer
//    private final int interval = 1000; // 1 Second
//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable(){
//        public void run() {
//            Toast.makeText(MyActivity.this, "C'Mom no hands!", Toast.LENGTH_SHORT).show();
//        }
//    };
//    ...
//            handler.postAtTime(runnable, System.currentTimeMillis()+interval);
//    handler.postDelayed(runnable, interval);


}


//    public void setupUI(View view) {
//
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard(TextSpeech.this);
//                    return false;
//                }
//            });
//        }
//    }
//
//    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);
//    }
//
