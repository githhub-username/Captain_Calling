package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MyTeamActivity extends AppCompatActivity {



    private RecyclerView recyclerView;
    private DatabaseReference teamRef;
    private FirebaseRecyclerAdapter<MyTeams, MyTeamViewHolder> adapter;
    private ProgressBar progressBar;
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
        setContentView(R.layout.activity_my_team);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_myteam);
        progressBar = findViewById(R.id.progress_myteam);
        textView = findViewById(R.id.xxccccsdsfsfdwwf);

        ImageView backBtn = findViewById(R.id.back_myteams);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        teamRef = FirebaseDatabase.getInstance().getReference("AllProfiles").child(Paper.book().read("Phone")).child("MyTeams");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        LoadData();
    }

    private void LoadData() {

        FirebaseRecyclerOptions<MyTeams> options =
                new FirebaseRecyclerOptions.Builder<MyTeams>()
                        .setQuery(teamRef, MyTeams.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MyTeams, MyTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyTeamViewHolder holder, int position, @NonNull MyTeams model) {

                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                holder.itemView.setVisibility(View.INVISIBLE);

                holder.setItemClickListener((view, position1, isLongClick) -> {

                });

                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AllTeam teamData = snapshot.child("AllTeam").child(model.getEntryId()).getValue(AllTeam.class);
                        assert teamData != null;

                        Glide.with(getApplicationContext())
                                .load(teamData.getPicture())
                                .listener(new RequestListener<Drawable>() {



                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.e("Glide", "Chat image loading failed: " + e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d("Glide", "Chat image loaded successfully");
                                        return false;
                                    }
                                })
                                .into(holder.circleImageView);


                        if (teamData.getCaptainPhone().equals(Paper.book().read("Phone")))
                        {
                            holder.sport.setText(teamData.getSport()+" (created by you)");
                        }
                        else
                        {
                            holder.sport.setText(teamData.getSport());
                        }

                        holder.title.setText(ProperCase.properCase(teamData.getTeamName()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MyTeamActivity.this, TeamInfoActivity.class); //TeamRoomActivity is changed to TeamInfoActivity
                                intent.putExtra("Team_Id",teamData.getEntryId());
                                intent.putExtra("Team_Name",teamData.getTeamName());
                                intent.putExtra("Team_Sport",teamData.getSport());
                                intent.putExtra("Team_Pic",teamData.getPicture());
                                startActivity(intent);
                            }
                        });

                        holder.openChatView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MyTeamActivity.this, TeamRoomActivity.class);
                                intent.putExtra("Team_Id",teamData.getEntryId());
                                intent.putExtra("Team_Name",teamData.getTeamName());
                                intent.putExtra("Team_Sport",teamData.getSport());
                                intent.putExtra("Team_Pic", teamData.getPicture());
                                startActivity(intent);
                            }
                        });

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                holder.itemView.setVisibility(View.VISIBLE);
                            }
                        }, 1000);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






            }

            @NonNull
            @Override
            public MyTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_team_model, parent,false);
                return  new MyTeamViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}