package com.md.tattle.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.md.tattle.encryption.encryptionAes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.md.tattle.Adapter.usersAdapter;
import com.md.tattle.Models.ssModel;
import com.md.tattle.Models.userModel;
import com.md.tattle.R;
import com.md.tattle.Utilities.constants;
import com.md.tattle.Utilities.preferanceManager;
import com.md.tattle.databinding.ActivityMainBinding;
import com.md.tattle.fragment.newGroupFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class MainActivity extends Base {

    public static String name, profUrl, number;
    AvatarView avatarView;
    IImageLoader imageLoader;
    ActivityMainBinding binding;
    preferanceManager preferanceManager;
    public static ArrayList<String> deviceContacts;
    ArrayList<userModel> finalList;
    Context context;
    usersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        context = getApplicationContext();

        avatarView = (AvatarView) findViewById(R.id.prof);

        preferanceManager = new preferanceManager(getApplicationContext());
        encryptionAes.initEncyrption();

        avatarView.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, settings.class);
            startActivity(i);
        });
        deviceContacts = new ArrayList<>();
        finalList = new ArrayList<>();
        checkUserPermission();

        binding.group.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this , group.class));
        });

        //background tasks
        bgTask bgTask = new bgTask();
        @SuppressLint("WrongThread") Void unused = bgTask.doInBackground();

        getUsers();

    }

    public void loadUserDetails() {
        name = preferanceManager.getString(constants.KEY_NAME);
        profUrl = preferanceManager.getString(constants.KEY_IMAGE);
        number = preferanceManager.getString(constants.KEY_PHONE_NUmber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGroup:
                newGroupFragment newGroupFragment = new newGroupFragment();
                newGroupFragment.show(getSupportFragmentManager() , newGroupFragment.getTag() );
                break;
            case R.id.settings:
                Intent intent1 = new Intent(getApplicationContext(), settings.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    public void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(constants.KEY_COLLECTION_USER).document(preferanceManager.getString(constants.KEY_USER_ID));
        documentReference.update(constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> {
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Fail to update token", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserPermission() {
        //ContextCompact -> ContextCompat is a class for replacing some work with base context.
        //getContext().getColor(R.color.black,theme); {deprecated}
        //ContextCompat.getColor(getContext(),R.color.black)
        //Also ContextCompat contains other methods for functional of API 22+ such as checking permissions or adding multiple activity to stack
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //if permission isn't granted request permission
            //A helper for accessing features in Activity in a backwards compatible fashio
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContacts();
        }

    }

    public void getContacts() {
        String sorting = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor cursor = getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, sorting);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String idPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";

                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{idPhone}, null);

                if (phoneCursor.moveToNext()) {
                    String phoneNumner = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");
                    deviceContacts.add(phoneNumner);
                }
                phoneCursor.close();
            }

        }
        cursor.close();
    }

    public void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(constants.KEY_COLLECTION_USER)
                .get()
                .addOnCompleteListener(task -> {
                   String currentUserId = preferanceManager.getString(constants.KEY_USER_ID);

                   if(task.isSuccessful() && task.getResult() != null) {

                       finalList.clear();

                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                           userModel user = new userModel();
                           user.name = queryDocumentSnapshot.getString(constants.KEY_NAME);
                           user.phoneNumber = queryDocumentSnapshot.getString(constants.KEY_PHONE_NUmber);
                           user.uid = queryDocumentSnapshot.getString(constants.KEY_USER_ID);
                           user.profImgUrl = queryDocumentSnapshot.getString(constants.KEY_IMAGE);
                           user.token =  queryDocumentSnapshot.getString(constants.KEY_FCM_TOKEN);
                           if(!currentUserId.equals(queryDocumentSnapshot.getId())
                                  && (deviceContacts.contains(user.phoneNumber) ||
                                        deviceContacts.contains("+91"+user.phoneNumber) ||
                                            deviceContacts.contains("+91 "+user.phoneNumber))
                           ) {

                               if(!finalList.contains(user)) {
                                   finalList.add(user);
                               }

                           }

                       }

                       if(finalList.size() > 0) {
                           adapter = new usersAdapter(finalList,MainActivity.this);
                           binding.mainRcv.setAdapter(adapter);

                       } else {
                           Toast.makeText(getApplicationContext(), "Fail to update contacts due to network error. Please restart the app", Toast.LENGTH_SHORT).show();
                       }

                   }

                });
    }

    public class bgTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            loadUserDetails();

            if (profUrl != null) {
                Picasso.get().load(profUrl).into(avatarView);
            } else {
                try {
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatarView, "http:/example.com/user/someUserAvatar.png", name);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

            getToken();

            preferanceManager = new preferanceManager(getApplicationContext());

            ssModel ss = new ssModel();
            ss.uid = FirebaseAuth.getInstance().getUid();
            ss.status = preferanceManager.getBoolean("screenShot");

            FirebaseDatabase.getInstance().getReference().child("ScreenShot").child(FirebaseAuth.getInstance().getUid())
                    .setValue(ss)
                    .addOnSuccessListener(v -> {
                    });

            return null;
        }
    }
}