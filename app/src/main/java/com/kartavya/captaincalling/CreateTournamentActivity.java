package com.kartavya.captaincalling;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class CreateTournamentActivity extends AppCompatActivity {
    private ImageView backButtonCreateTournament, tournamentBanner;
    private Uri uri;
    private Calendar calendar;
    private Button hostTournament;
    private ImageButton selectDate;
    private ProgressDialog loadingBar;
    private EditText tournamentNameEditText, tournamentStateEditText, tournamentDistrictEditText, tournamentAddressEditText, tournamentTeamsEditText, tournamentSportEditText;
    private String tournamentName, tournamentState, tournamentDistrict, tournamentAddress, tournamentTeams, tournamentDate, tournamentSport;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        backButtonCreateTournament = findViewById(R.id.back_create_tournament);
        tournamentNameEditText = findViewById(R.id.tournament_name);
        tournamentStateEditText = findViewById(R.id.tournament_state);
        tournamentDistrictEditText = findViewById(R.id.tournament_district);
        tournamentAddressEditText = findViewById(R.id.tournament_address);
        tournamentTeamsEditText = findViewById(R.id.no_of_teams);
        tournamentSportEditText = findViewById(R.id.tournament_sport);
        tournamentBanner = findViewById(R.id.add_tournament_banner);
        hostTournament = findViewById(R.id.create_tournament);
        selectDate = findViewById(R.id.buttonSelectDate);
        date = findViewById(R.id.textViewSelectedDate);

        backButtonCreateTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }

            private void showDatePicker() {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        CreateTournamentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
//                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                tournamentDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                                date.setText(tournamentDate);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        tournamentBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON) // Display guidelines
                        .setAspectRatio(16, 9) // Set aspect ratio (optional)
                        .start(CreateTournamentActivity.this); // Start cropping activity
            }
        });

        hostTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection()){
                    try{
                        checkDetails();
                        Log.d("Tournament Name","Name of tournament "+tournamentName);
                    } catch (Exception e){
                        Log.e("Tournament","Tournament cannot be created");
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(CreateTournamentActivity.this, "Internet Connection is not available. Try again later.",Toast.LENGTH_SHORT).show();
                }
            }

            private void checkDetails() throws IOException {

                tournamentName = tournamentNameEditText.getText().toString().toLowerCase();
                tournamentState = tournamentStateEditText.getText().toString().toLowerCase();
                tournamentDistrict = tournamentDistrictEditText.getText().toString().toLowerCase();
                tournamentAddress = tournamentAddressEditText.getText().toString().toLowerCase();
                tournamentTeams = tournamentTeamsEditText.getText().toString().toLowerCase();
                tournamentSport = tournamentSportEditText.getText().toString().toLowerCase();


                if(TextUtils.isEmpty(tournamentName)){
                    tournamentNameEditText.setError("required");
                    tournamentNameEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentSport)){
                    tournamentSportEditText.setError("required");
                    tournamentSportEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentState)){
                    tournamentStateEditText.setError("required");
                    tournamentStateEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentDistrict)){
                    tournamentDistrictEditText.setError("required");
                    tournamentDistrictEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentTeams)){
                    tournamentTeamsEditText.setError("required");
                    tournamentTeamsEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentAddress)){
                    tournamentAddressEditText.setError("required");
                    tournamentAddressEditText.requestFocus();
                } else if(TextUtils.isEmpty(tournamentDate)){
                    date.setText("required date...");
                    date.setTextColor(0xff0000);
                } else{
                    createTournament();
                }
            }

            private void createTournament() throws IOException {
                loadingBar.show();

                final DatabaseReference dbRef;
                dbRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

                final StorageReference imgRef;
                imgRef = FirebaseStorage.getInstance().getReference().child("images/"+tournamentName+".jpg");

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                UploadTask uploadTask = imgRef.putBytes(data);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return imgRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            final Map<String, Object> tournamentHashMap = new HashMap<>();

                            Uri downloadUri = task.getResult();
                            String imgUri = downloadUri.toString();

                            tournamentHashMap.put("TournamentName", tournamentName);
                            tournamentHashMap.put("TournamentSport", tournamentSport);
                            tournamentHashMap.put("TournamentState", tournamentState);
                            tournamentHashMap.put("TournamentDistrict", tournamentDistrict);
                            tournamentHashMap.put("TournamentAddress", tournamentAddress);
                            tournamentHashMap.put("TournamentTeams", tournamentTeams);
                            tournamentHashMap.put("TournamentDate", tournamentDate);
                            tournamentHashMap.put("TournamentBanner",imgUri);
                            tournamentHashMap.put("OrganiserPhone", Paper.book().read("Phone"));
                            tournamentHashMap.put("OrganiserName", Paper.book().read("Name"));

                            String tournamentKey = dbRef.push().getKey();

                            Paper.book().write("TournamentKey",tournamentKey);

                            // Save tournament data under the generated key
                            assert tournamentKey != null;
                            dbRef.child(tournamentKey).setValue(tournamentHashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Tournament data saved successfully
                                            Toast.makeText(CreateTournamentActivity.this, "Tournament created successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                            finish(); // Finish activity or perform other actions
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to save tournament data
                                            loadingBar.dismiss();
                                            Toast.makeText(CreateTournamentActivity.this, "Failed to create tournament", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }
        });


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                // Use the cropped image URI as needed (e.g., display in an ImageView)
                tournamentBanner.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle cropping error
                Toast.makeText(this, "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}