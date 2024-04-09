package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class PlayerProfileActivity extends AppCompatActivity {

    int savedStateValue, savedDistrictValue;
    private EditText editTextName,editTextPhone,editTextAddress,editTextPs,editTextSs, editPrimarySportLevel,editSecondarySportLevel,editPrimarySportExpert,editSecondarySportExpert;
    private ImageButton infoButton, infoButton2;
    private CircleImageView circleImageView;
    private Button button;
    private Uri uri;
    private String name,state,district,address,primarySportsExpert,secondarySportsExpert, selectedState, selectedDistrict, primarySportsLevel, secondarySportsLevel;
    private ProgressDialog loadingBar;
    public static final int PICK_IMAGE = 12;
    private StorageReference storageReference;
    private TextView textViewEdit, statesTextView, districtTextView, editTextState, editTextDistrict;
    private ImageView imageView;
    private Spinner spinnerState, spinnerDistrict;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private boolean isEdit=false;

    private String stateU = "Select Your State";
    private String districtU = "Select Your District";
    private int selectedStatePosition = 0;


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
        /// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player_profile);
        Paper.init(PlayerProfileActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        editTextName = findViewById(R.id.input_name2);
        editTextPhone = findViewById(R.id.input_phone2);
        editTextAddress = findViewById(R.id.input_address2);
        editTextSs = findViewById(R.id.input_second_sport2);
        editTextPs = findViewById(R.id.input_primary_sport2);
        editPrimarySportLevel = findViewById(R.id.input_Primary_Sport_level);
        editSecondarySportLevel = findViewById(R.id.input_secondary_sport_level);
        imageView = findViewById(R.id.bxxxhvhcgcvhv);
        editPrimarySportExpert = findViewById(R.id.input_Expertise_Primary_Sport);
        editSecondarySportExpert = findViewById(R.id.input_Expertise_Secondary_Sport);

        spinnerState = (Spinner) findViewById(R.id.edit_state_spinner);
        spinnerState.setEnabled(false);
        spinnerDistrict = (Spinner) findViewById(R.id.edit_district_spinner);
        spinnerDistrict.setEnabled(false);
        statesTextView = findViewById(R.id.edit_required_state);
        districtTextView = findViewById(R.id.edit_required_district);

        stateAdapter = ArrayAdapter.createFromResource(PlayerProfileActivity.this, R.array.states_array, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);

        if (savedInstanceState != null) {
            state = savedInstanceState.getString("selectedState", "Select Your State");
            selectedStatePosition = savedInstanceState.getInt("selectedStatePosition", 0);
            district = savedInstanceState.getString("selectedDistrict", "Select Your District");

            // Set the selected state and district back to the spinners
            spinnerState.setSelection(selectedStatePosition);
            // Assuming that the districtAdapter is already set up in onItemSelected for spinnerState
            spinnerDistrict.setSelection(districtAdapter.getPosition(district));
        }

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                state = spinnerState.getSelectedItem().toString();
                Log.d("Spinner", "State Selected: " + state); // Log the selected state
                int parentId = parent.getId();
                ArrayAdapter<CharSequence> districtAdapter = null;
                if (parentId == R.id.edit_state_spinner) {
                    switch (state) {
                        case "Select Your District":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_districts, R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                            break;
                        case "Andhra Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
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
                            Log.d("Spinner", "No case matched for state: " + state); // Log if no case matches
                            break;
                    }
                    if (districtAdapter != null) {
                        districtAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spinnerDistrict.setAdapter(districtAdapter);
                        Paper.book().write("selectedStatePosition", i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                district = adapterView.getItemAtPosition(i).toString();
                Log.d("Spinner", "District Selected: " + district); // Log the selected district
                Log.d("Spinner", "State Selected: " + state); // Log the state (make sure it's not null)
                Paper.book().write("selectedDistPosition", i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        textViewEdit =findViewById(R.id.edit_profile);
        infoButton = findViewById(R.id.info_section);
        infoButton2 = findViewById(R.id.info_section2);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerProfileActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Expertise in sports refers to the particular skill or role that a player is best known for. It's like their signature move or standout talent on the field.");

                // Set Alert Title
                builder.setTitle("Expertise");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("Close", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        infoButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerProfileActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Expertise in sports refers to the particular skill or role that a player is best known for. It's like their signature move or standout talent on the field.");

                // Set Alert Title
                builder.setTitle("Expertise");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("Close", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        ImageView backBtn = findViewById(R.id.back_profile);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        button = findViewById(R.id.submit_btn2);

        circleImageView = findViewById(R.id.add_pic2);


        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        LoadProfile();

        textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textViewEdit.getText().equals("Cancel"))
                {
                    textViewEdit.setText("Cancel");
                    editTextName.setEnabled(true);
                    spinnerState.setEnabled(true);
                    spinnerDistrict.setEnabled(true);
                    editTextAddress.setEnabled(true);
                    editPrimarySportExpert.setEnabled(true);
                    editSecondarySportExpert.setEnabled(true);
                    editPrimarySportLevel.setEnabled(true);
                    editSecondarySportLevel.setEnabled(true);
                    button.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    circleImageView.setClickable(true);
                    isEdit=true;
                }
                else
                {
                    textViewEdit.setText("Edit");
                    textViewEdit.setGravity(Gravity.CENTER);
                    editTextName.setEnabled(false);
                    spinnerState.setEnabled(false);
                    spinnerDistrict.setEnabled(false);
                    editTextAddress.setEnabled(false);
                    editPrimarySportExpert.setEnabled(true);
                    editSecondarySportExpert.setEnabled(true);
                    editPrimarySportLevel.setEnabled(true);
                    editSecondarySportLevel.setEnabled(true);
                    button.setVisibility(View.GONE);
                    imageView.setVisibility(View.INVISIBLE);
                    circleImageView.setClickable(false);
                    isEdit=false;
                    LoadProfile();
                }

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
                    Toast.makeText(PlayerProfileActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }
            }
        });


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON) // Display guidelines
                            .setAspectRatio(1, 1) // Set aspect ratio (optional)
                            .start(PlayerProfileActivity.this); // Start cropping activity
                }
            }
        });


    }

    private void LoadProfile() {
        String pic = Paper.book().read("Picture");
        if (!pic.equals("null"))
        {
            Glide.with(getApplicationContext()).load(pic).into(circleImageView);
        }
        editTextAddress.setText(ProperCase.properCase(Paper.book().read("Address")));
//        editTextDistrict.setText(ProperCase.properCase(Paper.book().read("District")));
        editPrimarySportLevel.setText(Paper.book().read("PrimarySportsLevel"));
        editSecondarySportLevel.setText(Paper.book().read("SecondarySportsLevel"));
        editTextName.setText(ProperCase.properCase(Paper.book().read("Name")));
        editTextPhone.setText(Paper.book().read("Phone"));
        editTextPs.setText(Paper.book().read("PrimarySport"));
        editTextSs.setText(Paper.book().read("SecondarySport"));
        editPrimarySportExpert.setText(Paper.book().read("PrimarySportsExpertise"));
        editSecondarySportExpert.setText(Paper.book().read("SecondarySportsExpertise"));
//        editTextState.setText(ProperCase.properCase(Paper.book().read("State")));


        // Set the spinner selections based on the saved positions
        spinnerState.setSelection(Paper.book().read("selectedStatePosition", 0));
        spinnerDistrict.setSelection(Paper.book().read("selectedDistPosition", 0));


    }

    private void CheckDetails() throws IOException {
        name = editTextName.getText().toString().toLowerCase();
        primarySportsExpert = editPrimarySportExpert.getText().toString().toLowerCase();
        secondarySportsExpert = editSecondarySportExpert.getText().toString().toLowerCase();
/*        state = editTextState.getText().toString().toLowerCase();
        district = editTextDistrict.getText().toString().toLowerCase();*/
        address = editTextAddress.getText().toString().toLowerCase();
        primarySportsLevel = editPrimarySportLevel.getText().toString().toLowerCase();
        secondarySportsLevel = editSecondarySportLevel.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(name))
        {
            editTextName.setError("required..");
            editTextName.requestFocus();
        }
        else if (TextUtils.isEmpty(state))
        {
            statesTextView.setError("required..");
            statesTextView.requestFocus();
        }
        else if (TextUtils.isEmpty(district))
        {
            districtTextView.setError("required..");
            districtTextView.requestFocus();
        }
        else if (TextUtils.isEmpty(address))
        {
            editTextAddress.setError("required..");
            editTextAddress.requestFocus();
        }
        else if (TextUtils.isEmpty(primarySportsExpert))
        {
            editPrimarySportExpert.setError("required..");
            editPrimarySportExpert.requestFocus();
        }
        /*else if (TextUtils.isEmpty(secondarySportsExpert))
        {
            editSecondarySportExpert.setError("required..");
            editSecondarySportExpert.requestFocus();
        }*/
        else if (TextUtils.isEmpty(primarySportsLevel))
        {
            editPrimarySportLevel.setError("required..");
            editPrimarySportLevel.requestFocus();
        }
        /*else if (TextUtils.isEmpty(secondarySportsLevel))
        {
            editSecondarySportLevel.setError("required..");
            editSecondarySportLevel.requestFocus();
        }*/
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


        final DatabaseReference ProfileRef;
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

        final HashMap<String, Object> objectHashMap3 = new HashMap<>();

        objectHashMap3.put("Name",name);
        objectHashMap3.put("State",state);
        objectHashMap3.put("District",district);
        objectHashMap3.put("Address",address);
        objectHashMap3.put("PrimarySportsExpertise",primarySportsExpert);
        objectHashMap3.put("SecondarySportsExpertise",secondarySportsExpert);
        objectHashMap3.put("PrimarySportsLevel",primarySportsLevel);
        objectHashMap3.put("SecondarySportsLevel",secondarySportsLevel);

        ProfileRef.child(Paper.book().read("Phone")).updateChildren(objectHashMap3).addOnCompleteListener(task4 -> {
            if (task4.isSuccessful())
            {
                Paper.book().write("Name",name);
                Paper.book().write("State",state);
                Paper.book().write("District",district);
                Paper.book().write("Address",address);
                Paper.book().write("PrimarySportsExpertise",primarySportsExpert);
                Paper.book().write("SecondarySportsExpertise",secondarySportsExpert);
                Paper.book().write("PrimarySportsLevel",primarySportsLevel);
                Paper.book().write("SecondarySportsLevel",secondarySportsLevel);
                Intent intent = new Intent(PlayerProfileActivity.this,MainActivity.class);
                startActivity(intent);
                loadingBar.dismiss();
                finish();
            }
        });

    }

    private void SubmitForm() throws IOException {
        loadingBar.show();

        final StorageReference ref = storageReference
                .child(Paper.book().read("Phone") + ".jpg");

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


                final DatabaseReference ProfileRef;
                ProfileRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

                final HashMap<String, Object> objectHashMap3 = new HashMap<>();

                objectHashMap3.put("Name",name);
                objectHashMap3.put("State",state);
                objectHashMap3.put("District",district);
                objectHashMap3.put("Address",address);
                objectHashMap3.put("Picture",picUrl);
                objectHashMap3.put("PrimarySportsExpertise",primarySportsExpert);
                objectHashMap3.put("SecondarySportsExpertise",secondarySportsExpert);
                objectHashMap3.put("PrimarySportsLevel",primarySportsLevel);
                objectHashMap3.put("SecondarySportsLevel",secondarySportsLevel);

                ProfileRef.child(Paper.book().read("Phone")).updateChildren(objectHashMap3).addOnCompleteListener(task4 -> {
                    if (task4.isSuccessful())
                    {
                        Paper.book().write("Name",name);
                        Paper.book().write("State",state);
                        Paper.book().write("District",district);
                        Paper.book().write("Address",address);
                        Paper.book().write("Picture",picUrl);
                        Paper.book().write("PrimarySportsExpertise",primarySportsExpert);
                        Paper.book().write("SecondarySportsExpertise",secondarySportsExpert);
                        Paper.book().write("PrimarySportsLevel",primarySportsLevel);
                        Paper.book().write("SecondarySportsLevel",secondarySportsLevel);
                        Intent intent = new Intent(PlayerProfileActivity.this,MainActivity.class);
                        startActivity(intent);
                        loadingBar.dismiss();
                        finish();
                    }
                });

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                // Use the cropped image URI as needed (e.g., display in an ImageView)
                circleImageView.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle cropping error
                Toast.makeText(this, "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedState", state);
        outState.putString("selectedDistrict", district);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getString("selectedState", "Select Your State");
        district = savedInstanceState.getString("selectedDistrict", "Select Your District");

        // Set the selected state and district back to the spinners
        spinnerState.setSelection(stateAdapter.getPosition(state));
        spinnerDistrict.setSelection(districtAdapter.getPosition(district));
    }
}