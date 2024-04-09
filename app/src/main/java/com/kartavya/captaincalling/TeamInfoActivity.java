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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.kartavya.captaincalling.Interface.ItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class TeamInfoActivity extends AppCompatActivity {
    
    private String id,sportN,teamName;
    private CircleImageView circleImageView;
    private TextView name,level,sport,captain,numberOfMember;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private DatabaseReference playerRef;
    private FirebaseRecyclerAdapter<Players, AllProfileViewHolder> adapter;
    private ProgressBar progressBar;
    private boolean isCaptain=false;
    private ProgressDialog loadingBar;
    private ImageView requestBtn,editTeamBtn;
    private ImageView buttonExit;
    private ImageView buttonChallenge;
    private ImageView seeTournamentRequests;
    private LinearLayout linearLayoutCP;
    private int NumberTeam=0;

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
        setContentView(R.layout.activity_team_info);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        circleImageView = findViewById(R.id.pic_info);
        name = findViewById(R.id.team_name_info);
        level = findViewById(R.id.level_info);
        sport = findViewById(R.id.sport_name_info);
        captain = findViewById(R.id.captain_info);
        numberOfMember = findViewById(R.id.member_of_grp);
        linearLayout = findViewById(R.id.ly_info);
        progressBar = findViewById(R.id.progress_info);
        buttonExit = findViewById(R.id.leave_team_btn);
        buttonChallenge = findViewById(R.id.challenge_btn);
        linearLayoutCP = findViewById(R.id.ly_captain_power);
        requestBtn = findViewById(R.id.request_join);
        editTeamBtn = findViewById(R.id.request_editTeam);
        seeTournamentRequests = findViewById(R.id.check_tournament_requests);

        recyclerView = findViewById(R.id.recycler_info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);


        ImageView backBtn = findViewById(R.id.back_team_info);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });






        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
            sportN = getIntent().getStringExtra("Team_Sport");
            teamName = getIntent().getStringExtra("Team_Name");
            LoadInfo(id);

        }

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeamInfoActivity.this,RequestReceiveActivity.class);
                intent.putExtra("Team_Id", id);
                startActivity(intent);
            }
        });

        editTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeamInfoActivity.this,EditTeamActivity.class);
                intent.putExtra("Team_Id", id);
                startActivity(intent);
            }
        });

        seeTournamentRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeamInfoActivity.this, AllTournamentRequestsActivity.class);
                startActivity(intent);
            }
        });

        buttonChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeamInfoActivity.this,ChallengeActivity.class);
                intent.putExtra("Team_Id", id);
                intent.putExtra("Team_Name", teamName);
                startActivity(intent);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCaptain)
                {
                    if (adapter.getItemCount()==1)
                    {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(TeamInfoActivity.this);
                        builder.setTitle("Delete Team");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    loadingBar.show();
                                    FirebaseDatabase.getInstance().getReference("AllTeam").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                FirebaseDatabase.getInstance().getReference("AllProfiles").child(Paper.book().read("Phone")).child("MyTeams")
                                                        .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        FirebaseDatabase.getInstance().getReference("Players").child(id).child(Paper.book().read("Phone"))
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                loadingBar.dismiss();
                                                                dialogInterface.dismiss();
                                                                Intent intent = new Intent(TeamInfoActivity.this,MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        }
                                    });

                                }
                                else
                                {
                                    dialogInterface.dismiss();
                                }

                            }
                        });
                        builder.show();
                    }
                    else
                    {
                        Toast.makeText(TeamInfoActivity.this, "Make a captain to anyone else before leaving team!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    CharSequence[] options = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamInfoActivity.this);
                    builder.setTitle("Leave Team");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i==0)
                            {
                                loadingBar.show();
                                FirebaseDatabase.getInstance().getReference("AllProfiles").child(Paper.book().read("Phone")).child("MyTeams")
                                        .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseDatabase.getInstance().getReference("Players").child(id).child(Paper.book().read("Phone"))
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            loadingBar.dismiss();
                                                            Intent intent = new Intent(TeamInfoActivity.this,MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                    }
                                                });

                                            }
                                        });

                                    }
                                });

                            }
                            else
                            {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();


                }
            }
        });


    }

    private void LoadPlayer(String id) {

        playerRef = FirebaseDatabase.getInstance().getReference("Players").child(id);


        FirebaseRecyclerOptions<Players> options =
                new FirebaseRecyclerOptions.Builder<Players>()
                        .setQuery(playerRef, Players.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Players, AllProfileViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull AllProfileViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Players model) {

                //holder.setIsRecyclable(false);

                holder.itemView.setVisibility(View.VISIBLE);

                numberOfMember.setText(adapter.getItemCount()+" players");

                if (!model.getPicture().equals("null"))
                {
                    Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.circleImageView);
                }

                holder.addBtn.setText("Make captain");

                holder.call.setVisibility(View.GONE);
                holder.phone.setVisibility(View.GONE);

                /*holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "tel:" + model.getPhone();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(uri));
                        startActivity(callIntent);
                    }
                });*/

                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AllProfile usersData = snapshot.child("AllProfiles").child(model.getPhone()).getValue(AllProfile.class);
                        assert usersData != null;
                        holder.level.setVisibility(View.GONE);
                        holder.name.setText(ProperCase.properCase(usersData.getName()));
                        holder.level2.setText(ProperCase.properCase(usersData.getLevel()));
                        holder.level_lyt.setVisibility(View.VISIBLE);
                        holder.address.setText(ProperCase.properCase(usersData.getAddress()));
                       // holder.phone.setText(usersData.getPhone());
                        holder.psport.setText(usersData.getPrimarySport());
                        holder.ssport.setText(usersData.getSecondarySport());
                        holder.state.setText(ProperCase.properCase(usersData.getState()));
                        holder.district.setText(ProperCase.properCase(usersData.getDistrict()));
                        holder.expertise.setText(usersData.getExpertise());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (isCaptain && !model.getIsCaptain().equals("true"))
                {
                    holder.delete.setVisibility(View.VISIBLE);



                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!model.getIsCaptain().equals("true"))
                            {
                                loadingBar.show();
                                FirebaseDatabase.getInstance().getReference("AllProfiles").child(model.getPhone()).child("MyTeams")
                                        .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                FirebaseDatabase.getInstance().getReference("AllProfiles").child(model.getPhone()).child("Topics").child(id).child(id).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        adapter.notifyDataSetChanged();
                                                                        if (adapter!=null)
                                                                        {
                                                                            adapter=null;
                                                                            LoadPlayer(id);
                                                                            loadingBar.dismiss();
                                                                        }
                                                                    }

                                                                }
                                                            });

                                                        }
                                                    }
                                                });


                                            }
                                        });
                                    }
                                });
                            }

                        }
                    });

                }

                if (isCaptain)
                {
                    if (model.getIsCaptain().equals("true"))
                    {
                        holder.addBtn.setVisibility(View.GONE);
                    }

                }
                else
                {
                    holder.addBtn.setVisibility(View.GONE);
                }


                if (!isCaptain && model.getIsCaptain().equals("true"))
                {
                    holder.captain.setVisibility(View.VISIBLE);
                }

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
                        if (isCaptain && !model.getIsCaptain().equals("true"))
                        {

                            NumberTeam=0;
                            LoadNumberOfTeam(model.getPhone());

                            CharSequence[] options = new CharSequence[]
                                    {
                                            "Yes",
                                            "No"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(TeamInfoActivity.this);
                            builder.setTitle("Make Captain");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0)
                                    {
                                        if (NumberTeam<2)
                                        {
                                            loadingBar.show();

                                            final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                            objectHashMap1.put("Captain", model.getName());
                                            objectHashMap1.put("CaptainPhone",model.getPhone());
                                            FirebaseDatabase.getInstance().getReference("AllTeam").child(id).updateChildren(objectHashMap1)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference("Players").child(id).child(Paper.book().read("Phone"))
                                                                    .child("isCaptain").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    FirebaseDatabase.getInstance().getReference("Players").child(id).child(model.getPhone())
                                                                            .child("isCaptain").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            Date dNow = new Date();
                                                                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                                                            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                                                                            String datetime2 = ft.format(dNow);

                                                                            final HashMap<String, Object> objectHashMap11 = new HashMap<>();

                                                                            objectHashMap11.put("Name",model.getName());
                                                                            objectHashMap11.put("Phone",model.getPhone());
                                                                            objectHashMap11.put("Date",currentDate.format(dNow));
                                                                            objectHashMap11.put("Message","Hello guys my name "+model.getName()+" as a new captain of this team, thanks!");

                                                                            FirebaseDatabase.getInstance().getReference().child("Chats").child(id).child(datetime2).updateChildren(objectHashMap11)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            adapter.notifyDataSetChanged();
                                                                                            if (adapter!=null)
                                                                                            {
                                                                                                adapter=null;
                                                                                                LoadPlayer(id);
                                                                                                loadingBar.dismiss();
                                                                                                isCaptain=false;
                                                                                                requestBtn.setVisibility(View.INVISIBLE);
                                                                                                linearLayoutCP.setVisibility(View.INVISIBLE);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        }
                                                                    });
                                                                }
                                                            });

                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            Toast.makeText(TeamInfoActivity.this, model.getName()+" "+" is already a captain of more than one team!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    if (i==1)
                                    {
                                        dialogInterface.dismiss();
                                    }

                                }
                            });
                            builder.show();
                        }
                    }
                });


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                    }
                });



                progressBar.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);


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

    private void LoadNumberOfTeam(String phone) {
        FirebaseDatabase.getInstance().getReference("AllTeam").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {

                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                    {

                        if (Objects.equals(dataSnapshot1.child("CaptainPhone").getValue(), phone))
                        {
                            NumberTeam++;
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadInfo(String id) {
        DatabaseReference TeamRef = FirebaseDatabase.getInstance().getReference().child("AllTeam").child(id);

        TeamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String pic = Objects.requireNonNull(snapshot.child("Picture").getValue()).toString();

                Glide.with(getApplicationContext()).load(pic).into(circleImageView);

                name.setText(ProperCase.properCase(Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString()));
                level.setText(Objects.requireNonNull(snapshot.child("Level").getValue()).toString());
                sport.setText(Objects.requireNonNull(snapshot.child("Sport").getValue()).toString());
                captain.setText(ProperCase.properCase(Objects.requireNonNull(snapshot.child("Captain").getValue()).toString())+" (Captain )");

                if (Objects.requireNonNull(snapshot.child("CaptainPhone").getValue()).toString().equals(Paper.book().read("Phone")))
                {
                    isCaptain=true;
                    linearLayout.setVisibility(View.VISIBLE);
                    requestBtn.setVisibility(View.VISIBLE);
                    linearLayoutCP.setVisibility(View.VISIBLE);
                    buttonExit.setVisibility(View.VISIBLE);
                    //deleteBtn.setVisibility(View.VISIBLE);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(TeamInfoActivity.this,AddPlayerActivity.class);
                                    intent.putExtra("Team_Id", id);
                                    intent.putExtra("Team_Sport", sportN);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }

                LoadPlayer(id);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}