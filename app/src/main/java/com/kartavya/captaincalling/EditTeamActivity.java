package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class EditTeamActivity extends AppCompatActivity {

    private EditText editTextName,editTextState,editTextDist,editTextAddress;
    private String id="";
    private String name,state,district,address;
    private Uri uri;
    private ImageView imageView;
    private Button button;
    private ProgressDialog loadingBar;
    private StorageReference storageReference;

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = (float) 0.92;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_edit_team);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        editTextName = findViewById(R.id.input_name_team2);
        editTextState = findViewById(R.id.input_state_team2);
        editTextDist = findViewById(R.id.input_district_team2);
        editTextAddress = findViewById(R.id.input_address_team2);

        imageView = findViewById(R.id.add_pic_team2);
        button = findViewById(R.id.submit_btn_team2);

        ImageView backBtn = findViewById(R.id.back_team_edit_team);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        storageReference = FirebaseStorage.getInstance().getReference().child("Team Pictures");

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Team_Id");
            LoadInfo(id);

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(uri)
                        .setAspectRatio(16,9)
                        .start(EditTeamActivity.this);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnection())
                {
                    try {
                        CheckDetails();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(EditTeamActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void LoadInfo(String id) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    AllTeam teamData = snapshot.child("AllTeam").child(id).getValue(AllTeam.class);
                    assert teamData != null;
                    Glide.with(getApplicationContext()).load(teamData.getPicture()).into(imageView);

                    editTextName.setText(teamData.getTeamName());
                    editTextState.setText(teamData.getState());
                    editTextDist.setText(teamData.getDistrict());
                    editTextAddress.setText(teamData.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void CheckDetails() throws IOException {
        name = editTextName.getText().toString().toLowerCase();
        state = editTextState.getText().toString().toLowerCase();
        district = editTextDist.getText().toString().toLowerCase();
        address = editTextAddress.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(name))
        {
            editTextName.setError("required..");
            editTextName.requestFocus();
        }
        else if (TextUtils.isEmpty(state))
        {
            editTextState.setError("required..");
            editTextState.requestFocus();
        }
        else if (TextUtils.isEmpty(district))
        {
            editTextDist.setError("required..");
            editTextDist.requestFocus();
        }
        else if (TextUtils.isEmpty(address))
        {
            editTextAddress.setError("required..");
            editTextAddress.requestFocus();
        }
        else
        {
            if (uri!=null)
            {
                SubmitForm();
            }
            else
            {
                SubmitForm2();
            }

        }

    }

    private void SubmitForm2() {
        loadingBar.show();

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("AllTeam");

        final HashMap<String, Object> objectHashMap = new HashMap<>();

        objectHashMap.put("TeamName",name);
        objectHashMap.put("State",state);
        objectHashMap.put("District",district);
        objectHashMap.put("Address",address);



        RootRef.child(id).updateChildren(objectHashMap).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful())
            {
                loadingBar.dismiss();
                Intent intent = new Intent(EditTeamActivity.this,MyTeamActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void SubmitForm() throws IOException {
        loadingBar.show();

        final StorageReference ref = storageReference
                .child(id + ".jpg");

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        final UploadTask uploadTask = ref.putBytes(data);

        uploadTask.continueWithTask(task1 -> {
            if (!task1.isSuccessful()) {
                throw Objects.requireNonNull(task1.getException());
            }
            return ref.getDownloadUrl();

        }).addOnCompleteListener(task -> {

            if (task.isSuccessful())
            {
                Uri downloadUri = task.getResult();
                assert downloadUri != null;
                String picUrl = downloadUri.toString();

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference().child("AllTeam");

                final HashMap<String, Object> objectHashMap = new HashMap<>();

                objectHashMap.put("TeamName",name);
                objectHashMap.put("State",state);
                objectHashMap.put("District",district);
                objectHashMap.put("Address",address);
                objectHashMap.put("Picture",picUrl);



                RootRef.child(id).updateChildren(objectHashMap).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful())
                    {
                        loadingBar.dismiss();
                        onBackPressed();
                    }
                });



            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();
            imageView.setImageURI(uri);

        }
        else
        {
            startActivity(new Intent(EditTeamActivity.this, EditTeamActivity.class));
            finish();
        }
    }

    public static String capitalizeFirstLetterOfEachWord(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Split the input string into words
        String[] words = input.split("\\s+");

        // Capitalize the first letter of each word
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Remove the trailing space and return the result
        return result.toString().trim();
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
}