package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.dialogs.CoverChooseDialog;
import ru.asterios.open.dialogs.FriendRequestActionDialog;
import ru.asterios.open.dialogs.MyPhotoActionDialog;
import ru.asterios.open.dialogs.MyPostActionDialog;
import ru.asterios.open.dialogs.PeopleNearbySettingsDialog;
import ru.asterios.open.dialogs.PeopleNearbySexDialog;
import ru.asterios.open.dialogs.PhotoChooseDialog;
import ru.asterios.open.dialogs.PhotoDeleteDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;
import ru.asterios.open.dialogs.PostShareDialog;
import ru.asterios.open.dialogs.ProfileBlockDialog;
import ru.asterios.open.dialogs.ProfileReportDialog;
import ru.asterios.open.dialogs.SearchSettingsDialog;

public class MainActivity extends ActivityBase implements FragmentDrawer.FragmentDrawerListener, PhotoChooseDialog.AlertPositiveListener, CoverChooseDialog.AlertPositiveListener, ProfileReportDialog.AlertPositiveListener, ProfileBlockDialog.AlertPositiveListener, PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener, PostShareDialog.AlertPositiveListener, PhotoDeleteDialog.AlertPositiveListener, MyPhotoActionDialog.AlertPositiveListener, PeopleNearbySettingsDialog.AlertPositiveListener, SearchSettingsDialog.AlertPositiveListener, FriendRequestActionDialog.AlertPositiveListener, PeopleNearbySexDialog.AlertPositiveListener {

    Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    // used to store app title
    private CharSequence mTitle;

    LinearLayout mContainerAdmob;

    Fragment fragment;
    Boolean action = false;
    int page = 0;

    private Boolean signup = false;

    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");
            signup = savedInstanceState.getBoolean("signup");

        } else {

            fragment = new FeedFragment();

            restore = false;
            mTitle = getString(R.string.app_name);

            signup = false;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        Intent i = getIntent();

        signup = i.getBooleanExtra("signup", false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        mContainerAdmob = (LinearLayout) findViewById(R.id.container_admob);

        mContainerAdmob.setVisibility(View.GONE);

        if (!restore) {

            // 3 FEED SECTION
            // 4 STREAM SECTION
            // 5 POPULAR SECTION
            // 6 NOTIFICATIONS SECTION
            // 7 MESSAGES SECTION

            // FOR OTHERS INDEXES: SEE displayView FUNCTION

            displayView(3);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("signup", signup);
        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onChangeDistance(int position) {

        PeopleNearbyFragment p = (PeopleNearbyFragment) fragment;
        p.onChangeDistance(position);
    }

    @Override
    public void onChangeSex(int position) {

        PeopleNearbyFragment p = (PeopleNearbyFragment) fragment;
        p.onChangeSex(position);
    }

    @Override
    public void onCloseSettingsDialog(int searchGender, int searchOnline, int searchPhoto) {

        SearchFragment p = (SearchFragment) fragment;
        p.onCloseSettingsDialog(searchGender, searchOnline, searchPhoto);
    }

    @Override
    public void onAcceptRequest(int position) {

        NotificationsFragment p = (NotificationsFragment) fragment;
        p.onAcceptRequest(position);
    }

    @Override
    public void onRejectRequest(int position) {

        NotificationsFragment p = (NotificationsFragment) fragment;
        p.onRejectRequest(position);
    }

    @Override
    public void onPhotoFromGallery() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.photoFromGallery();
    }

    @Override
    public void onPhotoFromCamera() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.photoFromCamera();
    }

    @Override
    public void onCoverFromGallery() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.coverFromGallery();
    }

    @Override
    public void onCoverFromCamera() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.coverFromCamera();
    }

    @Override
    public void onProfileReport(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onProfileReport(position);
    }

    @Override
    public void onProfileBlock() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onProfileBlock();
    }

    @Override
    public void onPostRePost(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostShare(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostShare(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostShare(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostShare(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostShare(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostShare(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostDelete(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostRemove(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostEdit(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostCopyLink(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostReportDialog(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.report(position);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.report(position);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.report(position);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.report(position);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.report(position);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 3: {

                FeedFragment p = (FeedFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 4: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 5: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 9: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {

        action = false;

        switch (position) {

            case 0: {

                break;
            }

            case 1: {

                page = 1;

                fragment = new ProfileFragment();
                getSupportActionBar().setTitle(R.string.page_1);

                action = true;

                break;
            }

            case 2: {

                page = 2;

                fragment = new GalleryFragment();
                getSupportActionBar().setTitle(R.string.page_11);

                action = true;

                break;
            }

            case 3: {

                if (signup) {

                    page = 4;

                    fragment = new StreamFragment();
                    getSupportActionBar().setTitle(R.string.page_3);

                    signup = false;

                } else {

                    page = 3;

                    fragment = new FeedFragment();
                    getSupportActionBar().setTitle(R.string.page_2);
                }

                action = true;

                break;
            }

            case 4: {

                page = 4;

                fragment = new StreamFragment();
                getSupportActionBar().setTitle(R.string.page_3);

                action = true;

                break;
            }

            case 5: {

                page = 5;

                fragment = new PopularFragment();
                getSupportActionBar().setTitle(R.string.page_4);

                action = true;

                break;
            }

            case 6: {

                page = 6;

                fragment = new FriendsFragment();
                getSupportActionBar().setTitle(R.string.page_5);

                action = true;

                break;
            }

            case 7: {

                page = 7;

                fragment = new NotificationsFragment();
                getSupportActionBar().setTitle(R.string.page_6);

                action = true;

                break;
            }

            case 8: {

                page = 8;

                fragment = new DialogsFragment();
                getSupportActionBar().setTitle(R.string.page_7);

                action = true;

                break;
            }

            case 9: {

                page = 9;

                fragment = new FavoritesFragment();
                getSupportActionBar().setTitle(R.string.page_8);

                action = true;

                break;
            }

            case 10: {

                page = 10;

                fragment = new GuestsFragment();
                getSupportActionBar().setTitle(R.string.page_12);

                action = true;

                break;
            }

            case 11: {

                page = 11;

                fragment = new GroupsFragment();
                getSupportActionBar().setTitle(R.string.page_9);

                action = true;

                break;
            }

           /* case 12: {

                page = 12;

                fragment = new UpgradesFragment();
                getSupportActionBar().setTitle(R.string.page_14);

                action = true;

                break;
            }*/

            case 12: {

                page = 12;

                fragment = new PeopleNearbyFragment();
                getSupportActionBar().setTitle(R.string.page_15);

                action = true;

                break;
            }

            case 13: {

                page = 13;

                fragment = new MarketFragment();
                getSupportActionBar().setTitle(R.string.page_16);

                action = true;

                break;
            }

            case 14: {

                page = 14;

                fragment = new SearchFragment();
                getSupportActionBar().setTitle("");

                action = true;

                break;
            }

            default: {

                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        }

        if (action) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_body, fragment)
                    .commit();
        }
    }

    @Override
    public void onPhotoDelete(int position) {

        GalleryFragment p = (GalleryFragment) fragment;
        p.onPhotoDelete(position);
    }

    @Override
    public void onPhotoRemoveDialog(int position) {

        GalleryFragment p = (GalleryFragment) fragment;
        p.onPhotoRemove(position);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
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

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment.isDrawerOpen()) {

            drawerFragment.closeDrawer();

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void hideAds() {

        if (App.getInstance().getAdmob() == ADMOB_DISABLED) {

            mContainerAdmob.setVisibility(View.GONE);
        }
    }
}
