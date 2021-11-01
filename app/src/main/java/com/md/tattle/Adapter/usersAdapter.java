package com.md.tattle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.md.tattle.Activities.chat;
import com.md.tattle.Models.userModel;
import com.md.tattle.R;
import com.md.tattle.encryption.encryptionAes;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class usersAdapter extends RecyclerView.Adapter<usersAdapter.viewHolder> {

    List<userModel> mlist;
    Context context;

    public usersAdapter(List<userModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_user , parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setUser(mlist.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context , chat.class);
                i.putExtra("name" , mlist.get(position).name);
                i.putExtra("profImg" , mlist.get(position).profImgUrl);
                i.putExtra("uid" , mlist.get(position).uid);
                i.putExtra("number",mlist.get(position).phoneNumber);
                i.putExtra("email" , mlist.get(position).email);
                i.putExtra("token", mlist.get(position).token);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public static ImageView sent;

    public class viewHolder extends RecyclerView.ViewHolder {

        AvatarView avatarView;
        TextView name , lastMsg, time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.rcv_img);
            name = itemView.findViewById(R.id.rcv_name);
            lastMsg = itemView.findViewById(R.id.rcv_number);
            time = itemView.findViewById(R.id.rcv_time);
            sent = itemView.findViewById(R.id.sent);

        }

        void setUser(userModel user) {
            if(user.profImgUrl != null) {
                Picasso.get().load(user.profImgUrl).into(avatarView);
            }else {
                try {
                    IImageLoader imageLoader;
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatarView , "http:/example.com/user/someUserAvatar.png", user.name);
                } catch (Exception e) {
                    Toast.makeText(context, "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

            name.setText(user.name);

            String senderRoom = FirebaseAuth.getInstance().getUid() + user.uid;

            try {
                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //setting last message
                                if (snapshot.child("lastMsg").getValue(String.class) != null) {
                                    try {
                                        String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                        if (!lastMsg.isEmpty()) {

                                            encryptionAes.initEncyrption();
                                            try {
                                                viewHolder.this.lastMsg.setText(encryptionAes.decrypt(lastMsg));
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }


                                } else {
                                      lastMsg.setText("+91 " + user.phoneNumber);
                                    sent.setVisibility(View.INVISIBLE);
                                }

                                //setting time
                                if(snapshot.child("lastMsgTime").getValue(Long.class) != null) {

                                    long mili = snapshot.child("lastMsgTime").getValue(Long.class);

                                    Instant now = Instant.now();

                                    Date lastDate = new Date(mili);
                                    Instant lst = lastDate.toInstant();

                                    Instant twentyFourHoursEarlier = now.minus( 24 , ChronoUnit.HOURS );
                                    Instant fourtyEightHoursEarlier = now.minus( 48 , ChronoUnit.HOURS );

                                    Boolean within24Hours = ( ! lst.isBefore( twentyFourHoursEarlier ) ) &&  lst.isBefore( now ) ;
                                    Boolean within48Hours = ( ! lst.isBefore( fourtyEightHoursEarlier ) ) &&  lst.isBefore( now ) ;

                                    if(within24Hours) {
                                        time.setText( new SimpleDateFormat("hh:mm a").format(mili));
                                    } else if (within48Hours) {
                                        time.setText("Yesterday");
                                    }else {
                                        time.setText( new SimpleDateFormat("dd/MM/yyyy").format(mili));
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        } catch (Exception e) {
                Toast.makeText(context, "error loading recent message", Toast.LENGTH_SHORT).show();
            }
        }
    }
}