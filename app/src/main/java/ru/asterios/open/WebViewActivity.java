package ru.asterios.open;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;


public class WebViewActivity extends ActivityBase {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    Toolbar toolbar;

    RelativeLayout mWebViewLoadingScreen, mWebViewErrorScreen, mWebViewContentScreen;

    WebView mWebView;

    String sUrl, sTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //        Initialize Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();
        sUrl = i.getStringExtra("url");
        sTitle = i.getStringExtra("title");

        getSupportActionBar().setTitle(sTitle);

        mWebViewErrorScreen = (RelativeLayout) findViewById(R.id.WebViewErrorScreen);
        mWebViewLoadingScreen = (RelativeLayout) findViewById(R.id.WebViewLoadingScreen);
        mWebViewContentScreen = (RelativeLayout) findViewById(R.id.WebViewContentScreen);

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                showLoadingScreen();
            }

            public void onPageFinished(WebView view, String url) {

                showContentScreen();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                showErrorScreen();
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }

        });

        mWebView.getSettings().setJavaScriptEnabled(true);

        if (App.getInstance().isConnected()) {

            mWebView.loadUrl(sUrl);

        } else {

            showErrorScreen();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {

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

        mWebViewErrorScreen.setVisibility(View.GONE);
        mWebViewContentScreen.setVisibility(View.GONE);

        mWebViewLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mWebViewLoadingScreen.setVisibility(View.GONE);
        mWebViewContentScreen.setVisibility(View.GONE);

        mWebViewErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mWebViewErrorScreen.setVisibility(View.GONE);
        mWebViewLoadingScreen.setVisibility(View.GONE);

        mWebViewContentScreen.setVisibility(View.VISIBLE);
    }
}
