package com.madboss.smsmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.madboss.smsmailer.R;
import com.madboss.smsmailer.activities.login.RequestOTPActivity;

public class TermsAndConditions extends AppCompatActivity {

    Button agreeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        initElement();
        registerListeners();
    }

    public void initElement() {
        agreeButton = (Button) findViewById(R.id.agreebutton);
    }

    public void registerListeners() {
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TermsAndConditions.this, RequestOTPActivity.class);
                startActivity(i);
            }
        });
    }

}
