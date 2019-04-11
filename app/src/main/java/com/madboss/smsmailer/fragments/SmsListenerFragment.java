package com.madboss.smsmailer.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.madboss.smsmailer.R;
import com.madboss.smsmailer.services.SmsListenerService;

public class SmsListenerFragment extends Fragment {

    public Intent smsServiceIntent;
    public Button startButton;
    public LottieAnimationView stopButton;

    public LinearLayout startButtonConatiner;
    public LinearLayout stopButtonConatiner;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        smsServiceIntent = new Intent(getContext(), SmsListenerService.class);
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms_listener, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        startButton =  (Button) view.findViewById(R.id.startSmsListener);
        stopButton =  (LottieAnimationView) view.findViewById(R.id.lottieListeningView);

        startButtonConatiner = (LinearLayout) view.findViewById(R.id.startSmsListenerButtonContainer);
        stopButtonConatiner = (LinearLayout) view.findViewById(R.id.stopSmsListenerButtonContainer);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
            }
        });

        if(SmsListenerService.isRunning()) {
            startListening();
        } else {
            stopSmsListener();
        }
    }


    public void startListening() {
        startSmsListener();
        startButtonConatiner.setVisibility(View.INVISIBLE);
        stopButtonConatiner.setVisibility(View.VISIBLE);
    }

    public void stopListening() {
        stopSmsListener();
        startButtonConatiner.setVisibility(View.VISIBLE);
        stopButtonConatiner.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void startSmsListener() {
        if(!SmsListenerService.isRunning()) {
            getContext().startService(smsServiceIntent);
        }
    }

    public void stopSmsListener() {
        if(SmsListenerService.isRunning()) {
            getContext().stopService(smsServiceIntent);
        }
    }
}
