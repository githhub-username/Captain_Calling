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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class ExploreTournamentsFragment extends Fragment {
    private RecyclerView tournamentRecyclerView;
    private DatabaseReference tournamentRef;
    private FirebaseRecyclerAdapter<ExploreTournaments, ExploreTournamentsViewHolder> tournamentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore_tournaments, container, false);

        tournamentRecyclerView = rootView.findViewById(R.id.explore_tournament_recycler_view);
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
        FirebaseRecyclerOptions<ExploreTournaments> options =
                new FirebaseRecyclerOptions.Builder<ExploreTournaments>()
                        .setQuery(tournamentRef, ExploreTournaments.class)
                        .build();

        tournamentAdapter = new FirebaseRecyclerAdapter<ExploreTournaments, ExploreTournamentsViewHolder>(options) {

            @NonNull
            @Override
            public ExploreTournamentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_tournament_model,parent, false);
                return new ExploreTournamentsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull ExploreTournamentsViewHolder holder, int position, @NonNull ExploreTournaments model) {
                String tournamentId = getRef(position).getKey();
                assert tournamentId != null;

                Log.d("ExploreTournamentsFrag", "TournamentId: " + tournamentId);


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

                            Glide.with(getActivity()).load(picture).into(holder.tournamentBanner);
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
                                    // Pass the tournament key to JoinTournamentActivity
                                    Paper.book().write("TournamentKey", tournamentId);
                                    Intent intent = new Intent(getActivity(), JoinTournamentActivity.class);
                                   // intent.putExtra("TournamentKey", tournamentId);
                                    Log.d("ExploreTournamentsFragment", "Passing tournamentId to JoinTournamentActivity: " + tournamentId);
                                    startActivity(intent);
                                }
                            });
                            holder.viewTeams.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Paper.book().write("TournamentKey", tournamentId);
                                    startActivity(new Intent(getActivity(), ViewTournamentTeamsActivity.class));
                                }
                            });

                            holder.inviteTeams.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Paper.book().write("TournamentKey", tournamentId);
                                    Intent intent = new Intent(getActivity(), InviteTeamsActivity.class);
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