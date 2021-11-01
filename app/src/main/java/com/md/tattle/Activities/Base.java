package com.md.tattle.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Base extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid())
                .setValue("Offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid())
                .setValue("Online");
    }

}
