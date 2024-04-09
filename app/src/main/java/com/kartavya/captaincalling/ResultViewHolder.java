package com.kartavya.captaincalling;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView des,date,status,title;
    public ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ResultViewHolder(@NonNull View itemView) {
        super(itemView);


        date = itemView.findViewById(R.id.date_pay_noti);
        des = itemView.findViewById(R.id.descr_model_noti);
        status = itemView.findViewById(R.id.bjvhycycytc);
        title = itemView.findViewById(R.id.title_model_noti);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
