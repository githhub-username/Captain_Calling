package com.kartavya.captaincalling;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ManageTournamentsFragment extends Fragment {
    private RecyclerView tournamentRecyclerView;
    private DatabaseReference tournamentRef;
    private FirebaseRecyclerAdapter<ManageTournaments, ManageTournamentViewHolder> tournamentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_tournaments, container, false);

        tournamentRecyclerView = rootView.findViewById(R.id.manage_tournament_recycler_view);
        tournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tournamentRecyclerView.setLayoutManager(linearLayoutManager);
        tournamentRecyclerView.setHasFixedSize(true);

        loadTournaments();

        return rootView;
    }

    private void loadTournaments() {
        FirebaseRecyclerOptions<ManageTournaments> options =
                new FirebaseRecyclerOptions.Builder<ManageTournaments>()
                        .setQuery(tournamentRef, ManageTournaments.class)
                        .build();

        tournamentAdapter = new FirebaseRecyclerAdapter<ManageTournaments, ManageTournamentViewHolder>(options) {

            @NonNull
            @Override
            public ManageTournamentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_tournament_model, viewGroup,false);
                return new ManageTournamentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ManageTournamentViewHolder manageTournamentViewHolder, int i, @NonNull ManageTournaments manageTournaments) {
                String tournamentId = getRef(i).getKey();
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

                            manageTournamentViewHolder.manageTournamentName.setText(name != null ? name.toUpperCase() : null);
                            Glide.with(getActivity()).load(picture).into(manageTournamentViewHolder.manageTournamentBanner);
                            manageTournamentViewHolder.manageTournamentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Paper.book().write("TourKey", tournamentId);

                                    Intent intent = new Intent(getActivity(), ManageTournamentActivity.class);
                                    intent.putExtra("tournament_name",name);
                                    intent.putExtra("tournament_date",date);
                                    intent.putExtra("tournament_state",state);
                                    intent.putExtra("tournament_district",district);
                                    intent.putExtra("tournament_address",address);
                                    intent.putExtra("tournament_teams",teams);
                                    startActivity(intent);
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
    }
}