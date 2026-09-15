package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息表的增删查改。
 */
public class MessageDao {

    private final AppDatabaseHelper helper;

    public MessageDao(Context context) {
        helper = AppDatabaseHelper.getInstance(context);
    }

    /** 查：某个好友的全部消息，按时间正序。 */
    public List<ChatMessage> listByFriend(long friendId) {
        List<ChatMessage> messages = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_MESSAGE, null,
                "friend_id = ?", new String[]{String.valueOf(friendId)},
                null, null, "time ASC, id ASC");
        try {
            while (cursor.moveToNext()) {
                messages.add(new ChatMessage(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("friend_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("from_me")) == 1,
                        cursor.getLong(cursor.getColumnIndexOrThrow("time"))));
            }
        } finally {
            cursor.close();
        }
        return messages;
    }

    /** 增：插入一条消息。 */
    public long insert(ChatMessage message) {
        ContentValues values = new ContentValues();
        values.put("friend_id", message.getFriendId());
        values.put("content", message.getContent());
        values.put("from_me", message.isFromMe() ? 1 : 0);
        values.put("time", message.getTime());
        return helper.getWritableDatabase().insert(AppDatabaseHelper.TABLE_MESSAGE, null, values);
    }

    /** 删：删除一条消息。 */
    public int delete(long messageId) {
        return helper.getWritableDatabase().delete(AppDatabaseHelper.TABLE_MESSAGE,
                "id = ?", new String[]{String.valueOf(messageId)});
    }

    /** 删：清空某个好友的全部聊天记录。 */
    public int deleteByFriend(long friendId) {
        return helper.getWritableDatabase().delete(AppDatabaseHelper.TABLE_MESSAGE,
                "friend_id = ?", new String[]{String.valueOf(friendId)});
    }

    /** 查：某个好友的消息条数。 */
    public int countByFriend(long friendId) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + AppDatabaseHelper.TABLE_MESSAGE + " WHERE friend_id = ?",
                new String[]{String.valueOf(friendId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** 查：全部消息条数。 */
    public int count() {
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM " + AppDatabaseHelper.TABLE_MESSAGE, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** 查：某个好友的最后一条消息，没有则返回 null。 */
    public ChatMessage lastByFriend(long friendId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppDatabaseHelper.TABLE_MESSAGE, null,
                "friend_id = ?", new String[]{String.valueOf(friendId)},
                null, null, "time DESC, id DESC", "1");
        try {
            if (cursor.moveToFirst()) {
                return new ChatMessage(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("friend_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("from_me")) == 1,
                        cursor.getLong(cursor.getColumnIndexOrThrow("time")));
            }
            return null;
        } finally {
            cursor.close();
        }
    }
}
