package com.example.sishirschatgpt;

import static com.example.sishirschatgpt.OpenAIServer.JSON;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton viewAllChats;
    ImageButton submitChat;
    EditText textBox;
    TextView pageTopic;
    ArrayList<ChatDTO> chatDTOS;
    ChatViewAdapter viewAdapter;
    DbQueries ds;
    Integer threadID;
    DbHelper dh;
    String ApiKey = "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";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.chatRecylerView);
        viewAllChats = findViewById(R.id.viewAllChats);
        viewAllChatsListener();
        submitChat = findViewById(R.id.submitButton);
        submitChatListener();
        textBox = findViewById(R.id.inputBoxEditText);
        pageTopic = findViewById(R.id.pageTopic);
        ds = new DbQueries(this);
        dh = new DbHelper(this);

        if(extras != null) {
            threadID = extras.getInt("threadID");
            try {
                ds.open();
                chatDTOS = ds.getChatDTOListBasedOfThread(threadID);
                ds.close();
            }
            catch (Exception e) {
                Toast.makeText(this, "Error retrieving last message", Toast.LENGTH_LONG).show();
            }
            changePageTopicText();
        }
        else{
            threadID = -1;
            chatDTOS = new ArrayList<ChatDTO>();
        }
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        viewAdapter = new ChatViewAdapter(chatDTOS, this);
        recyclerView.setAdapter(viewAdapter);
    }

    private void changePageTopicText(){
        String string = chatDTOS.get(0).getUserMessage();
        String substring = string.substring(0, Math.min(string.length(), 30));
        pageTopic.setText(substring);
    }

    private void submitChatListener(){
        submitChat.setOnClickListener(v -> {
            submitUserResponse();
            textBox.setText("");
        });
    }

    private void viewAllChatsListener(){
        viewAllChats.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, AllChatsActivity.class);
            startActivity(intent);
        });
    }

    void addToChat(ChatDTO dto){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatDTOS.add(dto);
                viewAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(viewAdapter.getItemCount());
            }
        });
    }
    void addResponse(ChatDTO dto){
        chatDTOS.remove(chatDTOS.size()-1);
        addToChat(dto);
        insertNewMessage(dto);
    }

    private void submitUserResponse(){
        String userInput = String.valueOf(textBox.getText());
        OpenAIServer server = new OpenAIServer(ChatActivity.this, threadID);
        ChatDTO dto = new ChatDTO();
        dto.setUserMessage(userInput);
        if(threadID == -1){
            int newThreadID = creatNewThread();
            dto.setThreadID(newThreadID);
        }
        else{
            dto.setThreadID(threadID);
        }
        contactOpenAI(dto);
    }

    private void insertNewMessage(ChatDTO dto){
        try {
            ds.open();
            ds.insertIntoMessageTable(dto);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Error retrieving last message", Toast.LENGTH_LONG).show();
        }
    }

    private int creatNewThread(){
        int newThreadID = -1;
        String userInput = String.valueOf(textBox.getText());
        String substring = userInput.substring(0, Math.min(userInput.length(), 30));
        try {
            ds.open();
            newThreadID =  ds.createNewThread(substring);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Error retrieving last message", Toast.LENGTH_LONG).show();
        }
        return newThreadID;
    }

    void showAiWaitingMessage(){
        ChatDTO temp = new ChatDTO();
        temp.setAiMessage("Thinking....");
        addToChat(temp);
    }

    void contactOpenAI(ChatDTO dto){
        JSONArray messages = new JSONArray();
        try {
            for (ChatDTO chatDTO : chatDTOS) {
            JSONObject message1 = new JSONObject();
            JSONObject message2 = new JSONObject();
            message1.put("role", "user");
            message1.put("content", chatDTO.getUserMessage());
            message2.put("role", "assistant");
            message2.put("content", chatDTO.getAiMessage());
            messages.put(message1);
            messages.put(message2);
            }
            JSONObject message1 = new JSONObject();
            message1.put("role", "user");
            message1.put("content", dto.getUserMessage());
            messages.put(message1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer"+ ApiKey)
                .post(body)
                .build();

        showAiWaitingMessage();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                dto.setAiMessage("Failed to load response due to "+e.getMessage());
                addResponse(dto);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject responseJson = new JSONObject(response.body().string());
                    JSONArray choices = responseJson.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                        String content = message.getString("content");
                        dto.setAiMessage(content);
                        addResponse(dto);
                        changePageTopicText();
                        // "content" now contains the value "\n\nHello there, how may I assist you today?"
                        // You can use this value as needed
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}