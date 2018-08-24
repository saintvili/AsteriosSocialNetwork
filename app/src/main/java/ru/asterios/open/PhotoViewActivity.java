package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoViewActivity extends ActivityBase {

    private static final String TAG = "photo_view_activity";

    Toolbar toolbar;

    ImageView photoView;

    LinearLayout mContentScreen;
    RelativeLayout mLoadingScreen;

    PhotoViewAttacher mAttacher;
    ImageLoader imageLoader;

    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        if (toolbar != null) {
//
//            setSupportActionBar(toolbar);
//        }

        Intent i = getIntent();

        imgUrl = i.getStringExtra("imgUrl");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);

        mContentScreen = (LinearLayout) findViewById(R.id.PhotoViewContentScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.PhotoViewLoadingScreen);

        photoView = (ImageView) findViewById(R.id.photoImageView);
//
//        toolbar.getBackground().setAlpha(100);
        getSupportActionBar().setTitle("");

        showLoadingScreen();

        imageLoader = App.getInstance().getImageLoader();

        imageLoader.get(imgUrl, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean isImmediate) {

                photoView.setImageBitmap(imageContainer.getBitmap());
                mAttacher = new PhotoViewAttacher(photoView);

                showContentScreen();
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
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

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.VISIBLE);
    }
}
