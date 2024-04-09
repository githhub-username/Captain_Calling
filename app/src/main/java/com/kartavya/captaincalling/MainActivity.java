package com.kartavya.captaincalling;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kartavya.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar materialToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView circleImageView;
    private TextView textViewUserName;
    private CardView cardViewCreate,cardViewJoin,cardViewChallenge,cardViewBlog;
    private ImageView adminBtn;
    private RecyclerView recyclerView;
    private DatabaseReference blogRef;
    private FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder> adapter;
    private ProgressBar progressBar;
    private ProgressDialog loadingBar;

    private BottomNavigationView bottomNavigationView;

    // private static final int STORAGE_PERMISSION_CODE = 1;
    //  private static final int CAMERA_PERMISSION_CODE = 2;

    //   private static final int VIBRATE_PERMISSION_CODE = 4;

    //    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private static final int NOTIFICATION_PERMISSION_CODE = 102;

    /*
     In summary, this method ensures that if the font scale exceeds a certain threshold (0.92)
     , it is adjusted to that threshold, and the display metrics are updated accordingly.
     This can be useful for maintaining consistent font sizes across different devices and configurations.
    */
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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        materialToolbar = findViewById(R.id.tool_bar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_bar);
        adminBtn = findViewById(R.id.admin_go);
        cardViewCreate = findViewById(R.id.create_team);
        cardViewJoin = findViewById(R.id.join_team);
        cardViewChallenge = findViewById(R.id.challenge);
        cardViewBlog = findViewById(R.id.my_teams);
        // bottomNavigationView = findViewById(R.id.bottom_navigation);

        recyclerView = findViewById(R.id.recycler_main_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("YourActivity", "Storage permission not granted. Requesting permission...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Permission already granted
            Log.d("YourActivity", "Storage permission already granted. Accessing storage...");

            // Your code to access storage goes here
        }


 */
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("YourActivity", "Notification permission not granted. Requesting permission...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE},
                    NOTIFICATION_PERMISSION_CODE);
        } else {
            // Permission already granted
            Log.d("YourActivity", "Notification permission already granted. Accessing notifications...");

            // Your code to access notifications goes here
        }

         */

        /*


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            // Permission already granted
            // Your code to access the camera goes here
        }

         */

        // checkCameraPermission();
        // checkStoragePermission();
        //  checkNotificationPermission();



        // This is working  remove comments
        // checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        // Check and request storage permission

        checkPermission(Manifest.permission.POST_NOTIFICATIONS, NOTIFICATION_PERMISSION_CODE);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);




        LoadData();



        View header = navigationView.getHeaderView(0);
        circleImageView = header.findViewById(R.id.profile_pics_head);
        textViewUserName = header.findViewById(R.id.user_name_head);


        String url = Paper.book().read("Picture");

        if (!url.equals("null"))
        {
            Glide.with(getApplicationContext()).load(url).into(circleImageView);
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayerProfileActivity.class);
                startActivity(intent);
            }
        });



        // this is changed to make capital letter

        String userName = Paper.book().read("Name");
        String capitalizedUserName = capitalizeFirstLetterOfEachWord(userName);
        textViewUserName.setText(capitalizedUserName);


        FirebaseMessaging.getInstance().subscribeToTopic("all");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (task.isSuccessful())
                        {

                            FirebaseDatabase.getInstance().getReference().child("Token").child(Paper.book().read(ProfileData.Phone)).setValue(task.getResult())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if (task1.isSuccessful())
                                            {
                                                String token = task.getResult();
                                            }
                                        }
                                    });
                        }

                    }
                });


        materialToolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id)
            {

//                case R.id.youtube_videos:
//                    Intent intent = new Intent(MainActivity.this, AllYoutubeVideoActivity.class);
//                    intent.putExtra("Admin","No");
//                    startActivity(intent);
//                    break;

                /*case R.id.social_media:
                    startActivity(new Intent(MainActivity.this, SocialMediaActivity.class));
                    break;*/
                case R.id.youtube:
                    startActivity(new Intent(MainActivity.this, SocialMediaActivity.class));
                    break;
                case R.id.facebook:
                    startActivity(new Intent(MainActivity.this, SocialMediaActivity.class));
                    break;
                case R.id.instagram:
                    startActivity(new Intent(MainActivity.this, SocialMediaActivity.class));
                    break;
                case R.id.share:
                    Toast.makeText(MainActivity.this, "share", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rate_us:
                    Toast.makeText(MainActivity.this, "rate", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.about_us:
                    Toast.makeText(MainActivity.this, "about", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.privacy_policy:
                    Intent intent2 = new Intent(MainActivity.this, PrivacyActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.term:
                    Intent intent21 = new Intent(MainActivity.this, TermActivity.class);
                    startActivity(intent21);
                    break;
                case R.id.logout:
                    CharSequence[] options = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i==0)
                            {
                                loadingBar.show();
                                Paper.book().delete("Phone");
                                Paper.book().delete("Name");
                                Paper.book().delete("State");
                                Paper.book().delete("District");
                                Paper.book().delete("Address");
                                Paper.book().delete("PrimarySport");
                                Paper.book().delete("SecondarySport");
                                Paper.book().delete("Level");
                                Paper.book().delete("Picture");
                                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();

                    break;
                default:
                    return true;
            }
            return true;


        });

        cardViewBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyTeamActivity.class);
                startActivity(intent);
            }
        });

        cardViewJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AllTeamActivity.class);
                startActivity(intent);
            }
        });

        cardViewCreate.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,CreateTeamActivity.class);
            startActivity(intent);
        });

        cardViewCreate.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,CreateTeamActivity.class);
            startActivity(intent);
        });

        cardViewChallenge.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AllBlogActivity.class);
            startActivity(intent);
        });

        adminBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AllBlogAdminActivity.class);
            startActivity(intent);
        });

       /*
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    // Replace with your HomeActivity.class
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish(); // Finish the current activity to prevent going back
                    break;
                case R.id.bottom_profile:
                    // Replace with your ShortsActivity.class
                    startActivity(new Intent(MainActivity.this, PlayerProfileActivity.class));
                    finish();
                    break;
                case R.id.bottom_blog:
                    // Replace with your SubscriptionsActivity.class
                    startActivity(new Intent(MainActivity.this, AllBlogActivity.class));
                    finish();
                    break;
                case R.id.bottom_video:
                    // Replace with your LibraryActivity.class
                    startActivity(new Intent(MainActivity.this, AddVideoActivity.class));
                    finish();
                    break;
            }
            return true;
        }); */




    }



    // Override onRequestPermissionsResult method to handle the result

