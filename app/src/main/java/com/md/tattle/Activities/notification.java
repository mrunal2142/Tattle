package com.md.tattle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import com.md.tattle.R;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityNotificationBinding;

public class notification extends Base {

    ActivityNotificationBinding b;
    com.md.tattle.Utilities.preferanceManager preferanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.notificationToolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferanceManager = new preferanceManager(this);
        b.notificaiton.setChecked(preferanceManager.getBoolean("notificaiton"));

        b.notificaiton.setOnClickListener(v -> {
            if(b.notificaiton.isChecked()) {
                preferanceManager.putBoolean("notificaiton" , true);
            } else {
                preferanceManager.putBoolean("notificaiton" , false);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}