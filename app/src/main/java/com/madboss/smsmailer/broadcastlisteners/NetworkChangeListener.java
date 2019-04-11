package com.madboss.smsmailer.broadcastlisteners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class NetworkChangeListener extends BroadcastReceiver {

    public static final String ACTION_TOKEN = ConnectivityManager.CONNECTIVITY_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_TOKEN)){
            onChange(isOnline(context));
        }
    }

    public static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter(ACTION_TOKEN);
        return filter;
    }

    public abstract void onChange(Boolean isOnline);

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
