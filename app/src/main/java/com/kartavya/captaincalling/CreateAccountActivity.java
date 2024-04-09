package com.kartavya.captaincalling;

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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class CreateAccountActivity extends AppCompatActivity {

    private String phone="",name,state,district,address,sport="",sport2="",primaryLevel="",secondaryLevel="",expertPrimary, expertSecondary, selectedState, selectedDistrict;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    private EditText editTextName,editTextPhone,editTextState,editTextDistrict,editTextAddress,editTextPrimaryExpert, editTextSecondaryExpert;
    private Spinner spinnerSport,spinnerSportSecond,spinnerLevelPrimary,spinnerLevelSecondary,spinnerState,spinnerDistrict;
    private Button button;
    private CircleImageView circleImageView;
    private Uri uri;
    private ProgressDialog loadingBar;
    public static final int PICK_IMAGE = 1;
    private StorageReference storageReference;
    private TextView textViewAlert, statesTextView, districtTextView;
    private String toast = "";
    private ImageButton infoSection, infoSection2;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;

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
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_create_account);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }

        editTextName = findViewById(R.id.input_name);
        editTextPhone = findViewById(R.id.input_phone);
        editTextState = findViewById(R.id.input_state);
        editTextDistrict = findViewById(R.id.input_district);
        infoSection = findViewById(R.id.info_section);
        infoSection2 = findViewById(R.id.info_section2);

        infoSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);

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

        infoSection2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);

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

        editTextAddress = findViewById(R.id.input_address);
        /*statesTextView = findViewById(R.id.required_states);
        districtTextView = findViewById(R.id.required_district);

        spinnerState = findViewById(R.id.state_spinner);
        spinnerDistrict = findViewById(R.id.district_spinner);

        stateAdapter = ArrayAdapter.createFromResource(CreateAccountActivity.this, R.array.states_array, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                selectedState = spinnerState.getSelectedItem().toString();
                state = selectedState;

                int parentId = parent.getId();
                if(parentId == R.id.state_spinner){
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
                    if(districtAdapter!=null){
                        districtAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spinnerDistrict.setAdapter(districtAdapter);

                        int statePosition = spinnerState.getSelectedItemPosition();
                    }
                    spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedDistrict = spinnerDistrict.getSelectedItem().toString();
                            district = selectedDistrict;

                            int districtPosition = spinnerDistrict.getSelectedItemPosition();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        editTextPrimaryExpert = findViewById(R.id.input_Expertise_Primary_Sport);
        editTextSecondaryExpert = findViewById(R.id.input_Expertise_Secondary_Sport);

        spinnerSport = findViewById(R.id.spinner_primary_sport);
        spinnerSportSecond = findViewById(R.id.spinner_sport_second);

        spinnerLevelPrimary = findViewById(R.id.spinner_primary_level);
        spinnerLevelSecondary = findViewById(R.id.spinner_level_second);

        ImageView backBtn = findViewById(R.id.back_create_acc);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        button = findViewById(R.id.submit_btn);

        circleImageView = findViewById(R.id.add_pic);

        textViewAlert = findViewById(R.id.note_alert);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);


        if (getIntent() != null)
        {
            phone = getIntent().getStringExtra("PhoneNumber");
            editTextPhone.setText(phone);
            editTextPhone.setEnabled(false);
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        String[] sportArray = new String[]{ "Select Primary Sport","Cricket","Football","Kabaddi","Volleyball","Basketball" };
        final List<String> sportList = new ArrayList<>(Arrays.asList(sportArray));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,sportList)
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

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerSport.setAdapter(spinnerArrayAdapter);

        spinnerSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    sport = selectedItemText;
                    spinnerSportSecond.setAdapter(null);
                    spinnerSportSecond.setClickable(true);
                    sport2="";
                    SetSecondSport(sport);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] sportArray1 = new String[]{ "Select Secondary Sport"};
        final List<String> sportList1 = new ArrayList<>(Arrays.asList(sportArray1));

        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,sportList1)
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

        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerSportSecond.setAdapter(spinnerArrayAdapter1);


        String[] levelArray = new String[]{ "Select Your Level","Beginner","Intermediate","Pro" };
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
        spinnerLevelPrimary.setAdapter(spinnerArrayAdapter2);
        spinnerLevelSecondary.setAdapter(spinnerArrayAdapter2);

        spinnerLevelPrimary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    primaryLevel = selectedItemText;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLevelSecondary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    secondaryLevel = selectedItemText;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImagePicker = new Intent(Intent.ACTION_PICK);
                galleryImagePicker.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryImagePicker, REQUEST_CODE_PICK_IMAGE);
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
                    Toast.makeText(CreateAccountActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void SetSecondSport(String sportS) {
        switch (sportS)
        {
            case "Cricket":
                String[] sportArray = new String[]{ "Select Secondary Sport","Football","Kabaddi","Volleyball","Basketball" };
                final List<String> sportList = new ArrayList<>(Arrays.asList(sportArray));

                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this,R.layout.support_simple_spinner_dropdown_item,sportList)
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

                spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerSportSecond.setAdapter(spinnerArrayAdapter);
                spinnerSportSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position > 0){
                            sport2 = selectedItemText;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;

            case "Football":
                String[] sportArray1 = new String[]{ "Select Secondary Sport","Cricket","Kabaddi","Volleyball","Basketball" };
                final List<String> sportList1 = new ArrayList<>(Arrays.asList(sportArray1));

                final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                        this,R.layout.support_simple_spinner_dropdown_item,sportList1)
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

                spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerSportSecond.setAdapter(spinnerArrayAdapter1);
                spinnerSportSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position > 0){
                            sport2 = selectedItemText;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "Kabaddi":
                String[] sportArray2 = new String[]{ "Select Secondary Sport","Cricket","Football","Volleyball","Basketball" };
                final List<String> sportList2 = new ArrayList<>(Arrays.asList(sportArray2));

                final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                        this,R.layout.support_simple_spinner_dropdown_item,sportList2)
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
                spinnerSportSecond.setAdapter(spinnerArrayAdapter2);
                spinnerSportSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position > 0){
                            sport2 = selectedItemText;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "Volleyball":
                String[] sportArray3 = new String[]{ "Select Secondary Sport","Cricket","Football","Kabaddi","Basketball" };
                final List<String> sportList3 = new ArrayList<>(Arrays.asList(sportArray3));

                final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                        this,R.layout.support_simple_spinner_dropdown_item,sportList3)
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

                spinnerArrayAdapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerSportSecond.setAdapter(spinnerArrayAdapter3);
                spinnerSportSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position > 0){
                            sport2 = selectedItemText;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "Basketball":
                String[] sportArray4 = new String[]{ "Select Secondary Sport","Cricket","Football","Kabaddi","Volleyball" };
                final List<String> sportList4 = new ArrayList<>(Arrays.asList(sportArray4));

                final ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(
                        this,R.layout.support_simple_spinner_dropdown_item,sportList4)
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

                spinnerArrayAdapter4.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerSportSecond.setAdapter(spinnerArrayAdapter4);
                spinnerSportSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position > 0){
                            sport2 = selectedItemText;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;


        }
    }

    private void CheckDetails() throws IOException {
        name = editTextName.getText().toString().toLowerCase();
        state = editTextState.getText().toString().toLowerCase();
        district = editTextDistrict.getText().toString().toLowerCase();
        address = editTextAddress.getText().toString().toLowerCase();
        expertPrimary = editTextPrimaryExpert.getText().toString().toLowerCase();
        expertSecondary = editTextSecondaryExpert.getText().toString().toLowerCase();

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
        else if (TextUtils.isEmpty(expertPrimary))
        {
            editTextPrimaryExpert.setError("required..");
            editTextPrimaryExpert.requestFocus();
        }
        /*else if (TextUtils.isEmpty(expertSecondary))
        {
            editTextSecondaryExpert.setError("required..");
            editTextSecondaryExpert.requestFocus();
        }*/
        else if (TextUtils.isEmpty(address))
        {
            editTextAddress.setError("required..");
            editTextAddress.requestFocus();
        }
        else if (sport.equals(""))
        {
            Toast.makeText(CreateAccountActivity.this, "Please select your primary sport!", Toast.LENGTH_SHORT).show();
        }
        /*else if (sport2.equals(""))
        {
            Toast.makeText(CreateAccountActivity.this, "Please select your secondary sport!", Toast.LENGTH_SHORT).show();
        }*/
        else if (primaryLevel.equals(""))
        {
            Toast.makeText(CreateAccountActivity.this, "Please select your sport level!", Toast.LENGTH_SHORT).show();
        }
        /*else if (secondaryLevel.equals(""))
        {
            Toast.makeText(CreateAccountActivity.this, "Please select your sport level!", Toast.LENGTH_SHORT).show();
        }*/
        else
            {
                if (uri!=null)
                {
                    SubmitForm();
                }
                else
                    {
                        if (toast.equals(""))
                        {
                            SubmitForm2();
                        }
                        else
                            {
                                Toast.makeText(CreateAccountActivity.this, ""+toast, Toast.LENGTH_SHORT).show();
                            }

                    }

            }

    }

    private void SubmitForm2() {
        loadingBar.show();


        final DatabaseReference ProfileRef;
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles");

        final HashMap<String, Object> objectHashMap3 = new HashMap<>();

        objectHashMap3.put("Name",name);
        objectHashMap3.put("Phone",phone);
        objectHashMap3.put("State",state);
        objectHashMap3.put("District",district);
        objectHashMap3.put("Address",address);
        objectHashMap3.put("PrimarySport",sport);
        objectHashMap3.put("SecondarySport",sport2);
        objectHashMap3.put("PrimarySportsExpertise",expertPrimary);
        objectHashMap3.put("SecondarySportsExpertise",expertSecondary);
        objectHashMap3.put("PrimarySportsLevel",primaryLevel);
        objectHashMap3.put("SecondarySportsLevel",secondaryLevel);
        objectHashMap3.put("Picture","null");

        ProfileRef.child(phone).updateChildren(objectHashMap3).addOnCompleteListener(task4 -> {
            if (task4.isSuccessful())
            {
                Paper.book().delete("Verified");
                Paper.book().write("Phone",phone);
                Paper.book().write("Name",name);
                Paper.book().write("State",state);
                Paper.book().write("District",district);
                Paper.book().write("Address",address);
                Paper.book().write("PrimarySport",sport);
                Paper.book().write("SecondarySport",sport2);
                Paper.book().write("PrimarySportsExpertise",expertPrimary);
                Paper.book().write("SecondarySportsExpertise", expertSecondary);
                Paper.book().write("PrimarySportsLevel",primaryLevel);
                Paper.book().write("SecondarySportsLevel",secondaryLevel);
                Paper.book().write("Picture","null");
                Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                startActivity(intent);
                loadingBar.dismiss();
                finish();
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
            circleImageView.setImageURI(uri);

        }
        else
        {
            startActivity(new Intent(CreateAccountActivity.this, CreateAccountActivity.class));
            finish();
        }
    }

    private void SubmitForm() throws IOException {
        loadingBar.show();

        final StorageReference ref = storageReference
                .child(phone + ".jpg");

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
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
                objectHashMap3.put("Phone",phone);
                objectHashMap3.put("State",state);
                objectHashMap3.put("District",district);
                objectHashMap3.put("Address",address);
                objectHashMap3.put("PrimarySport",sport);
                objectHashMap3.put("SecondarySport",sport2);
                objectHashMap3.put("PrimarySportsLevel",primaryLevel);
                objectHashMap3.put("SecondarySportsLevel",secondaryLevel);
                objectHashMap3.put("PrimarySportsExpertise",expertPrimary);
                objectHashMap3.put("SecondarySportsExpertise",expertSecondary);
                objectHashMap3.put("Picture",picUrl);

                ProfileRef.child(phone).updateChildren(objectHashMap3).addOnCompleteListener(task4 -> {
                    if (task4.isSuccessful())
                    {
                        Paper.book().delete("Verified");
                        Paper.book().write("Phone",phone);
                        Paper.book().write("Name",name);
                        Paper.book().write("State",state);
                        Paper.book().write("District",district);
                        Paper.book().write("Address",address);
                        Paper.book().write("PrimarySport",sport);
                        Paper.book().write("SecondarySport",sport2);
                        Paper.book().write("PrimarySportsExpertise",expertPrimary);
                        Paper.book().write("SecondarySportsExpertise",expertSecondary);
                        Paper.book().write("PrimarySportsLevel",primaryLevel);
                        Paper.book().write("SecondarySportsLevel",secondaryLevel);
                        Paper.book().write("Picture",picUrl);
                        Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                        startActivity(intent);
                        loadingBar.dismiss();
                        finish();
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
}