package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import ru.asterios.open.constants.Constants;


public class ViewYouTubeVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    Toolbar toolbar;

    YouTubePlayerView mYouTubeView;

    private static final int RECOVERY_REQUEST = 1;

    ImageLoader imageLoader;

    String mVideoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_youtube_video);

        mYouTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        mYouTubeView.initialize(Constants.YOUTUBE_API_KEY, this);

        Intent i = getIntent();

        mVideoCode = i.getStringExtra("videoCode");
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        if (!wasRestored) {

            player.cueVideo(mVideoCode);
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {

        if (errorReason.isUserRecoverableError()) {

            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();

        } else {

            String error = String.format(getString(R.string.youtube_player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RECOVERY_REQUEST) {

            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {

        return mYouTubeView;
    }
}
