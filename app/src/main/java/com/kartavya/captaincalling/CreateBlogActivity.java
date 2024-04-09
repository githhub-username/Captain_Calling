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

public class CreateBlogActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private Button button;
    private EditText editTextTitle,editTextDes;
    private String title,desc;
    private ProgressDialog loadingBar;
    private ImageView imageView;
    private Uri uri;
    public static final int PICK_IMAGE = 5;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        button = findViewById(R.id.upload_btn);
        editTextTitle = findViewById(R.id.input_title_blog);
        editTextDes = findViewById(R.id.input_des_blog);
        imageView = findViewById(R.id.photo_blog);

        ImageView backBtn = findViewById(R.id.back_create_blog);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        storageReference= FirebaseStorage.getInstance().getReference().child("BlogPicture");

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
                    Toast.makeText(CreateBlogActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }

            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryImagePicker = new Intent(Intent.ACTION_PICK);
                galleryImagePicker.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryImagePicker, REQUEST_CODE_PICK_IMAGE);
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

    }

    private void CheckData() throws IOException {

        title = editTextTitle.getText().toString();
        if(title.length()>25){
            Toast.makeText(this, "Title words limit should be less than 25 words", Toast.LENGTH_SHORT).show();
        }
        desc = editTextDes.getText().toString();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("required..");
            editTextTitle.requestFocus();
        } else if (TextUtils.isEmpty(desc)) {
            editTextDes.setError("required..");
            editTextDes.requestFocus();
        } else if (uri == null) {
            Toast.makeText(this, "Please upload product picture", Toast.LENGTH_SHORT).show();
        } else {
            title = capitalizeFirstLetterOfEachWord(title);
            desc = capitalizeFirstLetterOfEachWord(desc);
            Submit();
        }
    }

    private void Submit() throws IOException {
        loadingBar.show();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmss");
        String dateTime = ft.format(dNow);

        final StorageReference ref = storageReference
                .child(dateTime + ".jpg");

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

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    String product_pic = downloadUri.toString();

                    Date dNow = new Date();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

                    final DatabaseReference RootRef;
                    RootRef = FirebaseDatabase.getInstance().getReference().child("AllBlogs");

                    final HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("Title",title);
                    objectHashMap.put("Description",desc);
                    objectHashMap.put("Picture",product_pic);
                    objectHashMap.put("Status","off");
                    objectHashMap.put("ProfilePic", Paper.book().read(ProfileData.Picture));
                    objectHashMap.put("Name",Paper.book().read(ProfileData.Name));
                    objectHashMap.put("Phone",Paper.book().read(ProfileData.Phone));
                    objectHashMap.put("Date",currentDate.format(dNow));
                    objectHashMap.put("Category",Paper.book().read(ProfileData.PrimarySport));


                    RootRef.child(dateTime).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private String capitalizeFirstLetterOfEachWord(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!TextUtils.isEmpty(word)) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

}