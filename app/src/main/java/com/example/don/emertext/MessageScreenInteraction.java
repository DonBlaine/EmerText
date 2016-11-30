package com.example.don.emertext;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageScreenInteraction extends AppCompatActivity {
    Boolean colorChanger=true;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_screen_interaction);

        //get data from previous activities
        Intent i = getIntent();
        String gps = i.getExtras().getString("gps");
        String buttonSelected = i.getExtras().getString("buttonselected");
        String userLocation = i.getExtras().getString("userLocation");
        if(userLocation == null) userLocation = "n/a";
        String emergencyType = i.getExtras().getString("emergencyType");
        if(emergencyType == null) emergencyType = "n/a";
        String peopleWith = i.getExtras().getString("peopleWith");
        if(peopleWith == null) peopleWith = "n/a";
        String extraDetails = i.getExtras().getString("extraDetails");
        if(extraDetails == null) extraDetails = "n/a";

        //create initial sms message
        message = "I am hearing impaired and need help. \n" +
                "Please send: " + buttonSelected + ". \n" +
                "Location: " + userLocation + ". \n" +
                "GPS: " + gps + ". \n" +
                "Emergency Type: " + emergencyType + ". \n" +
                "People with me: " + peopleWith + ". \n" +
                "Additional details: " + extraDetails + ". \n";

        TextView messageBox = (TextView) findViewById(R.id.messageText);
        messageBox.setText(message);

        //send initial sms
        View view = this.findViewById(android.R.id.content).getRootView();
        sendSMS(view);
        messageBox.setText("");
    }

    public void sendSMS(View view) {
        SmsManager text = SmsManager.getDefault();
        String number = "0868303866";  // The number on which you want to send SMS
        TextView q = (TextView) findViewById(R.id.messageText);
        if (q.getText()!=null){message = q.getText().toString();}
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

        showMessage(message);
    }

    public void showMessage(String message){

        LinearLayout ll = (LinearLayout)findViewById(R.id.messageHolder);

        TextView c = new TextView(this);
        c.setText(message);

        ll.addView(c);
    }

    public class SMSListener extends BroadcastReceiver {
        private Bundle bundle;
        private SmsMessage currentSMS;
        private String sender;
        private String message;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                    if (pdu_Objects != null) {

                        for (Object aObject : pdu_Objects) {

                            currentSMS = getIncomingMessage(aObject, bundle);

                            sender = currentSMS.getDisplayOriginatingAddress();
                            message = currentSMS.getDisplayMessageBody();

                            if(PhoneNumberUtils.compare(sender, "0868303866")) {

                                showMessage(message);

                                abortBroadcast();
                            }

                        }
                    }
                }
            }
        }


        private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
            SmsMessage currentSMS;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
            } else {
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
            }
            return currentSMS;
        }
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