/*
    private void checkStoragePermission() {
        Log.d("Permission", "Checking storage permission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Permission", "Storage permission is not granted. Requesting..");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Provide additional rationale to the user if needed
                showStoragePermissionRationaleDialog();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        } else {
            // Permission already granted
            Log.d("Permission", "Storage permission already granted");
            // Your code to access storage goes here
            accessStorage();
        }
    }

    private void showStoragePermissionRationaleDialog() {
        // Show a dialog explaining why the storage permission is necessary
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Storage Permission Needed")
                .setMessage("The app requires storage access to save files. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission after the user acknowledges the rationale
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                STORAGE_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the case where the user cancels the permission request
                        Log.d("Permission", "Storage permission denied by user");
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Permission", "Camera permission is not granted. Requesting...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            // Permission already granted
            Log.d("Permission", "Camera permission already granted.");
            // Your code to access the camera goes here
            accessCamera();
        }
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d(TAG, "Notification permission not granted. Requesting permission...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE},
                    NOTIFICATION_PERMISSION_CODE);
        } else {
            // Permission already granted
            Log.d(TAG, "Notification permission already granted. Accessing notifications...");

            // Your code to access notifications goes here
            // For example, start a service or perform the notification-related task
            // startNotificationService();
        }
    }


    private void accessStorage() {
        // Your code to access storage goes here
        Log.d("Permission", "Accessing storage...");
    }

    private void accessCamera() {
        // Your code to access the camera goes here
        Log.d("Permission", "Accessing camera...");
    }

    private void handleNotifications() {
        // Your code for handling notifications goes here
        Log.d("Permission", "Handling notifications...");
    }

    // Override onRequestPermissionsResult method to handle the result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Notification permission granted
                Log.d("YourActivity", "Notification permission granted. Accessing notifications...");

                // Your code to access notifications goes here
            } else {
                // Notification permission denied
                Log.d("YourActivity", "Notification permission denied. Unable to access notifications.");
            }
        }
    }


 */

    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            Log.d("Permission", "Permission already granted: " + permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Camera Permission Granted");
                } else {
                    Log.d("Permission", "Camera Permission Denied");
                }
                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Storage Permission Granted");
                } else {
                    Log.d("Permission", "Storage Permission Denied");
                }
                break;

            case NOTIFICATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Notification Permission Granted");
                } else {
                    Log.d("Permission", "Notification Permission Denied");
                }
                break;
        }
    }







    // function added to make capital letter
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

    private void LoadData() {

        blogRef = FirebaseDatabase.getInstance().getReference("AllBlogs");

        FirebaseRecyclerOptions<AllBlogs> options =
                new FirebaseRecyclerOptions.Builder<AllBlogs>()
                        .setQuery(blogRef.orderByChild("Status").equalTo("on"), AllBlogs.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllBlogViewHolder holder, int position, @NonNull AllBlogs model) {

                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.imageView);
                if (!model.getProfilePic().equals("null"))
                {
                    Glide.with(getApplicationContext()).load(model.getProfilePic()).into(holder.circleImageView);
                }
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.datetime.setText(model.getDate());
                holder.name.setText(capitalizeFirstLetterOfEachWord(model.getName()));

//                holder.status.setVisibility(View.GONE);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(MainActivity.this,BlogDetailsActivity.class);
                        intent.putExtra("DateBlog",model.getDate());
                        intent.putExtra("NameBlog",model.getName());
                        intent.putExtra("DesBlog",model.getDescription());
                        intent.putExtra("PicBlog",model.getPicture());
                        intent.putExtra("TitleBlog",model.getTitle());
                        intent.putExtra("PPicBlog",model.getProfilePic());
                        startActivity(intent);
                    }
                });

                // progressBar.setVisibility(View.INVISIBLE);




            }

            @NonNull
            @Override
            public AllBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_model, parent,false);
                return  new AllBlogViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    return true;

                case R.id.bottom_profile:
                    startVideoPlayActivity();
                    return true;

                case R.id.bottom_blog:
                    startBlogActivity();
                    return true;

                case R.id.bottom_video:
                    startVideoActivity();
                    return true;

                case R.id.tournament:
                    startTournamentActivity();
                    return true;

                default:
                    return false;
            }
        });

        // Set the initial selected item
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
    }

    private void startTournamentActivity() {
        startActivity(new Intent(MainActivity.this, TournamentActivity.class));
    }


    private void startNewActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void startVideoPlayActivity() {
        startActivity(new Intent(this, AllYoutubeVideoActivity.class));
    }

    private void startBlogActivity() {
        startActivity(new Intent(this, CreateBlogActivity.class));
    }

    private void startVideoActivity() {
        startActivity(new Intent(this, AddVideoActivity.class));
    }


}