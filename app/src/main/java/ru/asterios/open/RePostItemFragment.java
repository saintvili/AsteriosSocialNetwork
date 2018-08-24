package ru.asterios.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import ru.asterios.open.util.CustomRequest;

public class RePostItemFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    EmojiconEditText mPostEdit;
    ImageView mChoicePostImg, mEmojiBtn;

    String postText = "", postImg = "", postArea = "", postCountry = "", postCity = "", postLat = "0.000000", postLng = "0.000000";
    private String selectedPostImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;
    private long rePostId;
    private int position;

    private int postMode = 0;

    private Boolean loading = false;

    EmojiconsPopup popup;

    public RePostItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        rePostId = i.getLongExtra("rePostId", 0);
        position = i.getIntExtra("position", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_repost_item, container, false);

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
        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);

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

                    getActivity().setTitle(getText(R.string.title_activity_repost));

                } else {

                    getActivity().setTitle(Integer.toString(400 - cnt));
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

                    postText = mPostEdit.getText().toString();
                    postText = postText.trim();

                    if (selectedPostImg != null && selectedPostImg.length() > 0) {

                        loading = true;

                        showpDialog();

                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "post.jpg");

                        uploadFile(METHOD_ITEMS_UPLOAD_IMG, f);

                    } else {

                        loading = true;

                        showpDialog();

                        sendPost();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_POST_IMG && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            selectedPostImg = getImageUrlWithAuthority(getActivity(), selectedImage, "post.jpg");

            try {

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

            } catch (Exception e) {

                Log.e("OnSelectPostImage", e.getMessage());
            }

        } else if (requestCode == CREATE_POST_IMG && resultCode == getActivity().RESULT_OK) {

            try {

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                mChoicePostImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
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

        return Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";
    }

    public void choiceImage() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostImageChooseDialog alert = new PostImageChooseDialog();

        alert.show(fm, "alert_dialog_image_choose");
    }

    public void imageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_POST_IMG);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
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

                            rePostSuccess();

                            Log.e("RE Post", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                rePostSuccess();

                Log.e("RE Post Error", error.toString());

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("rePostId", Long.toString(rePostId));
                params.put("postMode", Integer.toString(postMode));
                params.put("postText", postText);
                params.put("postImg", postImg);
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

    public void rePostSuccess() {

        loading = false;

        hidepDialog();

        Intent i = new Intent();
        i.putExtra("position", position);
        getActivity().setResult(RESULT_OK, i);

        Toast.makeText(getActivity(), getText(R.string.msg_post_has_been_shared), Toast.LENGTH_SHORT).show();

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