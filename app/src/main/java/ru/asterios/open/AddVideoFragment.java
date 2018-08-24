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
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
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
import java.util.HashMap;
import java.util.Map;

import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.util.CustomRequest;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddVideoFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;
    public static final int REQUEST_TAKE_GALLERY_VIDEO = 1001;

    private ProgressDialog pDialog;

    EmojiconEditText mCommentEdit;
    ImageView mChoicePostImg, mEmojiBtn;

    String commentText = "", imgUrl = "", videoUrl = "",  postArea = "", postCountry = "", postCity = "", postLat = "0.000000", postLng = "0.000000";
    private String selectedPostImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;
    private String selectedImagePath;

    private int postMode = 0;

    private Boolean loading = false;

    EmojiconsPopup popup;

    public AddVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_video, container, false);

        popup = new EmojiconsPopup(rootView, getActivity());

        popup.setSizeForSoftKeyboard();

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mCommentEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mCommentEdit.dispatchKeyEvent(event);
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

                mCommentEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mCommentEdit.dispatchKeyEvent(event);
            }
        });

        if (loading) {

            showpDialog();
        }

        mCommentEdit = (EmojiconEditText) rootView.findViewById(R.id.commentEdit);
        mChoicePostImg = (ImageView) rootView.findViewById(R.id.choicePostImg);
        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);

        mChoicePostImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedPostImg.length() == 0) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);
                        }

                    } else {

                        choiceImage();
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

                            mChoicePostImg.setImageResource(R.drawable.ic_action_video_file);
                            selectedPostImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        mCommentEdit.addTextChangedListener(new TextWatcher() {

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

                    getActivity().setTitle(getText(R.string.title_activity_new_video));

                } else {

                    getActivity().setTitle(Integer.toString(140 - cnt));
                }
            }

        });

        if (selectedPostImg != null && selectedPostImg.length() > 0) {

            mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
        }

        if (!EMOJI_KEYBOARD) {

            mEmojiBtn.setVisibility(View.GONE);
        }

        mEmojiBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()) {

                        popup.showAtBottom();
                        setIconSoftKeyboard();

                    } else {

                        mCommentEdit.setFocusableInTouchMode(true);
                        mCommentEdit.requestFocus();
                        popup.showAtBottomPending();

                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mCommentEdit, InputMethodManager.SHOW_IMPLICIT);
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

        mCommentEdit.setOnEditTextImeBackListener(er);

        // Inflate the layout for this fragment
        return rootView;
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

                    commentText = mCommentEdit.getText().toString();
                    commentText = commentText.trim();

                    if (selectedPostImg != null && selectedPostImg.length() > 0) {

                        hideEmojiKeyboard();

                        loading = true;

                        showpDialog();

                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "video.jpg");

                        File f2 = new File(selectedImagePath);

                        uploadFile(METHOD_VIDEO_UPLOAD, f, f2);

                    } else {

                        Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_video), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceImage();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == getActivity().RESULT_OK) {

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

                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedImagePath,   MediaStore.Images.Thumbnails.MINI_KIND);
                    Matrix matrix = new Matrix();
                    Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);

                    selectedPostImg = writeToTempImageAndGetPathUri(getActivity(), bmThumbnail, "video.jpg").toString();

                    selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "video.jpg";

                    mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
                }
            }
        }
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

        return Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "video.jpg";
    }

    public void choiceImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.label_select_video)), REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void sendPost() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_VIDEO_NEW, null,
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
                params.put("accessMode", Integer.toString(postMode));
                params.put("comment", commentText);
                params.put("imgUrl", imgUrl);
                params.put("videoUrl", videoUrl);
                params.put("postArea", postArea);
                params.put("postCountry", postCountry);
                params.put("postCity", postCity);
                params.put("postLat", postLat);
                params.put("postLng", postLng);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendPostSuccess() {

        loading = false;

        hidepDialog();

        Intent i = new Intent();
        getActivity().setResult(RESULT_OK, i);

        Toast.makeText(getActivity(), getText(R.string.msg_item_posted), Toast.LENGTH_SHORT).show();

        getActivity().finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public Boolean uploadFile(String serverURL, File file, File videoFile) {

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

                            imgUrl = result.getString("imgFileUrl");
                            videoUrl = result.getString("videoFileUrl");
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        sendPost();
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