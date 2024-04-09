package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name,phone;
    public ItemClickListener itemClickListener;
    public CircleImageView circleImageView;
    public ImageView delete,call,captain;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.player_name_player);
        phone = itemView.findViewById(R.id.phone_player);
        circleImageView = itemView.findViewById(R.id.team_pic_player);
        delete = itemView.findViewById(R.id.delete_player);
        call = itemView.findViewById(R.id.call_player);
        captain = itemView.findViewById(R.id.captain_logo);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
