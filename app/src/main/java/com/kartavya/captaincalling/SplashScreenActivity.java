package com.kartavya.captaincalling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setBackgroundDrawableResource(R.drawable.transparent);

        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.white));

        ImageView imageView;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, SignUpActivity.class));
                finish();
            }
        }, 1000);

        imageView = findViewById(R.id.app_logo);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_animation);

        imageView.startAnimation(animation);
    }
}