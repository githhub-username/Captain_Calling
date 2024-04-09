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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.checkerframework.checker.index.qual.LengthOf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class CreateTeamActivity extends AppCompatActivity {

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

    private EditText editTextName,editTextAddress;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private Spinner spinnerLevel,spinnerState,spinnerDistrict;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    private String name,state,district,address,level="",selectedState,selectedDistrict;
    private ImageView imageView;
    private Button button;
    private Uri uri;
    private ProgressDialog loadingBar;
    private static final int PICK_IMAGE = 1;
    private TextView textViewAlert,editTextState,editTextDist;
    private StorageReference storageReference;
    private String toast = "Please upload team profile pic!";
    private int Number=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_create_team);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        editTextName = findViewById(R.id.input_name_team);
        editTextState = findViewById(R.id.state_required);
        state = editTextState.getText().toString();
        editTextDist = findViewById(R.id.district_required);
        district = editTextDist.getText().toString();
        editTextAddress = findViewById(R.id.input_address_team);

        /*spinnerState = findViewById(R.id.states_spinner);
        spinnerDistrict = findViewById(R.id.district_spinner);
        stateAdapter = ArrayAdapter.createFromResource(CreateTeamActivity.this, R.array.states_array, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                selectedState = (String) parent.getItemAtPosition(i);
                state = selectedState;

                Log.d("StateValue","States "+state);

                int parentId = parent.getId();
                if(parentId == R.id.states_spinner){
                    switch (selectedState){
                        case "Select States":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_default_districts, R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                            break;
                        case "Andhra Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Arunachal Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_arunachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Assam":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_assam_districts, R.layout.spinner_layout);
                            break;
                        case "Bihar":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_bihar_districts, R.layout.spinner_layout);
                            break;
                        case "Chandigarh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_chandigarh_districts, R.layout.spinner_layout);
                            break;
                        case "Chhattisgarh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_chhattisgarh_districts, R.layout.spinner_layout);
                            break;
                        case "Dadra and Nagar Haveli":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_layout);
                            break;
                        case "Daman and Diu":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_daman_diu_districts, R.layout.spinner_layout);
                            break;
                        case "Delhi":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_delhi_districts, R.layout.spinner_layout);
                            break;
                        case "Goa":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_goa_districts, R.layout.spinner_layout);
                            break;
                        case "Gujarat":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_gujarat_districts, R.layout.spinner_layout);
                            break;
                        case "Haryana":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_haryana_districts, R.layout.spinner_layout);
                            break;
                        case "Himachal Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_himachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Jammu and Kashmir":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_jammu_kashmir_districts, R.layout.spinner_layout);
                            break;
                        case "Jharkhand":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_jharkhand_districts, R.layout.spinner_layout);
                            break;
                        case "Karnataka":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_karnataka_districts, R.layout.spinner_layout);
                            break;
                        case "Kerala":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_kerala_districts, R.layout.spinner_layout);
                            break;
                        case "Ladakh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_ladakh_districts, R.layout.spinner_layout);
                            break;
                        case "Lakshadweep":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_lakshadweep_districts, R.layout.spinner_layout);
                            break;
                        case "Madhya Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_madhya_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Maharashtra":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_maharashtra_districts, R.layout.spinner_layout);
                            break;
                        case "Manipur":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_manipur_districts, R.layout.spinner_layout);
                            break;
                        case "Meghalaya":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_meghalaya_districts, R.layout.spinner_layout);
                            break;
                        case "Mizoram":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_mizoram_districts, R.layout.spinner_layout);
                            break;
                        case "Nagaland":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_nagaland_districts, R.layout.spinner_layout);
                            break;
                        case "Orissa":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_odisha_districts, R.layout.spinner_layout);
                            break;
                        case "Puducherry":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_puducherry_districts, R.layout.spinner_layout);
                            break;
                        case "Punjab":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_punjab_districts, R.layout.spinner_layout);
                            break;
                        case "Rajasthan":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_rajasthan_districts, R.layout.spinner_layout);
                            break;
                        case "Sikkim":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_sikkim_districts, R.layout.spinner_layout);
                            break;
                        case "Tamil Nadu":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_tamil_nadu_districts, R.layout.spinner_layout);
                            break;
                        case "Telangana":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_telangana_districts, R.layout.spinner_layout);
                            break;
                        case "Tripura":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_tripura_districts, R.layout.spinner_layout);
                            break;
                        case "Uttarakhand":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_uttarakhand_districts, R.layout.spinner_layout);
                            break;
                        case "Uttar Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_uttar_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "West Bengal":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_west_bengal_districts, R.layout.spinner_layout);
                            break;
                        default:
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_default_districts, R.layout.spinner_layout);
                            break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDistrict = (String) adapterView.getItemAtPosition(i);
                district = selectedDistrict;
                Log.d("District","Value of district "+district);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

       // spinnerSport = findViewById(R.id.spinner_sport_team);
        spinnerLevel = findViewById(R.id.spinner_level_team);

        imageView = findViewById(R.id.add_pic_team);
        button = findViewById(R.id.submit_btn_team);
        button.setEnabled(true);

        textViewAlert = findViewById(R.id.note_alert_team);

        storageReference = FirebaseStorage.getInstance().getReference().child("Team Pictures");

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        ImageView backBtn = findViewById(R.id.back_cteam);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImagePicker = new Intent(Intent.ACTION_PICK);
                galleryImagePicker.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryImagePicker, REQUEST_CODE_PICK_IMAGE);
            }
        });

        LoadNumberofTeam();


        String[] levelArray = new String[]{ "Select team Level","Beginner","Intermediate","Pro" };
        final List<String> levelList = new ArrayList<>(Arrays.asList(levelArray));

        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,levelList)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(spinnerArrayAdapter2);

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    level = selectedItemText;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnection())
                {
                    if (Number<2)
                    {
                        try {
                            CheckDetails();
                        } catch (IOException e) {
                            Log.e("SubmitTeams","Teams not created");
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(CreateTeamActivity.this, "Your team creation limit is full", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(CreateTeamActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }
            }
        });







    }

    private void LoadNumberofTeam() {

        final String currentCaptainPhone = Paper.book().read("Phone");


        FirebaseDatabase.getInstance().getReference("AllTeam").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int teamCount = 0;

                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        if (Objects.equals(dataSnapshot1.child("CaptainPhone").getValue(), currentCaptainPhone)) {
                            teamCount++;
                        }
                    }

                    // Check if the user has reached the team creation limit (e.g., 5)
                    if (teamCount >= 2) {
                        // Display a message or take appropriate action
                        Toast.makeText(CreateTeamActivity.this, "You have reached the team creation limit (2 teams)", Toast.LENGTH_SHORT).show();
                        // Optionally, disable the team creation button or perform other actions
                        button.setEnabled(false);
                    } else {
                        // Allow team creation
                        button.setEnabled(true);
                    }
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
        else if (level.equals(""))
        {
            Toast.makeText(CreateTeamActivity.this, "Please select your sport level!", Toast.LENGTH_SHORT).show();
        }
        else if (uri==null)
        {
            Toast.makeText(CreateTeamActivity.this, ""+toast, Toast.LENGTH_SHORT).show();

        }
        else
            {
                SubmitForm();
            }

    }


    private void SubmitForm() throws IOException {
        loadingBar.show();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        String datetime = ft.format(dNow);

        final StorageReference ref = storageReference
                .child(datetime + ".jpg");

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

                final DatabaseReference PlayerRef;
                PlayerRef = FirebaseDatabase.getInstance().getReference().child("Players");

                final DatabaseReference ChatsRef;
                ChatsRef = FirebaseDatabase.getInstance().getReference().child("Chats");

                final DatabaseReference ProfRef;
                ProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");


                final HashMap<String, Object> objectHashMap = new HashMap<>();

                objectHashMap.put("TeamName",name);
                objectHashMap.put("State",state);
                objectHashMap.put("Sport",Paper.book().read("PrimarySport"));
                objectHashMap.put("District",district);
                objectHashMap.put("Address",address);
                objectHashMap.put("Level",level);
                objectHashMap.put("Picture",picUrl);
                objectHashMap.put("Captain",Paper.book().read("Name"));
                objectHashMap.put("CaptainPhone",Paper.book().read("Phone"));
                objectHashMap.put("EntryId",datetime);



                RootRef.child(datetime).updateChildren(objectHashMap).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful())
                    {
                        final HashMap<String, Object> objectHashMap2 = new HashMap<>();

                        objectHashMap2.put("EntryId",datetime);

                        ProfRef.child(Paper.book().read("Phone")).child("MyTeams").child(datetime).updateChildren(objectHashMap2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            final HashMap<String, Object> objectHashMap1 = new HashMap<>();

                                            objectHashMap1.put("Name",Paper.book().read("Name"));
                                            objectHashMap1.put("Picture",Paper.book().read("Picture"));
                                            objectHashMap1.put("Phone",Paper.book().read("Phone"));
                                            objectHashMap1.put("isCaptain","true");

                                            PlayerRef.child(datetime).child(Paper.book().read("Phone")).updateChildren(objectHashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task3) {
                                                    if (task3.isSuccessful())
                                                    {

                                                        Date dNow = new Date();
                                                        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                                                        String datetime2 = ft.format(dNow);

                                                        final HashMap<String, Object> objectHashMap11 = new HashMap<>();

                                                        objectHashMap11.put("Name",Paper.book().read("Name"));
                                                        objectHashMap11.put("Phone",Paper.book().read("Phone"));
                                                        objectHashMap11.put("Date",currentDate.format(dNow));
                                                        objectHashMap11.put("Message","Hello guys welcome to our team i am "+Paper.book().read("Name")+" captain of this team, thanks!");



                                                        ChatsRef.child(datetime).child(datetime2).updateChildren(objectHashMap11).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Intent intent = new Intent(CreateTeamActivity.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    loadingBar.dismiss();
                                                                    finish();
                                                                }

                                                            }
                                                        });



                                                    }

                                                }
                                            });


                                        }

                                    }
                                });

                        }
                });



            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== REQUEST_CODE_PICK_IMAGE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            uri = data.getData();
            imageView.setImageURI(uri);

        }
        else
        {
            startActivity(new Intent(CreateTeamActivity.this, CreateTeamActivity.class));
            finish();
        }
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