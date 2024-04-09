package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class ExploreTournamentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tournamentName, tournamentDate, tournamentState, tournamentDistrict, tournamentTeams, tournamentOrganiser, tournamentAddress, joinTournament, viewTeams, inviteTeams;
    public ImageView tournamentBanner;
    public LinearLayout tournamentLayout, tournamentCard;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ExploreTournamentsViewHolder(@NonNull View itemView) {
        super(itemView);

        tournamentName = itemView.findViewById(R.id.tournament_name);
        tournamentDate = itemView.findViewById(R.id.tournament_date);
        tournamentBanner = itemView.findViewById(R.id.tournament_pic);
        tournamentState = itemView.findViewById(R.id.tournament_state_expandable);
        tournamentDistrict = itemView.findViewById(R.id.district_tournament_expandable);
        tournamentTeams = itemView.findViewById(R.id.total_teams_expandable);
        tournamentAddress = itemView.findViewById(R.id.address_tournament_expandable);
        tournamentOrganiser = itemView.findViewById(R.id.tournament_organiser_expandable);
        tournamentLayout = itemView.findViewById(R.id.tournament_layout_expandable);
        tournamentCard = itemView.findViewById(R.id.tournament_layout);
        joinTournament = itemView.findViewById(R.id.send_join_request);
        viewTeams = itemView.findViewById(R.id.view_tournament_teams);
        inviteTeams = itemView.findViewById(R.id.invite_teams);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (itemClickListener != null) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}