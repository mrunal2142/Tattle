package com.md.tattle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.md.tattle.Adapter.gMessageAdapter;
import com.md.tattle.Models.gMessageModel;
import com.md.tattle.Models.messageModel;
import com.md.tattle.R;
import com.md.tattle.Utilities.constants;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityGroupBinding;
import com.md.tattle.databinding.ActivityGroupChatBinding;
import com.md.tattle.encryption.encryptionAes;
import com.md.tattle.fragment.newGroupFragment;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarviewglide.GlideLoader;

public class groupChat extends Base {

    ActivityGroupChatBinding binding;
    ArrayList<gMessageModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        {
            setSupportActionBar(binding.toolbar3);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            binding.gName.setText(getIntent().getStringExtra("name"));

            if(getIntent().getStringExtra("gImg") != null) {
                Picasso.get().load(getIntent().getStringExtra("gImg")).into(binding.gImg);
            }else {
                try {
                    IImageLoader imageLoader;
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(binding.gImg , "http:/example.com/user/someUserAvatar.png", getIntent().getStringExtra("name"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

            binding.gRcv.setLayoutManager(new LinearLayoutManager(groupChat.this));

            list = new ArrayList<>();

            encryptionAes.initEncyrption();
        }

        //sending chats
        binding.sendButton.setOnClickListener(v -> {

            if(binding.messageText.getText().toString().equals("")) {
                return;
            }

            preferanceManager preferanceManager = new preferanceManager(groupChat.this);

            try {
                gMessageModel gMsg = new gMessageModel(preferanceManager.getString(constants.KEY_NAME) , preferanceManager.getString(constants.KEY_IMAGE),
                        encryptionAes.encrypt( binding.messageText.getText().toString()), FirebaseAuth.getInstance().getUid(), new Date().getTime());

                binding.messageText.setText(null);

                FirebaseDatabase.getInstance().getReference("Groups")
                        .child(getIntent().getStringExtra("gId"))
                        .child("Messages")
                        .push()
                        .setValue(gMsg)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Msg sent", Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        //receive chats
        try {
            FirebaseDatabase.getInstance().getReference("Groups")
                    .child(getIntent().getStringExtra("gId"))
                    .child("Messages")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            list.clear();

                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                gMessageModel gMsg = dataSnapshot.getValue(gMessageModel.class);

                                try {
                                    gMsg.setSendersMessage(encryptionAes.decrypt(gMsg.getSendersMessage()));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                list.add(gMsg);

                            }

                            if(list.size() > 0) {
                                gMessageAdapter adapter = new gMessageAdapter(list , groupChat.this);
                                binding.gRcv.setAdapter(adapter);
                                if (list.size() > 2) {
                                    binding.gRcv.scrollToPosition(list.size() - 1);
                                }
                            } else {
                                Toast.makeText(groupChat.this, "Zero hai", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}