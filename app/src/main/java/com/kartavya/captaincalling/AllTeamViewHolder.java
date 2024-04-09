package com.kartavya.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllTeamViewHolder extends RecyclerView.ViewHolder{

    public TextView address,captain,captainPhone,diss,level,sport,state,teamName,requestBtn,cancelBtn,level2,area,place,time,date,chatBtn;
    public ImageView circleImageView;
    public LinearLayout linearLayout,ly_level,dateLyt,timeLyt,placeLyt,areLyt,lyt_match;
    public RelativeLayout relativeLayout;

    public AllTeamViewHolder(@NonNull View itemView) {
        super(itemView);
        address = itemView.findViewById(R.id.address_add);
        captain = itemView.findViewById(R.id.primary_addat);
        captainPhone = itemView.findViewById(R.id.secondary_addat);
        diss = itemView.findViewById(R.id.district_addat);
        level = itemView.findViewById(R.id.team_levelat);
        sport = itemView.findViewById(R.id.phone_addat);
        state = itemView.findViewById(R.id.state_addat);
        teamName = itemView.findViewById(R.id.player_name_addat);
        circleImageView = itemView.findViewById(R.id.team_pic_add_at);
        linearLayout = itemView.findViewById(R.id.ly_addat);
        relativeLayout = itemView.findViewById(R.id.mkbjvsxsxsxhchchchc);
        requestBtn = itemView.findViewById(R.id.add_btn_at);
        level2 = itemView.findViewById(R.id.team_level2);
        ly_level = itemView.findViewById(R.id.ly_team_Level);
        cancelBtn = itemView.findViewById(R.id.cancel_btn_at);
        dateLyt = itemView.findViewById(R.id.date_lyt);
        placeLyt = itemView.findViewById(R.id.place_lyt);
        timeLyt = itemView.findViewById(R.id.time_lyt);
        areLyt = itemView.findViewById(R.id.area_lyt);
        chatBtn = itemView.findViewById(R.id.chat_btn_at);

        date = itemView.findViewById(R.id.datejdjd);
        time = itemView.findViewById(R.id.timenfjfb);
        place = itemView.findViewById(R.id.place_jsd);
        area = itemView.findViewById(R.id.area_kar);

        lyt_match = itemView.findViewById(R.id.lyt_match_info);

        itemView.setVisibility(View.GONE);
    }
}
