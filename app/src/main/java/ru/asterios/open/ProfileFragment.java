package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.pkmmte.view.CircularImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.adapter.AdvancedItemListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.CoverChooseDialog;
import ru.asterios.open.dialogs.MyPostActionDialog;
import ru.asterios.open.dialogs.PhotoChooseDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;
import ru.asterios.open.dialogs.PostShareDialog;
import ru.asterios.open.dialogs.ProfileBlockDialog;
import ru.asterios.open.dialogs.ProfileReportDialog;
import ru.asterios.open.model.Item;
import ru.asterios.open.model.Profile;
import ru.asterios.open.util.Api;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.ItemInterface;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ItemInterface {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_COVER = 2;
    private static final int PROFILE_EDIT = 3;
    private static final int PROFILE_NEW_POST = 4;
    private static final int CREATE_PHOTO = 5;
    private static final int CREATE_COVER = 6;

    String [] names = {};

    Toolbar mToolbar;

    LinearLayout mProfileStatusContainer, mProfileActiveContainer, mProfileFacebookContainer, mProfileSiteContainer;

    Button profileActionBtn, profileMessageBtn;

    ImageView profileOnlineIcon, profileIcon;

    TextView profileFullname, profileUsername, mProfileWallMsg, mProfileErrorScreenMsg, mProfileDisabledScreenMsg;
    TextView mItemsCount, mGiftsCount, mPhotosCount, mVideosCount, mFriendsCount, mProfileLocation, mProfileActive, mProfileFacebookPage, mProfileInstagramPage, mProfileBio;

    RelativeLayout mProfileLoadingScreen, mProfileErrorScreen, mProfileDisabledScreen;
    LinearLayout mProfileLocationContainer, mProfileInfoContainer;

    SwipeRefreshLayout mProfileRefreshLayout;
    NestedScrollView mNestedScrollView;
    RecyclerView mRecyclerView;

    ImageView profileCover;
    CircularImageView profilePhoto;

    MaterialRippleLayout mProfileItemsBtn, mProfileFriendsBtn, mProfilePhotosBtn, mProfileVideosBtn, mProfileGiftsBtn;

    Profile profile;

    private ArrayList<Item> itemsList;

    private AdvancedItemListAdapter itemsAdapter;

    private String selectedPhoto, selectedCover;
    private Uri outputFileUri;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    private String profile_mention;
    public long profile_id;
    int itemId = 0;
    int arrayLength = 0;
    int accessMode = 0;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    private Boolean isMainScreen = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        profile_id = i.getLongExtra("profileId", 0);
        profile_mention = i.getStringExtra("profileMention");

        if (profile_id == 0 && (profile_mention == null || profile_mention.length() == 0)) {

            profile_id = App.getInstance().getId();
            isMainScreen = true;
        }

        profile = new Profile();
        profile.setId(profile_id);

        itemsList = new ArrayList<Item>();
        itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            itemId = savedInstanceState.getInt("itemId");

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            profile = savedInstanceState.getParcelable("profileObj");

        } else {

            itemsList = new ArrayList<Item>();
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            itemId = 0;

            restore = false;
            loading = false;
            preload = false;
        }

        if (loading) {


            showpDialog();
        }


        mProfileRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.profileRefreshLayout);
        mProfileRefreshLayout.setOnRefreshListener(this);

        mNestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);

        mProfileLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.profileLoadingScreen);
        mProfileErrorScreen = (RelativeLayout) rootView.findViewById(R.id.profileErrorScreen);
        mProfileDisabledScreen = (RelativeLayout) rootView.findViewById(R.id.profileDisabledScreen);

        mProfileErrorScreenMsg = (TextView) rootView.findViewById(R.id.profileErrorScreenMsg);
        mProfileDisabledScreenMsg = (TextView) rootView.findViewById(R.id.profileDisabledScreenMsg);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        itemsAdapter.setOnMoreButtonClickListener(new AdvancedItemListAdapter.OnItemMenuButtonClickListener() {

            @Override
            public void onItemClick(View v, Item obj, int actionId, int position) {

                switch (actionId){

                    case R.id.action_repost: {

                        if (obj.getFromUserId() != App.getInstance().getId()) {

                            if (obj.getRePostFromUserId() != App.getInstance().getId()) {

                                repost(position);

                            } else {

                                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_not_make_repost), Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(getActivity(), getActivity().getString(R.string.msg_not_make_repost), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }

                    case R.id.action_share: {

                        onPostShare(position);

                        break;
                    }

                    case R.id.action_report: {

                        report(position);

                        break;
                    }

                    case R.id.action_remove: {

                        remove(position);

                        break;
                    }
                }
            }
        });

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.setNestedScrollingEnabled(false);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mProfileRefreshLayout.isRefreshing())) {

                        mProfileRefreshLayout.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        profileActionBtn = (Button) rootView.findViewById(R.id.profileActionBtn);
        profileMessageBtn = (Button) rootView.findViewById(R.id.profileMessageBtn);

        profileFullname = (TextView) rootView.findViewById(R.id.profileFullname);
        profileUsername = (TextView) rootView.findViewById(R.id.profileUsername);

        mItemsCount = (TextView) rootView.findViewById(R.id.profileItemsCount);
        mFriendsCount = (TextView) rootView.findViewById(R.id.profileFriendsCount);
        mPhotosCount = (TextView) rootView.findViewById(R.id.profilePhotosCount);
        mVideosCount = (TextView) rootView.findViewById(R.id.profileVideosCount);
        mGiftsCount = (TextView) rootView.findViewById(R.id.profileGiftsCount);

        mProfileItemsBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileItemsBtn);
        mProfileFriendsBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileFriendsBtn);
        mProfilePhotosBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profilePhotosBtn);
        mProfileVideosBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileVideosBtn);
        mProfileGiftsBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileGiftsBtn);


        mProfileInfoContainer = (LinearLayout) rootView.findViewById(R.id.profileInfoContainer);

        mProfileLocationContainer = (LinearLayout) rootView.findViewById(R.id.profileLocationContainer);
        profileOnlineIcon = (ImageView) rootView.findViewById(R.id.profileOnlineIcon);
        profileIcon = (ImageView) rootView.findViewById(R.id.profileIcon);

        mProfileStatusContainer = (LinearLayout) rootView.findViewById(R.id.profileStatusContainer);
        mProfileActiveContainer = (LinearLayout) rootView.findViewById(R.id.profileActiveContainer);
        mProfileFacebookContainer = (LinearLayout) rootView.findViewById(R.id.profileFacebookContainer);
        mProfileSiteContainer = (LinearLayout) rootView.findViewById(R.id.profileSiteContainer);

        mProfileActive = (TextView) rootView.findViewById(R.id.profileActive);
        mProfileLocation = (TextView) rootView.findViewById(R.id.profileLocation);
        mProfileFacebookPage = (TextView) rootView.findViewById(R.id.profileFacebookUrl);
        mProfileInstagramPage = (TextView) rootView.findViewById(R.id.profileSiteUrl);
        mProfileBio = (TextView) rootView.findViewById(R.id.profileStatus);

        mProfilePhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfileGallery(profile.getId());
            }
        });

        mProfileVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfileVideoGallery(profile.getId());
            }
        });

        mProfileFacebookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!profile.getFacebookPage().startsWith("https://") && !profile.getFacebookPage().startsWith("http://")){

                    profile.setFacebookPage("http://" + profile.getFacebookPage());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(profile.getFacebookPage()));
                startActivity(i);
            }
        });

        mProfileInstagramPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!profile.getInstagramPage().startsWith("https://") && !profile.getInstagramPage().startsWith("http://")){

                    profile.setInstagramPage("http://" + profile.getInstagramPage());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(profile.getInstagramPage()));
                startActivity(i);
            }
        });

        mProfileFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfileFriends(profile.getId());
            }
        });

        mProfileGiftsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfileGifts(profile.getId());
            }
        });

        mProfileWallMsg = (TextView) rootView.findViewById(R.id.profileMessage);

        profilePhoto = (CircularImageView) rootView.findViewById(R.id.profilePhoto);
        profileCover = (ImageView) rootView.findViewById(R.id.profileCover);

        profileActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getId() == profile.getId()) {

                    Intent i = new Intent(getActivity(), AccountSettingsActivity.class);
                    i.putExtra("profileId", App.getInstance().getId());
                    i.putExtra("sex", profile.getSex());
                    i.putExtra("year", profile.getYear());
                    i.putExtra("month", profile.getMonth());
                    i.putExtra("day", profile.getDay());
                    i.putExtra("fullname", profile.getFullname());
                    i.putExtra("location", profile.getLocation());
                    i.putExtra("facebookPage", profile.getFacebookPage());
                    i.putExtra("instagramPage", profile.getInstagramPage());
                    i.putExtra("bio", profile.getBio());
                    startActivityForResult(i, PROFILE_EDIT);

                } else {

                    if (profile.isFriend()) {

                        removeFromFriends();

                    } else {

                        addFollower();
                    }
                }
            }
        });

        profileMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profile.getId() == App.getInstance().getId()) {

                    Intent intent = new Intent(getActivity(), NewItemActivity.class);
                    startActivityForResult(intent, PROFILE_NEW_POST);

                } else {

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatId", 0);
                    i.putExtra("profileId", profile.getId());
                    i.putExtra("withProfile", profile.getFullname());

                    i.putExtra("with_android_fcm_regId", profile.get_android_fcm_regId());
                    i.putExtra("with_ios_fcm_regId", profile.get_iOS_fcm_regId());
                    i.putExtra("with_android_msg_fcm_regId", profile.get_android_msg_fcm_regId());
                    i.putExtra("with_ios_msg_fcm_regId", profile.get_iOS_msg_fcm_regId());

                    i.putExtra("with_user_username", profile.getUsername());
                    i.putExtra("with_user_fullname", profile.getFullname());
                    i.putExtra("with_user_photo_url", profile.getNormalPhotoUrl());

                    i.putExtra("with_user_state", profile.getState());
                    i.putExtra("with_user_verified", profile.getVerify());

                    startActivityForResult(i, PROFILE_EDIT);
                }
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (profile.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", profile.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        if (profile.getFullname() == null || profile.getFullname().length() == 0) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getData();

                Log.e("Profile", "OnReload");

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                    showContentScreen();

                    loadingComplete();
                    updateProfile();

                } else {

                    showDisabledScreen();
                }

            } else {

                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt("itemId", itemId);

        outState.putBoolean("restore", restore);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);

        outState.putParcelable("profileObj", profile);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    private Bitmap resize(String path){

        int maxWidth = 512;
        int maxHeight = 512;

        // create the options
        BitmapFactory.Options opts = new BitmapFactory.Options();

        //just decode the file
        opts.inJustDecodeBounds = true;
        Bitmap bp = BitmapFactory.decodeFile(path, opts);

        //get the original size
        int orignalHeight = opts.outHeight;
        int orignalWidth = opts.outWidth;

        //initialization of the scale
        int resizeScale = 1;

        //get the good scale
        if (orignalWidth > maxWidth || orignalHeight > maxHeight) {

            final int heightRatio = Math.round((float) orignalHeight / (float) maxHeight);
            final int widthRatio = Math.round((float) orignalWidth / (float) maxWidth);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        //put the scale instruction (1 -> scale to (1/1); 8-> scale to 1/8)
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;

        //get the futur size of the bitmap
        int bmSize = (orignalWidth / resizeScale) * (orignalHeight / resizeScale) * 4;

        //check if it's possible to store into the vm java the picture
        if (Runtime.getRuntime().freeMemory() > bmSize) {

            //decode the file
            bp = BitmapFactory.decodeFile(path, opts);

        } else {

            return null;
        }

        return bp;
    }

    public void save(String outFile, String inFile) {

        try {

            Bitmap bmp = resize(outFile);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, inFile);
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception ex) {

            Log.e("Error", ex.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            selectedPhoto = getImageUrlWithAuthority(getActivity(), selectedImage, "photo.jpg");

            if (selectedPhoto != null) {

                save(selectedPhoto, "photo.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_PROFILE_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);
            }

        } else if (requestCode == SELECT_COVER && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            selectedCover = getImageUrlWithAuthority(getActivity(), selectedImage, "cover.jpg");

            if (selectedCover != null) {

                save(selectedCover, "cover.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "cover.jpg");

                uploadFile(METHOD_PROFILE_UPLOADCOVER, f, UPLOAD_TYPE_COVER);
            }

        } else if (requestCode == PROFILE_EDIT && resultCode == getActivity().RESULT_OK) {

            profile.setFullname(data.getStringExtra("fullname"));
            profile.setLocation(data.getStringExtra("location"));
            profile.setFacebookPage(data.getStringExtra("facebookPage"));
            profile.setInstagramPage(data.getStringExtra("instagramPage"));
            profile.setBio(data.getStringExtra("bio"));

            profile.setSex(data.getIntExtra("sex", 0));

            profile.setYear(data.getIntExtra("year", 0));
            profile.setMonth(data.getIntExtra("month", 0));
            profile.setDay(data.getIntExtra("day", 0));

            updateProfile();

        } else if (requestCode == PROFILE_NEW_POST && resultCode == getActivity().RESULT_OK) {

            getData();

        } else if (requestCode == CREATE_PHOTO && resultCode == getActivity().RESULT_OK) {

            try {

                selectedPhoto = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";

                save(selectedPhoto, "photo.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_PROFILE_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == CREATE_COVER && resultCode == getActivity().RESULT_OK) {

            try {

                selectedCover = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "cover.jpg";

                save(selectedCover, "cover.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "cover.jpg");

                uploadFile(METHOD_PROFILE_UPLOADCOVER, f, UPLOAD_TYPE_COVER);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setPost(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            itemsAdapter.notifyDataSetChanged();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            itemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choicePhoto();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceCover();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getApplicationContext(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    public void choicePhoto() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PhotoChooseDialog alert = new PhotoChooseDialog();

        alert.show(fm, "alert_dialog_photo_choose");
    }

    public void photoFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_PHOTO);
    }

    public void photoFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "photo.jpg");
            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_PHOTO);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void choiceCover() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        CoverChooseDialog alert = new CoverChooseDialog();

        alert.show(fm, "alert_dialog_cover_choose");
    }

    public void coverFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_COVER);
    }

    public void coverFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "cover.jpg");
            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_COVER);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri, String fileName) {

        InputStream is = null;

        if (uri.getAuthority() != null) {

            try {

                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);

                return writeToTempImageAndGetPathUri(context, bmp, fileName).toString();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (is != null) {

                        is.close();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage, String fileName) {

        String file_path = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER;
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);

        try {

            FileOutputStream fos = new FileOutputStream(file);

            inImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

            Toast.makeText(inContext, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + fileName;
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getData();

        } else {

            mProfileRefreshLayout.setRefreshing(false);
        }
    }

    public void showProfileFriends(long profileId) {

        if (profile.getAllowShowMyFriends() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), FriendsActivity.class);
            intent.putExtra("profileId", profileId);
            startActivity(intent);

        } else {

            if (profile.getAllowShowMyFriends() == 1 && profile.isFriend()) {

                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("profileId", profileId);
                startActivity(intent);

            }
        }
    }

    public void updateProfile() {

        if (profile.getLastActive() == 0) {

            mProfileActive.setText(getString(R.string.label_offline));
            profileOnlineIcon.setVisibility(View.GONE);

        } else {

            if (profile.isOnline()) {

                mProfileActive.setText(getString(R.string.label_online));
                profileOnlineIcon.setVisibility(View.VISIBLE);

            } else {

                mProfileActive.setText(profile.getLastActiveTimeAgo());
                profileOnlineIcon.setVisibility(View.GONE);
            }
        }

        // Profile Info

        mProfileInfoContainer.setVisibility(View.GONE);

        if (profile.getAllowShowMyInfo() == 0 || App.getInstance().getId() == profile.getId()) {

            mProfileInfoContainer.setVisibility(View.VISIBLE);

        } else {

            if (profile.getAllowShowMyInfo() == 1 && profile.isFriend()) {

                mProfileInfoContainer.setVisibility(View.VISIBLE);
            }
        }

        profileUsername.setText("@" + profile.getUsername());
        mProfileLocation.setText(profile.getLocation());
        mProfileFacebookPage.setText(profile.getFacebookPage());
        mProfileInstagramPage.setText(profile.getInstagramPage());
        mProfileBio.setText(profile.getBio());

        // hide follow button is your profile
        if (profile.getId() == App.getInstance().getId()) {

            profileActionBtn.setText(R.string.action_profile_edit);

            profileMessageBtn.setVisibility(View.VISIBLE);
            profileMessageBtn.setText(R.string.action_new_item);

        } else {

            profileMessageBtn.setText(R.string.action_message);
            profileMessageBtn.setVisibility(View.GONE);

            if (!profile.isInBlackList()) {

                if ((profile.getAllowMessages() == 1) || (profile.getAllowMessages() == 0 && profile.isFriend())) {

                    profileMessageBtn.setVisibility(View.VISIBLE);
                }
            }
        }

        if (profile.getLocation() != null && profile.getLocation().length() != 0) {

            mProfileLocationContainer.setVisibility(View.VISIBLE);

        } else {

            mProfileLocationContainer.setVisibility(View.GONE);
        }

        if (profile.getFacebookPage() != null && profile.getFacebookPage().length() != 0) {

            mProfileFacebookContainer.setVisibility(View.VISIBLE);

        } else {

            mProfileFacebookContainer.setVisibility(View.GONE);
        }

        if (profile.getInstagramPage() != null && profile.getInstagramPage().length() != 0) {

            mProfileSiteContainer.setVisibility(View.VISIBLE);

        } else {

            mProfileSiteContainer.setVisibility(View.GONE);
        }

        if (profile.getBio() != null && profile.getBio().length() != 0) {

            mProfileStatusContainer.setVisibility(View.VISIBLE);

        } else {

            mProfileStatusContainer.setVisibility(View.GONE);
        }

        updateFullname();
        updateFriendsCount();
        updateItemsCount();
        updatePhotosCount();
        updateVideosCount();
        updateGiftsCount();
        updateActionButton();

        showPhoto(profile.getLowPhotoUrl());
        showCover(profile.getNormalCoverUrl());

        showContentScreen();

        if (this.isVisible()) {

            try {

                getActivity().invalidateOptionsMenu();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void updateFullname() {

        if (profile.getFullname().length() == 0) {

            profileFullname.setText(profile.getUsername());
            if (!isMainScreen) getActivity().setTitle(profile.getUsername());

        } else {

            profileFullname.setText(profile.getFullname());
            if (!isMainScreen) getActivity().setTitle(profile.getFullname());
        }

        if (!profile.isVerify()) {

            profileIcon.setVisibility(View.GONE);

        } else {

            profileIcon.setVisibility(View.VISIBLE);
        }
    }

    private void updateFriendsCount() {

        mFriendsCount.setText(Integer.toString(profile.getFriendsCount()));
    }

    private void updatePhotosCount() {

        mPhotosCount.setText(Integer.toString(profile.getPhotosCount()));
    }

    private void updateVideosCount() {

        mVideosCount.setText(Integer.toString(profile.getVideosCount()));
    }

    private void updateGiftsCount() {

        mGiftsCount.setText(Integer.toString(profile.getGiftsCount()));
    }

    private void updateItemsCount() {

        if (profile.getItemsCount() == 0) {

            mProfileWallMsg.setVisibility(View.VISIBLE);

            if (App.getInstance().getId() == profile.getId()) {

                mProfileWallMsg.setText(getText(R.string.label_you_havent_items));

            } else {

                mProfileWallMsg.setText(getText(R.string.label_user_havent_items));
            }

        } else {

            mProfileWallMsg.setVisibility(View.GONE);
        }

        mItemsCount.setText(Integer.toString(profile.getItemsCount()));
    }

    public void updateActionButton() {

        if (profile.getId() == App.getInstance().getId()) {

            profileActionBtn.setText(R.string.action_profile_edit);
            profileActionBtn.setEnabled(true);

        } else {

            if (profile.isFriend()) {

                profileActionBtn.setText(R.string.action_remove_from_friends);
                profileActionBtn.setEnabled(true);

            } else {

                if (profile.isFollow()) {

                    profileActionBtn.setText(R.string.action_cancel_friends_request);
                    profileActionBtn.setEnabled(true);

                } else {

                    profileActionBtn.setText(R.string.action_add_to_friends);
                    profileActionBtn.setEnabled(true);
                }
            }
        }
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile = new Profile(response);

                                changeAccessMode();

                                if (profile.getItemsCount() > 0) {

                                    getItems();
                                }

                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();

                                    updateProfile();

                                } else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                            Log.d("Response GetData", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                Log.e("Error GetData", error.toString());

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        jsonReq.setRetryPolicy(new RetryPolicy() {

            @Override
            public int getCurrentTimeout() {

                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {

                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void removeFromFriends() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FRIENDS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setFriend(false);
                                profile.setFriendsCount(profile.getFriendsCount() - 1);

                                updateProfile();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("friendId", Long.toString(profile.getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void addFollower() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setFollow(response.getBoolean("follow"));
                                profile.setFollowersCount(response.getInt("followersCount"));

                                updateActionButton();

                                changeAccessMode();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void changeAccessMode() {

        if (App.getInstance().getId() == profile.getId() || profile.isFriend()) {

            accessMode = 1;

        } else {

            accessMode = 0;
        }
    }

    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        if (coverUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(coverUrl, ImageLoader.getImageListener(profileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

            if (Build.VERSION.SDK_INT > 15) {

                profileCover.setImageAlpha(200);
            }
        }
    }

    public void getItems() {

        if (loadingMore) {

            mProfileRefreshLayout.setRefreshing(true);

        } else{

            itemId = 0;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_WALL_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("postId");

                                if (response.has("posts")) {

                                    JSONArray itemsArray = response.getJSONArray("posts");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            item.setAd(0);

                                            itemsList.add(item);

                                            // Ad after first item
                                            if (i == MY_AD_AFTER_ITEM_NUMBER && App.getInstance().getAdmob() == ENABLED) {

                                                Item ad = new Item(itemObj);

                                                ad.setAd(1);

                                                itemsList.add(ad);
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                loadingComplete();

                Log.e("Profile getItems Error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
                params.put("itemId", Integer.toString(itemId));
                params.put("accessMode", Integer.toString(accessMode));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        mProfileRefreshLayout.setRefreshing(false);

        loadingMore = false;

//        if (this.isVisible()) getActivity().invalidateOptionsMenu();
    }

    public void showLoadingScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);

        mProfileLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showErrorScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileRefreshLayout.setVisibility(View.GONE);

        mProfileErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showDisabledScreen() {

        if (profile.getState() != ACCOUNT_STATE_ENABLED) {

            mProfileDisabledScreenMsg.setText(getText(R.string.msg_account_blocked));
        }

        getActivity().setTitle(getText(R.string.label_account_disabled));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        if (!isMainScreen) {

            getActivity().setTitle(profile.getFullname());
        }

        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileRefreshLayout.setVisibility(View.VISIBLE);
        mProfileRefreshLayout.setRefreshing(false);

        loadingComplete = true;
        restore = true;
    }

    public void report(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostReportDialog alert = new PostReportDialog();

        Bundle b  = new Bundle();
        b.putInt("position", position);
        b.putInt("reason", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_report");
    }

    public void onPostReport(int position, int reasonId) {

        final Item item = itemsList.get(position);

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.postReport(item.getId(), reasonId);

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void remove(int position) {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        PostDeleteDialog alert = new PostDeleteDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", position);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_post_delete");
    }

    public void onPostDelete(final int position) {

        final Item item = itemsList.get(position);

        itemsList.remove(position);
        itemsAdapter.notifyDataSetChanged();

        profile.setItemsCount(profile.getItemsCount() - 1);

        updateProfile();

        if (itemsAdapter.getItemCount() == 0) {

//            showEmptyScreen();

        } else {

            showContentScreen();
        }

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.postDelete(item.getId());

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void onPostRemove(final int position) {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        PostDeleteDialog alert = new PostDeleteDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", position);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_post_delete");
    }

    public void onPostShare(final int position) {

        final Item item = itemsList.get(position);

        Api api = new Api(getActivity());
        api.postShare(item);
    }

    public void onPostEdit(final int position) {

        Item item = itemsList.get(position);

        Intent i = new Intent(getActivity(), EditItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("postId", item.getId());
        i.putExtra("post", item.getPost());
        i.putExtra("imgUrl", item.getImgUrl());
        startActivityForResult(i, ITEM_EDIT);
    }

    public void onPostCopyLink(final int position) {

        final Item item = itemsList.get(position);

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("post url", item.getLink());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), getText(R.string.msg_post_link_copied), Toast.LENGTH_SHORT).show();
    }

    public void action(int position) {

        final Item item = itemsList.get(position);

        if (item.getFromUserId() == App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyPostActionDialog alert = new MyPostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_my_post_action");

        } else {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            PostActionDialog alert = new PostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_post_action");
        }
    }

    public void repost(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostShareDialog alert = new PostShareDialog();

        Bundle b  = new Bundle();

        b.putInt("position", position);

        alert.setArguments(b);

        alert.show(fm, "alert_repost_action");
    }

    public void onPostRePost(final int position) {

        Item item = itemsList.get(position);

        long rePostId = item.getId();

        if (item.getRePostId() != 0) {

            rePostId = item.getRePostId();
        }

        Intent i = new Intent(getActivity(), RePostItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("rePostId", rePostId);
        startActivityForResult(i, ITEM_REPOST);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);

//        MainMenu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (loadingComplete) {

            if (profile.getState() != ACCOUNT_STATE_ENABLED) {

                //hide all menu items
                hideMenuItems(menu, false);
            }

            if (App.getInstance().getId() != profile.getId()) {

                MenuItem menuItem = menu.findItem(R.id.action_profile_block);

                if (profile.isBlocked()) {

                    menuItem.setTitle(getString(R.string.action_unblock));

                } else {

                    menuItem.setTitle(getString(R.string.action_block));
                }

                menu.removeItem(R.id.action_profile_edit_photo);
                menu.removeItem(R.id.action_profile_edit_cover);

            } else {

                // your profile

                menu.removeItem(R.id.action_profile_report);
                menu.removeItem(R.id.action_profile_block);
                menu.removeItem(R.id.action_new_gift);
            }

            // If site not available - hide items

            if (!WEB_SITE_AVAILABLE) {

                menu.removeItem(R.id.action_profile_copy_url);
                menu.removeItem(R.id.action_profile_open_url);
            }

            //show all menu items
            hideMenuItems(menu, true);

        } else {

            //hide all menu items
            hideMenuItems(menu, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_new_gift: {

                if (!profile.isInBlackList()) {

                    Intent intent = new Intent(getActivity(), SelectGiftActivity.class);
                    intent.putExtra("profileId", profile.getId());
                    startActivity(intent);

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_action), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            case R.id.action_profile_refresh: {

                mProfileRefreshLayout.setRefreshing(true);
                onRefresh();

                return true;
            }

            case R.id.action_profile_block: {

                profileBlock();

                return true;
            }

            case R.id.action_profile_report: {

                profileReport();

                return true;
            }

            case R.id.action_profile_edit_photo: {

                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                    } else {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                    }

                } else {

                    choicePhoto();
                }

                return true;
            }

            case R.id.action_profile_edit_cover: {

                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER);

                    } else {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER);
                    }

                } else {

                    choiceCover();
                }

                return true;
            }

            case R.id.action_profile_copy_url: {

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(profile.getUsername(), API_DOMAIN + profile.getUsername());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_profile_link_copied), Toast.LENGTH_SHORT).show();

                return true;
            }

            case R.id.action_profile_open_url: {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(API_DOMAIN + profile.getUsername()));
                startActivity(i);

                return true;
            }

            case R.id.action_profile_settings: {

                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {

        for (int i = 0; i < menu.size(); i++){

            menu.getItem(i).setVisible(visible);
        }
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void profileReport() {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        ProfileReportDialog alert = new ProfileReportDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", 0);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_profile_report");
    }

    public  void onProfileReport(final int position) {

        Api api = new Api(getActivity());

        api.profileReport(profile.getId(), position);
    }

    public void profileBlock() {

        if (!profile.isBlocked()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            ProfileBlockDialog alert = new ProfileBlockDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putString("blockUsername", profile.getUsername());

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_dialog_profile_block");

        } else {

            loading = true;

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_REMOVE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                return;
                            }

                            try {

                                if (!response.getBoolean("error")) {

                                    profile.setBlocked(false);

                                    Toast.makeText(getActivity(), getString(R.string.msg_profile_removed_from_blacklist), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loading = false;

                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "ProfileFragment Not Added to Activity");

                        return;
                    }

                    loading = false;

                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("profileId", Long.toString(profile.getId()));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public  void onProfileBlock() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_ADD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setBlocked(true);

                                Toast.makeText(getActivity(), getString(R.string.msg_profile_added_to_blacklist), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
                params.put("reason", "example");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean uploadFile(String serverURL, File file, final int type) {

        loading = true;

        showpDialog();

        final OkHttpClient client = new OkHttpClient();

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                    loading = false;

                    hidepDialog();

                    Log.e("failure", request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    Log.e("response", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            switch (type) {

                                case 0: {

                                    profile.setLowPhotoUrl(result.getString("lowPhotoUrl"));
                                    profile.setBigPhotoUrl(result.getString("bigPhotoUrl"));
                                    profile.setNormalPhotoUrl(result.getString("normalPhotoUrl"));

                                    App.getInstance().setPhotoUrl(result.getString("lowPhotoUrl"));

                                    break;
                                }

                                default: {

                                    profile.setNormalCoverUrl(result.getString("normalCoverUrl"));

                                    App.getInstance().setCoverUrl(result.getString("normalCoverUrl"));

                                    break;
                                }
                            }
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + response.body().string() + "\"");

                    } finally {

                        loading = false;

                        hidepDialog();

                        getData();
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }

    public void showProfileVideoGallery(long profileId) {

        if (profile.getAllowShowMyVideos() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), VideoGalleryActivity.class);
            intent.putExtra("profileId", profile.getId());
            startActivity(intent);

        } else {

            if (profile.getAllowShowMyVideos() == 1 && profile.isFriend()) {

                Intent intent = new Intent(getActivity(), VideoGalleryActivity.class);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
            }
        }
    }

    public void showProfileGallery(long profileId) {

        if (profile.getAllowShowMyPhotos() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra("profileId", profile.getId());
            startActivity(intent);

        } else {

            if (profile.getAllowShowMyPhotos() == 1 && profile.isFriend()) {

                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
            }
        }
    }

    public void showProfileGifts(long profileId) {

        if (profile.getAllowShowMyGifts() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), GiftsActivity.class);
            intent.putExtra("profileId", profileId);
            startActivity(intent);

        } else {

            if (profile.getAllowShowMyGifts() == 1 && profile.isFriend()) {

                Intent intent = new Intent(getActivity(), GiftsActivity.class);
                intent.putExtra("profileId", profileId);
                startActivity(intent);
            }
        }
    }
}