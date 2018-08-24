package ru.asterios.open;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.asterios.open.common.ActivityBase;

public class FriendsActivity extends ActivityBase {

    Toolbar mToolbar;

    Fragment fragment;
    Boolean restore = false;

    private Boolean gcm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            gcm = savedInstanceState.getBoolean("gcm");

        } else {

            fragment = new FriendsFragment();
            getSupportActionBar().setTitle(R.string.title_activity_friends);

            restore = false;
            gcm = false;
        }

        Intent i = getIntent();

        gcm = i.getBooleanExtra("gcm", false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("gcm", gcm);
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                getMainActivity();

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // your code.

        getMainActivity();
    }

    public void getMainActivity() {

        if (gcm) {

            ActivityCompat.finishAffinity(FriendsActivity.this);

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

        } else {

            finish();
        }
    }
}