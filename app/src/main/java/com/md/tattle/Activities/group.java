package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.md.tattle.Adapter.groupsAdapter;
import com.md.tattle.Models.groupModel;
import com.md.tattle.databinding.ActivityGroupBinding;

import java.util.ArrayList;
import java.util.List;

public class group extends Base {

    ActivityGroupBinding binding;
    List<groupModel> list;
    public groupsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        {
            setSupportActionBar(binding.toolbar2);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getSupportActionBar().setTitle("Groups");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            list = new ArrayList<>();
            adapter = new groupsAdapter( list, group.this);
        }

        binding.groupRcv.setLayoutManager(new LinearLayoutManager(this));
        binding.groupRcv.setAdapter(adapter);

        getGroups();

    }

    private void getGroups() {

        FirebaseDatabase.getInstance().getReference("Groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();

                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(ds.child("Participants").child(FirebaseAuth.getInstance().getUid()).exists()) {
                                groupModel group = ds.getValue(groupModel.class);
                                list.add(group);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}