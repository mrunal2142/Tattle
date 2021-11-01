package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.md.tattle.databinding.ActivityPhoneAndOtpBinding;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class phoneAndOtp extends AppCompatActivity {

    ActivityPhoneAndOtpBinding b;
    private String verification_id ;
    PhoneAuthProvider.ForceResendingToken resendingToken ;
    Activity a;
    int p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPhoneAndOtpBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        b.progressBar.setVisibility(View.INVISIBLE);

        //cheacking if user already exists
        //if true send to main activity
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext() , passcode.class);
            startActivity(i);
        }

        a = this;

        b.otpView.setEnabled(false);

        p = 25;

        b.continueBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                //null check
                if( b.phoneInput.getText().toString().length() != 10) {
                    b.phoneInput.setError("Enter 10 digit number");
                    return;
                }

                b.continueBtn.setVisibility(View.INVISIBLE);
                b.progressBar.setVisibility(View.VISIBLE);
                b.progressBar.setProgress(p , true);
                p = p+25;



                b.changeText.setText("Please enter otp send to +91 " + b.phoneInput.getText().toString() + "\n please do not press back");

                String number = "+91"+b.phoneInput.getText().toString();

                //Send a verification code to the user's phone
                PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(number)
                        .setTimeout(60L , TimeUnit.SECONDS)
                        .setActivity(a)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Toast.makeText(getApplicationContext(), "OTP sent to +91 " + b.phoneInput.getText().toString() , Toast.LENGTH_SHORT).show();
                                b.progressBar.setProgress(p , true);
                                p = p+25;
                                verification_id = s;
                                resendingToken = forceResendingToken;
                                b.otpView.setEnabled(true);
                                b.otpView.requestFocus();
                            }
                        })
                        .build();
                //Then, pass their phone number to the PhoneAuthProvider.verifyPhoneNumber method to
                // request that Firebase verify the user's phone number.
                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

                b.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                    @Override
                    public void onOtpCompleted(String otp) {

                        //Create a PhoneAuthCredential object
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verification_id , otp);

                        b.progressBar.setVisibility(View.VISIBLE);
                        b.progressBar.setProgress(p , true);
                        p = p+25;


                        //Sign in the user
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {

                                            b.progressBar.setProgress(p , true);

                                            Intent i = new Intent(getApplicationContext() , profileSetup.class);
                                            i.putExtra("number" , b.phoneInput.getText().toString());
                                            startActivity(i);
                                            finishAffinity();
                                        } else {
                                            Toast.makeText(phoneAndOtp.this, "Wrong Otp with error message " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                
            }
        });
    }
}