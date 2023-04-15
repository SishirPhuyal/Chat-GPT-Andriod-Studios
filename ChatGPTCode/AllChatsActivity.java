package com.example.sishirschatgpt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class AllChatsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ChatThreadDTO> topicDTOS;
    DbQueries ds = new DbQueries(AllChatsActivity.this);
    ChatThreadViewAdapter viewAdapter;
    ImageButton newChat;
    ImageButton deleteChat;
    ImageButton removeDelete;
    Boolean deleteSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_chats_activity);
        recyclerView = findViewById(R.id.threadRecylerView);
        deleteChat = findViewById(R.id.deleteChat);
        newChat = findViewById(R.id.newChat);
        removeDelete = findViewById(R.id.removeDelete);
        newChatListener();


        if(extras != null) {
            deleteSwitch = extras.getBoolean("deleteSwitch");
            if (deleteSwitch){
                deleteChat.setVisibility(View.INVISIBLE);
                removeDelete.setVisibility(View.VISIBLE);
                removeDeleteListener();
            }
        }
        else{
            deleteChat.setVisibility(View.VISIBLE);
            removeDelete.setVisibility(View.INVISIBLE);
            deleteChatListener();
        }
        try {
            ds.open();
            topicDTOS = ds.getAllThreads();
            ds.close();
        }
        catch (Exception e) {
            topicDTOS = new ArrayList<ChatThreadDTO>();
            Toast.makeText(this, "Error retrieving threads", Toast.LENGTH_LONG).show();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        viewAdapter = new ChatThreadViewAdapter(topicDTOS, this, deleteSwitch);
        viewAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(viewAdapter);
    }

    private void removeDeleteListener(){
        removeDelete.setOnClickListener(v -> {
            Intent intent = new Intent(AllChatsActivity.this, AllChatsActivity.class);
            startActivity(intent);
        });
    }
    private void newChatListener(){
        newChat.setOnClickListener(v -> {
            Intent intent = new Intent(AllChatsActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void deleteChatListener(){
        deleteChat.setOnClickListener(v -> {
            Intent intent = new Intent(AllChatsActivity.this, AllChatsActivity.class);
            intent.putExtra("deleteSwitch", true);
            startActivity(intent);
        });
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int threadID = topicDTOS.get(position).getThreadID();
            Intent intent = new Intent(AllChatsActivity.this, ChatActivity.class);
            intent.putExtra("threadID", threadID);
            startActivity(intent);
        }
    };
}