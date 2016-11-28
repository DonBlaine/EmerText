package com.example.don.emertext;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TextSpeech extends AppCompatActivity {

    TextToSpeech ts;
    String toSpeak;
    TextView helperText;
    TextView speakText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);
        helperText = (TextView) findViewById(R.id.helperTextMessage);
        speakText = (TextView) findViewById(R.id.saidMessage);

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

        ((EditText)findViewById(R.id.editText)).setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if  ((actionId == EditorInfo.IME_ACTION_DONE)) {

                    helperText.setText(((EditText) findViewById(R.id.editText)).getText());
                }

                return false;
            }
        });
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

        findViewById(R.id.button21).setOnClickListener(buttonClickListener);
        findViewById(R.id.button22).setOnClickListener(buttonClickListener);
        findViewById(R.id.button23).setOnClickListener(buttonClickListener);
        findViewById(R.id.button24).setOnClickListener(buttonClickListener);

        findViewById(R.id.button31).setOnClickListener(buttonClickListener);
        findViewById(R.id.button32).setOnClickListener(buttonClickListener);
        findViewById(R.id.button33).setOnClickListener(buttonClickListener);
        findViewById(R.id.button34).setOnClickListener(buttonClickListener);

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
                case R.id.button21:
                    toSpeak = "Can you help me call one of my friends";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button22:
                    toSpeak = "could you please help me stand up";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button23:
                    toSpeak = "Could you please help me find my stuff";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button24:
                    toSpeak = "Can you help me inform my parents";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button31:
                    toSpeak = "My medicine is in my bag, can you please help me take it";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button32:
                    toSpeak = "I am hurt, could you help me with a cab";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button33:
                    toSpeak = "I am having a panic attack. Could you please talk to me for a while";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    speakNow();
                    break;
                case R.id.button34:
                    toSpeak = "Help Help Help Help";
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
