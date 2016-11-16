package com.example.don.emertext;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.TextView;

public class UnsupportedNetwork extends AppCompatActivity {

    TextView network_text;
    ImageView network_image;
    TextView sim_text;
    ImageView sim_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsupported_network);

        network_text=(TextView) findViewById(R.id.net_status_text);
        network_image=(ImageView) findViewById(R.id.network_icon);
        sim_text=(TextView) findViewById(R.id.sim_status_text);
        sim_image=(ImageView) findViewById(R.id.sim_icon);
        checkNetwork();
        checkSim();
    }

    public int operatorCode(String mnc) {
        int value = 0;
        String[] operators = {"27201", "27202", "27203", "27205"};
        for (int index = 0; index < operators.length; index++) {
            if (mnc.equals(operators[index])) {
                value = index;
            }}
        //Network codes 27205 and 27202 are both for Three IE, so return the same value
            if (value == 3) {
                value = 1;
            }
            return value;
        }




    public void checkNetwork(){
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
       // String sim = tel.getSimOperator();
        boolean isSupported=false;
        if (operatorCode(networkOperator)>0)
        {isSupported=true;}
       // if (!sim.equals(networkOperator)){
      //      if (!(sim.equals("27202") || sim.equals("27205")) && (networkOperator.equals("27202") || networkOperator.equals("27205"))){
       //         isSupported=false;
       //     }


        if (isSupported){
            network_text.setText("Connected network should be supported. ("+ networkOperator+")");
            network_image.setImageDrawable(getDrawable(R.drawable.green_check));
        }
        else {
                network_text.setText("Connected network is not supported. (" + networkOperator+")");
                network_image.setImageDrawable(getDrawable(R.drawable.red_block));

        }

    }

    public void checkSim(){
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getSimOperator();
        // String sim = tel.getSimOperator();
        boolean isSupported=false;
        if (operatorCode(networkOperator)>0)
        {isSupported=true;}
        // if (!sim.equals(networkOperator)){
        //      if (!(sim.equals("27202") || sim.equals("27205")) && (networkOperator.equals("27202") || networkOperator.equals("27205"))){
        //         isSupported=false;
        //     }


        if (isSupported){
            sim_text.setText("SIM network should be supported. ("+ networkOperator+")");
            sim_image.setImageDrawable(getDrawable(R.drawable.green_check));
        }
        else {
            sim_text.setText("SIM network is not supported. (" + networkOperator+")");
            sim_image.setImageDrawable(getDrawable(R.drawable.red_block));

        }

    }

}
