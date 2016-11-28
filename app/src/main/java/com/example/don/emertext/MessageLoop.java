package com.example.don.emertext;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

// The activity checks if the required permissions to send ans receive SMS are there or not.
// In case they are not there the intent to message interaction screen is not passed and a toast shows
// up on an empty page saying which permission is not granted
public class MessageLoop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_loop);
        enterDetails(null);
    }
    //  Function to just check if we have SEND_SMS permission
    public boolean checkSendSMS(View view){
        boolean permissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            return true;
        }
        else
        {  return false;
        }
    }

    // Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestSendSMS(View view){
        if (checkSendSMS(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    6);
            if (checkSendSMS(view)) {
                return true;
            }
            else{
                return false;
            }
        }
    }

    //  Function to just check if we have Receive sms permission
    public boolean checkReceiveSMS(View view){
        boolean permissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            return true;
        }
        else
        {  return false;
        }
    }

    // Function to check if we have receive sms and request it if we don't.
    public boolean requestReceiveSMS(View view){
        if (checkReceiveSMS(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECEIVE_SMS},
                    6);
            if (checkReceiveSMS(view)) {
                return true;
            }
            else{
                return false;
            }
        }
    }

    public void enterDetails(View view){
        if (requestSendSMS(view) & requestReceiveSMS(view)){
            Intent intent = new Intent(this, MessageScreenInteraction.class);
            startActivity(intent);}
        else {
            if (!requestSendSMS(view)) {
                Toast.makeText(this, "You did not grant send sms permission", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "You did not grant receive sms permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}
