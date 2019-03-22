package com.madboss.smsmailer.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.madboss.smsmailer.R;

import java.util.List;

import com.madboss.smsmailer.adapters.login.Country;
import com.madboss.smsmailer.adapters.login.CountryDataProvider;
import com.madboss.smsmailer.adapters.login.CountryListAdapter;

public class CountryPicker extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView countrySearch;
    List<Country> countryList;
    ListView countryListView;
    CountryListAdapter countryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_picker);
        addToolBar();
        addbackNavigation();
        initCountryListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_icon);
        countrySearch = (SearchView) searchItem.getActionView();
        setupSearch();
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (newText.equals("")) {
            countryListView.clearTextFilter();
        } else {
            countryAdapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    public void setupSearch() {
        countrySearch.setQueryHint("Search Country");
        TextView searchText = (TextView) countrySearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(Color.WHITE);
        countrySearch.setOnQueryTextListener(this);
        countrySearch.setIconifiedByDefault(false);
    }

    public void addToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Choose country");
        toolbar.setTitleTextColor(Color.rgb(255, 255, 255));
        setSupportActionBar(toolbar);
    }

    public void addbackNavigation() {
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


    public void initCountryListView() {
        countryList = CountryDataProvider.getCountryList();
        countryListView = (ListView) findViewById(R.id.countryListView);
        countryAdapter = new CountryListAdapter(this, R.layout.country_layout, countryList);
        countryListView.setAdapter(countryAdapter);
        addListViewListener();
    }

    public void addListViewListener() {
        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country selectedCountry = (Country) parent.getItemAtPosition(position);
                Intent countrySelectedIntent = new Intent();
                countrySelectedIntent.putExtra("selectedCountry", selectedCountry);
                setResult(RESULT_OK, countrySelectedIntent);
                finish();
            }
        });

    }
}
