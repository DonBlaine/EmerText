package com.example.don.emertext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // help gotten from https://www.youtube.com/watch?v=gh4nX-m6BEo
        // define all the buttons on the screen
        defineButtons();
    } // end onCreate

    public  void defineButtons(){
        findViewById(R.id.details_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.talk_stranger_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.fire_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.ambu_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.garda_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.coast_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.coast_btn).setOnClickListener(buttonClickListener);
        findViewById(R.id.fire_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.ambu_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.garda_btn).setOnLongClickListener(buttonLongClickListener);
        findViewById(R.id.coast_btn).setOnLongClickListener(buttonLongClickListener);

    } // end defineButtons
    private View.OnClickListener buttonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.details_btn:
                    Intent intent = new Intent(WelcomeActivity.this, BasicDetails.class);
                    startActivity(intent);
                    break;
                case R.id.talk_stranger_btn:
// change to point to the talk to stranger page
                    Intent intent2 = new Intent(WelcomeActivity.this, MedicalInformation.class);
                    startActivity(intent2);
                    break;
                case R.id.fire_btn: case R.id.ambu_btn: case R.id.garda_btn: case R.id.coast_btn:
                    Intent intent3 = new Intent(WelcomeActivity.this, LocationDetails.class);
                    startActivity(intent3);
                    break;
            }
        }
    }; // end OnClickListener

    private View.OnLongClickListener buttonLongClickListener = new View.OnLongClickListener(){
        // if a button is long clicked go straight to the final text screen.
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.fire_btn: case R.id.ambu_btn: case R.id.garda_btn: case R.id.coast_btn:
                    Intent intent3 = new Intent(WelcomeActivity.this, LocationDetails.class);
                    startActivity(intent3);
                    break;
            }
            return true;
        }
    }; // end OnLongClickListener
}// end main activity