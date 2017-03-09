package com.emertext;


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
import android.widget.EditText;
import android.widget.Toast;

//EmergencyContact Frangment class
/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyContactDetails extends Fragment {
    SharedPreferences sharedPref;
    private View rootView;
    private EditText contact_name_edittext;
    private EditText contact_number_edittext;
    private EditText emergency_service_edittext;
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
        //Find EditTexts  and buttons and set their onClicks
        Button getcontact = (Button) rootView.findViewById(R.id.fetch_button);
        getcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });
        contact_name_edittext = (EditText) rootView.findViewById(R.id.emergency_contact_name);
        contact_number_edittext = (EditText) rootView.findViewById(R.id.emergency_contact_phone);
        emergency_service_edittext = (EditText) rootView.findViewById(R.id.emergency_service_number_edittext);
        setEditableFocusChangeAutosave(contact_name_edittext);
        setEditableFocusChangeAutosave(contact_number_edittext);
        setEditableFocusChangeAutosave(emergency_service_edittext);
    }

    public void getContacts() {
        //Method to try to get a contact from the Contacts and request permission if required
        if (requestContactsPermission()) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, Utilities.CONTACT_INTENT_CODE);
        } else {
            String message = getString(R.string.no_contacts_permission_error);
            Toast toast = Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {


        // Make sure it's our original READ_CONTACTS request
        if (requestCode == Utilities.REQUEST_CONTACTS_CODE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, Utilities.CONTACT_INTENT_CODE);
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS);
                }

                if (!showRationale) {
                    String message = getString(R.string.no_contacts_permission_error);
                    Toast toast = Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        //Capture the contacts request result
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (Utilities.CONTACT_INTENT_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);

                            if (phones != null) {
                                phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String nameContact = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            cNumber = stripPhone(cNumber);
                            contact_name_edittext.setText(nameContact);
                            contact_number_edittext.setText(cNumber);
                            saveViewValue(contact_name_edittext);
                            saveViewValue(contact_number_edittext);
                                phones.close();
                            }
                        }
                        c.close();
                    }

                }
        }
    }


    //  Function to just check if we have SEND_SMS permission
    public boolean checkContactsPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
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
    public boolean requestContactsPermission() {
        if (checkContactsPermission()) {
            return true;
        }
        else{
            //try to get permission
            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    Utilities.REQUEST_CONTACTS_CODE);
            return checkContactsPermission();
        }
    }

    public void restoreAllValues() {
        restoreViewValue(contact_name_edittext);
        restoreViewValue(contact_number_edittext);
        restoreViewValue(emergency_service_edittext);
    }


    //Form helper methods here
    public void restoreViewValue(EditText e) {
        Utilities.restoreViewValue(sharedPref, e);

    }


    public void setEditableFocusChangeAutosave(final EditText e) {
        Utilities.setEditableFocusChangeAutosave(sharedPref, e);
    }

    public void saveViewValue(EditText e) {
        Utilities.saveViewValue(sharedPref, e);
    }


}
