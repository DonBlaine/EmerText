package com.example.don.emertext;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class TextSpeech extends AppCompatActivity {
    TextToSpeech ts;
    Button b11,b12,b13,b14,b21,b22,b23,b24,b31,b32,b33,b44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);
//        b1=(Button)findViewById(R.id.b2);
//
//        ts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    ts.setLanguage(Locale.UK);
//                }
//            }
//        });
//
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String toSpeak = "okay";
//                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
//                ts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });
//    }
//
//
//
//    public void onPause(){
//        if(ts !=null){
//            ts.stop();
//            ts.shutdown();
//        }
//        super.onPause();
//    }
    }
}
