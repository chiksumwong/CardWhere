package com.cs.cardwhere;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Toolbar
        initToolbar();
        // Init Bottom Navigation View => set two fragment card and account
        initBottomNavigationView();

    }

    private void initToolbar(){
        // Action Bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initBottomNavigationView(){
        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Bottom Navigation
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // initial fragment is Card List Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardFragment()).commit();
    }

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
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };


    // handle toolbar search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_toolbar_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Enter Company Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Todo on search text submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Todo on search text change
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // handle toolbar item (add card) clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){
            case R.id.add_card:
                intent = new Intent(this, ScanCardActivity.class);
                startActivity(intent);
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }
}
