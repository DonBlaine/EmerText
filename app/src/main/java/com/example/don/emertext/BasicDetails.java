package com.example.don.emertext;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.don.emertext.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicDetails extends Fragment {
    private boolean overrideNumber = false;
    private String EMERGENCY_NUMBER = "08313528";
    private int overrideCounter = 0;
    private EditText sendNumberEditText;

    private EditText firstname_edittext;
    private EditText lastname_edittext;
    private EditText address1_edittext;
    private EditText address2_edittext;
    private AutoCompleteTextView county_autocomplete;
    private EditText eircode_edittext;
    SharedPreferences sharedPref;
    private View rootView;

    public BasicDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_basic_details, container, false);

        sendNumberEditText = (EditText) rootView.findViewById(R.id.overrideNumber);
        sendNumberEditText.setText(EMERGENCY_NUMBER);

        //Make EditTexts easily finadable
        firstname_edittext = (EditText) rootView.findViewById(R.id.fnameInput);
        lastname_edittext = (EditText) rootView.findViewById(R.id.lnamepinput);
        address1_edittext = (EditText) rootView.findViewById(R.id.address1);
        address2_edittext = (EditText) rootView.findViewById(R.id.address2);
        county_autocomplete = (AutoCompleteTextView) rootView.findViewById(R.id.county_select_input);
        eircode_edittext = (EditText) rootView.findViewById(R.id.eircode_edittext);
        Button save_button=(Button) rootView.findViewById(R.id.save_values_button);


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveValues();
            }
        });
        firstname_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealOverride(v);
            }
        });
        county_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autosetDublin();
            }
        });


        //Set the contents for the county selector
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.county_list, android.R.layout.simple_dropdown_item_1line);

        county_autocomplete.setAdapter(adapter);

        sharedPref = getContext().getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        restoreSavedValue();
        return rootView;
    }


    @Override
    public void onDetach(){
        //Save values on detach view
        saveValues();
        super.onDetach();
    }

/*    @Override
    public void onDestroy(){
        //Release variables
        sendNumberEditText = null;
        firstname_edittext = null;
        lastname_edittext = null;
        address1_edittext = null;
        address2_edittext = null;
        county_autocomplete = null;
        eircode_edittext = null;
        sharedPref=null;
        super.onDestroy();
    }*/

    public void restoreSavedValue() {
        String blankString = "";
        String savedFirstname = sharedPref.getString(getString(R.string.firstName), blankString);
        String savedLastname = sharedPref.getString(getString(R.string.surname), blankString);
        String savedAddress1 = sharedPref.getString(getString(R.string.address_line_1), blankString);
        String savedAddress2 = sharedPref.getString(getString(R.string.address_line_2), blankString);
        String savedCounty = sharedPref.getString(getString(R.string.county), blankString);
        String savedEircode = sharedPref.getString(getString(R.string.eircode), blankString);

        firstname_edittext.setText(savedFirstname);
        lastname_edittext.setText(savedLastname);
        address1_edittext.setText(savedAddress1);
        address2_edittext.setText(savedAddress2);
        county_autocomplete.setText(savedCounty);
        eircode_edittext.setText(savedEircode);

    }


    public void autosetDublin(View view){
        autosetDublin();
    }

    public void autosetDublin() {
        String eircode = eircode_edittext.getText().toString();
        if (eircode.length() > 3 && eircode.toCharArray()[0] == 'D') {
            String post = eircode.substring(1, 3);
            if (post.toCharArray()[0] == '0') {
                post = post.substring(1, 2);
            }
            county_autocomplete.setText("Dublin " + post);
        }
    }


    public String composeMessage(View view) {
        AutoCompleteTextView county = (AutoCompleteTextView) rootView.findViewById(R.id.county_select_input);

        String message = firstname_edittext.getText().toString() + " " +
                lastname_edittext.getText().toString() + " " +
                address1_edittext.getText().toString() + " " +
                address2_edittext.getText().toString() + " " +
                county.getText().toString() + " " +
                "requires an ambulance.";
        return message;
    }


    public void saveValues() {
        SharedPreferences.Editor editor = sharedPref.edit();
        String firstnamevalue = firstname_edittext.getText().toString();
        String lastnamevalue = lastname_edittext.getText().toString();
        String address1value = address1_edittext.getText().toString();
        String address2value = address2_edittext.getText().toString();
        String countyvalue = county_autocomplete.getText().toString();
        String eircodevalue = eircode_edittext.getText().toString();

        editor.putString(getString(R.string.firstName), firstnamevalue);
        editor.putString(getString(R.string.surname), lastnamevalue);
        editor.putString(getString(R.string.address_line_1), address1value);
        editor.putString(getString(R.string.address_line_2), address2value);
        editor.putString(getString(R.string.county), countyvalue);
        editor.putString(getString(R.string.eircode), eircodevalue);

        editor.apply();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getContext(), "Saved", duration);
        toast.show();
    }


    public void revealOverride(View view) {
        if (++overrideCounter >= 5) {

            sendNumberEditText.setVisibility(View.VISIBLE);
            overrideNumber = true;
        }
    }

    public void sendSMS(View view) {

        SmsManager text = SmsManager.getDefault();
        String number;
        if (overrideNumber) {
            number = sendNumberEditText.getText().toString();
        } else {
            number = EMERGENCY_NUMBER;
        }
        String message = composeMessage(view);
        Intent resultIntent = new Intent(getContext(), MainActivity.class);
        PendingIntent intent = PendingIntent.getActivity(getContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        text.sendTextMessage(number // Number to send to
                , null               // Message centre to send to (we'll never want to change this)
                , message            // Message to send
                , null               // The PendingIntent to perform when the message is successfully sent
                , intent);           // The PendingIntent to perform when the message is successfully delivered
        TextView textView = (TextView) rootView.findViewById(R.id.return_message);
        textView.setVisibility(view.VISIBLE);
    }

}
