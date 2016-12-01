package com.example.don.emertext;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalInformation extends Fragment {
    SharedPreferences sharedPref;
    private EditText allergies_edittext;
    private EditText current_med_edittext;
    private EditText medical_conditions;
    private EditText custom_message;
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
        //Restore values for each of the EditTexts
    restoreViewValue(allergies_edittext);
        restoreViewValue(current_med_edittext);
        restoreViewValue(medical_conditions);
        restoreViewValue(custom_message);
    }


    public void findEditViews(){
        allergies_edittext=(EditText) rootView.findViewById(R.id.allergies_edittext);
        current_med_edittext=(EditText) rootView.findViewById(R.id.current_medications_edittext);
        medical_conditions = (EditText) rootView.findViewById(R.id.medical_conditions_edittext);
        custom_message = (EditText) rootView.findViewById(R.id.custom_message_edittext);
        setEditableFocusChangeAutosave(allergies_edittext);
        setEditableFocusChangeAutosave(current_med_edittext);
        setEditableFocusChangeAutosave(medical_conditions);
        setEditableFocusChangeAutosave(custom_message);
    }



    public void restoreViewValue(EditText e){
        Utilities.restoreViewValue(sharedPref, e);

    }
    public void setEditableFocusChangeAutosave(final EditText e){
        Utilities.setEditableFocusChangeAutosave(sharedPref, e);
    }

}
