package com.kartavya.captaincalling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ViewTournamentTeamsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView backButton;
    DatabaseReference viewTeamsRef;
    FirebaseRecyclerAdapter<ViewTournamentTeams, ViewTournamentTeamsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_tournament_teams);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.back_view_teams);
        recyclerView = findViewById(R.id.view_teams_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String tournamentKey = Paper.book().read("TournamentKey");
        if (tournamentKey != null) {
            viewTeamsRef = FirebaseDatabase.getInstance().getReference()
                    .child("tournaments")
                    .child(tournamentKey)
                    .child("Teams");
        } else {
            // Handle the case where "TournamentKey" is null
        }
        loadTeams();
    }

    private void loadTeams() {
        String tournamentKey = Paper.book().read("TournamentKey");
        if (tournamentKey != null) {
            DatabaseReference viewTeamsRef = FirebaseDatabase.getInstance().getReference()
                    .child("tournaments")
                    .child(tournamentKey)
                    .child("Teams");

            FirebaseRecyclerOptions<ViewTournamentTeams> options = new FirebaseRecyclerOptions.Builder<ViewTournamentTeams>()
                    .setQuery(viewTeamsRef, ViewTournamentTeams.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<ViewTournamentTeams, ViewTournamentTeamsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewTournamentTeamsViewHolder viewTournamentTeamsViewHolder, int i, @NonNull ViewTournamentTeams viewTournamentTeams) {
                    String teamKey = getRef(i).getKey();
                    assert teamKey != null;

                    DatabaseReference viewTeamsRef2 = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentKey).child("Teams").child(teamKey);

                    viewTeamsRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String teamName = dataSnapshot.child("ParticipatingTeamName").getValue(String.class);
                                String captainName = dataSnapshot.child("ParticipatingTeamCaptainName").getValue(String.class);

                                viewTournamentTeamsViewHolder.viewTournamentTeamName.setText(teamName);
                                viewTournamentTeamsViewHolder.viewTournamentTeamCaptainName.setText(captainName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public ViewTournamentTeamsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_tournament_teams_model, viewGroup, false);
                    return new ViewTournamentTeamsViewHolder(view);
                }
            };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        } else {
            // Handle the case where "TournamentKey" is null
        }
    }

}