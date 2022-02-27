package com.md.tattle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hanks.passcodeview.PasscodeView;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityPasscodeBinding;

public class passcode extends AppCompatActivity {
    preferanceManager preferanceManager;
    ActivityPasscodeBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPasscodeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        preferanceManager = new preferanceManager(getApplicationContext());
        if( preferanceManager.getBoolean("screenLock") && preferanceManager.getString("pincode") != null  && preferanceManager.getString("pincode").length() == 4 ) {

            b.passcodeView.setPasscodeLength(4)
                    .setLocalPasscode(preferanceManager.getString("pincode"))
                    .setListener(new PasscodeView.PasscodeViewListener() {
                        @Override
                        public void onFail() {
                            Toast.makeText(getApplicationContext(), "Wrong passcode", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(String number) {
                            startActivity(new Intent(passcode.this , MainActivity.class));
                        }
                    });
        } else {
            startActivity(new Intent(passcode.this , MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}