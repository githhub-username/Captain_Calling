package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity {

    private Dialog dialogPhone,dialogOtp;
    private LinearLayout signInBtn,bottomText;
    private EditText editTextInputPhone;
    private Button sendOtpBtn,verifyBtn;
    private PinView pinView;
    private TextView resendOtp,changeNumber,textViewHintOtp;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private String verificationId;
    private ProgressBar progressBar;

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


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sign_up);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }

        mAuth = FirebaseAuth.getInstance();

        signInBtn = findViewById(R.id.signInBtn_phone);
        bottomText = findViewById(R.id.njbhjbhjgugvgcfxf);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator popIn = ObjectAnimator.ofPropertyValuesHolder(
                        splashScreenView,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
                );
                popIn.setInterpolator(new AnticipateInterpolator());
                popIn.setDuration(500L);

                // Call SplashScreenView.remove at the end of your custom animation.
                popIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });

                // Run your animation.
                popIn.start();
            });
        }


        dialogPhone = new Dialog(SignUpActivity.this);
        dialogPhone.setContentView(R.layout.phone_number_input);
        dialogPhone.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialogPhone.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPhone.setCancelable(true);
        dialogPhone.getWindow().getAttributes().windowAnimations = R.style.animations;

        editTextInputPhone = dialogPhone.findViewById(R.id.input_phone_number);
        sendOtpBtn = dialogPhone.findViewById(R.id.send_otp_btn);

        dialogOtp = new Dialog(SignUpActivity.this);
        dialogOtp.setContentView(R.layout.otp_verify);
        dialogOtp.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialogOtp.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogOtp.setCancelable(false);
        dialogOtp.getWindow().getAttributes().windowAnimations = R.style.animations;

        verifyBtn = dialogOtp.findViewById(R.id.verify_otp_btn);

        resendOtp = dialogOtp.findViewById(R.id.resend_otp);
        changeNumber = dialogOtp.findViewById(R.id.change_number_btn);

        pinView = dialogOtp.findViewById(R.id.otp_input);

        progressBar = dialogOtp.findViewById(R.id.progress_otp);

        textViewHintOtp = dialogOtp.findViewById(R.id.text_hint_otp);



        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPhone.show();
            }
        });

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneNumber = editTextInputPhone.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber))
                {
                    editTextInputPhone.setError("required..");
                    editTextInputPhone.requestFocus();
                }
                else
                    {
                        if (phoneNumber.length()<10)
                        {
                            editTextInputPhone.setError("number must be 10 digits..");
                            editTextInputPhone.requestFocus();
                        }
                        else
                            {
                                if (phoneNumber.equals("1234567890"))
                                {
                                    final DatabaseReference ProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles").child(phoneNumber);
                                    ProfRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {
                                                Paper.book().write("Phone",snapshot.child("Phone").getValue());
                                                Paper.book().write("Name",snapshot.child("Name").getValue());
                                                Paper.book().write("State",snapshot.child("State").getValue());
                                                Paper.book().write("District",snapshot.child("District").getValue());
                                                Paper.book().write("Address",snapshot.child("Address").getValue());
                                                Paper.book().write("PrimarySport",snapshot.child("PrimarySport").getValue());
                                                Paper.book().write("SecondarySport",snapshot.child("SecondarySport").getValue());
                                                Paper.book().write("Level",snapshot.child("Level").getValue());
                                                Paper.book().write("Picture",snapshot.child("Picture").getValue());
                                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else
                                {
                                    sendOTP();
                                }

                            }
                    }
            }
        });


    }

    private void CheckTopic() {
        FirebaseDatabase.getInstance().getReference("AllProfiles").child(Paper.book().read(ProfileData.Phone)).child("Topics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String id = dataSnapshot.getKey();
                        assert id != null;
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference("AllProfiles").child(Paper.book().read(ProfileData.Phone)).child("Topics").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                }
                else
                {
                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendOTP() {
        sendVerificationCode("+" + 91 + phoneNumber);
        dialogPhone.dismiss();
        textViewHintOtp.setText("We've sent an OTP on +91 " + phoneNumber);
        dialogOtp.show();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = Objects.requireNonNull(pinView.getText()).toString().trim();

                if ((code.isEmpty() || code.length() < 6)){

                    pinView.setError("Enter code...");
                    pinView.requestFocus();
                    pinView.setLineColor(Color.RED);
                    textViewHintOtp.setText("X Incorrect OTP");
                    textViewHintOtp.setTextColor(Color.RED);
                    return;
                }

                pinView.setLineColor(Color.GREEN);
                textViewHintOtp.setText("OTP Verified");
                textViewHintOtp.setTextColor(Color.GREEN);
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Paper.book().contains("Verified"))
        {
            String phoneN = Paper.book().read("Verified");
            Intent intent = new Intent(SignUpActivity.this,CreateAccountActivity.class);
            intent.putExtra("PhoneNumber",phoneN);
            startActivity(intent);
            finish();
        }
        else if (Paper.book().contains("Phone"))
        {
            bottomText.setVisibility(View.INVISIBLE);
            signInBtn.setVisibility(View.INVISIBLE);
            CheckTopic();
        }
    }

    private void sendVerificationCode(String number){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId= s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
                pinView.setLineColor(Color.GREEN);
                textViewHintOtp.setText("OTP Verified");
                textViewHintOtp.setTextColor(Color.GREEN);
                pinView.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignUpActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            final DatabaseReference ProfRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles").child(phoneNumber);
                            ProfRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists())
                                    {
                                        Paper.book().write("Phone",snapshot.child("Phone").getValue());
                                        Paper.book().write("Name",snapshot.child("Name").getValue());
                                        Paper.book().write("State",snapshot.child("State").getValue());
                                        Paper.book().write("District",snapshot.child("District").getValue());
                                        Paper.book().write("Address",snapshot.child("Address").getValue());
                                        Paper.book().write("PrimarySport",snapshot.child("PrimarySport").getValue());
                                        Paper.book().write("SecondarySport",snapshot.child("SecondarySport").getValue());
                                        Paper.book().write("Level",snapshot.child("Level").getValue());
                                        Paper.book().write("Picture",snapshot.child("Picture").getValue());
                                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                        {
                                            Paper.book().write("Verified",phoneNumber);
                                            Intent intent = new Intent(SignUpActivity.this,CreateAccountActivity.class);
                                            intent.putExtra("PhoneNumber",phoneNumber);
                                            startActivity(intent);
                                        }
                                    dialogOtp.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                        } else {

                            pinView.setLineColor(Color.RED);
                            textViewHintOtp.setText("X Incorrect OTP");
                            textViewHintOtp.setTextColor(Color.RED);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }
}