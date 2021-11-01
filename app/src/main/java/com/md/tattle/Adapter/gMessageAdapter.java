package com.md.tattle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.md.tattle.Models.gMessageModel;
import com.md.tattle.Models.messageModel;
import com.md.tattle.R;
import com.md.tattle.databinding.ItemContainerReceiveGMessageBinding;
import com.md.tattle.databinding.ItemContainerReceiveMessageBinding;
import com.md.tattle.databinding.ItemContainerSentMessageBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarviewglide.GlideLoader;

public class gMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    private final List<gMessageModel> list;
    static Context context;

    public gMessageAdapter(List<gMessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_sent_message , parent , false);
            return new sentGMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_receive_g_message , parent , false);
            return new receiveGMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((sentGMessageViewHolder) holder).setData(list.get(position));
        }
        else {
            ((receiveGMessageViewHolder ) holder).setData(list.get(position));
        }
    }

    public int getItemViewType(int position) {
        if (FirebaseAuth.getInstance().getUid().equals(list.get(position).getSendersId())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class sentGMessageViewHolder extends RecyclerView.ViewHolder {

        ItemContainerSentMessageBinding binding;

        public sentGMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerSentMessageBinding.bind(itemView);
        }

        void setData(gMessageModel gMessageModel) {
            binding.messageBlueText.setText(gMessageModel.getSendersMessage());
            long time = gMessageModel.getSendersMsgTimeStamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            binding.messageBlueTimeStamp.setText(dateFormat.format(new Date(time)));
        }
    }

    static class receiveGMessageViewHolder extends RecyclerView.ViewHolder {

        ItemContainerReceiveGMessageBinding binding;
//        TextView text;

        public receiveGMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerReceiveGMessageBinding.bind(itemView);
//            text = itemView.findViewById(R.id.dMessageGreyText);
        }

        IImageLoader imageLoader;

        void setData(gMessageModel gMessageModel) {
            binding.dMessageGreyText.setText(gMessageModel.getSendersMessage());
            long time = gMessageModel.getSendersMsgTimeStamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            binding.dMessageGreyTimeStamp.setText(dateFormat.format(new Date(time)));
            binding.dName.setText(gMessageModel.getSendersName());

            if (gMessageModel.getSendersIcon() != null) {
                Picasso.get().load(gMessageModel.getSendersIcon()).into(binding.gMsgIcon);
            } else {
                try {
                    imageLoader = new GlideLoader();
                    imageLoader.loadImage(binding.gMsgIcon, "http:/example.com/user/someUserAvatar.png", gMessageModel.getSendersName());
                } catch (Exception e) {
                    Toast.makeText(context, "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
