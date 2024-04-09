package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.messaging.RemoteMessage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class TeamRoomActivity extends AppCompatActivity {

    private String id,name,sport,pic;
    private CircleImageView circleImageView;
    private TextView textViewName;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private DatabaseReference paymentRef;
    private FirebaseRecyclerAdapter<Chats, ChatsViewHolder> adapter;
    private ImageView sendBtn;
    private EditText editText;
    private ProgressBar progressBar;


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
        setContentView(R.layout.activity_team_room);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.bg_test));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_test));
        }

        textViewName = findViewById(R.id.room_team_name);
        circleImageView = findViewById(R.id.room_team_pic);
        linearLayout = findViewById(R.id.mknjbjvhvhcgc);

        recyclerView = findViewById(R.id.recycler_room);
        progressBar = findViewById(R.id.progress_room);

        editText = findViewById(R.id.edit_room);
        sendBtn = findViewById(R.id.send_msg_btn);

        ImageView backBtn = findViewById(R.id.back_teamroom);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
            name = getIntent().getStringExtra("Team_Name");
            sport = getIntent().getStringExtra("Team_Sport");
            pic = getIntent().getStringExtra("Team_Pic");

            Glide.with(getApplicationContext()).load(pic).into(circleImageView);
            textViewName.setText(name);

            paymentRef = FirebaseDatabase.getInstance().getReference("Chats").child(id);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);


            LoadData();


        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeamRoomActivity.this,TeamInfoActivity.class);
                intent.putExtra("Team_Id", id);
                intent.putExtra("Team_Sport", sport);
                intent.putExtra("Team_Name", name);
                startActivity(intent);
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if (!msg.equals(""))
                {

                    editText.setText(null);
                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddHHmmssMs");
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    String datetime2 = ft.format(dNow);

                    final HashMap<String, Object> objectHashMap11 = new HashMap<>();

                    objectHashMap11.put("Name",Paper.book().read("Name"));
                    objectHashMap11.put("Phone",Paper.book().read("Phone"));
                    objectHashMap11.put("Date",currentDate.format(dNow));
                    objectHashMap11.put("Message",msg);

                    final DatabaseReference ChatsRef;
                    ChatsRef = FirebaseDatabase.getInstance().getReference().child("Chats");


                    ChatsRef.child(id).child(datetime2).updateChildren(objectHashMap11).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                //editText.setText(null);
                                recyclerView.scrollToPosition(adapter.getItemCount()-1);

                                String title = Paper.book().read("Name");

                                FirebaseMessaging.getInstance().unsubscribeFromTopic(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender( "/topics/"+id+"",title,msg,getApplicationContext(),TeamRoomActivity.this);
                                        notificationsSender.SendNotifications();
                                    }
                                });





                            }

                        }
                    });






                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseMessaging.getInstance().subscribeToTopic(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseMessaging.getInstance().subscribeToTopic(id);
    }

    private void LoadData() {

        FirebaseRecyclerOptions<Chats> options =
                new FirebaseRecyclerOptions.Builder<Chats>()
                        .setQuery(paymentRef, Chats.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder,final int position, @NonNull Chats model) {

                holder.setIsRecyclable(false);


                if (model.getPhone().equals(Paper.book().read("Phone")))
                {
                    holder.linearLayoutOut.setVisibility(View.VISIBLE);
                    holder.dateOut.setVisibility(View.VISIBLE);
                    holder.dateOut.setText(model.getDate());
                    holder.nameOut.setVisibility(View.GONE);
                    holder.msgOut.setText(model.getMessage());

                }
                else
                    {
                        holder.linearLayoutIn.setVisibility(View.VISIBLE);
                        holder.dateIn.setVisibility(View.VISIBLE);
                        holder.dateIn.setText(model.getDate());
                        holder.nameIn.setText(ProperCase.properCase(model.getName()));
                        holder.msgIn.setText(model.getMessage());
                    }


                holder.setItemClickListener((view, position1, isLongClick) -> {

                });

                progressBar.setVisibility(View.INVISIBLE);

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_model, parent,false);
                return  new ChatsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}