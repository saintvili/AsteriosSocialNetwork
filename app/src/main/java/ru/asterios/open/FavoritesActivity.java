package ru.asterios.open;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.dialogs.MyPostActionDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;

public class FavoritesActivity extends ActivityBase implements PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener {

    Toolbar mToolbar;

    Fragment fragment;
    Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");

        } else {

            fragment = new FavoritesFragment();
            getSupportActionBar().setTitle(R.string.title_activity_favorites);

            restore = false;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onPostDelete(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostDelete(position);
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostReport(position, reasonId);
    }

    @Override
    public void onPostRemove(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostRemove(position);
    }

    @Override
    public void onPostEdit(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostEdit(position);
    }

    @Override
    public void onPostShare(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostShare(position);
    }

    @Override
    public void onPostCopyLink(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.onPostCopyLink(position);
    }

    @Override
    public void onPostReportDialog(int position) {

        FavoritesFragment p = (FavoritesFragment) fragment;
        p.report(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

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

        finish();
    }
}