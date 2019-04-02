package com.madboss.smsmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.madboss.smsmailer.R;
import com.madboss.smsmailer.activities.login.RequestOTPActivity;
import com.madboss.smsmailer.activities.main.BaseActivity;

public class TermsAndConditions extends AppCompatActivity {

    Button agreeButton;
    public FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserLoggedIn();

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

    public void checkUserLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            Intent i = new Intent(TermsAndConditions.this, BaseActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

}
