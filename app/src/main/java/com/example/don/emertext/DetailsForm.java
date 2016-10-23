package com.example.don.emertext;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class DetailsForm extends Activity {

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

    public void sendSMS(View view)
    {
        SmsManager text = SmsManager.getDefault();
        String number = "0831453528";  // The number on which you want to send SMS
        String message = "";
        Intent resultIntent= new Intent(this,MainActivity.class);
        PendingIntent intent=PendingIntent.getActivity(this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        text.sendTextMessage(number, null, message,null,intent);
        TextView textView = (TextView) findViewById(R.id.return_message);
        textView.setVisibility(view.VISIBLE);
    }


}
