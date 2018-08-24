package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
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

import ru.asterios.open.adapter.GroupListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.MyGroupPostActionDialog;
import ru.asterios.open.dialogs.PhotoChooseDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;
import ru.asterios.open.dialogs.PostShareDialog;
import ru.asterios.open.dialogs.ProfileReportDialog;
import ru.asterios.open.model.Group;
import ru.asterios.open.model.Item;
import ru.asterios.open.util.Api;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.ItemInterface;

public class GroupFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ItemInterface {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    private static final String TAG = GroupFragment.class.getSimpleName();

    private static final int GROUP_PHOTO = 1;
    private static final int GROUP_EDIT = 3;
    private static final int GROUP_NEW_POST = 4;
    private static final int GROUP_CREATE_PHOTO = 5;

    String [] names = {};

    Toolbar mToolbar;

    Button groupActionBtn;

    TextView groupName, groupFollowersCount, groupItemsCount, mGroupWallMsg, mProfileErrorScreenMsg, mProfileDisabledScreenMsg;
    TextView mItemsCount, mFollowersCount, mGroupLocation, mGroupWebsite, mGroupDesc;

    SwipeRefreshLayout mContentScreen;
    RelativeLayout mLoadingScreen, mErrorScreen, mDisabledScreen;
    LinearLayout mContainer;

    FloatingActionButton mFabButton;

    ListView mListView;
    View mListViewHeader;

    CircularImageView groupPhoto;

    Group group;

    private ArrayList<Item> itemsList;

    private GroupListAdapter adapter;

    private String selectedPhoto;
    private Uri outputFileUri;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    public long group_id;
    int itemId = 0;
    int arrayLength = 0;
    int accessMode = 0;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    private Boolean isMainScreen = false;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        group_id = i.getLongExtra("groupId", 0);

        group = new Group();
        group.setId(group_id);

        itemsList = new ArrayList<Item>();
        adapter = new GroupListAdapter(getActivity(), itemsList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            adapter = new GroupListAdapter(getActivity(), itemsList, this);

            itemId = savedInstanceState.getInt("itemId");

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            group = savedInstanceState.getParcelable("groupObj");

        } else {

            itemsList = new ArrayList<Item>();
            adapter = new GroupListAdapter(getActivity(), itemsList, this);

            itemId = 0;

            restore = false;
            loading = false;
            preload = false;
        }

        if (loading) {


            showpDialog();
        }


        mContentScreen = (SwipeRefreshLayout) rootView.findViewById(R.id.contentScreen);
        mContentScreen.setOnRefreshListener(this);

        mLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.loadingScreen);
        mErrorScreen = (RelativeLayout) rootView.findViewById(R.id.errorScreen);
        mDisabledScreen = (RelativeLayout) rootView.findViewById(R.id.disabledScreen);

        mContainer = (LinearLayout) rootView.findViewById(R.id.container);

        mProfileErrorScreenMsg = (TextView) rootView.findViewById(R.id.profileErrorScreenMsg);
        mProfileDisabledScreenMsg = (TextView) rootView.findViewById(R.id.profileDisabledScreenMsg);

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fabButton);

        mFabButton.setVisibility(View.GONE);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mFabButton.attachToListView(mListView, new ScrollDirectionListener() {

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }

        }, new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mContentScreen.isRefreshing())) {

                    loadingMore = true;

                    getItems();
                }
            }
        });

        mListViewHeader = getActivity().getLayoutInflater().inflate(R.layout.group_listview_header, null);

        mListView.addHeaderView(mListViewHeader);

        groupActionBtn = (Button) mListViewHeader.findViewById(R.id.groupActionBtn);

        groupName = (TextView) mListViewHeader.findViewById(R.id.groupName);
        groupFollowersCount = (TextView) mListViewHeader.findViewById(R.id.groupFollowersCount);
        groupItemsCount = (TextView) mListViewHeader.findViewById(R.id.groupItemsCount);

        mGroupLocation = (TextView) mListViewHeader.findViewById(R.id.groupLocation);
        mGroupWebsite = (TextView) mListViewHeader.findViewById(R.id.groupWebsite);
        mGroupDesc = (TextView) mListViewHeader.findViewById(R.id.groupDesc);

        mItemsCount = (TextView) mListViewHeader.findViewById(R.id.itemsCount);
        mFollowersCount = (TextView) mListViewHeader.findViewById(R.id.followersCount);

        mGroupWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!group.getWebPage().startsWith("https://") && !group.getWebPage().startsWith("http://")){

                    group.setWebPage("http://" + group.getWebPage());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(group.getWebPage()));
                startActivity(i);
            }
        });

        groupFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowers();
            }
        });

        mFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowers();
            }
        });

        mGroupWallMsg = (TextView) mListViewHeader.findViewById(R.id.groupWallMsg);

        groupPhoto = (CircularImageView) mListViewHeader.findViewById(R.id.groupPhoto);

        mListView.setAdapter(adapter);

        groupActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getId() == group.getAuthorId()) {

                    Intent i = new Intent(getActivity(), GroupSettingsActivity.class);
                    i.putExtra("group_id", group.getId());
                    i.putExtra("group_name", group.getFullname());
                    i.putExtra("group_location", group.getLocation());
                    i.putExtra("group_site", group.getWebPage());
                    i.putExtra("year", group.getYear());
                    i.putExtra("day", group.getDay());
                    i.putExtra("month", group.getMonth());
                    i.putExtra("group_allow_comments", group.getAllowComments());
                    i.putExtra("group_allow_posts", group.getAllowPosts());
                    i.putExtra("group_category", group.getCategory());
                    i.putExtra("group_desc", group.getBio());
                    startActivityForResult(i, GROUP_EDIT);

                } else {

                    addFollower();
                }
            }
        });

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewItemActivity.class);
                intent.putExtra("groupId", group.getId());
                startActivityForResult(intent, GROUP_NEW_POST);
            }
        });

        groupPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (group.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", group.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        if (group.getFullname() == null || group.getFullname().length() == 0) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getData();

                Log.e("Group", "OnReload");

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (group.getState() == ACCOUNT_STATE_ENABLED) {

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

        outState.putParcelable("groupObj", group);
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

        if (requestCode == GROUP_PHOTO && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            selectedPhoto = getImageUrlWithAuthority(getActivity(), selectedImage, "photo.jpg");

            if (selectedPhoto != null) {

                save(selectedPhoto, "photo.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_GROUP_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);
            }

        } else if (requestCode == GROUP_EDIT && resultCode == getActivity().RESULT_OK) {

            group.setFullname(data.getStringExtra("group_name"));
            group.setLocation(data.getStringExtra("group_location"));
            group.setWebPage(data.getStringExtra("group_site"));
            group.setBio(data.getStringExtra("group_desc"));

            group.setCategory(data.getIntExtra("group_category", 0));
            group.setAllowPosts(data.getIntExtra("group_allow_posts", 0));
            group.setAllowComments(data.getIntExtra("group_allow_comments", 0));

            group.setYear(data.getIntExtra("year", 0));
            group.setMonth(data.getIntExtra("month", 0));
            group.setDay(data.getIntExtra("day", 0));

            updateProfile();

        } else if (requestCode == GROUP_NEW_POST && resultCode == getActivity().RESULT_OK) {

            getData();

        } else if (requestCode == GROUP_CREATE_PHOTO && resultCode == getActivity().RESULT_OK) {

            try {

                selectedPhoto = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";

                save(selectedPhoto, "photo.jpg");

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_PROFILE_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setPost(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            adapter.notifyDataSetChanged();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            adapter.notifyDataSetChanged();
        }
    }

    public void choicePhoto() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PhotoChooseDialog alert = new PhotoChooseDialog();

        alert.show(fm, "alert_dialog_photo_choose");
    }

    public void photoFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), GROUP_PHOTO);
    }

    public void photoFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "photo.jpg");
            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, GROUP_CREATE_PHOTO);

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

            mContentScreen.setRefreshing(false);
        }
    }

    public void getFollowers() {

        Intent intent = new Intent(getActivity(), FollowersActivity.class);
        intent.putExtra("profileId", group.getId());
        startActivity(intent);
    }

    public void updateProfile() {

        mGroupLocation.setText(group.getLocation());
        mGroupWebsite.setText(group.getWebPage());
        mGroupDesc.setText(group.getBio());

        if (group.getAllowPosts() == 0 && App.getInstance().getId() != group.getAuthorId()) {

            mFabButton.setVisibility(View.GONE);

        } else {

            mFabButton.setImageResource(R.drawable.ic_action_new);

            mFabButton.setVisibility(View.VISIBLE);
        }

        // hide follow button is your profile
        if (group.getAuthorId() == App.getInstance().getId()) {

            groupActionBtn.setText(R.string.action_profile_edit);
        }

        if (group.getLocation() != null && group.getLocation().length() != 0) {

            mGroupLocation.setVisibility(View.VISIBLE);

        } else {

            mGroupLocation.setVisibility(View.GONE);
        }

        if (group.getWebPage() != null && group.getWebPage().length() != 0) {

            mGroupWebsite.setVisibility(View.VISIBLE);

        } else {

            mGroupWebsite.setVisibility(View.GONE);
        }

        if (group.getBio() != null && group.getBio().length() != 0) {

            mGroupDesc.setVisibility(View.VISIBLE);

        } else {

            mGroupDesc.setVisibility(View.GONE);
        }

        updateFullname();
        updateFollowersCount();
        updateItemsCount();

        showPhoto(group.getLowPhotoUrl());

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

        if (group.getFullname().length() == 0) {

            groupName.setText(group.getUsername());
            if (!isMainScreen) getActivity().setTitle(group.getUsername());

        } else {

            groupName.setText(group.getFullname());
            if (!isMainScreen) getActivity().setTitle(group.getFullname());
        }

        if (!group.isVerify()) {

            groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void updateFollowersCount() {

        mFollowersCount.setText(Integer.toString(group.getFollowersCount()));
    }

    private void updateItemsCount() {

        if (group.getItemsCount() == 0) {

            mGroupWallMsg.setVisibility(View.VISIBLE);

            mGroupWallMsg.setText(getText(R.string.label_group_havent_items));

        } else {

            mGroupWallMsg.setVisibility(View.GONE);
        }

        mItemsCount.setText(Integer.toString(group.getItemsCount()));
    }

    public void updateFollowButton(Boolean follow) {

        updateFollowersCount();

        if (follow) {

            groupActionBtn.setText(R.string.action_unfollow);

        } else {

            groupActionBtn.setText(R.string.action_follow);
        }
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "GroupFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                group = new Group(response);

                                if (group.getItemsCount() > 0) {

                                    getItems();
                                }

                                if (group.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();

                                    updateFollowButton(group.isFollow());
                                    updateProfile();

                                } else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "GroupFragment Not Added to Activity");

                    return;
                }

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("groupId", Long.toString(group_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void addFollower() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "GroupFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                group.setFollowersCount(response.getInt("followersCount"));

                                updateFollowButton(response.getBoolean("follow"));
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

                    Log.e("ERROR", "GroupFragment Not Added to Activity");

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
                params.put("groupId", Long.toString(group_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(groupPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void getItems() {

        if (loadingMore) {

            mContentScreen.setRefreshing(true);

        } else{

            itemId = 0;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_GET_WALL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "GroupFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            itemsList.add(item);
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

                    Log.e("ERROR", "GroupFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("groupId", Long.toString(group.getId()));
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

        adapter.notifyDataSetChanged();

        mContentScreen.setRefreshing(false);

        loadingMore = false;
    }

    public void showLoadingScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_group));

        mContainer.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mDisabledScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showErrorScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_group));

        mLoadingScreen.setVisibility(View.GONE);
        mDisabledScreen.setVisibility(View.GONE);
        mContainer.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showDisabledScreen() {

        if (group.getState() != ACCOUNT_STATE_ENABLED) {

            mProfileDisabledScreenMsg.setText(getText(R.string.msg_account_blocked));
        }

        getActivity().setTitle(getText(R.string.label_account_disabled));

        mContainer.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        if (!isMainScreen) {

            getActivity().setTitle(group.getFullname());
        }

        mDisabledScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mContainer.setVisibility(View.VISIBLE);
        mContentScreen.setVisibility(View.VISIBLE);
        mContentScreen.setRefreshing(false);

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
        adapter.notifyDataSetChanged();

        group.setItemsCount(group.getItemsCount() - 1);

        updateProfile();

        if (mListView.getAdapter().getCount() == 0) {

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

        if (item.getFromUserId() == App.getInstance().getId() || App.getInstance().getId() == group.getAuthorId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyGroupPostActionDialog alert = new MyGroupPostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_group_my_post_action");

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

            alert.show(fm, "alert_group_post_action");
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

            if (group.getState() != ACCOUNT_STATE_ENABLED) {

                //hide all menu items
                hideMenuItems(menu, false);
            }

            if (App.getInstance().getId() != group.getAuthorId()) {

                menu.removeItem(R.id.action_profile_edit_photo);
                menu.removeItem(R.id.action_profile_edit_cover);

                menu.removeItem(R.id.action_profile_block);

                menu.removeItem(R.id.action_new_gift);

            } else {

                // your profile

                menu.removeItem(R.id.action_profile_report);
                menu.removeItem(R.id.action_profile_block);

                menu.removeItem(R.id.action_profile_edit_cover);

                menu.removeItem(R.id.action_new_gift);
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

            case R.id.action_profile_refresh: {

                mListView.smoothScrollToPosition(0);
                mContentScreen.setRefreshing(true);
                onRefresh();

                return true;
            }

            case R.id.action_profile_block: {

                return true;
            }

            case R.id.action_profile_report: {

                profileReport();

                return true;
            }

            case R.id.action_profile_edit_photo: {

                choicePhoto();

                return true;
            }

            case R.id.action_profile_edit_cover: {

                return true;
            }

            case R.id.action_profile_copy_url: {

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(group.getUsername(), API_DOMAIN + group.getUsername());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_profile_link_copied), Toast.LENGTH_SHORT).show();

                return true;
            }

            case R.id.action_profile_open_url: {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(API_DOMAIN + group.getUsername()));
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

        api.profileReport(group.getId(), position);
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
                    .addFormDataPart("groupId", Long.toString(group.getId()))
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

                            group.setLowPhotoUrl(result.getString("lowPhotoUrl"));
                            group.setBigPhotoUrl(result.getString("bigPhotoUrl"));
                            group.setNormalPhotoUrl(result.getString("normalPhotoUrl"));
                        }

                        Log.d("My Group", response.toString());

                    } catch (Throwable t) {

                        Log.e("My Group", "Could not parse malformed JSON: \"" + response.body().string() + "\"");

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
}