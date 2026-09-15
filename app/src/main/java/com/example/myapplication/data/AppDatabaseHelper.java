package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类。
 *
 * <p>本作业要求"所有的用户数据以及业务数据均需要存储在数据库中"，
 * 所以用户、好友、天气数据都放在 SQLite 里。
 *
 * <p>表结构：
 * <ul>
 *   <li>t_user        用户（用户名、密码、头像序号）</li>
 *   <li>t_friend      好友（姓名、城市、天气、头像序号）</li>
 *   <li>t_weather_day 未来 7 天天气（第几天 + 状况 + 最高/最低温）</li>
 *   <li>t_weather_now 实时天气（城市、温度、湿度、风力、空气质量）</li>
 * </ul>
 *
 * <p>天气日期不直接入库，只存"第几天"（0 = 今天），
 * 读取时再按当前日期换算，这样数据不会过期。
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "weather_app.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USER = "t_user";
    public static final String TABLE_FRIEND = "t_friend";
    public static final String TABLE_WEATHER_DAY = "t_weather_day";
    public static final String TABLE_WEATHER_NOW = "t_weather_now";

    private static AppDatabaseHelper instance;

    public static synchronized AppDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL,"
                + "avatar_index INTEGER NOT NULL DEFAULT 0,"
                + "created_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_FRIEND + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "city TEXT NOT NULL,"
                + "condition TEXT,"
                + "temp INTEGER,"
                + "avatar_index INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_WEATHER_DAY + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "day_offset INTEGER NOT NULL,"
                + "condition TEXT,"
                + "high INTEGER,"
                + "low INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_WEATHER_NOW + " ("
                + "id INTEGER PRIMARY KEY,"
                + "city TEXT,"
                + "temp INTEGER,"
                + "humidity TEXT,"
                + "wind TEXT,"
                + "air TEXT)");

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 课程项目里的简单做法：直接重建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER_NOW);
        onCreate(db);
    }

    /** 首次建库时写入初始数据。 */
    private void seedInitialData(SQLiteDatabase db) {
        // 好友
        Object[][] friends = {
                {"林小雨", "上海", "多云", 27, 1},
                {"陈子昂", "广州", "雷阵雨", 31, 2},
                {"王思远", "成都", "阴", 22, 3},
                {"赵晓晓", "哈尔滨", "小雪", 3, 0},
                {"刘一鸣", "西安", "晴", 25, 1},
                {"周未然", "杭州", "小雨", 20, 2},
        };
        for (Object[] row : friends) {
            ContentValues values = new ContentValues();
            values.put("name", (String) row[0]);
            values.put("city", (String) row[1]);
            values.put("condition", (String) row[2]);
            values.put("temp", (Integer) row[3]);
            values.put("avatar_index", (Integer) row[4]);
            db.insert(TABLE_FRIEND, null, values);
        }

        // 未来 7 天
        String[] conditions = {"晴", "多云", "阴", "小雨", "雷阵雨", "多云", "晴"};
        int[] highs = {30, 29, 27, 24, 23, 26, 28};
        int[] lows = {21, 20, 19, 18, 17, 19, 20};
        for (int i = 0; i < conditions.length; i++) {
            ContentValues values = new ContentValues();
            values.put("day_offset", i);
            values.put("condition", conditions[i]);
            values.put("high", highs[i]);
            values.put("low", lows[i]);
            db.insert(TABLE_WEATHER_DAY, null, values);
        }

        // 实时天气
        ContentValues now = new ContentValues();
        now.put("id", 1);
        now.put("city", "北京");
        now.put("temp", 26);
        now.put("humidity", "45%");
        now.put("wind", "东北风 3 级");
        now.put("air", "优");
        db.insert(TABLE_WEATHER_NOW, null, now);
    }
}
