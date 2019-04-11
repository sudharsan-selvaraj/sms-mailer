package com.madboss.smsmailer.activities.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madboss.smsmailer.R;
import com.madboss.smsmailer.activities.main.BaseActivity;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    public EditText[] otpTextFields;

    public Button nextButton;

    public TextView resendOTP;

    public ProgressDialog loadingDialog;

    public String phoneNumber;
    public String verificationId;

    public FirebaseAuth firebaseAuth;

    public PhoneAuthProvider.ForceResendingToken resendOtpToken = null;

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        firebaseAuth = FirebaseAuth.getInstance();

        resendOTP = (TextView) findViewById(R.id.resendOtp);
        nextButton = (Button) findViewById(R.id.nextButton);
        phoneNumber = getIntent().getStringExtra("phone");

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

        resendOTP.setVisibility(View.GONE);
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode();
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Intent i = new Intent();
                if (e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseTooManyRequestsException) {
                    i.putExtra("exception", e.getMessage());
                    setResult(2, i);
                    finish();
                }

                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCodeSent(String vId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                resendOtpToken = token;
                verificationId = vId;
                hideProgressbar();
                Toast.makeText(getBaseContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                resendOTP.setVisibility(View.VISIBLE);
            }
        };

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getEnteredOTP();
                if (otp.length() == 6) {
                    verifyCode(otp);
                } else {
                    Toast.makeText(getBaseContext(), "Please enter the 6 digit OTP number", Toast.LENGTH_LONG).show();
                }
            }
        });


        addToolBar();
        switchOtpEditViewListener();
        initProgressBar();
        startPhoneNumberVerification();
    }

    public String getEnteredOTP() {
        String otp = "";
        for (int i = 1; i < otpTextFields.length - 1; i++) {
            otp+=otpTextFields[i].getText();
        }
        return otp;
    }

    public void verifyCode(String code) {
        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredenticals(credentials);
    }

    public void signInWithCredenticals(PhoneAuthCredential credentials) {
        showProgress();
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressbar();
                if (task.isSuccessful()) {
                    final FirebaseUser firebaseUser = task.getResult().getUser();
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) {
                                FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).setValue(firebaseUser);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Intent i = new Intent(OTPVerificationActivity.this, BaseActivity.class);
                    i.putExtra("user",firebaseUser);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startPhoneNumberVerification() {
        showProgress();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks);

    }

    private void resendVerificationCode() {
        showProgress();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks,
                resendOtpToken);
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

    public void initProgressBar() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Please wait");
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    public void showProgress() {
        loadingDialog.show();
    }

    public void hideProgressbar() {
        loadingDialog.dismiss();
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
                if (curr.getText().toString().equals("")) {
                    moveFocusToPreviousField(true);
                    return true;
                }
            }
            return false;
        }

        public void moveFocusToNextField(boolean clearValue) {
            if (next != null) {
                next.requestFocus();
                if (clearValue) {
                    next.setText("");
                }
            }
        }

        public void moveFocusToPreviousField(boolean clearValue) {
            if (prev != null) {
                prev.requestFocus();
                if (clearValue) {
                    prev.setText("");
                }
            }
        }

    }
}
