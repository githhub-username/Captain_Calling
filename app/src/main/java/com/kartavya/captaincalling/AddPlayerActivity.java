package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
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
import java.util.Objects;

import io.paperdb.Paper;

public class AddPlayerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference paymentRef;
    private FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder> adapter;
    private ProgressBar progressBar;
    private SearchView searchView;
    private String mQueryString;
    private String id,sport;
    private ProgressDialog loadingBar;
    public String queryFire="Name";

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
        setContentView(R.layout.activity_add_player);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_add_player);
        progressBar = findViewById(R.id.dsdjsbhjsvdhsvdshd);
        searchView = findViewById(R.id.searchBtnhhgc);

        View v = searchView.findViewById(R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);



        searchView.setMaxWidth(Integer.MAX_VALUE);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
            sport = getIntent().getStringExtra("Team_Sport");
        }

        paymentRef = FirebaseDatabase.getInstance().getReference("AllProfiles");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.onActionViewExpanded();
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                progressBar.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals(""))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    mQueryString = query;
                    if(mQueryString.matches("\\d+(?:\\.\\d+)?"))
                    {
                        queryFire="Phone";
                    }

                    SearchUser(mQueryString.toLowerCase());

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchUser2();

    }

    private void SearchUser(String mQueryString) {
        FirebaseRecyclerOptions<AllProfile> options =
                new FirebaseRecyclerOptions.Builder<AllProfile>()
                        .setQuery(paymentRef.orderByChild(queryFire).startAt(mQueryString).endAt(mQueryString + "\uf8ff"), AllProfile.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProfileViewHolder holder, int position, @NonNull AllProfile model) {

                holder.setIsRecyclable(false);

                if (model.getPrimarySport().equals(sport) || model.getSecondarySport().equals(sport))
                {
                    if (model.getPicture()!=null)
                    {
                        Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                    }
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
                    holder.phone.setVisibility(View.GONE);
                    holder.psport.setText(model.getPrimarySport());
                    holder.ssport.setText(model.getSecondarySport());
                    holder.state.setText(ProperCase.properCase(model.getState()));

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

                    holder.addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.linearLayout.setVisibility(View.GONE);
                            loadingBar.show();

                            final DatabaseReference PlayerRef;
                            PlayerRef = FirebaseDatabase.getInstance().getReference().child("Players");

                            final DatabaseReference AllProfRef;
                            AllProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

                            PlayerRef.child(id).child(model.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists())
                                    {
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
                                                                    AllProfRef.child(model.getPhone()).child("MyTeams").child(id).setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            loadingBar.dismiss();
                                                                            Toast.makeText(AddPlayerActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();
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
                                    else
                                    {
                                        holder.linearLayout.setVisibility(View.GONE);
                                        loadingBar.dismiss();
                                        Toast.makeText(AddPlayerActivity.this, "This player is already exist in your team!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });



                    holder.itemView.setVisibility(View.VISIBLE);
                }
                else
                    {
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        holder.itemView.setLayoutParams(params);
                    }

                progressBar.setVisibility(View.INVISIBLE);




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

    private void SearchUser2() {
        FirebaseRecyclerOptions<AllProfile> options =
                new FirebaseRecyclerOptions.Builder<AllProfile>()
                        .setQuery(paymentRef, AllProfile.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProfileViewHolder holder, int position, @NonNull AllProfile model) {

                holder.setIsRecyclable(false);

                if (model.getPrimarySport().equals(sport) || model.getSecondarySport().equals(sport))
                {
                    if (!model.getPicture().equals("null"))
                    {
                        Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                    }
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
                    holder.expertise.setText(model.getExpertise());
                    holder.phone.setVisibility(View.GONE);
                    holder.psport.setText(model.getPrimarySport());
                    holder.ssport.setText(model.getSecondarySport());
                    holder.state.setText(ProperCase.properCase(model.getState()));

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

                    holder.addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.linearLayout.setVisibility(View.GONE);
                            loadingBar.show();

                            final DatabaseReference PlayerRef;
                            PlayerRef = FirebaseDatabase.getInstance().getReference().child("Players");

                            final DatabaseReference AllProfRef;
                            AllProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

                            PlayerRef.child(id).child(model.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists())
                                    {
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
                                                                    AllProfRef.child(model.getPhone()).child("MyTeams").child(id).setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            FirebaseDatabase.getInstance().getReference("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                                    if (snapshot2.exists())
                                                                                    {
                                                                                        String token = Objects.requireNonNull(snapshot2.child(model.getPhone()).getValue()).toString();
                                                                                        String title = "New Team "+ProperCase.properCase(Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString());
                                                                                        String msg = "You have been added to the "+ ProperCase.properCase(Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString())+" team";
                                                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,title,msg,getApplicationContext(),AddPlayerActivity.this);

                                                                                        notificationsSender.SendNotifications();
                                                                                        loadingBar.dismiss();
                                                                                        Toast.makeText(AddPlayerActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

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
                                    else
                                    {
                                        holder.linearLayout.setVisibility(View.GONE);
                                        loadingBar.dismiss();
                                        Toast.makeText(AddPlayerActivity.this, "This player is already exist in your team!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });



                    holder.itemView.setVisibility(View.VISIBLE);
                }
                else
                {
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                }

                progressBar.setVisibility(View.INVISIBLE);




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