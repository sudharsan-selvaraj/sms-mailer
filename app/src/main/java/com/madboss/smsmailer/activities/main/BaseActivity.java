package com.madboss.smsmailer.activities.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madboss.smsmailer.R;
import com.madboss.smsmailer.activities.TermsAndConditions;
import com.madboss.smsmailer.fragments.ActiveConnectionsFragment;
import com.madboss.smsmailer.fragments.SmsListenerFragment;
import com.madboss.smsmailer.services.SmsListenerService;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FirebaseAuth firebaseAuth;
    public FirebaseAuth.AuthStateListener authStateListener;

    public NavigationView navigationView;

    public HashMap<String, Integer> fragmentViews = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(BaseActivity.this, TermsAndConditions.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        initNavigationDrawer();

        fragmentViews.put("activeconnections", R.id.nav_active_connections);
        fragmentViews.put("smslistener", R.id.nav_sms_listener);

        loadFragment(getIntent().getStringExtra("view"));
        createNotificationChannel();

    }

    public void createNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT > 25) {
            NotificationChannel notificationChannel = new NotificationChannel("sms_mailer", "SMS mailer", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void loadFragment(@Nullable String fragmentName) {
        Fragment fragment = null;
        if (fragmentName == null || fragmentName.equalsIgnoreCase("smslistener")) {
            fragmentName = "smslistener";
            fragment = new SmsListenerFragment();
        } else if (fragmentName.equalsIgnoreCase("activeconnections")) {
            fragment = new ActiveConnectionsFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        navigationView.setCheckedItem(fragmentViews.get(fragmentName));
    }

    public void signOutUser(View view) {
        if (SmsListenerService.isRunning()) {
            Toast.makeText(getBaseContext(),"Please stop the SMS listener before signing out from the application",Toast.LENGTH_LONG).show();
//            new AlertDialog.Builder(BaseActivity.this)
//                    .setTitle("Stop SMS listener")
//                    .setMessage("Signing out will stop the SMS listener. Are you sure you want to sign out?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            firebaseAuth.signOut();
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    })
//                    .create()
//                    .show();
        } else {
            firebaseAuth.signOut();
        }

    }

    public void initNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SMS Mailer");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_active_connections:
                loadFragment("activeconnections");
                break;
            case R.id.nav_sms_listener:
                loadFragment("smslistener");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
