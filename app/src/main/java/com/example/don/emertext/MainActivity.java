package com.example.don.emertext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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

    public void enterDetails(View view){
            if (requestSMS(view)){
            Intent intent = new Intent(this, DetailsForm.class);
            startActivity(intent);}
        else {
                Button button = (Button) findViewById(R.id.button);
                button.setText("You haven't granted SMS permissions");
            }

}}
