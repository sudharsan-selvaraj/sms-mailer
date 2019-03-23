package com.madboss.smsmailer.activities.login;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.madboss.smsmailer.R;

public class OTPVerificationActivity extends AppCompatActivity {

    public EditText[] otpTextFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        otpTextFields = new EditText[]{
                null,
                findViewById(R.id.otp_1),
                findViewById(R.id.otp_2),
                findViewById(R.id.otp_3),
                findViewById(R.id.otp_4),
                findViewById(R.id.otp_5),
                findViewById(R.id.otp_6),
                null
        };

        addToolBar();
        switchOtpEditViewListener();
    }

    public void addToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOtpVerification);
        toolbar.setTitle("Enter verification number");
        toolbar.setTitleTextColor(Color.rgb(255, 255, 255));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchOtpEditViewListener() {
        for (int i = 1; i < otpTextFields.length - 1; i++) {
            OTPWatcher watcher = new OTPWatcher(otpTextFields[i], otpTextFields[i - 1], otpTextFields[i + 1]);
            otpTextFields[i].addTextChangedListener(watcher);
            otpTextFields[i].setOnKeyListener(watcher);
        }
    }

    private class OTPWatcher implements TextWatcher, View.OnKeyListener {

        EditText curr, prev, next;

        OTPWatcher(EditText curr, EditText prev, EditText next) {
            this.prev = prev;
            this.next = next;
            this.curr = curr;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals("")) {
                moveFocusToNextField(false);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if(curr.getText().toString().equals("")) {
                    moveFocusToPreviousField(true);
                    return true;
                }
            }
            return false;
        }

        public void moveFocusToNextField(boolean clearValue) {
            if (next != null) {
                next.requestFocus();
                if(clearValue){
                    next.setText("");
                }
            }
        }

        public void moveFocusToPreviousField(boolean clearValue) {
            if (prev != null) {
                prev.requestFocus();
                if(clearValue){
                    prev.setText("");
                }
            }
        }

    }
}
