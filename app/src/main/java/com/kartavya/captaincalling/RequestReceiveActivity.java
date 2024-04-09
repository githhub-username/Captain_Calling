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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class RequestReceiveActivity extends AppCompatActivity {
    
    private String id;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder> adapter;
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
        setContentView(R.layout.activity_request_receive);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_request_receive);
        progressBar = findViewById(R.id.bhvhgcgxfrxrfzx);
        textView = findViewById(R.id.scscscscsbcb);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        ImageView backBtn = findViewById(R.id.back_rr);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
            LoadRequest(id);

        }


    }

    private void LoadRequest(String id) {
        FirebaseRecyclerOptions<AllProfile> options =
                new FirebaseRecyclerOptions.Builder<AllProfile>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("RequestReceiver").child(id), AllProfile.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProfileViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AllProfile model) {


                progressBar.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                holder.address.setText(ProperCase.properCase(model.getAddress()));
                holder.district.setText(ProperCase.properCase(model.getDistrict()));

                if (model.getLevel().equals("Beginner"))
                {
                    holder.level.setTextColor(Color.parseColor("#FFA000"));
                }
                else if (model.getLevel().equals("Intermediate"))
                {
                    holder.level.setTextColor(Color.BLUE);
                }
                else
                {
                    holder.level.setTextColor(Color.parseColor("#018707"));
                }

                holder.level.setText(model.getLevel());

                holder.name.setText(ProperCase.properCase(model.getName()));
                holder.phone.setText(model.getPhone());
                holder.psport.setText(model.getPrimarySport());
                holder.ssport.setText(model.getSecondarySport());
                holder.state.setText(ProperCase.properCase(model.getState()));

                holder.addBtn.setText("Accept");
                holder.cancelBtn.setVisibility(View.VISIBLE);

                holder.addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.linearLayout.setVisibility(View.GONE);
                        loadingBar.show();

                        FirebaseDatabase.getInstance().getReference("RequestSender").child(model.getPhone())
                                .child(id).child("Status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                final DatabaseReference PlayerRef;
                                PlayerRef = FirebaseDatabase.getInstance().getReference().child("Players");

                                final DatabaseReference AllProfRef;
                                AllProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

                                final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                objectHashMap1.put("Name", model.getName());
                                objectHashMap1.put("Picture",model.getPicture());
                                objectHashMap1.put("Phone",model.getPhone());
                                objectHashMap1.put("isCaptain","false");

                                PlayerRef.child(id).child(model.getPhone()).updateChildren(objectHashMap1)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                FirebaseDatabase.getInstance().getReference().child("AllTeam").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists())
                                                        {
                                                            AllProfRef.child(model.getPhone()).child("MyTeams").child(id).child("EntryId").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                                                        @Override
                                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                                            adapter.notifyDataSetChanged();
                                                                            if (adapter!=null)
                                                                            {
                                                                                adapter=null;
                                                                                LoadRequest(id);
                                                                                loadingBar.dismiss();
                                                                                Toast.makeText(RequestReceiveActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();

                                                                            }

                                                                        }
                                                                    });
                                                                    }
                                                            });
                                                        }
                                                        else
                                                        {
                                                            loadingBar.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                            }
                        });
                    }
                });

                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.linearLayout.setVisibility(View.GONE);
                        loadingBar.show();

                        FirebaseDatabase.getInstance().getReference("RequestSender").child(model.getPhone())
                                .child(id).child("Status").setValue("Cancelled").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        adapter.notifyDataSetChanged();

                                        if (adapter!=null)
                                        {
                                            adapter=null;
                                            LoadRequest(id);
                                            loadingBar.dismiss();
                                        }
                                    }
                                });

                            }
                        });

                    }
                });

                holder.setItemClickListener((view, position1, isLongClick) -> {

                });

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



                holder.itemView.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @NonNull
            @Override
            public AllProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_player_model, parent,false);
                return  new AllProfileViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}