package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class YoutubeVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView title,description,status;
    public ImageView imageView;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public YoutubeVideoViewHolder(@NonNull View itemView) {
        super(itemView);


        title = itemView.findViewById(R.id.blog_title_yt);
        description = itemView.findViewById(R.id.blog_des_yt);
        imageView = itemView.findViewById(R.id.blog_pic_yt);
        status = itemView.findViewById(R.id.blog_status_yt);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
