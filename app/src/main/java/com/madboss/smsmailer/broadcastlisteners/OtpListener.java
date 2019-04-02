package com.madboss.smsmailer.broadcastlisteners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

public abstract class OtpListener extends BroadcastReceiver {

    public static final String ACTION_TOKEN = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_TOKEN.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String msgBody = msgs[i].getMessageBody();
                    }
                } catch(Exception e) {

                }
            }
        }
    }

    public static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter(ACTION_TOKEN);
        return filter;
    }

    public abstract void onNewToken(String token);

}