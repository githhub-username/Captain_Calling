package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AddVideoActivity extends AppCompatActivity {

    private Button button;
    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private EditText editTextTitle,editTextDes,editTextUrl;
    private String title,des,urlYt;
    private ProgressDialog loadingBar;
    private ImageView imageView;
    private Uri uri;
    private TextView textView;
    public static final int PICK_IMAGE = 8;
    private boolean isEdit=true;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        button = findViewById(R.id.add_btn_yt);
        editTextTitle = findViewById(R.id.input_name_yt);
//        editTextDes = findViewById(R.id.input_des_yt);
        editTextUrl = findViewById(R.id.input_url);
        imageView = findViewById(R.id.photo_yt);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        ImageView backBtn = findViewById(R.id.back_add_video);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        storageReference= FirebaseStorage.getInstance().getReference().child("YoutubePicture");

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
                    Toast.makeText(AddVideoActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();

                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON) // Display guidelines
                            .setAspectRatio(16, 9) // Set aspect ratio (optional)
                            .start(AddVideoActivity.this); // Start cropping activity
                }
            }
        });
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(AddVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(AddVideoActivity.this, "Permission Required", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                // Use the cropped image URI as needed (e.g., display in an ImageView)
                imageView.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle cropping error
                Toast.makeText(this, "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void CheckData() throws IOException {

        title = editTextTitle.getText().toString();
        urlYt = editTextUrl.getText().toString();

        if (TextUtils.isEmpty(title))
        {
            editTextTitle.setError("required..");
            editTextTitle.requestFocus();
        }
        else if (TextUtils.isEmpty(urlYt))
        {
            editTextUrl.setError("required..");
            editTextUrl.requestFocus();
        }
        else if (uri==null)
        {
            Toast.makeText(this, "Please upload thumbnail", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Submit();
        }



    }

    private void Submit() throws IOException {
        loadingBar.show();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmss");
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

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    String product_pic = downloadUri.toString();

                    final DatabaseReference RootRef;
                    RootRef = FirebaseDatabase.getInstance().getReference().child("YoutubeVideos");

                    final HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("Title",title);
                    objectHashMap.put("Description",des);
                    objectHashMap.put("Picture",product_pic);
                    objectHashMap.put("Url",urlYt);
                    objectHashMap.put("Status","off");

                    RootRef.child(datetime).updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}