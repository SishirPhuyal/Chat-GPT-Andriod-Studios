package com.example.sishirschatgpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

public class DbQueries {

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DbQueries(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /*public ArrayList<ChatDTO> getLastChatDTO(int threadID) {
        ArrayList<ChatDTO> newList = new ArrayList<ChatDTO>();
    }*/

   /* public int getLastThreadID() {
        try {
            String query = "SELECT * FROM pastMessageThreads";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                cursor.close();
                return 0;
            } while (cursor.moveToNext());

        } catch (Exception e) {
        }
        return -1;
    }*/


    public ArrayList<ChatThreadDTO> getAllThreads() {
        ArrayList<ChatThreadDTO> newList = new ArrayList<ChatThreadDTO>();
        try {
            String query = "SELECT * FROM pastMessageThreads order by threadID desc";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                ChatThreadDTO temp = new ChatThreadDTO();
                temp.setThreadID(cursor.getInt(0));
                temp.setTopic(cursor.getString(1));
                newList.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            newList = new ArrayList<ChatThreadDTO>();
        }
        return newList;
    }


    public ArrayList<ChatDTO> getChatDTOListBasedOfThread(int threadID) {
        ArrayList<ChatDTO> newList = new ArrayList<ChatDTO>();

        try {
            String query = "SELECT * FROM pastMessageThreads natural join message where threadID = " + threadID + " order by messageID asc";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                ChatDTO temp = new ChatDTO();
                temp.setAiMessage(cursor.getString(4));
                temp.setUserMessage(cursor.getString(5));
                temp.setThreadID(cursor.getInt(0));
                temp.setMessageID(cursor.getInt(3));
                newList.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            newList = new ArrayList<ChatDTO>();
        }
        return newList;
    }

    public int createNewThread(String topicTittle) {
        ContentValues values = new ContentValues();
        values.put("topicTittle", topicTittle);
        long newRowId = database.insert("pastMessageThreads", null, values);
        return (int) newRowId;
    }


    public void insertIntoMessageTable(ChatDTO dto) {
        ContentValues values = new ContentValues();
        values.put("aiMessage", dto.getAiMessage());
        values.put("userMessage", dto.getUserMessage());
        values.put("threadID", dto.getThreadID());
        long newRowId = database.insert("message", null, values);

    }

    public boolean deleteThread(int threadID) {
        boolean didDelete = false;
        ArrayList<ChatDTO> thread = getChatDTOListBasedOfThread(threadID);
        try {
            didDelete = database.delete("pastMessageThreads", "threadID=" + threadID, null) > 0;
        } catch (Exception e) {
        }
        removeAllBets(thread);
        return didDelete;
    }

    public void removeAllBets(ArrayList<ChatDTO> dtos) {
        for (ChatDTO dto : dtos){
            try {
                boolean didDelete = false;
                didDelete = database.delete("message", "messageID=" + dto.getMessageID(), null) > 0;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
    /*public int addFightsAdminOnly(FightCardObject object) {
        ContentValues values = new ContentValues();
        values.put("fighterOneName", object.getFighterOneName());
        values.put("fighterTwoName", object.getFighterTwoName());
        values.put("fighterOneOdds", object.getFighterOneOdds());
        values.put("fighterTwoOdds", object.getFighterTwoOdds());
        values.put("startTime", object.getStartTime());
        long newRowId = database.insert("activeFights", null, values);
        return (int) newRowId;
    }


    public void insertBetIntoTable(int fightID, int betAmount, int fighterPick) {
        ContentValues values = new ContentValues();
        values.put("fightID", fightID);
        values.put("betAmount", betAmount);
        values.put("fighterPick", fighterPick);
        long newRowId = database.insert("bets", null, values);
        updateFightStatus(fightID);
    }

    public ArrayList<FightCardObject> getActiveFights() {
        ArrayList<FightCardObject> fightCardObjects = new ArrayList<FightCardObject>();
        try {
            String query = "SELECT * FROM activeFights where isActive = 1 order by startTime asc";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                FightCardObject temp = new FightCardObject(
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(2),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(0)
                );
                fightCardObjects.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            fightCardObjects = new ArrayList<FightCardObject>();
        }
        return fightCardObjects;
    }

    public FightCardObject getFightCardByFightID(int fightID) {
        FightCardObject fightCardObject = new FightCardObject();
        try {
            String query = "SELECT * FROM activeFights where fightID =" + String.valueOf(fightID);
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                FightCardObject temp = new FightCardObject(
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(2),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(0)

                );
                cursor.close();
                return temp;
            } while (cursor.moveToNext());


        } catch (Exception e) {

        }
        return fightCardObject;
    }

    private void updateFightStatus(int fightID) {
        boolean didSucceed = false;
        try {
            ContentValues values = new ContentValues();

            values.put("isActive", 0);
            didSucceed = database.update("activeFights", values, "fightID=" + fightID, null) > 0;

        } catch (Exception e) {
            //Do nothing -will return false if there is an exception
        }
    }


    public ArrayList<MyBetDTO> getMyBets() {
        ArrayList<MyBetDTO> arrayList = new ArrayList<MyBetDTO>();
        try {
            String query = "SELECT * FROM bets";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            do {
                MyBetDTO temp = new MyBetDTO();
                temp.setBetID(cursor.getInt(0));
                temp.setFightCard(getFightCardByFightID(cursor.getInt(1)));
                temp.setBetAmount(cursor.getInt(2));
                temp.setFighterPick(cursor.getInt(3));
                arrayList.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            arrayList = new ArrayList<MyBetDTO>();
        }
        return arrayList;
    }

    public void removeAllBets() {
        ArrayList<MyBetDTO> dtos = getMyBets();
        for (MyBetDTO dto : dtos){
            try {
                boolean didDelete = false;
                didDelete = database.delete("bets", "betID=" + dto.getBetID(), null) > 0;
                if (didDelete){
                    deleteFightByFightID(dto.getFightCard().getFightID());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void deleteFightByFightID(int fightID) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("activeFights", "fightID=" + fightID, null) > 0;
        }
        catch (Exception e) {
        }
    }*/


