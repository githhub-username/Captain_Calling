package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kartavya.captaincalling.Interface.ItemClickListener;

import java.util.Objects;

import io.paperdb.Paper;

public class AllYoutubeVideoActivity extends AppCompatActivity {

    private Button button;
    private RecyclerView recyclerView;
    private DatabaseReference blogRef;
    private FirebaseRecyclerAdapter<YoutubeVideos, YoutubeVideoViewHolder> adapter;
    private ProgressBar progressBar;
    private ProgressDialog loadingBar;
    private TextView textView;

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
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_all_youtube_video);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_yt);

        progressBar = findViewById(R.id.xmknkcnskcnsknc);

        textView = findViewById(R.id.xcxscsvsvs);

        ImageView backBtn = findViewById(R.id.back_all_yt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllYoutubeVideoActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        button = findViewById(R.id.create_new_yt);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(AllYoutubeVideoActivity.this, AddVideoActivity.class);
            startActivity(intent);
        });

        if (getIntent() != null)
        {
            String key = getIntent().getStringExtra("Admin");
            if (key != null && key.equals("Yes"))
            {
                LoadData2();
            }
            else
            {
                button.setVisibility(View.GONE);
                LoadData();
            }
        }


    }

    private void LoadData2() {

        blogRef = FirebaseDatabase.getInstance().getReference("YoutubeVideos");

        FirebaseRecyclerOptions<YoutubeVideos> options =
                new FirebaseRecyclerOptions.Builder<YoutubeVideos>()
                        .setQuery(blogRef, YoutubeVideos.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<YoutubeVideos, YoutubeVideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull YoutubeVideoViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull YoutubeVideos model) {

                progressBar.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.imageView);

                holder.description.setText(model.getDescription());
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());

                if (model.getStatus().equals("off"))
                {
                    holder.status.setTextColor(Color.RED);
                    holder.status.setText("Pending");
                }
                else
                {
                    holder.status.setTextColor(Color.GREEN);
                    holder.status.setText("Public");
                }

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete",
                                        "Public",
                                        "Private"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllYoutubeVideoActivity.this);
                        builder.setTitle("Choose option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();
                                    adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }

                                if (i==1)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();
                                    FirebaseDatabase.getInstance().getReference().child("YoutubeVideos").child(Objects.requireNonNull(adapter.getRef(position).getKey()))
                                            .child("Status").setValue("on").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }

                                if (i==2)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();
                                    FirebaseDatabase.getInstance().getReference().child("YoutubeVideos").child(Objects.requireNonNull(adapter.getRef(position).getKey()))
                                            .child("Status").setValue("off").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }

                            }
                        });
                        builder.show();
                        return true;
                    }
                });


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(AllYoutubeVideoActivity.this,PlayVideoActivity.class);
                        intent.putExtra("TitleYt",model.getTitle());
                        intent.putExtra("DesYt",model.getDescription());
                        intent.putExtra("urlYt",model.getUrl());
                        startActivity(intent);

                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);




            }

            @NonNull
            @Override
            public YoutubeVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_model, parent,false);
                return  new YoutubeVideoViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        if (adapter==null)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void LoadData() {

        blogRef = FirebaseDatabase.getInstance().getReference("YoutubeVideos");

        FirebaseRecyclerOptions<YoutubeVideos> options =
                new FirebaseRecyclerOptions.Builder<YoutubeVideos>()
                        .setQuery(blogRef.orderByChild("Status").equalTo("on"), YoutubeVideos.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<YoutubeVideos, YoutubeVideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull YoutubeVideoViewHolder holder, int position, @NonNull YoutubeVideos model) {

                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.imageView);

                holder.description.setText(model.getDescription());
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());

                holder.status.setVisibility(View.GONE);


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(AllYoutubeVideoActivity.this,PlayVideoActivity.class);
                        intent.putExtra("TitleYt",model.getTitle());
                        intent.putExtra("DesYt",model.getDescription());
                        intent.putExtra("urlYt",model.getUrl());
                        startActivity(intent);

                    }
                });

                progressBar.setVisibility(View.INVISIBLE);




            }

            @NonNull
            @Override
            public YoutubeVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_model, parent,false);
                return  new YoutubeVideoViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        if (adapter==null)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}