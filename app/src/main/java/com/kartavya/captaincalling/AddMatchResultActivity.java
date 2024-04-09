package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kartavya.captaincalling.Interface.ItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class AddMatchResultActivity extends AppCompatActivity {

    private String team1="",team2="",teamName="",teamName2="",captainPhone="";
    private Button addBtn;
    private Dialog dialog;
    private EditText editText;
    private Button submitBtn;
    private ProgressDialog loadingBar;
    private RecyclerView recyclerView;
    private DatabaseReference resultRef;
    private FirebaseRecyclerAdapter<Result, ResultViewHolder> adapter;
    private ProgressBar progressBar;
    private TextView textView,textViewEmpty;

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

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_match_result);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        addBtn = findViewById(R.id.btn_result);

        recyclerView = findViewById(R.id.recycler_result);

        progressBar = findViewById(R.id.bjvhcgcxgtxtzx);

        textView = findViewById(R.id.xzzjxbjxbjaxajvx);
        textViewEmpty = findViewById(R.id.nxnzbzcbzcb);

        ImageView backBtn = findViewById(R.id.back_add_match);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddMatchResultActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        if (getIntent() != null)
        {
            team1 = getIntent().getStringExtra("Team_1_Id");
            team2 = getIntent().getStringExtra("Team_2_Id");
            teamName = getIntent().getStringExtra("TeamName");
            captainPhone = getIntent().getStringExtra("CaptainPhone");
            LoadResult(team1);
            LoadTeamName();
        }

        if (team2.equals(""))
        {
            addBtn.setVisibility(View.GONE);
            textView.setText("Team History");
        }



        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        dialog = new Dialog(AddMatchResultActivity.this);
        dialog.setContentView(R.layout.add_result_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animations;

        editText = dialog.findViewById(R.id.vhctdnkbkbtststts);
        submitBtn = dialog.findViewById(R.id.okbhvhvbhvhvhaybtn);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();

                if (TextUtils.isEmpty(msg))
                {
                    editText.setError("Area is required");
                    editText.requestFocus();
                }
                else
                {
                    dialog.dismiss();
                    loadingBar.show();

                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

                    String datetime = ft.format(dNow);

                    final HashMap<String, Object> objectHashMap = new HashMap<>();

                    objectHashMap.put("Team1", team1);
                    objectHashMap.put("Team2",team2);
                    objectHashMap.put("Status","not approved");
                    objectHashMap.put("Date",currentDate.format(dNow));
                    objectHashMap.put("Title",ProperCase.properCase(teamName)+" Vs "+ProperCase.properCase(teamName2));
                    objectHashMap.put("Msg",msg);

                    FirebaseDatabase.getInstance().getReference().child("MatchResult").child(datetime).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseDatabase.getInstance().getReference("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {
                                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(Objects.requireNonNull(snapshot.child(captainPhone).getValue()).toString(),"Match Result "+ProperCase.properCase(teamName)+" Vs "+ProperCase.properCase(teamName2),msg,getApplicationContext(),AddMatchResultActivity.this);
                                            notificationsSender.SendNotifications();
                                            loadingBar.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });

                }
            }
        });


    }

    private void LoadTeamName() {
        FirebaseDatabase.getInstance().getReference("AllTeam").child(team1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    teamName2 = Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadResult(String team1) {

        resultRef = FirebaseDatabase.getInstance().getReference("MatchResult");

        FirebaseRecyclerOptions<Result> options =
                new FirebaseRecyclerOptions.Builder<Result>()
                        .setQuery(resultRef, Result.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Result, ResultViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ResultViewHolder holder, int position, @NonNull Result model) {

                progressBar.setVisibility(View.VISIBLE);
                if (model.getTeam2().equals(team1) || model.getTeam1().equals(team1))
                {
                    holder.date.setText(model.getDate());
                    holder.des.setText(model.getMsg());
                    holder.status.setText(model.getStatus());
                    holder.title.setText(model.getTitle());


                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            if (!team2.equals("") && model.getTeam2().equals(team1))
                            {

                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddMatchResultActivity.this);
                                builder.setTitle("Approved?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i==0)
                                        {
                                            dialogInterface.dismiss();
                                            loadingBar.show();
                                            adapter.getRef(position).child("Status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loadingBar.dismiss();
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
                else
                {
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                }
                progressBar.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.INVISIBLE);


            }

            @NonNull
            @Override
            public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_model, parent,false);
                return  new ResultViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}