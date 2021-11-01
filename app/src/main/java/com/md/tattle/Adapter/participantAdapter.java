package com.md.tattle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.md.tattle.Models.participants;
import com.md.tattle.Models.userModel;
import com.md.tattle.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class participantAdapter extends RecyclerView.Adapter<participantAdapter.viewHolder> {

    List<userModel> mlist;
    Context context;
    String id;

    public participantAdapter(List<userModel> mlist, Context context , String id) {
        this.mlist = mlist;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.participant_layout, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.name.setText(mlist.get(position).name);
        holder.lastMsg.setText(mlist.get(position).phoneNumber);
        if(mlist.get(position).profImgUrl != null) {
            Picasso.get().load(mlist.get(position).profImgUrl).into(holder.avatarView);
        }else {
            try {
                IImageLoader imageLoader;
                imageLoader = new GlideLoader();
                imageLoader.loadImage(holder.avatarView , "http:/example.com/user/someUserAvatar.png", mlist.get(position).name);
            } catch (Exception e) {
                Toast.makeText(context, "Fail to load image", Toast.LENGTH_SHORT).show();
            }
        }

        holder.add.setOnClickListener(v -> {

            participants participants = new participants( mlist.get(position).name , mlist.get(position).uid);

            FirebaseDatabase.getInstance().getReference("Groups")
                    .child(id)
                    .child("Participants")
                    .child(mlist.get(position).uid)
                    .setValue(participants)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            holder.add.setVisibility(View.INVISIBLE);
                            holder.done.setVisibility(View.VISIBLE);
                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        AvatarView avatarView;
        TextView name , lastMsg;
        Button add;
        ImageView done;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.addPImg);
            name = (TextView) itemView.findViewById(R.id.addPName);
            lastMsg = itemView.findViewById(R.id.addPNumber);
            add = itemView.findViewById(R.id.addP);
            done = itemView.findViewById(R.id.done);
        }

    }

}