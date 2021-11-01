package com.md.tattle.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.md.tattle.Activities.MainActivity;
import com.md.tattle.Activities.addParticipants;
import com.md.tattle.Models.participants;
import com.md.tattle.Models.userModel;
import com.md.tattle.databinding.FragmentNewGroupBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class newGroupFragment extends BottomSheetDialogFragment {

    FragmentNewGroupBinding binding;
    public static String timeStamp;
    ArrayList<userModel> list;
    String des = null;
    int p = 25;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewGroupBinding.inflate(inflater , container , false);
        // Inflate the layout for this fragment

        //init
        uri = null;

        //Image
        binding.profileImg.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i , 100);
        });

        //creating group
        binding.continueBtn.setOnClickListener(v -> {
            timeStamp =  String.valueOf(System.currentTimeMillis());
            setCancelable(false);
            startCreatingGroup(timeStamp);
        });

        return binding.getRoot();
    }

    private void startCreatingGroup(String timeStamp) {

        if(binding.groupname.getText().toString().equals("")) {
            binding.groupname.setError("Required");
            return;
        }

        if(!binding.des.getText().toString().equals("")) {
            des = binding.des.getText().toString();
        }

        binding.continueBtn.setVisibility(View.INVISIBLE);
        binding.profprogressBar.setVisibility(View.VISIBLE);
        binding.profprogressBar.setProgress(p);
        p += 25;

        String file_path = "Group_Img/"+ "images" + timeStamp;

        if(uri != null) {
            FirebaseStorage.getInstance().getReference(file_path).putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            binding.profprogressBar.setProgress(p);
                            p += 10;

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    binding.profprogressBar.setProgress(p);
                                    p += 15;

                                    HashMap<String , String> hashMap = new HashMap<>();
                                    hashMap.put("gId" ,timeStamp);
                                    hashMap.put("gIcon" , uri.toString());
                                    hashMap.put("gNmae" , binding.groupname.getText().toString());
                                    hashMap.put("gDes" , des);
                                    hashMap.put("gAdmin" , FirebaseAuth.getInstance().getUid());

                                    FirebaseDatabase.getInstance().getReference("Groups")
                                            .child(timeStamp)
                                            .setValue(hashMap)
                                            .addOnSuccessListener(unused -> {
                                                ADMINUSER();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity().getApplicationContext(), "Failed to create group", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            });
                                }
                            });
                        }
                    });
        } else {

            binding.profprogressBar.setProgress(p);
            p += 10;

            binding.profprogressBar.setProgress(p);
            p += 15;

            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("gId" ,timeStamp);
            hashMap.put("gIcon" , null);
            hashMap.put("gNmae" , binding.groupname.getText().toString());
            hashMap.put("gDes" , des);
            hashMap.put("gAdmin" , FirebaseAuth.getInstance().getUid());

            FirebaseDatabase.getInstance().getReference("Groups")
                    .child(timeStamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(unused -> {
                        ADMINUSER();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity().getApplicationContext(), "Failed to create group", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }


    }

    void ADMINUSER() {
        participants participants = new participants(MainActivity.name, FirebaseAuth.getInstance().getUid() , "Admin");
        putUser(participants);
    }

    public void putUser(participants participants) {
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(timeStamp)
                .child("Participants")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(participants)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity().getApplicationContext(), "Group created please add participants", Toast.LENGTH_SHORT).show();
                        binding.profprogressBar.setProgress(100);
                        dismiss();
                        startActivity(new Intent(getActivity().getApplicationContext() , addParticipants.class)
                        .putExtra("gId" , timeStamp));
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK) {
            uri = data.getData();
            if(uri != null) {
                Picasso.get().load(uri).resize(50 , 50 ).into(binding.profileImg);
            }
        }
    }

}