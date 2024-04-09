package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TournamentActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TournamentFragmentAdapter tournamentFragmentAdapter;
    private ImageView backButton;
    private FloatingActionButton createTournament;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        tabLayout = findViewById(R.id.tab_layout_tournament);
        viewPager = findViewById(R.id.view_page_tournament);
        createTournament = findViewById(R.id.host_tournament);
        backButton = findViewById(R.id.back_tournament);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        createTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TournamentActivity.this, CreateTournamentActivity.class));
            }
        });

        tournamentFragmentAdapter = new TournamentFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(tournamentFragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Explore"));
        tabLayout.addTab(tabLayout.newTab().setText("Manage"));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Explore");
                    break;
                case 1:
                    tab.setText("Manage");
                    break;
            }
                }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*
        tournamentRecyclerView = findViewById(R.id.tournament_recycler_view);
        tournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tournamentRecyclerView.setLayoutManager(linearLayoutManager);
        tournamentRecyclerView.setHasFixedSize(true);

        loadTournaments();
    }

    private void loadTournaments() {
        FirebaseRecyclerOptions<Tournaments> options =
                new FirebaseRecyclerOptions.Builder<Tournaments>()
                        .setQuery(tournamentRef, Tournaments.class)
                        .build();

        tournamentAdapter = new FirebaseRecyclerAdapter<Tournaments, TournamentsViewHolder>(options) {

            @NonNull
            @Override
            public TournamentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_model,parent, false);
                return new TournamentsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull TournamentsViewHolder holder, int position, @NonNull Tournaments model) {
                String tournamentId = getRef(position).getKey();
                assert tournamentId != null;

                DatabaseReference tournamentRef2 = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentId);

                tournamentRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("TournamentName").getValue(String.class);
                            String date = snapshot.child("TournamentDate").getValue(String.class);
                            String picture = snapshot.child("TournamentBanner").getValue(String.class);
                            String state = snapshot.child("TournamentState").getValue(String.class);
                            String district = snapshot.child("TournamentDistrict").getValue(String.class);
                            String organiser = snapshot.child("OrganiserName").getValue(String.class);
                            String teams = snapshot.child("TournamentTeams").getValue(String.class);
                            String address = snapshot.child("TournamentAddress").getValue(String.class);

                            Glide.with(getApplicationContext()).load(picture).into(holder.tournamentBanner);
                            holder.tournamentName.setText(name);
                            holder.tournamentDate.setText(date);
                            holder.tournamentState.setText(state);
                            holder.tournamentDistrict.setText(district);
                            holder.tournamentOrganiser.setText(organiser);
                            holder.tournamentTeams.setText(teams);
                            holder.tournamentAddress.setText(address);

                            holder.tournamentCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(holder.tournamentLayout.getVisibility() == View.VISIBLE){
                                        holder.tournamentLayout.setVisibility(View.GONE);
                                    } else{
                                        holder.tournamentLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            holder.joinTournament.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(TournamentActivity.this, JoinTournamentActivity.class));
                                }
                            });

                            Log.d("Tournament Name","Name "+name);
                        } else {
                            Log.e("TAG", "Tournament does not exist for tournamentId: " + tournamentId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        tournamentRecyclerView.setAdapter(tournamentAdapter);
        tournamentAdapter.startListening();
    }*/
    }
}