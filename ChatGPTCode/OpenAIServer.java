package com.example.sishirschatgpt;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class OpenAIServer {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "JeHbsZ56CA1Hy0GgU97mT3BlbkFJ7eaS5OBRDHBJC4WWPjYb";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private Context mainActivityContext;
    private Integer threadID;
    private DbQueries ds;
    private Integer bool = 0;

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

    private String newString = new String();

    public OpenAIServer(Context context, Integer threadID) {
        this.mainActivityContext = context;
        this.threadID = threadID;
        this.ds = new DbQueries(mainActivityContext);
    }

    public void setBool(){

    }

    public void makeApiRequest(String question) {
        client = new OkHttpClient();
        JSONArray messages = new JSONArray();

        /*if(threadID >= 0){
            try {
                ds.open();
                ArrayList<ChatDTO> dto = ds.getChatDTOListBasedOfThread(threadID);
                ds.close();

                for (ChatDTO chatDTO : dto) {
                    JSONObject message1 = new JSONObject();
                    JSONObject message2 = new JSONObject();
                    message1.put("role", "user");
                    message1.put("content", chatDTO.getUserMessage());
                    message2.put("role", "assistant");
                    message2.put("content", chatDTO.getAiMessage());
                    messages.put(message1);
                    messages.put(message2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        try {
            JSONObject message1 = new JSONObject();
            message1.put("role", "user");
            message1.put("content", question);
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

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();
        do {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    bool = 1;
                    newString = response.body().string();
                    ResponseBody body1 = response.body();

                    /*try {
                     *//*JSONObject responseJson = new JSONObject(response.body().string());
                    JSONArray choices = responseJson.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        newString = result.trim();
                    }*//*
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                }
            });
        } while (bool == 0);
    }
}

