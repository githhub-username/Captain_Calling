package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class JoinTournamentActivity extends AppCompatActivity {
    ImageView backButtonJoinTournament;
    String joinTournamentTeamName, joinTournamentTeamCaptainName;
    EditText joinTournamentTeamNameEditText, joinTournamentTeamCaptainEditText;
    DatabaseReference joinTournamentRef;
    Button sendJoinRequestButton;
    HashMap<String, Object> addRequestHashmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tournament);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        backButtonJoinTournament = findViewById(R.id.back_join_tournament);
        sendJoinRequestButton = findViewById(R.id.send_join_tournament_request);
        joinTournamentTeamNameEditText = findViewById(R.id.tournament_join_team_name);
        joinTournamentTeamCaptainEditText = findViewById(R.id.tournament_join_team_captain);

        backButtonJoinTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // String tournamentKey = getIntent().getStringExtra("TournamentKey");
        String tournamentKey = Paper.book().read("TournamentKey");
        Log.d("JoinTournamentActivity", "Received tournamentId: " + tournamentKey);
        if (tournamentKey != null && !tournamentKey.isEmpty()) {
            // Use the tournament key to construct the database reference
            joinTournamentRef = FirebaseDatabase.getInstance().getReference()
                    .child("tournaments")
                    .child(tournamentKey)
                    .child("Requests")
                    .child("Received");
            Log.e("JoinTournamentActivity", "Tournament key is not null or empty");
        } else {
            // Handle the case where tournament key is null or empty
            Toast.makeText(JoinTournamentActivity.this, "Tournament key is null or empty", Toast.LENGTH_SHORT).show();
            Log.e("JoinTournamentActivity", "Tournament key is null or empty");
        }


        sendJoinRequestButton.setOnClickListener(view -> {
            // Obtain team name and captain name from EditText fields
            joinTournamentTeamName = joinTournamentTeamNameEditText.getText().toString();
            joinTournamentTeamCaptainName = joinTournamentTeamCaptainEditText.getText().toString();

            // Check if both team name and captain name are provided
            if (joinTournamentTeamName.isEmpty() || joinTournamentTeamCaptainName.isEmpty()) {
                // Show error message if any of the fields are empty
                Toast.makeText(JoinTournamentActivity.this, "Please enter both team name and captain name", Toast.LENGTH_SHORT).show();
                return; // Exit the method without proceeding further
            }

            // Create HashMap to store join request data
            addRequestHashmap.put("JoinTournamentTeamName", joinTournamentTeamName);
            addRequestHashmap.put("JoinTournamentTeamCaptainName", joinTournamentTeamCaptainName);
            addRequestHashmap.put("JoinStatus", "Pending");

            // Check if database reference is not null and then proceed with adding join request
            if (joinTournamentRef != null) {
                joinTournamentRef.child(joinTournamentTeamName).setValue(addRequestHashmap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Handle successful join request
                                Toast.makeText(JoinTournamentActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();

                                // Send notification when request is sent successfully
                                // sendNotification(joinTournamentTeamName, joinTournamentTeamCaptainName);

                                // Optionally, finish the activity or perform other actions upon success
                            } else {
                                // Handle failed join request
                                Toast.makeText(JoinTournamentActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                                Log.e("JoinTournamentActivity", "Failed to send request", task.getException());
                            }
                        });
            } else {
                // Handle the case where joinTournamentRef is null
                Toast.makeText(JoinTournamentActivity.this, "Database reference is null", Toast.LENGTH_SHORT).show();
                Log.e("JoinTournamentActivity", "Database reference is null");
            }
        });
    }

    private void sendNotification(String teamName, String captainName) {
        String userFcmToken = "YOUR_FCM_TOKEN"; // Replace with the FCM token of the recipient
        String title = "Join Request";
        String body = "Join request sent for team: " + teamName + " - Captain: " + captainName;

        // Initialize FcmNotificationsSender
        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(userFcmToken, title, body, JoinTournamentActivity.this, JoinTournamentActivity.this);

        // Send notification
        fcmNotificationsSender.SendNotifications();
    }

}