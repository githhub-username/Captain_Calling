package com.kartavya.captaincalling;

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

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class BlogDetailsActivity extends AppCompatActivity {

    private TextView name,title,des,date;
    private CircleImageView circleImageView;
    private ImageView imageView;

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
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_blog_details);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        name = findViewById(R.id.bd_name);
        title = findViewById(R.id.bd_title);
        des = findViewById(R.id.bd_des);
        date = findViewById(R.id.bd_date);
        circleImageView = findViewById(R.id.bd_profp);
        imageView = findViewById(R.id.bd_pic);

        ImageView backBtn = findViewById(R.id.back_blog_d);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent() != null)
        {
            String DateBlog = getIntent().getStringExtra("DateBlog");
            String NameBlog = getIntent().getStringExtra("NameBlog");
            String DesBlog = getIntent().getStringExtra("DesBlog");
            String PicBlog = getIntent().getStringExtra("PicBlog");
            String TitleBlog = getIntent().getStringExtra("TitleBlog");
            String PPicBlog = getIntent().getStringExtra("PPicBlog");

            name.setText(capitalizeFirstLetterOfEachWord(NameBlog));
            date.setText(DateBlog);
            des.setText(DesBlog);
            title.setText(TitleBlog);

            Glide.with(getApplicationContext()).load(PicBlog).into(imageView);
            if (!PPicBlog.equals("null")) {
                Glide.with(getApplicationContext()).load(PPicBlog).into(circleImageView);
            }
        }

    }

    public static String capitalizeFirstLetterOfEachWord(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Split the input string into words
        String[] words = input.split("\\s+");

        // Capitalize the first letter of each word
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Remove the trailing space and return the result
        return result.toString().trim();
    }
}