package com.cs.cardwhere;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_card:
                    selectedFragment = new CardFragment();
                    break;
                case R.id.nav_camera:
                    selectedFragment = new CameraFragment();
                    break;
                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Action Bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Set initial fragment is Card Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardFragment()).commit();

        // Bottom Navigation
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    // actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_card:
                Intent intent = new Intent(this, ScanCardActivity.class);
                startActivity(intent);
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }
}
