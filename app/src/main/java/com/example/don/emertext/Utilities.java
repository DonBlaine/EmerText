package com.example.don.emertext;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by gerard on 20/11/16.
 * Class to store repeated methods used in several classes and variables which need to be globally unique
 */

public class Utilities {
    //Permission request codes
    static final int SMS_REQUEST_CODE = 1;
    static final int REQUEST_CONTACTS_CODE = 2;
    static final int REQUEST_LOCATION = 3;

    //Variables to override certain functionalities for testing
    static final boolean SKIP_NETWORK_CHECK = true;

    //Intent result codes (these can overlap with the ones above）
    static final int CONTACT_INTENT_CODE = 1;

    public static void restoreViewValue(SharedPreferences sharedPref, EditText e){
        //Takes an EditText and retrieves the value stored for it based on its tag, or blank string otherwise
        String key=e.getTag().toString();
        String retrievedValue = sharedPref.getString(key, "");
        e.setText(retrievedValue);

    }

    public static void setEditableFocusChangeAutosave(final SharedPreferences sharedPref, final CheckBox c){
        //Quickset an autosave for a CheckBox when it loses focus
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveViewValue(sharedPref,c);
            }
        });}

    public static void setEditableFocusChangeAutosave(final SharedPreferences sharedPref, final EditText e){
        //Quickset an autosave for an EditText when it loses focus
        e.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    saveViewValue(sharedPref, e);
                }
            }
        })
        ;
    }


    public static void restoreViewValue(SharedPreferences sharedPref, CheckBox c){
        //Takes an Checkbox and retrieves the value stored for it based on its tag, or false otherwise
        String key=c.getTag().toString();
        boolean retrievedValue = sharedPref.getBoolean(key,false);
        c.setChecked(retrievedValue);

    }

    public static void saveViewValue(SharedPreferences sharedPref, EditText e){
        //Saves EditText to key based on its tag
        SharedPreferences.Editor editor = sharedPref.edit();
        String value = e.getText().toString();
        String key = e.getTag().toString();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveViewValue(SharedPreferences sharedPref, CheckBox c){
        //Saves CheckBox to key based on its tag
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean value = c.isChecked();
        String key= c.getTag().toString();
        editor.putBoolean(key,value);
        editor.apply();

    }
}
