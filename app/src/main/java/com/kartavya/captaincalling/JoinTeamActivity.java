package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class JoinTeamActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder> adapter;
    private Button button;
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
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_join_team);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        button = findViewById(R.id.explore_btn);

        recyclerView = findViewById(R.id.recycler_join);
        progressBar = findViewById(R.id.mkbjvdtdstdtchc);

        textView = findViewById(R.id.xxccccdwwf);

        ImageView backBtn = findViewById(R.id.back_join_team);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinTeamActivity.this,AllTeamActivity.class);
                startActivity(intent);
            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        LoadData();


    }

    private void LoadData() {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(FirebaseDatabase.getInstance()
                                .getReference("RequestSender")
                                .child(Paper.book().read("Phone")), AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllTeamViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AllTeam model) {

                progressBar.setVisibility(View.VISIBLE);

                //holder.setIsRecyclable(false);

                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                holder.address.setText(ProperCase.properCase(model.getAddress()));
                holder.diss.setText(ProperCase.properCase(model.getDistrict()));

                if (model.getStatus().equals("Pending"))
                {
                    holder.level.setTextColor(Color.parseColor("#FF0000"));
                }
                else if (model.getStatus().equals("Cancelled"))
                {
                    holder.level.setTextColor(Color.parseColor("#FF0000"));
                    holder.requestBtn.setVisibility(View.GONE);
                }
                else
                {
                    holder.level.setTextColor(Color.parseColor("#388E3C"));
                    holder.requestBtn.setVisibility(View.GONE);
                }
                holder.level.setText(model.getStatus());

                FirebaseDatabase.getInstance().getReference("Players").child(model.getEntryId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.captainPhone.setText(String.valueOf(snapshot.getChildrenCount()));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                holder.requestBtn.setText("Withdraw");
                holder.requestBtn.setBackgroundColor(Color.parseColor("#E64A19"));


                holder.level2.setText(model.getLevel());
                holder.ly_level.setVisibility(View.VISIBLE);

                holder.teamName.setText(ProperCase.properCase(ProperCase.properCase(model.getTeamName())));
                holder.captain.setText(ProperCase.properCase(ProperCase.properCase(model.getCaptain())));
                //holder.captainPhone.setText(ProperCase.properCase(model.getCaptainPhone()));
                holder.sport.setText(ProperCase.properCase(model.getSport()));
                holder.state.setText(ProperCase.properCase(ProperCase.properCase(model.getState())));

                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.linearLayout.getVisibility()==View.VISIBLE)
                        {
                            holder.linearLayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            holder.linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.linearLayout.getVisibility()==View.VISIBLE)
                        {
                            holder.linearLayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            holder.linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                holder.cancelBtn.setText("Team History");
                holder.cancelBtn.setBackgroundColor(Color.parseColor("#388E3C"));
                holder.cancelBtn.setVisibility(View.VISIBLE);

                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(JoinTeamActivity.this,AddMatchResultActivity.class);
                        intent.putExtra("Team_1_Id",model.getEntryId());
                        intent.putExtra("Team_2_Id","");
                        startActivity(intent);
                    }
                });


                holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.linearLayout.setVisibility(View.GONE);
                        loadingBar.show();

                        FirebaseDatabase.getInstance().getReference().child("RequestReceiver").child(model.getEntryId()).child(Paper.book().read("Phone"))
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                adapter.notifyDataSetChanged();
                                                if (adapter!=null)
                                                {
                                                    adapter=null;
                                                    LoadData();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });



                    }
                });

                holder.itemView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public AllTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_team_model, parent,false);
                return  new AllTeamViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}