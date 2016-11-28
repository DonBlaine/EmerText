package com.example.don.emertext;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by gerard on 20/11/16.
 */

public class FormUtilities {


    public static void restoreViewValue(SharedPreferences sharedPref, EditText e){
        String key=e.getTag().toString();
        String retrievedValue = sharedPref.getString(key, "");
        e.setText(retrievedValue);

    }

    public static void setEditableFocusChangeAutosave(final SharedPreferences sharedPref, final CheckBox c){
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveViewValue(sharedPref,c);
            }
        });}

    public static void setEditableFocusChangeAutosave(final SharedPreferences sharedPref, final EditText e){
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
        String key=c.getTag().toString();
        boolean retrievedValue = sharedPref.getBoolean(key,false);
        c.setChecked(retrievedValue);

    }

    public static void saveViewValue(SharedPreferences sharedPref, EditText e){
        SharedPreferences.Editor editor = sharedPref.edit();
        String value = e.getText().toString();
        String key = e.getTag().toString();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveViewValue(SharedPreferences sharedPref, CheckBox c){
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean value = c.isChecked();
        String key= c.getTag().toString();
        editor.putBoolean(key,value);
        editor.apply();

    }
}
