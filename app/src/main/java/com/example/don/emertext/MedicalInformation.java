package com.example.don.emertext;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.don.emertext.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalInformation extends Fragment {
    private EditText allergies_edittext;
    private EditText current_med_edittext;
    SharedPreferences sharedPref;
    private View rootView;

    public MedicalInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_medical_information, container, false);

        sharedPref = getContext().getSharedPreferences(getString(R.string.personal_details_file), Context.MODE_PRIVATE);
        findEditViews();
        restoreSavedValue();
        return rootView;
    }

    public void restoreSavedValue(){
    restoreViewValue(allergies_edittext);
        restoreViewValue(current_med_edittext);
    }


    public void findEditViews(){
        allergies_edittext=(EditText) rootView.findViewById(R.id.allergies_edittext);
        current_med_edittext=(EditText) rootView.findViewById(R.id.current_medications_edittext);
        setEditableFocusChangeAutosave(allergies_edittext);
        setEditableFocusChangeAutosave(current_med_edittext);
    }



    public void restoreViewValue(EditText e){
        FormUtilities.restoreViewValue(sharedPref,e);

    }
    public void restoreViewValue(CheckBox c){
        FormUtilities.restoreViewValue(sharedPref,c);
    }
    public void setEditableFocusChangeAutosave(final EditText e){
        FormUtilities.setEditableFocusChangeAutosave(sharedPref,e);
    }
    public void saveViewValue(EditText e){
        FormUtilities.saveViewValue(sharedPref,e);
    }
    public void saveViewValue(CheckBox c){
        FormUtilities.saveViewValue(sharedPref,c);

    }
    public void setEditableFocusChangeAutosave(CheckBox c){
        FormUtilities.setEditableFocusChangeAutosave(sharedPref,c);
    }

}
