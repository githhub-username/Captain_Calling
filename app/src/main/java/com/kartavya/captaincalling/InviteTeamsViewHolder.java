package com.kartavya.captaincalling;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class InviteTeamsViewHolder extends RecyclerView.ViewHolder{

    public TextView address,captain,captainPhone,diss,level,sport,state,teamName,requestBtn,cancelBtn,level2,area,place,time,date,chatBtn, inviteTeams;
    public ImageView circleImageView;
    public LinearLayout linearLayout,ly_level,dateLyt,timeLyt,placeLyt,areLyt,lyt_match;
    public RelativeLayout relativeLayout, inviteTeamsLayout;

    DatabaseReference sendInvitationRef, addToTournamentRef;

    HashMap<String, Object> addRequestHashmap = new HashMap<>();
    HashMap<String, Object> addRequestHashmap2 = new HashMap<>();


    public InviteTeamsViewHolder(@NonNull View itemView) {
        super(itemView);
        address = itemView.findViewById(R.id.address_add);
        captain = itemView.findViewById(R.id.primary_addat);
        captainPhone = itemView.findViewById(R.id.secondary_addat);
        diss = itemView.findViewById(R.id.district_addat);
        level = itemView.findViewById(R.id.team_levelat);
        sport = itemView.findViewById(R.id.phone_addat);
        state = itemView.findViewById(R.id.state_addat);
        teamName = itemView.findViewById(R.id.player_name_addat);
        circleImageView = itemView.findViewById(R.id.team_pic_add_at);
        linearLayout = itemView.findViewById(R.id.ly_addat);
        relativeLayout = itemView.findViewById(R.id.mkbjvsxsxsxhchchchc);
        requestBtn = itemView.findViewById(R.id.add_btn_at);
        level2 = itemView.findViewById(R.id.team_level2);
        ly_level = itemView.findViewById(R.id.ly_team_Level);
        cancelBtn = itemView.findViewById(R.id.cancel_btn_at);
        dateLyt = itemView.findViewById(R.id.date_lyt);
        placeLyt = itemView.findViewById(R.id.place_lyt);
        timeLyt = itemView.findViewById(R.id.time_lyt);
        areLyt = itemView.findViewById(R.id.area_lyt);
        chatBtn = itemView.findViewById(R.id.chat_btn_at);

        date = itemView.findViewById(R.id.datejdjd);
        time = itemView.findViewById(R.id.timenfjfb);
        place = itemView.findViewById(R.id.place_jsd);
        area = itemView.findViewById(R.id.area_kar);

        inviteTeams = itemView.findViewById(R.id.invite_text);
        inviteTeamsLayout = itemView.findViewById(R.id.invite_team_layout);

        lyt_match = itemView.findViewById(R.id.lyt_match_info);

        itemView.setVisibility(View.GONE);

        inviteTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String teamNameText = teamName.getText().toString();
                String captainText = captain.getText().toString();
                String addressText = address.getText().toString();
                String sportNameText = sport.getText().toString();
                String districtNameText = diss.getText().toString();

                String tournamentKey = Paper.book().read("TournamentKey");

                if (tournamentKey != null && !tournamentKey.isEmpty()) {
                    // Use the tournament key to construct the database reference
                    sendInvitationRef = FirebaseDatabase.getInstance().getReference()
                            .child("tournaments")
                            .child(tournamentKey)
                            .child("Requests")
                            .child("Sent");

                    addToTournamentRef = FirebaseDatabase.getInstance().getReference()
                            .child("tournaments")
                            .child(tournamentKey)
                            .child("Teams");

                } else {
                    // Handle the case where tournament key is null or empty
                    Toast.makeText(itemView.getContext(), "Tournament key is null or empty", Toast.LENGTH_SHORT).show();
                }

                // Create HashMap to store join request data
                addRequestHashmap.put("TeamName", teamNameText);
                addRequestHashmap.put("TeamCaptainName", captainText);
                addRequestHashmap.put("TeamDistrict", districtNameText);
                addRequestHashmap.put("TeamAddress", addressText);
                addRequestHashmap.put("TeamSport", sportNameText);

                addRequestHashmap2.put("ParticipatingTeamName", teamNameText);
                addRequestHashmap2.put("ParticipatingTeamCaptainName", captainText);

                // Check if database reference is not null and then proceed with adding join request
                if (sendInvitationRef != null) {
                    sendInvitationRef.child(teamNameText).setValue(addRequestHashmap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Handle successful join request
                                    Toast.makeText(itemView.getContext(), "Request sent successfully", Toast.LENGTH_SHORT).show();

                                    // Send notification when request is sent successfully
                                    // sendNotification(joinTournamentTeamName, joinTournamentTeamCaptainName);

                                    // Optionally, finish the activity or perform other actions upon success
                                } else {
                                    // Handle failed join request
                                    Toast.makeText(itemView.getContext(), "Failed to send request", Toast.LENGTH_SHORT).show();
                                    Log.e("JoinTournamentActivity", "Failed to send request", task.getException());
                                }
                            });
                } else {
                    // Handle the case where joinTournamentRef is null
                    Toast.makeText(itemView.getContext(), "Database reference is null", Toast.LENGTH_SHORT).show();
                    Log.e("JoinTournamentActivity", "Database reference is null");
                }

                if(addToTournamentRef != null) {
                    addToTournamentRef.child(teamNameText).setValue(addRequestHashmap2)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(itemView.getContext(), "Team joined Tournaments", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(itemView.getContext(), "Some error", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(itemView.getContext(), "Database 2 reference is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
