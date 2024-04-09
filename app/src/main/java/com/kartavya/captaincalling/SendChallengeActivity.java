package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import io.paperdb.Paper;

public class SendChallengeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference allTeamRef;
    private FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder> adapter;
    private ProgressBar progressBar;
    private SearchView searchView;
    private String mQueryString;
    private String id;
    private ProgressDialog loadingBar;
    public String queryFire="Name";
    private Dialog dialog;
    private EditText editTextAdd,editTextPlace;
    private String add,place,date="",time="";
    private Spinner stateSpinner, districtSpinner;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    private Button button;
    private RelativeLayout relativeLayoutDate,relativeLayoutTime;
    private TextView dateTv,timeTv;
    final Calendar myCalendar = Calendar.getInstance();
    private String EntryId,Address,Picture,TeamName,Captain,CaptainPhone,Sport,Level,District,State, challengeState, challengeDistrict;

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
        setContentView(R.layout.activity_send_challenge);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }



        recyclerView = findViewById(R.id.recycler_all_team123);
        progressBar = findViewById(R.id.dsdjsbhbjvjsvdhsvdshd123);
        searchView = findViewById(R.id.bjvbjvjhjbjvjvhvhcv123);

        dialog = new Dialog(SendChallengeActivity.this);
        dialog.setContentView(R.layout.send_challenge_dialog);

        View v = searchView.findViewById(R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
        }


        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animations;

        editTextAdd = dialog.findViewById(R.id.sdsscshjujuvfsfsf);
        editTextPlace = dialog.findViewById(R.id.ssdsdnkbbsdsd);
        button = dialog.findViewById(R.id.okbhvhvbjvhaybtn);
        relativeLayoutDate = dialog.findViewById(R.id.choose_date);
        relativeLayoutTime = dialog.findViewById(R.id.choose_time);
        dateTv = dialog.findViewById(R.id.dob_view);
        timeTv = dialog.findViewById(R.id.dob_vieddw);


        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();

        };

        relativeLayoutDate.setOnClickListener(view -> new DatePickerDialog(SendChallengeActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        relativeLayoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SendChallengeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time = selectedHour + ":" + selectedMinute;
                        timeTv.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });





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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date = sdf.format(myCalendar.getTime());

        dateTv.setText(date);
    }

    private void SearchTeam(String toLowerCase) {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(allTeamRef.orderByChild("TeamName").startAt(toLowerCase), AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllTeamViewHolder holder, int position, @NonNull AllTeam model) {
                holder.setIsRecyclable(false);
                if (model.getSport().equals(Paper.book().read("PrimarySport")) && !model.getCaptainPhone().equals(Paper.book().read("Phone")))
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

                    holder.cancelBtn.setText("Team History");
                    holder.cancelBtn.setBackgroundColor(Color.parseColor("#000000"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);

                    holder.level.setText(model.getLevel());

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
                    // holder.captainPhone.setText(ProperCase.properCase(model.getCaptainPhone()));
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

                    holder.requestBtn.setText("Send Challenge Request");

                    holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.linearLayout.setVisibility(View.GONE);
                            EntryId = model.getEntryId();
                            Address = model.getAddress();
                            Picture = model.getPicture();
                            TeamName = model.getTeamName();
                            Captain = model.getCaptain();
                            CaptainPhone = model.getCaptainPhone();
                            Sport = model.getSport();
                            Level=model.getLevel();
                            District = model.getDistrict();
                            State = model.getState();
                            openDialog();
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
            public AllTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_team_model, parent,false);
                return  new AllTeamViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void openDialog() {
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add = editTextAdd.getText().toString();
                place = editTextPlace.getText().toString();

                if (TextUtils.isEmpty(add))
                {
                    editTextAdd.setError("Area is required");
                    editTextAdd.requestFocus();
                }
                else if (TextUtils.isEmpty(place))
                {
                    editTextPlace.setError("Place is required");
                    editTextPlace.requestFocus();
                }
                else if (date.equals(""))
                {
                    Toast.makeText(SendChallengeActivity.this, "Please choose date!", Toast.LENGTH_SHORT).show();
                }
                else if (time.equals(""))
                {
                    Toast.makeText(SendChallengeActivity.this, "Please choose time!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.dismiss();
                    loadingBar.show();

                    FirebaseDatabase.getInstance().getReference().child("Players").child(EntryId).child(Paper.book().read("Phone"))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists())
                                    {
                                        FirebaseDatabase.getInstance().getReference().child("ChallengeSender").child(EntryId).child(id)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.exists())
                                                        {
                                                            FirebaseDatabase.getInstance().getReference().child("ChallengeReceiver").child(EntryId).child(id)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (!snapshot.exists())
                                                                            {
                                                                                FirebaseDatabase.getInstance().getReference().child("AllTeam").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        if (snapshot.exists())
                                                                                        {
                                                                                            final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                                            objectHashMap1.put("Address", Objects.requireNonNull(snapshot.child("Address").getValue()).toString());
                                                                                            objectHashMap1.put("EntryId",Objects.requireNonNull(snapshot.child("EntryId").getValue()).toString());
                                                                                            objectHashMap1.put("Picture",Objects.requireNonNull(snapshot.child("Picture").getValue()).toString());
                                                                                            objectHashMap1.put("TeamName",Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString());
                                                                                            objectHashMap1.put("Captain",Objects.requireNonNull(snapshot.child("Captain").getValue()).toString());
                                                                                            objectHashMap1.put("CaptainPhone",Objects.requireNonNull(snapshot.child("CaptainPhone").getValue()).toString());
                                                                                            objectHashMap1.put("Sport",Objects.requireNonNull(snapshot.child("Sport").getValue()).toString());
                                                                                            objectHashMap1.put("Level",Objects.requireNonNull(snapshot.child("Level").getValue()).toString());
                                                                                            objectHashMap1.put("District",Objects.requireNonNull(snapshot.child("District").getValue()).toString());
                                                                                            objectHashMap1.put("State",Objects.requireNonNull(snapshot.child("State").getValue()).toString());
                                                                                            objectHashMap1.put("Status","Pending");
                                                                                            objectHashMap1.put("Date",date);
                                                                                            objectHashMap1.put("Time",time);
                                                                                            objectHashMap1.put("Area",add);
                                                                                            objectHashMap1.put("Place",place);


                                                                                            FirebaseDatabase.getInstance().getReference().child("ChallengeSender").child(EntryId).child(id).updateChildren(objectHashMap1)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                                                                                objectHashMap.put("Address",Address);
                                                                                                                objectHashMap.put("EntryId",EntryId);
                                                                                                                objectHashMap.put("Picture",Picture);
                                                                                                                objectHashMap.put("TeamName",TeamName);
                                                                                                                objectHashMap.put("Captain",Captain);
                                                                                                                objectHashMap.put("CaptainPhone",CaptainPhone);
                                                                                                                objectHashMap.put("Sport",Sport);
                                                                                                                objectHashMap.put("Level",Level);
                                                                                                                objectHashMap.put("District",District);
                                                                                                                objectHashMap.put("State",State);
                                                                                                                objectHashMap.put("Status","Pending");
                                                                                                                objectHashMap.put("Date",date);
                                                                                                                objectHashMap.put("Time",time);
                                                                                                                objectHashMap.put("Area",add);
                                                                                                                objectHashMap.put("Place",place);

                                                                                                                FirebaseDatabase.getInstance().getReference().child("ChallengeReceiver").child(id).child(EntryId).updateChildren(objectHashMap)
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                FirebaseDatabase.getInstance().getReference("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                                                                                        if (snapshot2.exists()) {
                                                                                                                                            DataSnapshot captainTokenSnapshot = snapshot2.child(CaptainPhone);
                                                                                                                                            if (captainTokenSnapshot.exists()) {
                                                                                                                                                String token = Objects.requireNonNull(captainTokenSnapshot.getValue()).toString();

                                                                                                                                                DataSnapshot teamNameSnapshot = snapshot.child("TeamName");
                                                                                                                                                if (teamNameSnapshot.exists()) {
                                                                                                                                                    String teamName = ProperCase.properCase(Objects.requireNonNull(teamNameSnapshot.getValue()).toString());

                                                                                                                                                    String msg = "You got a new challenge from " + teamName;

                                                                                                                                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, "New Challenge!", msg, getApplicationContext(), SendChallengeActivity.this);
                                                                                                                                                    notificationsSender.SendNotifications();

                                                                                                                                                    loadingBar.dismiss();
                                                                                                                                                    Toast.makeText(SendChallengeActivity.this, "Your request has been sent successfully!", Toast.LENGTH_SHORT).show();
                                                                                                                                                } else {
                                                                                                                                                    loadingBar.dismiss();
                                                                                                                                                    Toast.makeText(SendChallengeActivity.this, "TeamName not found in ChallengeReceiver", Toast.LENGTH_SHORT).show();
                                                                                                                                                }
                                                                                                                                            } else {
                                                                                                                                                loadingBar.dismiss();
                                                                                                                                                Toast.makeText(SendChallengeActivity.this, "Token not found for CaptainPhone", Toast.LENGTH_SHORT).show();
                                                                                                                                            }
                                                                                                                                        } else {
                                                                                                                                            loadingBar.dismiss();
                                                                                                                                            Toast.makeText(SendChallengeActivity.this, "Token node not found", Toast.LENGTH_SHORT).show();
                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                                    }
                                                                                                                                });


                                                                                                                            }
                                                                                                                        });
                                                                                                            }

                                                                                                        }
                                                                                                    });

                                                                                        }

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                            }
                                                                            else
                                                                            {
                                                                                if (Objects.equals(snapshot.child("Status").getValue(), "Pending"))
                                                                                {
                                                                                    loadingBar.dismiss();
                                                                                    Toast.makeText(SendChallengeActivity.this, "Please check you have already a pending request of this team!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                else if (Objects.equals(snapshot.child("Status").getValue(), "Accepted"))
                                                                                {
                                                                                    loadingBar.dismiss();
                                                                                    Toast.makeText(SendChallengeActivity.this, "You have accepted this team's request!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                else if (Objects.equals(snapshot.child("Status").getValue(), "Rejected"))
                                                                                {
                                                                                    FirebaseDatabase.getInstance().getReference().child("AllTeam").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (snapshot.exists())
                                                                                            {
                                                                                                final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                                                                                objectHashMap1.put("Address", Objects.requireNonNull(snapshot.child("Address").getValue()).toString());
                                                                                                objectHashMap1.put("EntryId",Objects.requireNonNull(snapshot.child("EntryId").getValue()).toString());
                                                                                                objectHashMap1.put("Picture",Objects.requireNonNull(snapshot.child("Picture").getValue()).toString());
                                                                                                objectHashMap1.put("TeamName",Objects.requireNonNull(snapshot.child("TeamName").getValue()).toString());
                                                                                                objectHashMap1.put("Captain",Objects.requireNonNull(snapshot.child("Captain").getValue()).toString());
                                                                                                objectHashMap1.put("CaptainPhone",Objects.requireNonNull(snapshot.child("CaptainPhone").getValue()).toString());
                                                                                                objectHashMap1.put("Sport",Objects.requireNonNull(snapshot.child("Sport").getValue()).toString());
                                                                                                objectHashMap1.put("Level",Objects.requireNonNull(snapshot.child("Level").getValue()).toString());
                                                                                                objectHashMap1.put("District",Objects.requireNonNull(snapshot.child("District").getValue()).toString());
                                                                                                objectHashMap1.put("State",Objects.requireNonNull(snapshot.child("State").getValue()).toString());
                                                                                                objectHashMap1.put("Status","Pending");
                                                                                                objectHashMap1.put("Date",date);
                                                                                                objectHashMap1.put("Time",time);
                                                                                                objectHashMap1.put("Area",add);
                                                                                                objectHashMap1.put("Place",place);


                                                                                                FirebaseDatabase.getInstance().getReference().child("ChallengeSender").child(EntryId).child(id).updateChildren(objectHashMap1)
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful())
                                                                                                                {
                                                                                                                    final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                                                                                    objectHashMap.put("Address",Address);
                                                                                                                    objectHashMap.put("EntryId",EntryId);
                                                                                                                    objectHashMap.put("Picture",Picture);
                                                                                                                    objectHashMap.put("TeamName",TeamName);
                                                                                                                    objectHashMap.put("Captain",Captain);
                                                                                                                    objectHashMap.put("CaptainPhone",CaptainPhone);
                                                                                                                    objectHashMap.put("Sport",Sport);
                                                                                                                    objectHashMap.put("Level",Level);
                                                                                                                    objectHashMap.put("District",District);
                                                                                                                    objectHashMap.put("State",State);
                                                                                                                    objectHashMap.put("Status","Pending");
                                                                                                                    objectHashMap.put("Date",date);
                                                                                                                    objectHashMap.put("Time",time);
                                                                                                                    objectHashMap.put("Area",add);
                                                                                                                    objectHashMap.put("Place",place);

                                                                                                                    FirebaseDatabase.getInstance().getReference().child("ChallengeReceiver").child(id).child(EntryId).updateChildren(objectHashMap)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                    loadingBar.dismiss();
                                                                                                                                    Toast.makeText(SendChallengeActivity.this, "Your request has been sent successfully!", Toast.LENGTH_SHORT).show();


                                                                                                                                }
                                                                                                                            });
                                                                                                                }

                                                                                                            }
                                                                                                        });

                                                                                            }

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });

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
                                                            if (Objects.equals(snapshot.child("Status").getValue(), "Rejected"))
                                                            {
                                                                FirebaseDatabase.getInstance().getReference().child("ChallengeSender").child(EntryId).child(id).child("Status").setValue("Pending")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful())
                                                                                {

                                                                                    final HashMap<String, Object> objectHashMap = new HashMap<>();

                                                                                    objectHashMap.put("Address",Address);
                                                                                    objectHashMap.put("EntryId",EntryId);
                                                                                    objectHashMap.put("Picture",Picture);
                                                                                    objectHashMap.put("TeamName",TeamName);
                                                                                    objectHashMap.put("Captain",Captain);
                                                                                    objectHashMap.put("CaptainPhone",CaptainPhone);
                                                                                    objectHashMap.put("Sport",Sport);
                                                                                    objectHashMap.put("Level",Level);
                                                                                    objectHashMap.put("District",District);
                                                                                    objectHashMap.put("State",State);
                                                                                    objectHashMap.put("Status","Pending");

                                                                                    FirebaseDatabase.getInstance().getReference().child("ChallengeReceiver").child(id).child(EntryId).updateChildren(objectHashMap)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    loadingBar.dismiss();
                                                                                                    Toast.makeText(SendChallengeActivity.this, "Your request has been sent once more!", Toast.LENGTH_SHORT).show();

                                                                                                }
                                                                                            });
                                                                                }

                                                                            }
                                                                        });


                                                            }
                                                            else if (Objects.equals(snapshot.child("Status").getValue(), "Accepted"))
                                                            {
                                                                loadingBar.dismiss();
                                                                Toast.makeText(SendChallengeActivity.this, "Your request has been already accepted!", Toast.LENGTH_SHORT).show();

                                                            }
                                                            else
                                                            {
                                                                loadingBar.dismiss();
                                                                Toast.makeText(SendChallengeActivity.this, "Your request has been already sent!", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(SendChallengeActivity.this, "You are already a member of this team!", Toast.LENGTH_SHORT).show();
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




    private void SearchTeam2() {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(allTeamRef, AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllTeamViewHolder holder, int position, @NonNull AllTeam model) {
                holder.setIsRecyclable(false);
                if (model.getSport().equals(Paper.book().read("PrimarySport")) && !model.getCaptainPhone().equals(Paper.book().read("Phone")))
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

                    holder.cancelBtn.setText("Team History");
                    holder.cancelBtn.setBackgroundColor(Color.parseColor("#388E3C"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);

                    holder.level.setText(model.getLevel());

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
                    // holder.captainPhone.setText(ProperCase.properCase(model.getCaptainPhone()));
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

                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SendChallengeActivity.this,AddMatchResultActivity.class);
                            intent.putExtra("Team_1_Id",model.getEntryId());
                            intent.putExtra("Team_2_Id","");
                            startActivity(intent);
                        }
                    });

                    holder.requestBtn.setText("Send Challenge Request");

                    holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.linearLayout.setVisibility(View.GONE);
                            EntryId = model.getEntryId();
                            Address = model.getAddress();
                            Picture = model.getPicture();
                            TeamName = model.getTeamName();
                            Captain = model.getCaptain();
                            CaptainPhone = model.getCaptainPhone();
                            Sport = model.getSport();
                            Level=model.getLevel();
                            District = model.getDistrict();
                            State = model.getState();
                            openDialog();
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
            public AllTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_team_model, parent,false);
                return  new AllTeamViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}