package app.hcl.com.videotest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoActivity extends AppCompatActivity {


    MediaController mediaController;
    private static final String VIDEO_SAMPLE = "welcome";
    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    PlayerView playerView;
    String url="https://youtu.be/HZuWeJ_Sa5A";
    String url2="";
    SimpleExoPlayer player;
    //private static final String VIDEO_SAMPLE = "tacoma_narrows";

    int currentWindow=0;long playbackPosition=0;
    boolean playWhenReady=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int currentOrientation = this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        playerView=findViewById(R.id.videoview);
        Intent intent=getIntent();
        url2=intent.getStringExtra("url");





        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_TIME);
            currentWindow=savedInstanceState.getInt("currentwindow");
            Log.d("My_log","pos="+playbackPosition+" "+currentWindow);
        }

    }

    private void initializePlayer()
    {
        player= ExoPlayerFactory.newSimpleInstance(this,new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),new DefaultLoadControl());
        playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        //player.seekTo(currentWindow,playbackPosition);
        if (playbackPosition>0)
        {player.seekTo(playbackPosition);
        Log.d("My_log","resuming");}
        else
            player.seekTo(currentWindow,playbackPosition);
        Log.d("My_log","current="+currentWindow+"  "+playbackPosition);
        Uri uri=Uri.parse(url2);
        MediaSource mediaSource=buildMediaSource(uri);
        //player.prepare(mediaSource);
        player.prepare(mediaSource,true,false);

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }


    @Override
    public void onStart() {
        super.onStart();
        hideSystemUi();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        Log.d("My_log","call release");
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            Log.d("My_log",playbackPosition+" "+currentWindow);
            player.release();
            player = null;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(PLAYBACK_TIME, playbackPosition);
        outState.putInt("currentwindow",currentWindow);
    }

}
