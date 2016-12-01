package com.example.don.emertext;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicDetails extends Fragment {
    //Fragment for basic details section of the

    SharedPreferences sharedPref;
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

        sharedPref = getContext().getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        findEditViews();
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


        restoreAllValues();
        return rootView;
    }



    public void restoreAllValues() {
        //Function to load all view values from stored values
        restoreViewValue(firstname_edittext);
        restoreViewValue(lastname_edittext);
        restoreViewValue(address1_edittext);
        restoreViewValue(address2_edittext);
        restoreViewValue(county_autocomplete);
        restoreViewValue(eircode_edittext);
        restoreViewValue(fingerprint_checkbox);
    }


    public void autosetDublin() {
        //Function to autoset county if the Eircode is supplied and starts with D
        //The rest of the Eircode database is random, and is not open, therefore only D-- ---- Eircodes can be inferred
        String eircode = eircode_edittext.getText().toString();
        if (eircode.length() > 3 && eircode.toCharArray()[0] == 'D') {
            String post = Integer.parseInt(eircode.substring(1, 3)) + "";
            //   if (post.toCharArray()[0] == '0') {
            //      post = post.substring(1, 2);
            // }
            county_autocomplete.setText(getString(R.string.dublin) + post);
        }
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


    //Methods below just mirror those in Utilities but with the SharedPreferences parameter passed
    public void restoreViewValue(EditText e) {
        Utilities.restoreViewValue(sharedPref, e);

    }

    public void restoreViewValue(CheckBox c) {
        Utilities.restoreViewValue(sharedPref, c);
    }

    public void setEditableFocusChangeAutosave(final EditText e) {
        Utilities.setEditableFocusChangeAutosave(sharedPref, e);
    }


    public void setEditableFocusChangeAutosave(CheckBox c) {
        Utilities.setEditableFocusChangeAutosave(sharedPref, c);
    }
}
