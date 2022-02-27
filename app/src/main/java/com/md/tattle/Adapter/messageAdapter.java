package com.md.tattle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.md.tattle.Models.messageModel;
import com.md.tattle.R;
import com.md.tattle.databinding.ItemContainerReceiveMessageBinding;
import com.md.tattle.databinding.ItemContainerSentMessageBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    private final List<messageModel> list;
    public static Context context;

    public messageAdapter(List<messageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_sent_message , parent , false);
            return new sentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_receive_message , parent , false);
            return new receiveMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((sentMessageViewHolder ) holder).setData(list.get(position));
        } else {
            ((receiveMessageViewHolder ) holder).setData(list.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (FirebaseAuth.getInstance().getUid().equals(list.get(position).senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class sentMessageViewHolder extends RecyclerView.ViewHolder {

        ItemContainerSentMessageBinding binding;

        public sentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerSentMessageBinding.bind(itemView);
        }

        void setData(messageModel messageModel) {
            binding.messageBlueText.setText(messageModel.message);
            long time = messageModel.timeStamp;
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            binding.messageBlueTimeStamp.setText(dateFormat.format(new Date(time)));
        }
    }

    static class receiveMessageViewHolder extends RecyclerView.ViewHolder {

         ItemContainerReceiveMessageBinding binding;

        public receiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerReceiveMessageBinding.bind(itemView);
        }

        void setData(messageModel messageModel ) {
            binding.messageGreyText.setText(messageModel.message);
            long time = messageModel.timeStamp;
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            binding.messageGreyTimeStamp.setText(dateFormat.format(new Date(time)));
            //translation part
            binding.materialCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(context, binding.messageGreyText.getText().toString(), Toast.LENGTH_SHORT).show();

                    TranslatorOptions options =
                            new TranslatorOptions.Builder()
                                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                                    .setTargetLanguage(TranslateLanguage.HINDI)
                                    .build();
                    final Translator englishToHindi =
                            Translation.getClient(options);

                    DownloadConditions conditions = new DownloadConditions.Builder()
                            .requireWifi()
                            .build();

                    englishToHindi.downloadModelIfNeeded(conditions)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                                englishToHindi.translate(binding.messageGreyText.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                Toast.makeText(context, "Inner success " + s, Toast.LENGTH_SHORT).show();
                                                binding.messageGreyText.setText(s);
                                            }
                                        })

                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error.
                                                        // ...
                                                        Toast.makeText(context, "Inner failure", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                                }
                            });

                    return false;
                }
            });
        }
    }
}
