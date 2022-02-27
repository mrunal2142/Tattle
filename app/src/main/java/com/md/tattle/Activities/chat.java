package com.md.tattle.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.md.tattle.Utilities.constants;
import com.md.tattle.encryption.encryptionAes;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.md.tattle.Adapter.messageAdapter;
import com.md.tattle.Models.messageModel;
import com.md.tattle.Models.ssModel;
import com.md.tattle.Models.userModel;
import com.md.tattle.R;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class chat extends Base {

    ActivityChatBinding b;
    AvatarView avatarView;
    IImageLoader imageLoader;
    Intent i;
    List<messageModel> messageModelList;
    FirebaseDatabase database;
    String senderRoom, receiverRoom ,  senderId , receiverId;
    userModel receiverUser;
    private messageAdapter adapter;
    public static Vibrator vibrator ;
    preferanceManager preferanceManager;

    @SuppressLint({"WrongThread", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        inti();

        setSupportActionBar(b.chatToolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b.chatName.setText(i.getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatBgTask bgTask = new chatBgTask();
        Void unused = bgTask.doInBackground();

//        Toast.makeText(getApplicationContext(), receiverUser.token, Toast.LENGTH_SHORT).show();

        //sending message
        b.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (b.messageText.getText().toString().equals("")) {
                    return;
                }

                try {

                    String encryptedText = encryptionAes.encrypt(b.messageText.getText().toString());

                    messageModel messageModel = new messageModel(senderId, encryptedText, new Date().getTime());

                    b.messageText.setText(null);

                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", messageModel.message);
                    lastMsgObj.put("lastMsgTime", messageModel.timeStamp);

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //vibrate

                            preferanceManager = new preferanceManager(getApplicationContext());

                            if(!preferanceManager.getBoolean("notificaiton")) {
                                vibrate();
                            }

                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //send notification
                                    try {
                                        sendNotification(receiverUser.name , "You have a new message from " + preferanceManager.getString(constants.KEY_NAME) , receiverUser.token);
                                    }catch(Exception e) {

                                    }

                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "sending error to team", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        //receiving message
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModelList.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            messageModel message = snapshot1.getValue(messageModel.class);

                            //decrypting
                            try {
                                message.setMessage(encryptionAes.decrypt(message.getMessage()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            messageModelList.add(message);
                        }

                        adapter.notifyDataSetChanged();
                        if (messageModelList.size() > 2) {
                            b.chatsRcv.smoothScrollToPosition(messageModelList.size() - 1);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void inti() {

        avatarView = (AvatarView) findViewById(R.id.chatProf);
        vibrator = (Vibrator) chat.this.getSystemService(Context.VIBRATOR_SERVICE);

        i = getIntent();
        messageModelList = new ArrayList<>();
        adapter = new messageAdapter(messageModelList, chat.this);
        b.chatsRcv.setAdapter(adapter);

        receiverUser = new userModel();
        receiverUser.name = i.getStringExtra("name");
        receiverUser.profImgUrl = i.getStringExtra("profImg");
        receiverUser.phoneNumber = i.getStringExtra("number");
        receiverUser.uid = i.getStringExtra("uid");
        receiverUser.email = i.getStringExtra("email");
        receiverUser.token = i.getStringExtra("token");

        senderId= FirebaseAuth.getInstance().getUid();
        receiverId = receiverUser.uid;

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void getScreenShotStatus() {
        FirebaseDatabase.getInstance().getReference().child("ScreenShot")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ssModel ssInfo = dataSnapshot.getValue(ssModel.class);

                            if (ssInfo.uid.equals(receiverUser.uid)) {

                                if (ssInfo.status) {
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                                }

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public class chatBgTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (i.getStringExtra("profImg") != null) {
                Picasso.get().load(i.getStringExtra("profImg")).into(avatarView);
            } else {
                try {
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatarView, "http:/example.com/user/someUserAvatar.png", i.getStringExtra("name"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

            getScreenShotStatus();

            getAvailability();

            return null;
        }
    }

    private void getAvailability() {

        FirebaseDatabase.getInstance().getReference().child("presence").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    String presence = snapshot.getValue(String.class);

                    if(!presence.isEmpty()) {
                        b.presence.setText(presence);
                    }
                } else {
                        b.presence.setText("Last seen few moments ago");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void vibrate (){

        VibrationEffect vibrationEffect1;
        // this is the only type of the vibration which requires system version Oreo (API 26)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // this effect creates the vibration of default amplitude for 1000ms(1 sec)
            vibrationEffect1 = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE);

            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }

    }

    private void sendNotification(String name , String message , String token) {

        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            //data goes to api in json format
            /*https://images.app.goo.gl/h2rPsQuRtyUKC6rA8*/

            //inner obejct
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", name);
            jsonObject.put("body" , message);

            //mainobject
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("notification" , jsonObject);
            notificationObject.put("to" , token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationObject, response -> {
//                    Toast.makeText(chat.this , "Success", Toast.LENGTH_SHORT).show();
            }, error -> Toast.makeText(chat.this , "onErrorResponse" + error.getMessage(), Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap <String , String>  map = new HashMap<>();
                    String key = "Key=AAAA4jiNcVw:APA91bFkoK4yeM3ucctkVJ-3oNp70RuqGzmdkemaXSI_It51tEvB4UYCk-rWBMVWXUlYyrGjuQn4Wb3i9Cj3IHv_OHozjIBl3nD8l3T0YJGBGGgnVLsaBx86TFBp4sxj7Mq6wq0ST3W7";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);
                    return map;
                }
            };

            queue.add(request);

        } catch (Exception ex) {
//          ex.printStackTrace();
        }

    }


}