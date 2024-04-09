package com.kartavya.captaincalling;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class TournamentTeamsFragment extends Fragment {
    TextView noDataTextView;
    LinearLayout linearLayout;
    RecyclerView recyclerView;
    DatabaseReference tournamentTeamRef;
    FirebaseRecyclerAdapter<ViewTournamentTeams, ViewTournamentTeamsViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_teams, container, false);

        linearLayout = view.findViewById(R.id.see_join_requests);
        recyclerView = view.findViewById(R.id.tournament_teams_recycler_view);
        noDataTextView = view.findViewById(R.id.no_data_tv);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), JoinTournamentRequestsActivity.class));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        // Read the tournament key using Paper.
        String tournamentKey = Paper.book().read("TournamentKey");
        if (tournamentKey == null || tournamentKey.isEmpty()) {
            // If the tournament key is null or empty, display an error message.
            noDataTextView.setText("No tournament key found.");
            noDataTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // If a valid tournament key is found, initialize the DatabaseReference and call loadTeams().
            tournamentTeamRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentKey).child("Teams");
            loadTeams(); // Move this inside the else block
        }

        return view;
    }
    public void loadTeams(){

        FirebaseRecyclerOptions<ViewTournamentTeams> options = new FirebaseRecyclerOptions.Builder<ViewTournamentTeams>()
                .setQuery(tournamentTeamRef, ViewTournamentTeams.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ViewTournamentTeams, ViewTournamentTeamsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewTournamentTeamsViewHolder viewTournamentTeamsViewHolder, int i, @NonNull ViewTournamentTeams viewTournamentTeams) {
                String teamKey = getRef(i).getKey();
                assert teamKey != null;

                DatabaseReference viewTeamsRef2 = FirebaseDatabase.getInstance().getReference().child("tournaments").child(Paper.book().read("TournamentKey")).child("Teams").child(teamKey);

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

                /*linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Choose option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    dialogInterface.dismiss();
//                                    loadingBar.show();
                                    adapter.getRef(i).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                            loadingBar.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                });*/
            }

            @NonNull
            @Override
            public ViewTournamentTeamsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.view_tournament_teams_model, viewGroup, false);
                return new ViewTournamentTeamsViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(getItemCount() == 0){
                    recyclerView.setVisibility(View.GONE);
                    noDataTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataTextView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}