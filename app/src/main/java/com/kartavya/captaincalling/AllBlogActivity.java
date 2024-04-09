package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kartavya.captaincalling.Interface.ItemClickListener;

import io.paperdb.Paper;

public class AllBlogActivity extends AppCompatActivity {

    private Button button,categoryButton,homeButton;
    private RecyclerView recyclerView;
    private DatabaseReference blogRef;
    private FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder> adapter;
    private ProgressBar progressBar;
    private TextView empty;

    private Query filteredBlogQuery;

    private DatabaseReference filteredBlogRef;

    private String selectedCategory = ""; // Add this variable to store the selected category




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
        setContentView(R.layout.activity_all_blog);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_blog_my);

        progressBar = findViewById(R.id.mkbjvdtdstdtchcnjb);

        empty = findViewById(R.id.cxmckcnbccb);

        ImageView backBtn = findViewById(R.id.back_all_vlog);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllBlogActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        /* button = findViewById(R.id.create_blog_btn);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(AllBlogActivity.this, CreateBlogActivity.class);
            startActivity(intent);
        }); */

        Button cricketButton = findViewById(R.id.cricket);
        Button footballButton = findViewById(R.id.football);
        Button basketballButton = findViewById(R.id.basketball);
        Button volleyballButton = findViewById(R.id.volleyball);
        Button kabaddiButton = findViewById(R.id.kabaddi);
        homeButton = findViewById(R.id.home);

        // Set click listeners for category buttons
        cricketButton.setOnClickListener(view -> filterBlogsByCategory("Cricket"));
        footballButton.setOnClickListener(view -> filterBlogsByCategory("Football"));
        basketballButton.setOnClickListener(view -> filterBlogsByCategory("Basketball"));
        volleyballButton.setOnClickListener(view -> filterBlogsByCategory("Volleyball"));
        kabaddiButton.setOnClickListener(view -> filterBlogsByCategory("Kabaddi"));


        LoadData();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle home button click (show all blogs)
                filterBlogItemsByCategory("");
            }
        });


    }

    private void LoadData() {

        blogRef = FirebaseDatabase.getInstance().getReference("AllBlogs");

        if (!selectedCategory.isEmpty()) {
            filteredBlogQuery = blogRef.orderByChild("Category").equalTo(selectedCategory);
        } else {
            filteredBlogQuery = blogRef; // Use the unfiltered query if no category is selected
        }

        FirebaseRecyclerOptions<AllBlogs> options =
                new FirebaseRecyclerOptions.Builder<AllBlogs>()
                        .setQuery(filteredBlogQuery, AllBlogs.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllBlogViewHolder holder, int position, @NonNull AllBlogs model) {

                progressBar.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(model.getPicture()).into(holder.imageView);
                if (!model.getProfilePic().equals("null"))
                {
                    Glide.with(getApplicationContext()).load(model.getProfilePic()).into(holder.circleImageView);
                }
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.datetime.setText(model.getDate());
                holder.name.setText(capitalizeFirstLetterOfEachWord(model.getName()));


                if (model.getStatus().equals("off"))
                {
                    /*holder.status.setTextColor(Color.RED);
                    holder.status.setText("Pending");*/
                }
                else
                {
                    /*holder.status.setTextColor(Color.GREEN);
                    holder.status.setText("Public");*/
                }

                holder.blogLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AllBlogActivity.this,BlogDetailsActivity.class);
                        intent.putExtra("DateBlog",model.getDate());
                        intent.putExtra("NameBlog",model.getName());
                        intent.putExtra("DesBlog",model.getDescription());
                        intent.putExtra("PicBlog",model.getPicture());
                        intent.putExtra("TitleBlog",model.getTitle());
                        intent.putExtra("PPicBlog",model.getProfilePic());
                        intent.putExtra("Category",model.getCategory());
                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
                empty.setVisibility(View.INVISIBLE);


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
    }

    private void filterBlogItemsByCategory(String category) {
        // Use the original blogRef for the initial query
        filteredBlogRef = FirebaseDatabase.getInstance().getReference("AllBlogs");

        // Add the category filter to the query
        if (!category.isEmpty()) {
            filteredBlogQuery = filteredBlogRef.orderByChild("Category").equalTo(category);
        } else {
            filteredBlogQuery = filteredBlogRef; // Use the unfiltered query if no category is selected
        }

        FirebaseRecyclerOptions<AllBlogs> options =
                new FirebaseRecyclerOptions.Builder<AllBlogs>()
                        .setQuery(filteredBlogQuery, AllBlogs.class)
                        .build();

        adapter.updateOptions(options); // Update adapter with new options
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
        return result.toString().trim();
    }

    private void showCategorySelectionDialog() {
        // Implement the logic to show a dialog or dropdown to select a category
        // Set the selectedCategory variable based on the user's selection
        // For simplicity, let's assume you have a method to show a dialog, showCategoryDialog()
        showCategoryDialog();
    }

    private void showCategoryDialog() {
    }

    private void filterBlogsByCategory(String category) {
        // Set the selected category
        selectedCategory = category;

        // Refresh the RecyclerView with filtered data
        LoadData();
    }
}