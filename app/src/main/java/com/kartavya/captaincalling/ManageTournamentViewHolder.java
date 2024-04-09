package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class ManageTournamentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ItemClickListener itemClickListener;
    public ImageView manageTournamentBanner;
    public TextView manageTournamentName, manageTournamentButton;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ManageTournamentViewHolder(@NonNull View itemView) {
        super(itemView);

        manageTournamentBanner = itemView.findViewById(R.id.tournament_pic);
        manageTournamentName = itemView.findViewById(R.id.tournament_name);
        manageTournamentButton = itemView.findViewById(R.id.manage_tournament_button);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(itemClickListener!=null){
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
