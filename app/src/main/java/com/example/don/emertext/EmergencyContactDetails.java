package com.example.don.emertext;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyContactDetails extends Fragment {
    private View rootView;
    private EditText contact_name_edittext;
    private EditText contact_number_edittext;
    final int CONTACT_INTENT_CODE = 1;
    private final int REQUEST_CONTACTS_CODE = 10;
    SharedPreferences sharedPref;
    public EmergencyContactDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_emergency_contact_details, container, false);
        sharedPref = getContext().getSharedPreferences(
                getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        viewSetup();
        restoreAllValues();
        return rootView;
    }

    public void viewSetup() {
        Button getcontact = (Button) rootView.findViewById(R.id.fetch_button);
        getcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(v);
            }
        });
        contact_name_edittext = (EditText) rootView.findViewById(R.id.emergency_contact_name);
        contact_number_edittext = (EditText) rootView.findViewById(R.id.emergency_contact_phone);
        setEditableFocusChangeAutosave(contact_name_edittext);
        setEditableFocusChangeAutosave(contact_number_edittext);
    }

    public void getContacts(View view){
        if (requestContactsPermission(view)) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, CONTACT_INTENT_CODE);
        } else {
            String message = getString(R.string.no_contacts_permission_error);
            Toast toast = Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (CONTACT_INTENT_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String nameContact = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            cNumber = stripPhone(cNumber);
                            contact_name_edittext.setText(nameContact);
                            contact_number_edittext.setText(cNumber);
                            saveViewValue(contact_name_edittext);
                            saveViewValue(contact_number_edittext);

                        }
                    }
                }
        }
    }
    //  Function to just check if we have SEND_SMS permission
    public boolean checkContactsPermission(View view){
        boolean permissionGranted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            return true;
        }
        else
        {  return false;
        }
    }

    public String stripPhone(String phone) {
        char[] chars = phone.toCharArray();
        String out = "";
        for (char x : chars) {
            if ((x >= '0' && x <= '9') || x == '+') {
                out = out + x;
            }
        }
        return out;
    }


    // Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestContactsPermission(View view){
        if (checkContactsPermission(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS_CODE);
            if (checkContactsPermission(view)) {
                return true;
            }
            else{
                return false;
            }
        }
    }

    public void restoreAllValues() {
        restoreViewValue(contact_name_edittext);
        restoreViewValue(contact_number_edittext);

    }


    //Form helper methods here
    public void restoreViewValue(EditText e) {
        FormUtilities.restoreViewValue(sharedPref, e);

    }

    public void restoreViewValue(CheckBox c) {
        FormUtilities.restoreViewValue(sharedPref, c);
    }

    public void setEditableFocusChangeAutosave(final EditText e) {
        FormUtilities.setEditableFocusChangeAutosave(sharedPref, e);
    }

    public void saveViewValue(EditText e) {
        FormUtilities.saveViewValue(sharedPref, e);
    }

    public void saveViewValue(CheckBox c) {
        FormUtilities.saveViewValue(sharedPref, c);

    }

    public void setEditableFocusChangeAutosave(CheckBox c) {
        FormUtilities.setEditableFocusChangeAutosave(sharedPref, c);
    }

}
