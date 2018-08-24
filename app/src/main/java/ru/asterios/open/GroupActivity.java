package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.dialogs.MyGroupPostActionDialog;
import ru.asterios.open.dialogs.MyPostActionDialog;
import ru.asterios.open.dialogs.PhotoChooseDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;
import ru.asterios.open.dialogs.PostShareDialog;
import ru.asterios.open.dialogs.ProfileReportDialog;


public class GroupActivity extends ActivityBase implements PhotoChooseDialog.AlertPositiveListener, ProfileReportDialog.AlertPositiveListener, PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener, PostShareDialog.AlertPositiveListener, MyGroupPostActionDialog.AlertPositiveListener {

    Toolbar mToolbar;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new GroupFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPhotoFromGallery() {

        GroupFragment p = (GroupFragment) fragment;
        p.photoFromGallery();
    }

    @Override
    public void onPhotoFromCamera() {

        GroupFragment p = (GroupFragment) fragment;
        p.photoFromCamera();
    }

    @Override
    public void onProfileReport(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onProfileReport(position);
    }

    @Override
    public void onPostRePost(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostRePost(position);
    }

    @Override
    public void onPostDelete(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostDelete(position);
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostReport(position, reasonId);
    }

    @Override
    public void onPostRemove(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostRemove(position);
    }

    @Override
    public void onPostEdit(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostEdit(position);
    }

    @Override
    public void onPostShare(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostShare(position);
    }

    @Override
    public void onPostCopyLink(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.onPostCopyLink(position);
    }

    @Override
    public void onPostReportDialog(int position) {

        GroupFragment p = (GroupFragment) fragment;
        p.report(position);
    }

    @Override
    public void onBackPressed(){

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
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
}
