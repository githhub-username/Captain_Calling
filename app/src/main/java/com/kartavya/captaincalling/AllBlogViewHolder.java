package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kartavya.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllBlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name,title,description,datetime,status;
    public CircleImageView circleImageView;
    public ImageView imageView;
    public CardView blogLayout;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AllBlogViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.blog_name);
        title = itemView.findViewById(R.id.blog_title);
        description = itemView.findViewById(R.id.blog_des);
        datetime = itemView.findViewById(R.id.blog_date);
        circleImageView = itemView.findViewById(R.id.blog_profile_pic);
        imageView = itemView.findViewById(R.id.blog_pic);
//        status = itemView.findViewById(R.id.blog_status);
        blogLayout = itemView.findViewById(R.id.blog_layout);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
