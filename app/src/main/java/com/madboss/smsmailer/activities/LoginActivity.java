package com.madboss.smsmailer.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madboss.smsmailer.R;


import java.util.ArrayList;
import java.util.List;

import com.madboss.smsmailer.adapters.login.Country;
import com.madboss.smsmailer.adapters.login.CountryDataProvider;

public class LoginActivity extends AppCompatActivity {

    EditText phoneNumberInput;
    Button nextButton;
    TextView country;
    TextView countryCode;
    Country selectedCountry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initElements();
        initView();
        initData();
        registerListeners();
    }

    private void initElements() {
        phoneNumberInput = (EditText) findViewById(R.id.phonenumber);
        nextButton = (Button) (findViewById(R.id.nextButton));
        country = (TextView) (findViewById(R.id.loginCountryName));
        countryCode = (TextView) (findViewById(R.id.loginCountryCode));

    }

    private void initData() {
        CountryDataProvider.setCountryList(loadCountryInformationFromResource());
        selectedCountry = CountryDataProvider.getCountyInfo("in");
        refreshViewWithData(selectedCountry);
    }

    private void registerListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getPhoneNumber = getPhoneNumber();
                if (getPhoneNumber.length() == 0) {
                    Toast.makeText(getBaseContext(), "Please enter the phone number", Toast.LENGTH_LONG).show();
                }
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, CountryPicker.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(i, 1);
            }
        });
    }

    private void initView() {
        phoneNumberInput.requestFocus();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                refreshViewWithData((Country) data.getSerializableExtra("selectedCountry"));
            }
        }
    }

    public void refreshViewWithData(Country selectedCountry) {
        country.setText(selectedCountry.name);
        countryCode.setText(selectedCountry.code);
    }

    public String getPhoneNumber() {
        return phoneNumberInput.getText().toString();
    }

    public List<Country> loadCountryInformationFromResource() {
        List<Country> countryList = new ArrayList<Country>();
        Resources res = getResources();
        TypedArray countryArray = res.obtainTypedArray(R.array.countries);
        int n = countryArray.length();
        String[] array;
        for (int i = 0; i < n; ++i) {
            int id = countryArray.getResourceId(i, 0);
            if (id > 0) {
                array = res.getStringArray(id);
                countryList.add(new Country(array[0], array[1], array[2], getCountryFlag(array[3])));
            }
        }
        countryArray.recycle();
        return countryList;
    }

    public int getCountryFlag(String flagname) {
        flagname = flagname.substring(flagname.lastIndexOf('/') + 1);
        int id = getResources().getIdentifier(flagname, "drawable", LoginActivity.this.getPackageName());
        return id;
    }
}
