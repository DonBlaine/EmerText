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
import android.widget.EditText;
import android.widget.Toast;

import com.example.don.emertext.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalInformation extends Fragment {
    private EditText allergies_edittext;
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
        allergies_edittext=(EditText) rootView.findViewById(R.id.allegies_input);
        allergies_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Context context = getContext();

                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, s, duration);
                toast.show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

}
