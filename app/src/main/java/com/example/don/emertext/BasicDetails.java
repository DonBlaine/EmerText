package com.example.don.emertext;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import static com.example.don.emertext.R.string.details_initialised_key;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicDetails extends Fragment {

    SharedPreferences sharedPref;
    private String EMERGENCY_NUMBER;
    private EditText firstname_edittext;
    private EditText lastname_edittext;
    private EditText address1_edittext;
    private EditText address2_edittext;
    private AutoCompleteTextView county_autocomplete;
    private EditText eircode_edittext;
    private View rootView;
    private CheckBox fingerprint_checkbox;

    public BasicDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_basic_details, container, false);
        EMERGENCY_NUMBER = getString(R.string.default_emergency_number);

        sharedPref = getContext().getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        findEditViews();
        Button return_button = (Button) rootView.findViewById(R.id.welcome_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWelcomeScreen();
            }
        });
        county_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autosetDublin();
            }
        });


        //Check for Fingerprint sensor presence and reveal checkbox if there
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Activity.FINGERPRINT_SERVICE);
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.USE_FINGERPRINT) ==
                    PackageManager.PERMISSION_GRANTED) {

                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    fingerprint_checkbox.setText(getString(R.string.lock_using_fingerprint));
                }
            }
        }

        //Set the contents for the county selector
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.county_list, android.R.layout.simple_dropdown_item_1line);

        county_autocomplete.setAdapter(adapter);

        if (sharedPref.getBoolean(getString(R.string.details_initialised_key), false)) {
            return_button.setVisibility(View.GONE);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(details_initialised_key), true);
        editor.apply();
        restoreAllValues();
        return rootView;
    }



    public void restoreAllValues() {
        restoreViewValue(firstname_edittext);
        restoreViewValue(lastname_edittext);
        restoreViewValue(address1_edittext);
        restoreViewValue(address2_edittext);
        restoreViewValue(county_autocomplete);
        restoreViewValue(eircode_edittext);
        restoreViewValue(fingerprint_checkbox);
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

    public void findEditViews(){
        firstname_edittext = (EditText) rootView.findViewById(R.id.fnameInput);
        lastname_edittext = (EditText) rootView.findViewById(R.id.lnameinput);
        address1_edittext = (EditText) rootView.findViewById(R.id.address1);
        address2_edittext = (EditText) rootView.findViewById(R.id.address2);
        county_autocomplete = (AutoCompleteTextView) rootView.findViewById(R.id.county_select_input);
        eircode_edittext = (EditText) rootView.findViewById(R.id.eircode_edittext);
        fingerprint_checkbox = (CheckBox) rootView.findViewById(R.id.fingerprint_checkbox);
        setEditableFocusChangeAutosave(firstname_edittext);
        setEditableFocusChangeAutosave(lastname_edittext);
        setEditableFocusChangeAutosave(address1_edittext);
        setEditableFocusChangeAutosave(address2_edittext);
        setEditableFocusChangeAutosave(county_autocomplete);
        setEditableFocusChangeAutosave(eircode_edittext);
        setEditableFocusChangeAutosave(fingerprint_checkbox);
    }

    public void goToWelcomeScreen() {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(intent);

    }



    public void sendSMS(View view) {

        SmsManager text = SmsManager.getDefault();
        String number = EMERGENCY_NUMBER;
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

    }

    //Form helper methods here
    public void restoreViewValue(EditText e) {
        Utilities.restoreViewValue(sharedPref, e);

    }

    public void restoreViewValue(CheckBox c) {
        Utilities.restoreViewValue(sharedPref, c);
    }

    public void setEditableFocusChangeAutosave(final EditText e) {
        Utilities.setEditableFocusChangeAutosave(sharedPref, e);
    }

    public void saveViewValue(EditText e) {
        Utilities.saveViewValue(sharedPref, e);
    }

    public void saveViewValue(CheckBox c) {
        Utilities.saveViewValue(sharedPref, c);

    }

    public void setEditableFocusChangeAutosave(CheckBox c) {
        Utilities.setEditableFocusChangeAutosave(sharedPref, c);
    }
}
