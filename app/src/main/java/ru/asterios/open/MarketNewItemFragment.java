package ru.asterios.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

import github.ankushsachdeva.emojicon.EmojiconEditText;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.PostImageChooseDialog;
import ru.asterios.open.util.CustomRequest;

public class MarketNewItemFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    EmojiconEditText mItemEdit;
    EditText mItemTitle, mItemPrice;
    ImageView mChoiceItemImg;

    String title = "", description = "", imgUrl = "", previewImgUrl = "", postArea = "", postCountry = "", postCity = "", postLat = "0.000000", postLng = "0.000000";
    private String selectedPostImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;

    private int price = 0, btn_number = 0;

    private Boolean loading = false, img2_upload = false, img3_upload = false, img4_upload = false;

    public MarketNewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_market_new_item, container, false);

        if (loading) {

            showpDialog();
        }

        mItemEdit = (EmojiconEditText) rootView.findViewById(R.id.itemDescription);
        mItemTitle = (EditText) rootView.findViewById(R.id.itemTitle);
        mItemPrice = (EditText) rootView.findViewById(R.id.itemPrice);

        mChoiceItemImg = (ImageView) rootView.findViewById(R.id.choiceItemImg);

        if (price != 0) {

            mItemPrice.setText(Integer.toString(price));
        }

        mItemTitle.setText(title);
        mItemEdit.setText(description);

        setEditTextMaxLength(POST_CHARACTERS_LIMIT);

        mChoiceItemImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedPostImg.length() == 0) {

                    btn_number = 1;

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                        }

                    } else {

                        choiceImage(1);
                    }

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

                            mChoiceItemImg.setImageResource(R.drawable.ic_action_camera);
                            selectedPostImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        mItemEdit.addTextChangedListener(new TextWatcher() {

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

        mItemTitle.addTextChangedListener(new TextWatcher() {

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

                getActivity().setTitle(getText(R.string.title_activity_new_item));
            }

        });

        mItemPrice.addTextChangedListener(new TextWatcher() {

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

                getActivity().setTitle(getText(R.string.title_activity_new_item));
            }

        });

        if (selectedPostImg != null && selectedPostImg.length() > 0) {

            mChoiceItemImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceImage(btn_number);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }
        }
    }

    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getActivity(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
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
        mItemEdit.setFilters(FilterArray);
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

                    description = mItemEdit.getText().toString();
                    description = description.trim();

                    if (mItemPrice.getText().toString().length() > 0) {

                        price = Integer.parseInt(mItemPrice.getText().toString());

                    } else {

                        price = 0;
                    }

                    title = mItemTitle.getText().toString();
                    title = title.trim();

                    if (selectedPostImg != null && selectedPostImg.length() > 0) {

                        if (title.length() > 0) {

                            if (price > 0) {

                                if (description.length() > 0) {

                                    loading = true;

                                    showpDialog();

                                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "post.jpg");

                                    uploadFile(METHOD_MARKET_UPLOAD_IMG, f, 0);

                                } else {

                                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_description), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            } else {

                                Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_price), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } else {

                            Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_title), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else {

                        Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_img), Toast.LENGTH_SHORT);
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

            switch (btn_number) {

                case 1: {

                    selectedPostImg = getImageUrlWithAuthority(getActivity(), selectedImage, "post.jpg");

                    try {

                        save(selectedPostImg, "post.jpg");

                        selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                        mChoiceItemImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

                    } catch (Exception e) {

                        Log.e("OnSelectPostImage", e.getMessage());
                    }

                    break;
                }

                default: {

                    break;
                }
            }

        } else if (requestCode == CREATE_POST_IMG && resultCode == getActivity().RESULT_OK) {

            switch (btn_number) {

                case 1: {

                    try {

                        selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "post.jpg";

                        save(selectedPostImg, "post.jpg");

                        mChoiceItemImg.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));


                    } catch (Exception ex) {

                        Log.v("OnCameraCallBack", ex.getMessage());
                    }

                    break;
                }

                default: {

                    break;
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

    public void choiceImage(int btn) {

        btn_number = btn;

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

            switch (btn_number) {

                case 1: {

                    File sdImageMainDirectory = new File(root, "post.jpg");
                    outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

                    break;
                }
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_POST_IMG);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendItem() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MARKET_NEW_ITEM, null,
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

                            Log.d("Success", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                sendPostSuccess();

                Log.e("Error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("price", Integer.toString(price));
                params.put("title", title);
                params.put("description", description);
                params.put("imgUrl", imgUrl);
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



    public Boolean uploadFile(String serverURL, File file, final int imgId) {

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

                            switch (imgId) {

                                case 0: {

                                    imgUrl = result.getString("imgUrl");

                                    break;
                                }

                                default: {

                                    break;
                                }
                            }
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        sendItem();
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