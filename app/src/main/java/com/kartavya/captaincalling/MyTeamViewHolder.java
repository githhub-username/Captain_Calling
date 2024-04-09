package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTeamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView title,sport;
    public ItemClickListener itemClickListener;
    public CircleImageView circleImageView;
    public ImageView openChatView;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MyTeamViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.model_team_name);
        sport = itemView.findViewById(R.id.model_team_sport);
        openChatView = itemView.findViewById(R.id.chat);
        circleImageView = itemView.findViewById(R.id.model_team_pic);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
