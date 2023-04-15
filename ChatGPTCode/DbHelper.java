package com.example.sishirschatgpt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sishirChat.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_PAST_MESSAGE_THREADS =
            "CREATE TABLE pastMessageThreads (threadID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "topicTittle TEXT NOT NULL," +
                    "isActive INTEGER DEFAULT 1)";

    private static final String SQL_CREATE_MESSAGE_TABLE =
            "CREATE TABLE message (messageID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "aiMessage TEXT NOT NULL," +
                    "userMessage TEXT NOT NULL," +

                    "threadID INTEGER NOT NULL,"+
                    "FOREIGN KEY (threadID) REFERENCES  pastMessageThreads(threadID))";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PAST_MESSAGE_THREADS);
        db.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS pastMessageThreads");
        db.execSQL("DROP TABLE IF EXISTS message");
        onCreate(db);
    }
}
