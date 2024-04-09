package com.kartavya.captaincalling;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class TournamentInfoFragment extends Fragment {
    String name, date, state, district, teams, address;
    TextView editTournamentName, editTournamentDate, editTournamentState, editTournamentDistrict, editTournamentAddress, editTournamentTeams, editTournamentDetails;
    ImageView editTournamentBanner;
    Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tournament_info, container, false);

        Paper.init(getActivity().getApplicationContext());

        editTournamentName = view.findViewById(R.id.edit_tournament_name);
        editTournamentDate = view.findViewById(R.id.edit_tournament_date);
        editTournamentState = view.findViewById(R.id.edit_tournament_state);
        editTournamentDistrict = view.findViewById(R.id.edit_tournament_district);
        editTournamentAddress = view.findViewById(R.id.edit_tournament_address);
        editTournamentTeams = view.findViewById(R.id.edit_tournament_teams);
        editTournamentBanner = view.findViewById(R.id.edit_tournament_banner);
        editTournamentDetails = view.findViewById(R.id.edit_tournament_details);
        submitButton = view.findViewById(R.id.update_tournament_details);

        if(getActivity().getIntent() != null){
            editTournamentName.setText(getActivity().getIntent().getStringExtra("tournament_name"));
            editTournamentDate.setText(getActivity().getIntent().getStringExtra("tournament_date"));
            editTournamentState.setText(getActivity().getIntent().getStringExtra("tournament_state"));
            editTournamentDistrict.setText(getActivity().getIntent().getStringExtra("tournament_district"));
            editTournamentAddress.setText(getActivity().getIntent().getStringExtra("tournament_address"));
            editTournamentTeams.setText(getActivity().getIntent().getStringExtra("tournament_teams"));
            Glide.with(getActivity().getApplicationContext()).load(getActivity().getIntent().getStringExtra("tournament_picture")).into(editTournamentBanner);
        }

        editTournamentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTournamentDetails.getText() == "Edit"){
                    editTournamentDetails.setText("Cancel");
                    editTournamentName.setEnabled(true);
                    editTournamentState.setEnabled(true);
                    editTournamentDistrict.setEnabled(true);
                    editTournamentAddress.setEnabled(true);
                    editTournamentDate.setEnabled(true);
                    editTournamentTeams.setEnabled(true);
                } else {
                    editTournamentDetails.setText("Edit");
                    editTournamentName.setEnabled(false);
                    editTournamentState.setEnabled(false);
                    editTournamentDistrict.setEnabled(false);
                    editTournamentAddress.setEnabled(false);
                    editTournamentDate.setEnabled(false);
                    editTournamentTeams.setEnabled(false);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(haveNetworkConnection()){
                    updateTournament();
                } else{
                    Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void checkBlankSpaces() {
        name = editTournamentName.getText().toString();
        state = editTournamentState.getText().toString();
        district = editTournamentDistrict.getText().toString();
        address = editTournamentAddress.getText().toString();
        teams = editTournamentTeams.getText().toString();
        date = editTournamentDate.getText().toString();

        if(TextUtils.isEmpty(name)){
            editTournamentName.setError("Required...");
            editTournamentName.requestFocus();
        } else if(TextUtils.isEmpty(date)){
            editTournamentDate.setError("Required...");
            editTournamentDate.requestFocus();
        } else if(TextUtils.isEmpty(state)){
            editTournamentState.setError("Required...");
            editTournamentState.requestFocus();
        } else if(TextUtils.isEmpty(district)){
            editTournamentDistrict.setError("Required...");
            editTournamentDistrict.requestFocus();
        } else if(TextUtils.isEmpty(address)){
            editTournamentAddress.setError("Required...");
            editTournamentAddress.requestFocus();
        } else if (TextUtils.isEmpty(teams)){
            editTournamentTeams.setError("Required...");
            editTournamentTeams.requestFocus();
        }
    }

    private void updateTournament() {
        checkBlankSpaces();

        DatabaseReference updateTournamentRef;
        updateTournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

        final HashMap<String, Object> updateTournamentHashmap = new HashMap<>();

        updateTournamentHashmap.put("TournamentName",  name);
        updateTournamentHashmap.put("TournamentState", state);
        updateTournamentHashmap.put("TournamentDistrict", district);
        updateTournamentHashmap.put("TournamentAddress", address);
        updateTournamentHashmap.put("TournamentTeams", teams);
        updateTournamentHashmap.put("TournamentDate", date);

        String updateTournamentKey = Paper.book().read("TournamentKey");
        if (updateTournamentKey == null) {
            Toast.makeText(getActivity(), "Failed to update tournament: Key not found", Toast.LENGTH_SHORT).show();
            return;
        }

        updateTournamentRef.child(updateTournamentKey).updateChildren(updateTournamentHashmap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getActivity(),"Tournament Successfully Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}