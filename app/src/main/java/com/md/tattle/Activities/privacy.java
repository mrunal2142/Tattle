package com.md.tattle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.md.tattle.Models.ssModel;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityPrivacyBinding;

import java.util.HashMap;

public class privacy extends Base {

    public static ActivityPrivacyBinding b;
    public static preferanceManager preferanceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.privacyToolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Privacy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferanceManager = new preferanceManager(this);

        //screenshot
        b.screenshots.setChecked(preferanceManager.getBoolean("screenShot"));
        b.screenshots.setOnClickListener(v -> {
            if (b.screenshots.isChecked()) {
                preferanceManager.putBoolean("screenShot", true);
                uploadData(true);
            } else {
                preferanceManager.putBoolean("screenShot", false);
                b.screenshots.setChecked(false);
                uploadData(false);
            }
        });

        //lock screen
        privacy.preferanceManager.putString("pincode", null);
        b.screenlock.setChecked(preferanceManager.getBoolean("screenLock"));
        b.screenlock.setOnClickListener(v -> {
            if (b.screenlock.isChecked()) {
                preferanceManager.putBoolean("screenLock", true);
                preferanceManager.putBoolean("back", false);
                opendialog();
            } else {
                b.dialog.setVisibility(View.INVISIBLE);
                preferanceManager.putBoolean("screenLock", false);
                b.screenlock.setChecked(false);
            }
        });

    }

    void opendialog() {
       b.dialog.setVisibility(View.VISIBLE);

       b.positive.setOnClickListener(v -> {

           if(b.editTextTextPassword.getText().toString().length() != 4) {
               b.editTextTextPassword.setError("Enter 4 digit passcode");
               return;
           } else {
               privacy.preferanceManager.putString("pincode", b.editTextTextPassword.getText().toString());
               b.dialog.setVisibility(View.INVISIBLE);
               Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
           }

       });

       b.negative.setOnClickListener(v -> {
           privacy.preferanceManager.putString("pincode", null);
           privacy.preferanceManager.putBoolean("screenLock", false);
           privacy.b.screenlock.setChecked(false);
           b.dialog.setVisibility(View.INVISIBLE);
       });

    }

    public void uploadData(boolean val) {
        ssModel ss = new ssModel();
        ss.uid = FirebaseAuth.getInstance().getUid();
        ss.status = preferanceManager.getBoolean("screenShot");

        FirebaseDatabase.getInstance().getReference().child("ScreenShot").child(FirebaseAuth.getInstance().getUid())
                .setValue(ss)
                .addOnSuccessListener(v -> {
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}