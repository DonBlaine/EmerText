package com.emertext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

// A broadcast receiver class that receives incoming messages and checks if the message is
// from the number he sent while using the app then it sends an intent to interaction screen with the message

// The code is inspired from Stack overflow

public class SmsListener extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        if(pdus!=null) {
            for (Object pdu : pdus) {
                SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = SMessage.getOriginatingAddress();
                String referenceNumber;
                // Getting number if user manually entered it or the default one
                referenceNumber = context.getSharedPreferences(
                        context.getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(context.getString(R.string.emergency_service_number_key), context.getString(R.string.default_emergency_number));
                String body = SMessage.getMessageBody();

                // Comparing if the number sent to and received are the same
                // In case it is transferring the intent
                if (PhoneNumberUtils.compare(sender, referenceNumber)) {
                    Intent in = new Intent("SmsMessage.intent.EMERGENCY").
                            putExtra("get_msg", body);

                    context.sendBroadcast(in);
                }
            }
        }
    }
}