package com.example.don.emertext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Initial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Button return_button = (Button) findViewById(R.id.welcome_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWelcomeScreen();
            }
        });
    }


    public void registerForService(View view) {
        Context context = getApplicationContext();

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendRegistrationText();

        } else {
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    Utilities.SMS_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {


        // Make sure it's our original READ_CONTACTS request
        if (requestCode == Utilities.SMS_REQUEST_CODE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendRegistrationText();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!showRationale) {
                    String message = getString(R.string.no_sms_permission_error);
                    Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void sendRegistrationText() {

        SmsManager text = SmsManager.getDefault();
        String number = getString(R.string.default_emergency_number);
        String message = "register";

        text.sendTextMessage(number // Number to send to
                , null               // Message centre to send to (we'll never want to change this)
                , message            // Message to send
                , null               // The PendingIntent to perform when the message is successfully sent
                , null);           // The PendingIntent to perform when the message is successfully delivered
        findViewById(R.id.enter_details_button).setEnabled(true);
        findViewById(R.id.welcome_button).setEnabled(true);
        SharedPreferences.Editor editor = getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE).edit();
        editor.putBoolean(getString(R.string.setup_complete_key), true);
        editor.putString(getString(R.string.emergency_service_number_key), getString(R.string.default_emergency_number));
        editor.apply();
    }


    public void enterFragmentDetails(View view) {
        Intent intent = new Intent(this, TabbedDetails.class);
        startActivity(intent);

    }


    public void goToWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

    }
}
