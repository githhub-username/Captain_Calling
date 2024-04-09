package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class InviteTeamsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference allTeamRef;
    private FirebaseRecyclerAdapter<AllTeam, InviteTeamsViewHolder> adapter;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_invite_teams);
        Paper.init(getApplicationContext());
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_all_team);
        progressBar = findViewById(R.id.dsdjsbhbjvjsvdhsvdshd);
        searchView = findViewById(R.id.bjvbjvjhjbjvjvhvhcv);

        View v = searchView.findViewById(R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);



        searchView.setMaxWidth(Integer.MAX_VALUE);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        allTeamRef = FirebaseDatabase.getInstance().getReference("AllTeam");


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
                    SearchTeam(mQueryString.toLowerCase());

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchTeam2();
    }

    private void SearchTeam2() {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(allTeamRef, AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, InviteTeamsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull InviteTeamsViewHolder holder, int position, @NonNull AllTeam model) {
                holder.setIsRecyclable(false);
                if (model.getSport().equals(Paper.book().read("PrimarySport")) || model.getSport().equals(Paper.book().read("SecondarySport")))
                {
                    Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                    holder.address.setText(ProperCase.properCase(model.getAddress()));
                    holder.diss.setText(ProperCase.properCase(model.getDistrict()));

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

                    holder.cancelBtn.setText("Team History");
                    holder.cancelBtn.setBackgroundColor(Color.parseColor("#388E3C"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);

                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(InviteTeamsActivity.this,AddMatchResultActivity.class);
                            intent.putExtra("Team_1_Id",model.getEntryId());
                            intent.putExtra("Team_2_Id","");
                            startActivity(intent);
                        }
                    });

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


                    holder.teamName.setText(ProperCase.properCase(ProperCase.properCase(model.getTeamName())));
                    holder.captain.setText(ProperCase.properCase(ProperCase.properCase(model.getCaptain())));

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

                    holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            holder.linearLayout.setVisibility(View.GONE);
                            loadingBar.show();

                            FirebaseDatabase.getInstance().getReference().child("Players").child(model.getEntryId()).child(Paper.book().read("Phone"))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists())
                                            {
                                                FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.exists())
                                                        {
                                                            final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                            objectHashMap.put("Address",model.getAddress());
                                                            objectHashMap.put("EntryId",model.getEntryId());
                                                            objectHashMap.put("Picture",model.getPicture());
                                                            objectHashMap.put("TeamName",model.getTeamName());
                                                            objectHashMap.put("Captain",model.getCaptain());
                                                            objectHashMap.put("CaptainPhone",model.getCaptainPhone());
                                                            objectHashMap.put("Sport",model.getSport());
                                                            objectHashMap.put("Level",model.getLevel());
                                                            objectHashMap.put("District",model.getDistrict());
                                                            objectHashMap.put("State",model.getState());
                                                            objectHashMap.put("Status","Pending");

                                                            FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                        objectHashMap1.put("Phone",Paper.book().read("Phone"));
                                                                        objectHashMap1.put("Name",Paper.book().read("Name"));
                                                                        objectHashMap1.put("State",Paper.book().read("State"));
                                                                        objectHashMap1.put("District",Paper.book().read("District"));
                                                                        objectHashMap1.put("Address",Paper.book().read("Address"));
                                                                        objectHashMap1.put("PrimarySport",Paper.book().read("PrimarySport"));
                                                                        objectHashMap1.put("SecondarySport",Paper.book().read("SecondarySport"));
                                                                        objectHashMap1.put("Picture",Paper.book().read("Picture"));
                                                                        objectHashMap1.put("Level",Paper.book().read("Level"));
                                                                        objectHashMap1.put("Status","Pending");

                                                                        FirebaseDatabase.getInstance().getReference().child("RequestReceiver").child(model.getEntryId()).child(Paper.book().read("Phone")).updateChildren(objectHashMap1)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        loadingBar.dismiss();
                                                                                        Toast.makeText(InviteTeamsActivity.this, "Your request has been sent successfully!", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                });








                                                                    }

                                                                }
                                                            });


                                                        }
                                                        else
                                                        {
                                                            if (Objects.equals(snapshot.child("Status").getValue(), "Cancelled") || Objects.equals(snapshot.child("Status").getValue(), "Approved"))
                                                            {
                                                                final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                                objectHashMap.put("Address",model.getAddress());
                                                                objectHashMap.put("EntryId",model.getEntryId());
                                                                objectHashMap.put("Picture",model.getPicture());
                                                                objectHashMap.put("TeamName",model.getTeamName());
                                                                objectHashMap.put("Captain",model.getCaptain());
                                                                objectHashMap.put("CaptainPhone",model.getCaptainPhone());
                                                                objectHashMap.put("Sport",model.getSport());
                                                                objectHashMap.put("Level",model.getLevel());
                                                                objectHashMap.put("District",model.getDistrict());
                                                                objectHashMap.put("State",model.getState());
                                                                objectHashMap.put("Status","Pending");

                                                                FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                            objectHashMap1.put("Phone",Paper.book().read("Phone"));
                                                                            objectHashMap1.put("Name",Paper.book().read("Name"));
                                                                            objectHashMap1.put("State",Paper.book().read("State"));
                                                                            objectHashMap1.put("District",Paper.book().read("District"));
                                                                            objectHashMap1.put("Address",Paper.book().read("Address"));
                                                                            objectHashMap1.put("PrimarySport",Paper.book().read("PrimarySport"));
                                                                            objectHashMap1.put("SecondarySport",Paper.book().read("SecondarySport"));
                                                                            objectHashMap1.put("Picture",Paper.book().read("Picture"));
                                                                            objectHashMap1.put("Level",Paper.book().read("Level"));
                                                                            objectHashMap1.put("Status","Pending");

                                                                            FirebaseDatabase.getInstance().getReference().child("RequestReceiver").child(model.getEntryId()).child(Paper.book().read("Phone")).updateChildren(objectHashMap1)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            loadingBar.dismiss();
                                                                                            Toast.makeText(InviteTeamsActivity.this, "Your request has been sent once more!", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    });


                                                                        }

                                                                    }
                                                                });


                                                            }
                                                            else
                                                            {
                                                                holder.linearLayout.setVisibility(View.GONE);
                                                                loadingBar.dismiss();
                                                                      Toast.makeText(InviteTeamsActivity.this, "Your request has been already sent!", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            else
                                            {
                                                loadingBar.dismiss();
                                                Toast.makeText(InviteTeamsActivity.this, "You are already a member of this team!", Toast.LENGTH_SHORT).show();
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
                else {
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public InviteTeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_teams_model, parent,false);
                return  new InviteTeamsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void SearchTeam(String mQueryString) {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(allTeamRef.orderByChild("TeamName").startAt(mQueryString).endAt(mQueryString + "\uf8ff"), AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, InviteTeamsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull InviteTeamsViewHolder holder, int position, @NonNull AllTeam model) {
                holder.setIsRecyclable(false);
                if (model.getSport().equals(Paper.book().read("PrimarySport")) || model.getSport().equals(Paper.book().read("SecondarySport")))
                {
                    Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                    holder.address.setText(ProperCase.properCase(model.getAddress()));
                    holder.diss.setText(ProperCase.properCase(model.getDistrict()));

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

                    holder.cancelBtn.setText("Team History");
                    holder.cancelBtn.setBackgroundColor(Color.parseColor("#388E3C"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);

                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(InviteTeamsActivity.this,AddMatchResultActivity.class);
                            intent.putExtra("Team_1_Id",model.getEntryId());
                            intent.putExtra("Team_2_Id","");
                            startActivity(intent);
                        }
                    });

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


                    holder.teamName.setText(ProperCase.properCase(ProperCase.properCase(model.getTeamName())));
                    holder.captain.setText(ProperCase.properCase(ProperCase.properCase(model.getCaptain())));

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

                    holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            holder.linearLayout.setVisibility(View.GONE);
                            loadingBar.show();

                            FirebaseDatabase.getInstance().getReference().child("Players").child(model.getEntryId()).child(Paper.book().read("Phone"))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists())
                                            {
                                                FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.exists())
                                                        {
                                                            final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                            objectHashMap.put("Address",model.getAddress());
                                                            objectHashMap.put("EntryId",model.getEntryId());
                                                            objectHashMap.put("Picture",model.getPicture());
                                                            objectHashMap.put("TeamName",model.getTeamName());
                                                            objectHashMap.put("Captain",model.getCaptain());
                                                            objectHashMap.put("CaptainPhone",model.getCaptainPhone());
                                                            objectHashMap.put("Sport",model.getSport());
                                                            objectHashMap.put("Level",model.getLevel());
                                                            objectHashMap.put("District",model.getDistrict());
                                                            objectHashMap.put("State",model.getState());
                                                            objectHashMap.put("Status","Pending");

                                                            FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                        objectHashMap1.put("Phone",Paper.book().read("Phone"));
                                                                        objectHashMap1.put("Name",Paper.book().read("Name"));
                                                                        objectHashMap1.put("State",Paper.book().read("State"));
                                                                        objectHashMap1.put("District",Paper.book().read("District"));
                                                                        objectHashMap1.put("Address",Paper.book().read("Address"));
                                                                        objectHashMap1.put("PrimarySport",Paper.book().read("PrimarySport"));
                                                                        objectHashMap1.put("SecondarySport",Paper.book().read("SecondarySport"));
                                                                        objectHashMap1.put("Picture",Paper.book().read("Picture"));
                                                                        objectHashMap1.put("Level",Paper.book().read("Level"));
                                                                        objectHashMap1.put("Status","Pending");

                                                                        FirebaseDatabase.getInstance().getReference().child("RequestReceiver").child(model.getEntryId()).child(Paper.book().read("Phone")).updateChildren(objectHashMap1)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        loadingBar.dismiss();
                                                                                        Toast.makeText(InviteTeamsActivity.this, "Your request has been sent successfully!", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                });








                                                                    }

                                                                }
                                                            });


                                                        }
                                                        else
                                                        {
                                                            if (Objects.equals(snapshot.child("Status").getValue(), "Cancelled") || Objects.equals(snapshot.child("Status").getValue(), "Approved"))
                                                            {
                                                                final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                                objectHashMap.put("Address",model.getAddress());
                                                                objectHashMap.put("EntryId",model.getEntryId());
                                                                objectHashMap.put("Picture",model.getPicture());
                                                                objectHashMap.put("TeamName",model.getTeamName());
                                                                objectHashMap.put("Captain",model.getCaptain());
                                                                objectHashMap.put("CaptainPhone",model.getCaptainPhone());
                                                                objectHashMap.put("Sport",model.getSport());
                                                                objectHashMap.put("Level",model.getLevel());
                                                                objectHashMap.put("District",model.getDistrict());
                                                                objectHashMap.put("State",model.getState());
                                                                objectHashMap.put("Status","Pending");

                                                                FirebaseDatabase.getInstance().getReference().child("RequestSender").child(Paper.book().read("Phone")).child(model.getEntryId()).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                            objectHashMap1.put("Phone",Paper.book().read("Phone"));
                                                                            objectHashMap1.put("Name",Paper.book().read("Name"));
                                                                            objectHashMap1.put("State",Paper.book().read("State"));
                                                                            objectHashMap1.put("District",Paper.book().read("District"));
                                                                            objectHashMap1.put("Address",Paper.book().read("Address"));
                                                                            objectHashMap1.put("PrimarySport",Paper.book().read("PrimarySport"));
                                                                            objectHashMap1.put("SecondarySport",Paper.book().read("SecondarySport"));
                                                                            objectHashMap1.put("Picture",Paper.book().read("Picture"));
                                                                            objectHashMap1.put("Level",Paper.book().read("Level"));
                                                                            objectHashMap1.put("Status","Pending");

                                                                            FirebaseDatabase.getInstance().getReference().child("RequestReceiver").child(model.getEntryId()).child(Paper.book().read("Phone")).updateChildren(objectHashMap1)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            loadingBar.dismiss();
                                                                                            Toast.makeText(InviteTeamsActivity.this, "Your request has been sent once more!", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    });


                                                                        }

                                                                    }
                                                                });


                                                            }
                                                            else
                                                            {
                                                                holder.linearLayout.setVisibility(View.GONE);
                                                                loadingBar.dismiss();
                                                                Toast.makeText(InviteTeamsActivity.this, "Your request has been already sent!", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            else
                                            {
                                                loadingBar.dismiss();
                                                Toast.makeText(InviteTeamsActivity.this, "You are already a member of this team!", Toast.LENGTH_SHORT).show();
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
                else {
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public InviteTeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_teams_model, parent,false);
                return  new InviteTeamsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}