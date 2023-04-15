package com.example.sishirschatgpt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class ChatViewAdapter extends RecyclerView.Adapter{
    private ArrayList<ChatDTO> myChatDTO;
    private View.OnClickListener myOnItemClickListener;
    private Context parentContext;

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView aiMessage;
        public TextView userMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            aiMessage = itemView.findViewById(R.id.aiMessage);
            userMessage = itemView.findViewById(R.id.userMessage);
            itemView.setTag(this);
        }
        public TextView getAiMessage() {
            return aiMessage;
        }
        public TextView getUserMessage() {
            return userMessage;
        }
    }

    public ChatViewAdapter(ArrayList<ChatDTO> chatDTOS, Context context) {
        myChatDTO = chatDTOS;
        parentContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

     if(myChatDTO.get(position).getAiMessage() != null){
         ChatViewHolder cvh = (ChatViewHolder) holder;
         cvh.getAiMessage().setText((CharSequence) myChatDTO.get(position).getAiMessage());
     }
    if(myChatDTO.get(position).getAiMessage() != null){
        ChatViewHolder cvh2 = (ChatViewHolder) holder;
        cvh2.getUserMessage().setText((CharSequence) myChatDTO.get(position).getUserMessage());
        }
    }

    @Override
    public int getItemCount() {
        return myChatDTO.size();
    }
}