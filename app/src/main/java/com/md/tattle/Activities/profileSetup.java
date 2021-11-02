package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.md.tattle.Utilities.constants;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityProfileSetupBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class profileSetup extends AppCompatActivity {

    ActivityProfileSetupBinding b;
    Uri uri;
    preferanceManager preferanceManager;
    private String profile_url = null;
    int p = 25;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityProfileSetupBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        preferanceManager = new preferanceManager(getApplicationContext());

        b.profprogressBar.setVisibility(View.GONE);

        //picking profileImg
        b.profileImg.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i , 100);
        });

         p= 25;
        b.continueBtn.setOnClickListener(v -> {
            if(validFields()) {

                if(uri != null) {
                   //uploading user with profile picture

                    b.profprogressBar.setVisibility(View.VISIBLE);
                    b.profprogressBar.setProgress(p , true);

                    b.continueBtn.setVisibility(View.INVISIBLE);

                    FirebaseStorage.getInstance().getReference().child("User Image").child(FirebaseAuth.getInstance().getUid())
                            .putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            FirebaseStorage.getInstance().getReference().child("User Image").child(FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profile_url = uri.toString();
                                    p = p +25;
                                    b.profprogressBar.setProgress(p , true);
                                    signUp();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Fail to upload profile image. Reason: "+e.getMessage()  , Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            });
                        }
                    });

                } else {
                    //uploading user without profiel picture

                    b.continueBtn.setVisibility(View.INVISIBLE);

                    b.profprogressBar.setVisibility(View.VISIBLE);
                    b.profprogressBar.setProgress(p , true);

                    p = p +25;
                    b.profprogressBar.setProgress(p , true);

                    signUp();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            uri = data.getData();
            if(uri != null) {
                Picasso.get().load(uri).into(b.profileImg);
            }
        }
    }

    boolean validFields() {
        if(b.name.getText().toString().equals("") || b.name.getText().toString().length() == 0) {
            b.name.setError("Invalid");
            return false;
        }
        if(b.email.getText().toString().equals("") || b.email.getText().toString().length() == 0 ) {
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void signUp() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String , Object> user = new HashMap<>();
        user.put(constants.KEY_NAME , b.name.getText().toString());
        user.put(constants.KEY_EMAIL , b.email.getText().toString());
        user.put(constants.KEY_USER_ID ,FirebaseAuth.getInstance().getUid());
        user.put(constants.KEY_PHONE_NUmber ,getIntent().getStringExtra("number"));
        user.put(constants.KEY_IMAGE , profile_url);

        db.collection(constants.KEY_COLLECTION_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    b.profprogressBar.setProgress(p+25 , true);

                    preferanceManager.putString(constants.KEY_USER_ID , documentReference.getId());
                    preferanceManager.putString(constants.KEY_NAME , b.name.getText().toString());
                    preferanceManager.putString(constants.KEY_IMAGE , profile_url);
                    preferanceManager.putString(constants.KEY_PHONE_NUmber , getIntent().getStringExtra("number"));

                    Toast.makeText(getApplicationContext(), "Done !", Toast.LENGTH_SHORT).show();
                    b.profprogressBar.setProgress(p+25+25 , true);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

}