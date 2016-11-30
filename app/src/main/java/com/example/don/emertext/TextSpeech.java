package com.example.don.emertext;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TextSpeech extends AppCompatActivity {

    TextToSpeech ts;
    String toSpeak;
    ScrollView scroll;
    EditText writable;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);
        scroll = (ScrollView) findViewById(R.id.helperTextMessage);
        writable = (EditText)findViewById(R.id.editText);
        i=0;

    }

    protected void onStart(){
        super.onStart();
        ts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ts.setLanguage(Locale.UK);
                }
            }
        });
        defineButtons();



        (writable).setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if  ((actionId == EditorInfo.IME_ACTION_DONE)) {

                    if (!writable.getText().equals("")) {
                        showMessage();
                    }
                }

                return false;
            }
        });
    }


    public void showMessage(){

        i+=1;
        LinearLayout ll1 = (LinearLayout)findViewById(R.id.textexchange);

        TextView nmsg = new TextView(this);
        nmsg.setText(writable.getText());

        if(i%2==0) {
            nmsg.setBackgroundResource(R.drawable.message_received_wrap);
        }
        else {
            nmsg.setBackgroundResource(R.drawable.message_wrap);
        }
        nmsg.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prop.setMargins(0,0,0,8);

        nmsg.setLayoutParams(prop);
        nmsg.setPadding(5,5,5,5);
        nmsg.setTextSize(18);

        ll1.addView(nmsg);
        writable.setText("");
    }

    protected void onRestart(){
        super.onRestart();
    }

    protected void onResume(){
        super.onResume();
    }
    protected void onPause(){
        if(ts !=null){
            ts.stop();
            ts.shutdown();
        }
        super.onPause();
    }
    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    public  void defineButtons(){

        findViewById(R.id.button11).setOnClickListener(buttonClickListener);
        findViewById(R.id.button12).setOnClickListener(buttonClickListener);
        findViewById(R.id.button13).setOnClickListener(buttonClickListener);
        findViewById(R.id.button14).setOnClickListener(buttonClickListener);


        findViewById(R.id.agree).setOnClickListener(buttonClickListener);
        findViewById(R.id.decline).setOnClickListener(buttonClickListener);

        findViewById(R.id.drawButton).setOnClickListener(buttonClickListener);


    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.button11:
                    toSpeak = "Can you get me some water to drink please";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button12:
                    toSpeak = "Can you sprinkle some water on my face please";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button13:
                    toSpeak = "Can you help me to a corner with back support please";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button14:
                    toSpeak = "Can you help me to some place warm please";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.agree:
                    toSpeak = "Yes, Thank you very much";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.decline:
                    toSpeak = "No Just that please, Thank you";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.drawButton:
                    Intent i = new Intent(TextSpeech.this, DrawNotifyScreen.class);
                    startActivity(i);
                    break;

            }
        }
    };



    public void speakNow() {

        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        ts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
    }



}
