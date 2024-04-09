package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class PlayVideoActivity extends AppCompatActivity {

    private TextView title,des;
    private YouTubePlayerView youTubePlayerView;

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = (float) 0.92;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play_video);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        title = findViewById(R.id.bd_title_yt);
        des = findViewById(R.id.bd_des_yt);
        youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);

        if (getIntent() != null)
        {
            String titleYt = getIntent().getStringExtra("TitleYt");
            String desYt = getIntent().getStringExtra("DesYt");
            String urlYt = getIntent().getStringExtra("urlYt");

            title.setText(titleYt);
            des.setText(desYt);


            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = urlYt.replace("https://youtu.be/","");
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });


        }


    }
}