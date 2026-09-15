package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.model.User;

/**
 * 用户表的增删查改。
 */
public class UserDao {

    private final AppDatabaseHelper helper;

    public UserDao(Context context) {
        helper = AppDatabaseHelper.getInstance(context);
    }

    /** 查：按用户名查询用户，不存在返回 null。 */
    public User findByUsername(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppDatabaseHelper.TABLE_USER, null,
                "username = ?", new String[]{username}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("avatar_index")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    /** 增：插入新用户，返回自增 id，失败返回 -1。 */
    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("avatar_index", user.getAvatarIndex());
        values.put("created_at", user.getCreatedAt());
        return helper.getWritableDatabase().insert(AppDatabaseHelper.TABLE_USER, null, values);
    }

    /** 改：更新头像。 */
    public int updateAvatar(long userId, int avatarIndex) {
        ContentValues values = new ContentValues();
        values.put("avatar_index", avatarIndex);
        return helper.getWritableDatabase().update(AppDatabaseHelper.TABLE_USER, values,
                "id = ?", new String[]{String.valueOf(userId)});
    }

    /** 删：按用户名删除。 */
    public int deleteByUsername(String username) {
        return helper.getWritableDatabase().delete(AppDatabaseHelper.TABLE_USER,
                "username = ?", new String[]{username});
    }

    /** 查：用户总数。 */
    public int count() {
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM " + AppDatabaseHelper.TABLE_USER, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }
}
