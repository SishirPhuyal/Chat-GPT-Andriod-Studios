package com.example.sishirschatgpt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class ChatThreadViewAdapter extends RecyclerView.Adapter{
    private ArrayList<ChatThreadDTO> dtos;
    private View.OnClickListener myOnItemClickListener;
    private Context parentContext;
    private Boolean deleteSwitch;

    public class DishViewHolder extends RecyclerView.ViewHolder {
        public TextView chatTopic;
        public ImageView deleteThread;
        public ImageView chatXML;
        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            chatTopic = itemView.findViewById(R.id.pageTopic);
            deleteThread = itemView.findViewById(R.id.deleteThread);
            chatXML = itemView.findViewById(R.id.chatImage);
            itemView.setTag(this);
            itemView.setOnClickListener(myOnItemClickListener);
        }
        public TextView getChatTopic() {
            return chatTopic;
        }
        public ImageView getDeleteThread() {
            return deleteThread;
        }
        public ImageView getChatXML() {
            return chatXML;
        }
    }

    public ChatThreadViewAdapter(ArrayList<ChatThreadDTO> arrayList, Context context, Boolean deleteOn ) {
        dtos = arrayList;
        parentContext = context;
        deleteSwitch = deleteOn;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.myOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_list_item, parent, false);
        return new DishViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
     DishViewHolder cvh = (DishViewHolder) holder;
        cvh.getChatTopic().setText(dtos.get(position).getTopic());
        if (deleteSwitch){
            cvh.getDeleteThread().setVisibility(View.VISIBLE);
            cvh.getChatXML().setVisibility(View.INVISIBLE);
            cvh.getDeleteThread().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(position);
                }
            });
        }
        else{
            cvh.getDeleteThread().setVisibility(View.INVISIBLE);
            cvh.getChatXML().setVisibility(View.VISIBLE);
        }
    }

    private void deleteItem(int position) {
        ChatThreadDTO dto = dtos.get(position);
        DbQueries ds = new DbQueries(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteThread(dto.getThreadID());
            ds.close();
            if (didDelete) {
                dtos.remove(position);
                notifyDataSetChanged();
            }
            else {
                Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return dtos.size();
    }
}