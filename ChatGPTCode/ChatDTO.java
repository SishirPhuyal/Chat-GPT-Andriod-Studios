package com.example.sishirschatgpt;

public class ChatDTO {
    public String getAiMessage() {
        return aiMessage;
    }
    public void setAiMessage(String aiMessage) {
        this.aiMessage = aiMessage;
    }
    private String aiMessage;

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    private String userMessage;

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    private int messageID;

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    private int threadID;
    ChatDTO(){
    }
}
