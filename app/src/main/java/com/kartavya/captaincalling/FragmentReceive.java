package com.kartavya.captaincalling;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import java.util.Objects;

import io.paperdb.Paper;

public class FragmentReceive extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder> adapter;
    private ProgressBar progressBar;
    private ProgressDialog loadingBar;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__receive, container,false);

        recyclerView = rootView.findViewById(R.id.recycler_receive_fg);
        progressBar = rootView.findViewById(R.id.progress_receive_fg);

        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //assert getArguments() != null;
        ChallengeActivity activity = (ChallengeActivity) getActivity();
        assert activity != null;
        String id = activity.getMyData();
        name = activity.getMyData2();


        LoadData(id);

        return rootView;
    }

    private void LoadData(String id) {
        FirebaseRecyclerOptions<AllTeam> options =
                new FirebaseRecyclerOptions.Builder<AllTeam>()
                        .setQuery(FirebaseDatabase.getInstance()
                                .getReference("ChallengeSender")
                                .child(id), AllTeam.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllTeam, AllTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllTeamViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AllTeam model) {

                progressBar.setVisibility(View.VISIBLE);

                //holder.setIsRecyclable(false);

                Glide.with(getActivity()).load(model.getPicture()).into(holder.circleImageView);
                holder.address.setText(ProperCase.properCase(model.getAddress()));
                holder.diss.setText(ProperCase.properCase(model.getDistrict()));

                if (model.getStatus().equals("Pending"))
                {
                    holder.level.setTextColor(Color.parseColor("#FF0000"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.requestBtn.setText("Accept");
                    holder.requestBtn.setBackgroundColor(Color.parseColor("#000000"));
                }
                else if (model.getStatus().equals("Rejected"))
                {
                    holder.level.setTextColor(Color.parseColor("#FF0000"));
                    holder.requestBtn.setVisibility(View.GONE);
                }
                else
                {
                    holder.level.setTextColor(Color.parseColor("#388E3C"));
                    holder.cancelBtn.setText("Cancel");
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.requestBtn.setText("Add Result");
                    holder.requestBtn.setBackgroundColor(Color.parseColor("#000000"));
                    holder.chatBtn.setVisibility(View.VISIBLE);
                }

                holder.chatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatCaptainActivity.class);
                        intent.putExtra("Id",id+model.getEntryId());
                        intent.putExtra("CaptainPhone",model.getCaptainPhone());
                        intent.putExtra("TeamName",name);
                        startActivity(intent);
                    }
                });

                holder.level.setText(model.getStatus());

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


                holder.level2.setText(model.getLevel());
                holder.ly_level.setVisibility(View.VISIBLE);

                holder.teamName.setText(ProperCase.properCase(ProperCase.properCase(model.getTeamName())));
                holder.captain.setText(ProperCase.properCase(ProperCase.properCase(model.getCaptain())));
                //holder.captainPhone.setText(ProperCase.properCase(model.getCaptainPhone()));
                holder.sport.setText(ProperCase.properCase(model.getSport()));
                holder.state.setText(ProperCase.properCase(ProperCase.properCase(model.getState())));

                holder.area.setText(ProperCase.properCase(model.getArea()));
                holder.place.setText(ProperCase.properCase(model.getPlace()));
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());

                holder.timeLyt.setVisibility(View.VISIBLE);
                holder.dateLyt.setVisibility(View.VISIBLE);
                holder.placeLyt.setVisibility(View.VISIBLE);
                holder.areLyt.setVisibility(View.VISIBLE);
                holder.lyt_match.setVisibility(View.VISIBLE);




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

                holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (holder.requestBtn.getText().equals("Accept"))
                        {
                            holder.linearLayout.setVisibility(View.GONE);
                            loadingBar.show();

                            FirebaseDatabase.getInstance().getReference("ChallengeSender").child(id)
                                    .child(model.getEntryId()).child("Status").setValue("Accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        FirebaseDatabase.getInstance().getReference("ChallengeReceiver").child(model.getEntryId()).child(id)
                                                .child("Status").setValue("Accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {

                                                    FirebaseDatabase.getInstance().getReference("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            if (snapshot2.exists())
                                                            {
                                                                String token = Objects.requireNonNull(snapshot2.child(model.getCaptainPhone()).getValue()).toString();
                                                                String msg = "You challenge is accepted by "+ProperCase.properCase(name);
                                                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,"Challenge Accepted!",msg,getContext(),getActivity());
                                                                notificationsSender.SendNotifications();


                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                                    }

                                }
                            });

                        }
                        else
                        {
                            Intent intent = new Intent(getActivity(),AddMatchResultActivity.class);
                            intent.putExtra("Team_1_Id",id);
                            intent.putExtra("Team_2_Id",model.getEntryId());
                            intent.putExtra("TeamName",model.getTeamName());
                            startActivity(intent);
                        }


                    }
                });

                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.linearLayout.setVisibility(View.GONE);
                        loadingBar.show();

                        FirebaseDatabase.getInstance().getReference("ChallengeReceiver").child(model.getEntryId())
                                .child(id).child("Status").setValue("Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        adapter.notifyDataSetChanged();

                                        if (adapter!=null)
                                        {
                                            adapter=null;
                                            LoadData(id);
                                            loadingBar.dismiss();

                                            FirebaseDatabase.getInstance().getReference("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                    if (snapshot2.exists())
                                                    {
                                                        String token = Objects.requireNonNull(snapshot2.child(model.getCaptainPhone()).getValue()).toString();
                                                        String msg = "You challenge is rejected by "+ProperCase.properCase(name);
                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,"Challenge Rejected!",msg,getContext(),getActivity());
                                                        notificationsSender.SendNotifications();


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
                        });

                    }
                });

                holder.itemView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
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