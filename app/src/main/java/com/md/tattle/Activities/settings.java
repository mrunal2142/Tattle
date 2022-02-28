package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.md.tattle.R;
import com.md.tattle.Utilities.constants;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class settings extends Base {

    ActivitySettingsBinding b;
    AvatarView avatarView;
    IImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.settingsToolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatarView = (AvatarView) findViewById(R.id.prof);

        ProgressDialog nDialog;
        nDialog = new ProgressDialog(settings.this);
        nDialog.dismiss();

        //setting name , profileImage , number
        {
            if (MainActivity.profUrl != null) {
                Picasso.get().load(MainActivity.profUrl).into(avatarView);
            } else {
                try {
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatarView, "http:/example.com/user/someUserAvatar.png", MainActivity.name);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail to load image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            b.settingsName.setText(MainActivity.name);
            b.number.setText("+91 " + MainActivity.number);

        }

        //signOut
        b.signOut.setOnClickListener(v -> {

            nDialog.setIndeterminate(false);
            nDialog.setCancelable(false);
            nDialog.setMessage("Deleting your account...");
            nDialog.show();
            AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("Delete account")
            .setMessage("Are you sure ypu want to delete account ?")
            .setPositiveButton("Delete account", (dialog, which) -> {

                preferanceManager preferanceManager = new preferanceManager(settings.this);

                FirebaseFirestore.getInstance().collection(constants.KEY_COLLECTION_USER)
                        .document(preferanceManager.getString(constants.KEY_USER_ID))
                        .delete()
                        .addOnSuccessListener(unused -> {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseAuth.getInstance().signOut();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                nDialog.dismiss();
                                                Intent intent = new Intent(settings.this, phoneAndOtp.class);
                                                startActivity(intent);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }).show();

//            Toast.makeText(getApplicationContext(), "under build", Toast.LENGTH_SHORT).show();
        });

        //linkedDevice
        b.linkedDevices.setOnClickListener(v -> {
//            throw new RuntimeException("Test Crash"); // Force a crash
            Toast.makeText(getApplicationContext(), "Feature under build", Toast.LENGTH_SHORT).show();
        });

        //appearance
        b.appearance.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Feature under build", Toast.LENGTH_SHORT).show());

        //notification
        b.notification.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext() , notification.class));
        });

        //privacy
        b.privacy.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext() , privacy.class);
            startActivity(i);
        });


        //help
        b.Help.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage("Please email us at mrunald2104@gmail.com")
                    .show();
        });

        //contribute to tattle


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}