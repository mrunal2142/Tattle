package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.md.tattle.Adapter.participantAdapter;
import com.md.tattle.Models.participants;
import com.md.tattle.Models.userModel;
import com.md.tattle.Utilities.constants;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityAddParticipantsBinding;

import java.util.ArrayList;

public class addParticipants extends AppCompatActivity {

    ActivityAddParticipantsBinding binding;
    ArrayList<userModel> list;
    participantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddParticipantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        {
            setSupportActionBar(binding.addPArt);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding.title.setText("Add participants");
            list = new ArrayList<>();
            binding.partRcv.setLayoutManager(new LinearLayoutManager(addParticipants.this));
        }

        getUsers();

        binding.done.setOnClickListener(v -> {
            startActivity(new Intent(addParticipants.this , MainActivity.class));
        });
    }

    public void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(constants.KEY_COLLECTION_USER)
                .get()
                .addOnCompleteListener(task -> {
                    preferanceManager preferanceManager = new preferanceManager(this);
                    String currentUserId = preferanceManager.getString(constants.KEY_USER_ID);

                    if(task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            userModel user = new userModel();
                            user.name = queryDocumentSnapshot.getString(constants.KEY_NAME);
                            user.phoneNumber = queryDocumentSnapshot.getString(constants.KEY_PHONE_NUmber);
                            user.uid = queryDocumentSnapshot.getString(constants.KEY_USER_ID);
                            user.profImgUrl = queryDocumentSnapshot.getString(constants.KEY_IMAGE);
                            user.token =  queryDocumentSnapshot.getString(constants.KEY_FCM_TOKEN);
                            if(!currentUserId.equals(queryDocumentSnapshot.getId())) {
                                    list.add(user);
                            }

                        }

                        list.size();

                        if(list.size() > 0) {
                            adapter = new participantAdapter(list,addParticipants.this , getIntent().getStringExtra("gId"));
                            binding.partRcv.setAdapter(adapter);
                        } else {
                            Toast.makeText(getApplicationContext(), "Fail to update contacts due to network error. Please restart the app", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }

}