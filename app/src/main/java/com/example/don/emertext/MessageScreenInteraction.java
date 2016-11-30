package com.example.don.emertext;

import android.app.PendingIntent;
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
    }

    public void sendSMS(View view) {
        SmsManager text = SmsManager.getDefault();
        String message = "";
        TextView q = (TextView) findViewById(R.id.messageText);

        if (q.getText().toString().trim() !=null)
        {
            message = q.getText().toString();
        }
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
                            message = currentSMS.getDisplayMessageBody().toString();

                            if(PhoneNumberUtils.compare(sender, "0868303866")) {

                                message = message.trim();
                                if (!message.equals("")) {
                                    showReceiverMessage(message);
                                }
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
