package com.madboss.smsmailer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madboss.smsmailer.R;
import com.madboss.smsmailer.activities.main.BaseActivity;
import com.madboss.smsmailer.broadcastlisteners.NetworkChangeListener;
import com.madboss.smsmailer.broadcastlisteners.SMSReciever;
import com.madboss.smsmailer.models.SmsMessageModel;

public class SmsListenerService extends Service {

    private static boolean isRunning = false;
    public SMSReciever smsReciever;
    public NetworkChangeListener networkChangeListener;
    public String channelId = "sms_mailer";
    public FirebaseUser firebaseUser;
    public DatabaseReference userReference;
    public DatabaseReference messageReference;
    public DatabaseReference connectionReference;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        messageReference = userReference.child("Messages");
        connectionReference = userReference.child("Connections");

        smsReciever = new SMSReciever() {
            @Override
            public void onMessage(SmsMessage message) {
               try  {
                   String messageKey = messageReference.push().getKey();
                   messageReference.child(messageKey).setValue(new SmsMessageModel(message));
                   Toast.makeText(getBaseContext(), message.getDisplayMessageBody(), Toast.LENGTH_LONG).show();
               } catch (Exception e) {
                   System.out.println(e.getMessage());
               }
            }
        };

        networkChangeListener = new NetworkChangeListener() {
            @Override
            public void onChange(Boolean _isOnline) {
                //updateOnlineStatus(_isOnline && isRunning);
            }
        };

        registerReceiver(networkChangeListener, networkChangeListener.getFilter());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            registerReceiver(smsReciever, SMSReciever.getFilter());

            Intent notificationIntent = new Intent(getBaseContext(),BaseActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(getBaseContext(), channelId)
                    .setContentTitle("SMS Listener started")
                    .setContentText("Waiting for clients to connect")
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(R.drawable.ic_menu_camera)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(100,notification);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isRunning) {
            isRunning = false;
            unregisterReceiver(smsReciever);
            unregisterReceiver(networkChangeListener);
        }
        super.onDestroy();
    }


    public void updateOnlineStatus(Boolean status) {
        userReference.child("online").setValue(status);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        SmsListenerService.isRunning = isRunning;
    }
}
