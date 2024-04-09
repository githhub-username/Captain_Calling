package com.kartavya.captaincalling;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class ViewTournamentTeamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView viewTournamentTeamName, viewTournamentTeamCaptainName;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ViewTournamentTeamsViewHolder(@NonNull View itemView) {
        super(itemView);

        viewTournamentTeamName = itemView.findViewById(R.id.view_team_name);
        viewTournamentTeamCaptainName = itemView.findViewById(R.id.view_team_captain_name);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
