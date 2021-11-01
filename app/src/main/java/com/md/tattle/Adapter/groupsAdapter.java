package com.md.tattle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.md.tattle.Activities.groupChat;
import com.md.tattle.Models.groupModel;
import com.md.tattle.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class groupsAdapter extends RecyclerView.Adapter<groupsAdapter.viewHolder> {

    List<groupModel> mlist;
    Context context;

    public groupsAdapter(List<groupModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_group , parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setUser(mlist.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context , groupChat.class);
                i.putExtra("name" , mlist.get(position).getgNmae());
                i.putExtra("gId" , mlist.get(position).getgId());
                i.putExtra("gImg" , mlist.get(position).getgIcon());
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

        void setUser(groupModel group) {

            //group photo
            if(group.getgIcon() != null) {
                Picasso.get().load(group.getgIcon()).into(avatarView);
            }else {
                try {
                    IImageLoader imageLoader;
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatarView , "http:/example.com/user/someUserAvatar.png", group.getgNmae());
                } catch (Exception e) {
                    Toast.makeText(context, "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

            //group name
            name.setText(group.getgNmae());

            //last message
            lastMsg.setText("Tap to chat! ");
        }
    }
}