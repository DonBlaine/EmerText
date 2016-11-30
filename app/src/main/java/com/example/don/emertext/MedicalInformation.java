package com.example.don.emertext;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalInformation extends Fragment {
    SharedPreferences sharedPref;
    private EditText allergies_edittext;
    private EditText current_med_edittext;
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
        restoreAllSavedValues();
        return rootView;
    }

    public void restoreAllSavedValues() {
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
        Utilities.restoreViewValue(sharedPref, e);

    }
    public void restoreViewValue(CheckBox c){
        Utilities.restoreViewValue(sharedPref, c);
    }
    public void setEditableFocusChangeAutosave(final EditText e){
        Utilities.setEditableFocusChangeAutosave(sharedPref, e);
    }
    public void saveViewValue(EditText e){
        Utilities.saveViewValue(sharedPref, e);
    }
    public void saveViewValue(CheckBox c){
        Utilities.saveViewValue(sharedPref, c);

    }
    public void setEditableFocusChangeAutosave(CheckBox c){
        Utilities.setEditableFocusChangeAutosave(sharedPref, c);
    }

}
