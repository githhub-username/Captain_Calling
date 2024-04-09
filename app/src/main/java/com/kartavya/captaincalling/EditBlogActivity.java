package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class EditBlogActivity extends AppCompatActivity {

    private Button button;
    private EditText editTextTitle,editTextDes;
    private String title,desc;
    private ProgressDialog loadingBar;
    private ImageView imageView;
    private Uri uri;
    private String id="";
    public static final int PICK_IMAGE = 5;
    private StorageReference storageReference;
    private String PicBlog="";
    private boolean editNow=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        button = findViewById(R.id.upload_btne);
        editTextTitle = findViewById(R.id.input_title_bloge);
        editTextDes = findViewById(R.id.input_des_bloge);
        imageView = findViewById(R.id.photo_bloge);

        ImageView backBtn = findViewById(R.id.back_create_bloge);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        if (getIntent() != null)
        {
            id = getIntent().getStringExtra("Id");
            String DesBlog = getIntent().getStringExtra("DesBlog");
            PicBlog = getIntent().getStringExtra("PicBlog");
            String TitleBlog = getIntent().getStringExtra("TitleBlog");
            editTextTitle.setText(TitleBlog);
            editTextDes.setText(DesBlog);
            Glide.with(getApplicationContext()).load(PicBlog).into(imageView);

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (haveNetworkConnection())
                {
                    try {
                        CheckData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(EditBlogActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }



    private void CheckData() throws IOException {

        title = editTextTitle.getText().toString();
        desc = editTextDes.getText().toString();

        if (TextUtils.isEmpty(title))
        {
            editTextTitle.setError("required..");
            editTextTitle.requestFocus();
        }
        else if (TextUtils.isEmpty(desc))
        {
            editTextDes.setError("required..");
            editTextDes.requestFocus();
        }
        else
        {
            Submit2();

        }



    }

    private void Submit2() {

        loadingBar.show();
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("AllBlogs");

        final HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Title",title);
        objectHashMap.put("Description",desc);


        RootRef.child(id).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    loadingBar.dismiss();
                    onBackPressed();
                }
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