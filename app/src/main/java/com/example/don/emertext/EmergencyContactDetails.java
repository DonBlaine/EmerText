package com.example.don.emertext;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyContactDetails extends Fragment {
View rootView;

    public EmergencyContactDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_emergency_contact_details, container, false);
        requestContactsPermission(rootView);
        ((Button)rootView.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(v);
            }
        });
        return rootView;
    }

    public void getContacts(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);

    }

   /* @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone));
                EditText edit =(EditText) rootView.findViewById(R.id.fnameInput);
                edit.setText(name);
            }
        }
    }*/


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

    // Function to check if we have SEND_SMS and request it if we don't.
    public boolean requestContactsPermission(View view){
        if (checkContactsPermission(view)){
            return true;
        }
        else{
            //try to get permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    10);
            if (checkContactsPermission(view)) {
                return true;
            }
            else{
                return false;
            }
        }
    }



}
