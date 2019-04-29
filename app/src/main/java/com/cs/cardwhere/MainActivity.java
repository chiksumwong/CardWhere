package com.cs.cardwhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // initial fragment is Card List Fragment
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

        Intent intent;

        switch (item.getItemId()){
            case R.id.add_card:
                intent = new Intent(this, ScanCardActivity.class);
                startActivity(intent);
                return true;

//            case R.id.search_card:
//                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }
}
