package com.kartavya.captaincalling;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class JoinTournamentRequestsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView backButton;
    FirebaseRecyclerAdapter<JoinTournamentRequests, JoinTournamentRequestsViewHolder> adapter;
    DatabaseReference joinRequestsRef;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_tournament_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Paper.init(this);

        backButton = findViewById(R.id.back_tournament_request);
        recyclerView = findViewById(R.id.join_request_recycler_view);
        floatingActionButton = findViewById(R.id.send_request);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JoinTournamentRequestsActivity.this, SendTournamentRequestsActivity.class));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        String tournamentKey = Paper.book().read("TournamentKey");
        if (tournamentKey == null) {
            // Handle the case where the tournament key is null, e.g., show an error message.
            Log.e("JoinTournamentRequests", "Tournament key is null");
            return; // Exit the method to avoid further execution
        }

        joinRequestsRef = FirebaseDatabase.getInstance().getReference()
                .child("tournaments")
                .child(tournamentKey)
                .child("Requests")
                .child("Received");
        loadRequests();
    }

    public void loadRequests(){
        FirebaseRecyclerOptions<JoinTournamentRequests> options = new FirebaseRecyclerOptions.Builder<JoinTournamentRequests>()
                .setQuery(joinRequestsRef, JoinTournamentRequests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<JoinTournamentRequests, JoinTournamentRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JoinTournamentRequestsViewHolder joinTournamentRequestsViewHolder, int i, @NonNull JoinTournamentRequests joinTournamentRequests) {
                String requestTeamName = getRef(i).getKey();
                assert requestTeamName != null;

                DatabaseReference joinRequestRef2 = FirebaseDatabase.getInstance().getReference()
                        .child("tournaments")
                        .child(Paper.book().read("TournamentKey"))
                        .child("Requests")
                        .child("Received")
                        .child(requestTeamName);

                joinRequestRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String teamName = dataSnapshot.child("JoinTournamentTeamName").getValue(String.class);
                            String teamCaptainName = dataSnapshot.child("JoinTournamentTeamCaptainName").getValue(String.class);

                            assert teamName != null;
                            assert teamCaptainName != null;

                            Log.d("Team Name",teamName);
                            Log.d("Team Captain", teamCaptainName);

                            joinTournamentRequestsViewHolder.tournamentTeamName.setText(teamName);
                            joinTournamentRequestsViewHolder.tournamentTeamCaptainName.setText(teamCaptainName);

                            joinTournamentRequestsViewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final HashMap<String, Object> updateRequestsHashmap = new HashMap<>();

                                    updateRequestsHashmap.put("JoinTournamentTeamName",teamName);
                                    updateRequestsHashmap.put("JoinTournamentTeamCaptainName",teamCaptainName);
                                    updateRequestsHashmap.put("JoinStatus","Accepted");

                                    joinRequestRef2.updateChildren(updateRequestsHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(JoinTournamentRequestsActivity.this, "Request Accepted, " + teamName + " is now a part of the Tournament", Toast.LENGTH_SHORT).show();

                                            joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.GONE);
                                            joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                            joinTournamentRequestsViewHolder.requestStatus.setText("Accepted");
                                            joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#00BF00"));

                                            DatabaseReference updateTournamentTeamsRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(Paper.book().read("TournamentKey")).child("Teams").child(requestTeamName);

                                            final HashMap<String, Object> updateTeamRequests = new HashMap<>();

                                            updateTeamRequests.put("ParticipatingTeamName",teamName);
                                            updateTeamRequests.put("ParticipatingTeamCaptainName",teamCaptainName);

                                            updateTournamentTeamsRef.setValue(updateTeamRequests)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    });
                                }
                            });

                            joinTournamentRequestsViewHolder.declineRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final HashMap<String, Object> updateRequestsHashmap = new HashMap<>();

                                    updateRequestsHashmap.put("JoinTournamentTeamName",teamName);
                                    updateRequestsHashmap.put("JoinTournamentTeamCaptainName",teamCaptainName);
                                    updateRequestsHashmap.put("JoinStatus","Rejected");

                                    Toast.makeText(JoinTournamentRequestsActivity.this, "Request Declined", Toast.LENGTH_SHORT).show();

                                    joinRequestRef2.updateChildren(updateRequestsHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(JoinTournamentRequestsActivity.this, "Request Accepted, " + teamName + " is now a part of the Tournament", Toast.LENGTH_SHORT).show();

                                            joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.GONE);
                                            joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                            joinTournamentRequestsViewHolder.requestStatus.setText("Rejected");
                                            joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#FF0000"));
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public JoinTournamentRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.join_tournament_request_model, viewGroup, false);
                return new JoinTournamentRequestsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}