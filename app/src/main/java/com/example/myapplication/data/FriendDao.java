package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.model.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友表的增删查改。
 */
public class FriendDao {

    private final AppDatabaseHelper helper;

    public FriendDao(Context context) {
        helper = AppDatabaseHelper.getInstance(context);
    }

    /** 查：全部好友。 */
    public List<Friend> getAll() {
        List<Friend> friends = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_FRIEND, null, null, null, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                friends.add(read(cursor));
            }
        } finally {
            cursor.close();
        }
        return friends;
    }

    /** 查：按姓名模糊查询。 */
    public List<Friend> searchByName(String keyword) {
        List<Friend> friends = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_FRIEND, null,
                "name LIKE ?", new String[]{"%" + keyword + "%"}, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                friends.add(read(cursor));
            }
        } finally {
            cursor.close();
        }
        return friends;
    }

    /** 增：新增好友，返回自增 id。 */
    public long insert(Friend friend) {
        ContentValues values = new ContentValues();
        values.put("name", friend.getName());
        values.put("city", friend.getCity());
        values.put("condition", friend.getCondition());
        values.put("temp", friend.getTemp());
        values.put("avatar_index", friend.getAvatarIndex());
        return helper.getWritableDatabase().insert(AppDatabaseHelper.TABLE_FRIEND, null, values);
    }

    /** 改：按 id 更新好友资料。 */
    public int update(Friend friend) {
        ContentValues values = new ContentValues();
        values.put("name", friend.getName());
        values.put("city", friend.getCity());
        values.put("condition", friend.getCondition());
        values.put("temp", friend.getTemp());
        values.put("avatar_index", friend.getAvatarIndex());
        return helper.getWritableDatabase().update(AppDatabaseHelper.TABLE_FRIEND, values,
                "id = ?", new String[]{String.valueOf(friend.getId())});
    }

    /** 删：按 id 删除好友。 */
    public int delete(long friendId) {
        return helper.getWritableDatabase().delete(AppDatabaseHelper.TABLE_FRIEND,
                "id = ?", new String[]{String.valueOf(friendId)});
    }

    /** 查：好友总数。 */
    public int count() {
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM " + AppDatabaseHelper.TABLE_FRIEND, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** 查：按 id 查询单个好友。 */
    public Friend findById(long friendId) {
        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_FRIEND, null,
                "id = ?", new String[]{String.valueOf(friendId)}, null, null, null);
        try {
            return cursor.moveToFirst() ? read(cursor) : null;
        } finally {
            cursor.close();
        }
    }

    private Friend read(Cursor cursor) {
        return new Friend(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("city")),
                cursor.getString(cursor.getColumnIndexOrThrow("condition")),
                cursor.getInt(cursor.getColumnIndexOrThrow("temp")),
                cursor.getInt(cursor.getColumnIndexOrThrow("avatar_index")));
    }
}
