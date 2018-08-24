package ru.asterios.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.PostImageChooseDialog;
import ru.asterios.open.dialogs.PostModeDialog;
import ru.asterios.open.util.CustomRequest;

import static com.facebook.FacebookSdk.getApplicationContext;
import static ru.asterios.open.AddVideoFragment.REQUEST_TAKE_GALLERY_VIDEO;

public class NewItemFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    EmojiconEditText mPostEdit;
    ImageView mChoicePostImg, mChoiceVideo, mAddLocation, mLocationDelete, mEmojiBtn;

    TextView mLabelPostMode, mLocationCountry, mLocationCity;
    LinearLayout mPostModeChoose, mLocationContainer;

    String videoImgUrl = "", videoUrl = "", postText = "", postImg = "", postArea = "", postCountry = "", postCity = "", postLat = "0.000000", postLng = "0.000000";
    private String selectedPostImg = "";
    private String selectedVideoImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;
    private String selectedImagePath;

    private int postMode = 0;
    private long groupId = 0;

    private Boolean loading = false;

    private Boolean video_upload = false;
    private Boolean image_upload = false;

    private Activity mActivity;

    EmojiconsPopup popup;

    public NewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        groupId = i.getLongExtra("groupId", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_new_item, container, false);

        popup = new EmojiconsPopup(rootView, getActivity());

        popup.setSizeForSoftKeyboard();

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mPostEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mPostEdit.dispatchKeyEvent(event);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                setIconEmojiKeyboard();
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {

                if (popup.isShowing())

                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mPostEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mPostEdit.dispatchKeyEvent(event);
            }
        });

        if (loading) {

            showpDialog();
        }

        mPostEdit = (EmojiconEditText) rootView.findViewById(R.id.postEdit);
        mChoicePostImg = (ImageView) rootView.findViewById(R.id.choicePostImg);
        mChoiceVideo = (ImageView) rootView.findViewById(R.id.choiceVideo);
        mAddLocation = (ImageView) rootView.findViewById(R.id.addLocation);
        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);
        mLocationDelete = (ImageView) rootView.findViewById(R.id.locationDelete);

        setEditTextMaxLength(POST_CHARACTERS_LIMIT);

        mLocationDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteLocation();
            }
        });

        mAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getCountry().length() != 0 || App.getInstance().getCity().length() != 0) {

                    setLocation();

                } else {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                        }

                    } else {

                        Intent i = new Intent(getActivity(), LocationActivity.class);
                        startActivityForResult(i, 77);
                    }
                }
            }
        });

        mLocationContainer = (LinearLayout) rootView.findViewById(R.id.locationContainer);

        mLocationCountry = (TextView) rootView.findViewById(R.id.locationCountry);
        mLocationCity = (TextView) rootView.findViewById(R.id.locationCity);

        mLabelPostMode = (TextView) rootView.findViewById(R.id.labelPostMode);
        mPostModeChoose = (LinearLayout) rootView.findViewById(R.id.postModeChoose);

        if (groupId != 0) {

            mPostModeChoose.setVisibility(View.GONE);
            mLocationContainer.setVisibility(View.GONE);
            mAddLocation.setVisibility(View.GONE);

            // No add video in group posts

            mChoiceVideo.setVisibility(View.GONE);
        }

        mPostModeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseMode();
            }
        });

        mChoicePostImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedPostImg.length() == 0) {

                    choiceImage();

                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getText(R.string.action_remove));

                    alertDialog.setMessage(getText(R.string.label_delete_img));
                    alertDialog.setCancelable(true);

                    alertDialog.setNeutralButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton(getText(R.string.action_remove), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            mChoicePostImg.setImageResource(R.drawable.ic_camera);
                            selectedPostImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        mChoiceVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedVideoImg.length() == 0) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);
                        }

                    } else {

                        choiceVideo();
                    }

                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getText(R.string.action_remove));

                    alertDialog.setMessage(getText(R.string.label_delete_video));
                    alertDialog.setCancelable(true);

                    alertDialog.setNeutralButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton(getText(R.string.action_remove), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            mChoiceVideo.setImageResource(R.drawable.ic_action_video_file);
                            selectedVideoImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        mPostEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int cnt = s.length();

                if (cnt == 0) {

                    getActivity().setTitle(getText(R.string.title_activity_new_item));

                } else {

                    getActivity().setTitle(Integer.toString(POST_CHARACTERS_LIMIT - cnt));
                }
            }

        });

        if (selectedPostImg != null && selectedPostImg.length() > 0) {

            mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
        }

        if (selectedVideoImg != null && selectedVideoImg.length() > 0) {

            mChoiceVideo.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedVideoImg)));
        }

        if (!EMOJI_KEYBOARD) {

            mEmojiBtn.setVisibility(View.GONE);
        }

        mEmojiBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()){

                        popup.showAtBottom();
                        setIconSoftKeyboard();

                    } else {

                        mPostEdit.setFocusableInTouchMode(true);
                        mPostEdit.requestFocus();
                        popup.showAtBottomPending();

                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mPostEdit, InputMethodManager.SHOW_IMPLICIT);
                        setIconSoftKeyboard();
                    }

                } else {

                    popup.dismiss();
                }
            }
        });

        EditTextImeBackListener er = new EditTextImeBackListener() {

            @Override
            public void onImeBack(EmojiconEditText ctrl, String text) {

                hideEmojiKeyboard();
            }
        };

        mPostEdit.setOnEditTextImeBackListener(er);

        setPostModeText();
        showLocation();

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(getActivity(), LocationActivity.class);
                    startActivityForResult(i, 77);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        showNoLocationPermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceImageAction();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceVideo();

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

    public void showNoLocationPermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_location_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getApplicationContext(), getString(R.string.label_grant_location_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    public void setEditTextMaxLength(int length) {

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        mPostEdit.setFilters(FilterArray);
    }

    public void hideEmojiKeyboard() {

        popup.dismiss();
    }

    public void setIconEmojiKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_emoji);
    }

    public void setIconSoftKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_keyboard);
    }

    public void setLocation() {

        postCountry = App.getInstance().getCountry();
        postCity = App.getInstance().getCity();
        postArea = App.getInstance().getArea();
        postLat = Double.toString(App.getInstance().getLat());
        postLng = Double.toString(App.getInstance().getLng());

        showLocation();
    }

    public void deleteLocation() {

        postCountry = "";
        postCity = "";
        postArea = "";
        postLat = "0.000000";
        postLng = "0.000000";

        showLocation();
    }

    public void showLocation() {

        if (postCountry.length() > 0 || postCity.length() > 0) {

            mLocationContainer.setVisibility(View.VISIBLE);

            if (postCountry.length() > 0) {

                mLocationCountry.setText(postCountry);
                mLocationCountry.setVisibility(View.VISIBLE);

            } else {

                mLocationCountry.setVisibility(View.VISIBLE);
            }

            if (postCity.length() > 0) {

                mLocationCity.setText(postCity);
                mLocationCity.setVisibility(View.VISIBLE);

            } else {

                mLocationCity.setVisibility(View.VISIBLE);
            }

        } else {

            mLocationContainer.setVisibility(View.GONE);
        }
    }


    public void setPostModeText() {

        switch (postMode) {

            case 0: {

                mLabelPostMode.setText(getString(R.string.label_post_to_public));

                break;
            }

            default: {

                mLabelPostMode.setText(getString(R.string.label_post_to_followers));

                break;
            }
        }
    }

    public void onPostMode(int mode) {

        postMode = mode;

        setPostModeText();
    }

    public void chooseMode() {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        PostModeDialog alert = new PostModeDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("mode", postMode);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_post_mode");
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
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
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_post: {

                if (App.getInstance().isConnected()) {

                    postText = mPostEdit.getText().toString();
                    postText = postText.trim();

                    if (selectedPostImg == null) selectedPostImg = "";
                    if (selectedVideoImg == null) selectedVideoImg = "";

                    if (selectedPostImg.length() == 0 && selectedVideoImg.length() == 0 && postText.length() == 0) {

                        Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_item), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {

                        if (selectedPostImg.length() == 0 && selectedVideoImg.length() == 0) {

                            hideEmojiKeyboard();

                            loading = true;

                            showpDialog();

                            sendPost();

                        } else {

                            if (selectedPostImg.length() > 0) {

                                hideEmojiKeyboard();

                                loading = true;

                                showpDialog();

                                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "post.jpg");

                                uploadFile(METHOD_ITEMS_UPLOAD_IMG, f);

                            } else {

                                hideEmojiKeyboard();

                                loading = true;

                                showpDialog();

                                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "video.jpg");

                                File f2 = new File(selectedImagePath);

                                uploadVideoFile(METHOD_VIDEO_UPLOAD, f, f2);
                            }
                        }
                    }

//                    if (selectedPostImg != null && selectedPostImg.length() > 0) {
//
//                        hideEmojiKeyboard();
//
//                        loading = true;
//
//                        showpDialog();
//
//                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "post.jpg");
//
//                        uploadFile(METHOD_ITEMS_UPLOAD_IMG, f);
//
//                    } else {
//
//                        if (postText.length() > 0) {
//
//                            hideEmojiKeyboard();
//
//                            loading = true;
//
//                            showpDialog();
//
//                            sendPost();
//
//                        } else {
//
//                            Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_item), Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                        }
//                    }

                } else {

                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                return true;
            }

            default: {

                break;
            }
        }

        return false;
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

        if (requestCode == SELECT_POST_IMG && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            selectedPostImg = getImageUrlWithAuthority(getActivity(), selectedImage, "post.jpg");

            try {

                save(selectedPostImg, "post.jpg");

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

            } catch (Exception e) {

                Log.e("OnSelectPostImage", e.getMessage());
            }

        } else if (requestCode == CREATE_POST_IMG && resultCode == getActivity().RESULT_OK) {

            try {

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                save(selectedPostImg, "post.jpg");

                mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == 77 && resultCode == getActivity().RESULT_OK) {

            setLocation();

        } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == getActivity().RESULT_OK) {

            Uri selectedImageUri = data.getData();

            // OI FILE Manager
//            filemanagerstring = selectedImageUri.getPath();

            // MEDIA GALLERY
            selectedImagePath = getRealPathFromURI(selectedImageUri);

            File videoFile = new File(selectedImagePath);

            if (videoFile.length() > VIDEO_FILE_MAX_SIZE) {

                Toast.makeText(getActivity(), getString(R.string.msg_video_too_large), Toast.LENGTH_SHORT).show();

            } else {

                if (selectedImagePath != null) {

                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedImagePath, MediaStore.Images.Thumbnails.MINI_KIND);
                    Matrix matrix = new Matrix();
                    Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);

                    selectedVideoImg = writeToTempImageAndGetPathUri(getActivity(), bmThumbnail, "video.jpg").toString();

                    selectedVideoImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "video.jpg";

                    mChoiceVideo.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedVideoImg)));
                }
            }
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

    public String getRealPathFromURI(Uri contentUri) {

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor.moveToFirst()) {

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }

        cursor.close();

        return res;
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

    public void choiceImage() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
            }

        } else {

            choiceImageAction();
        }
    }

    public void choiceVideo() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.label_select_video)), REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void choiceImageAction() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostImageChooseDialog alert = new PostImageChooseDialog();

        alert.show(fm, "alert_dialog_image_choose");
    }

    public void imageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_POST_IMG);
    }

    public void imageFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "post.jpg");
            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_POST_IMG);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareSendPost() {

        if (image_upload && video_upload) {

            sendPost();

        } else if (image_upload && selectedVideoImg.length() == 0) {

            sendPost();

        } else if (video_upload && selectedPostImg.length() == 0) {

            sendPost();

        } else if (image_upload && selectedVideoImg.length() > 0) {

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "video.jpg");

            File f2 = new File(selectedImagePath);

            uploadVideoFile(METHOD_VIDEO_UPLOAD, f, f2);

        } else if (video_upload && selectedPostImg.length() > 0) {

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "post.jpg");

            uploadFile(METHOD_ITEMS_UPLOAD_IMG, f);
        }
    }

    public void sendPost() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_NEW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            sendPostSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                sendPostSuccess();

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("groupId", Long.toString(groupId));
                params.put("postMode", Integer.toString(postMode));
                params.put("postText", postText);
                params.put("postImg", postImg);
                params.put("postArea", postArea);
                params.put("postCountry", postCountry);
                params.put("postCity", postCity);
                params.put("postLat", postLat);
                params.put("postLng", postLng);

                params.put("videoImgUrl", videoImgUrl);
                params.put("videoUrl", videoUrl);

                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendPostSuccess() {

        loading = false;

        hidepDialog();

        if (isAdded()) {

            Intent i = new Intent();
            getActivity().setResult(RESULT_OK, i);

            Toast.makeText(getActivity(), getText(R.string.msg_item_posted), Toast.LENGTH_SHORT).show();

            getActivity().finish();
        }
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (context instanceof Activity){

            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }



    public Boolean uploadFile(String serverURL, File file) {

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
                    .addHeader("Accept", "application/json;")
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

                            postImg = result.getString("imgUrl");
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        image_upload = true;

                        prepareSendPost();
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

    public Boolean uploadVideoFile(String serverURL, File file, File videoFile) {

        final OkHttpClient client = new OkHttpClient();

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("uploaded_video_file", videoFile.getName(), RequestBody.create(MediaType.parse("text/csv"), videoFile))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
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

                            videoImgUrl = result.getString("imgFileUrl");
                            videoUrl = result.getString("videoFileUrl");
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        video_upload = true;

                        prepareSendPost();
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