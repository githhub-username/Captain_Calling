package com.kartavya.captaincalling;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView dateIn,nameIn,msgIn,nameOut,msgOut,dateOut;
    public LinearLayout linearLayoutIn,linearLayoutOut;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        dateIn = itemView.findViewById(R.id.date_time_chat);
        dateOut = itemView.findViewById(R.id.date_time_chat2);
        nameIn = itemView.findViewById(R.id.name_in);
        msgIn = itemView.findViewById(R.id.msg_in);
        nameOut = itemView.findViewById(R.id.name_out);
        msgOut = itemView.findViewById(R.id.msg_out);
        linearLayoutIn = itemView.findViewById(R.id.incoming_lyt);
        linearLayoutOut = itemView.findViewById(R.id.outgoing_lyt);


        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
