package com.example.don.emertext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = SMessage.getOriginatingAddress();
            String referenceNumber = context.getSharedPreferences(
                    context.getString(R.string.personal_details_file), Context.MODE_PRIVATE).getString(context.getString(R.string.emergency_service_number_key), context.getString(R.string.default_emergency_number));
            String body = SMessage.getMessageBody().toString();

            if (PhoneNumberUtils.compare(sender, referenceNumber)) {
                Intent in = new Intent("SmsMessage.intent.EMERGENCY").
                        putExtra("get_msg", body);

                context.sendBroadcast(in);
            }
        }
    }
}