package com.example.don.emertext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TextSpeech extends AppCompatActivity {

    TextToSpeech ts;
    String toSpeak;
    LinearLayout helperText;
    int i=1;
    EditText writable;
    ScrollView scroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);
        helperText = (LinearLayout) findViewById(R.id.helperTextMessage);
        writable = (EditText) findViewById(R.id.editText);
        scroll = (ScrollView) findViewById(R.id.scroller1);

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

                    showMessage();
                }

                return false;
            }
        });
    }


    public void showMessage(){

        i+=1;
        LinearLayout ll1 = (LinearLayout)findViewById(R.id.helperTextMessage);

        TextView nmsg = new TextView(this);
        toSpeak = writable.getText().toString();
        if (writable.getText().toString()!= null) {
            nmsg.setText(writable.getText());
            if (i % 2 == 0) {
                nmsg.setBackgroundResource(R.drawable.message_received_wrap);
                nmsg.setTextColor(Color.BLACK);
            } else {
                nmsg.setBackgroundResource(R.drawable.message_wrap);
                nmsg.setTextColor(Color.WHITE);
            }
//            nmsg.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams prop = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            prop.setMargins(0, 0, 0, 8);

            nmsg.setLayoutParams(prop);
            nmsg.setPadding(15, 5, 15, 5);
            nmsg.setTextSize(18);

            ll1.addView(nmsg);
            scroll.fullScroll(View.FOCUS_DOWN);
            writable.setText("");
            speakNow();
        }
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
        findViewById(R.id.drawButton).setOnClickListener(buttonClickListener);

        findViewById(R.id.button11).setOnLongClickListener(longClickListener);
        findViewById(R.id.button12).setOnLongClickListener(longClickListener);
        findViewById(R.id.button13).setOnLongClickListener(longClickListener);
        findViewById(R.id.button14).setOnLongClickListener(longClickListener);
        findViewById(R.id.button21).setOnLongClickListener(longClickListener);
        findViewById(R.id.button22).setOnLongClickListener(longClickListener);
        findViewById(R.id.button23).setOnLongClickListener(longClickListener);
        findViewById(R.id.button24).setOnLongClickListener(longClickListener);

    }

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            values(v, false);
            return false;
        }
    };

    private View.OnClickListener buttonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            values(view, true);
        }
    };

    public void values(View view, boolean speak) {
        switch (view.getId()) {
            case R.id.button11:
                toSpeak = "Can you help me please";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button12:
                toSpeak = "Can you get my medicines";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button13:
                toSpeak = "Can you please make a call for me";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button14:
                toSpeak = "Can you please call a taxi for me";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                speakNow();
                break;
            case R.id.button21:
                toSpeak = "Yes please";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button22:
                toSpeak = getSharedPreferences(
                        getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(getString(R.string.custom_message_key), "");
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button23:
                toSpeak = "Can you get me some water to drink please";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.button24:
                toSpeak = "No thank you";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                if (speak) speakNow();
                break;
            case R.id.drawButton:
                Intent i = new Intent(TextSpeech.this, DrawNotifyScreen.class);
                startActivity(i);
                break;

        }
    }

    public void speakNow() {

        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        ts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
    }

}
