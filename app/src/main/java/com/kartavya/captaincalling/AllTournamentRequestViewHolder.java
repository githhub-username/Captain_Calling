package com.kartavya.captaincalling;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class AllTournamentRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ItemClickListener itemClickListener;
    public LinearLayout statusDecisionLayout;
    public TextView tournamentTeamName, tournamentTeamCaptainName, requestStatus;
    public ImageView acceptRequest, declineRequest;


    public AllTournamentRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        tournamentTeamName = itemView.findViewById(R.id.tournament_team_name);
        tournamentTeamCaptainName = itemView.findViewById(R.id.tournament_team_captain);
        acceptRequest = itemView.findViewById(R.id.accept_request);
        declineRequest = itemView.findViewById(R.id.decline_request);
        statusDecisionLayout = itemView.findViewById(R.id.request_decision);
        requestStatus = itemView.findViewById(R.id.request_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener1){
        this.itemClickListener = itemClickListener1;
    }

    @Override
    public void onClick(View view) {
        if(itemClickListener!=null){
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
