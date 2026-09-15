package com.example.myapplication.data;

import android.content.Context;
import android.database.Cursor;

import com.example.myapplication.common.WeatherIcons;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.model.WeatherNow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 天气数据的读取。
 */
public class WeatherDao {

    private static final String[] WEEK_NAMES = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六",
    };

    private final AppDatabaseHelper helper;

    public WeatherDao(Context context) {
        helper = AppDatabaseHelper.getInstance(context);
    }

    /** 未来 7 天预报，第 0 天是今天。 */
    public List<WeatherDay> getForecast() {
        List<WeatherDay> days = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_WEATHER_DAY, null, null, null, null, null, "day_offset ASC");
        try {
            while (cursor.moveToNext()) {
                int offset = cursor.getInt(cursor.getColumnIndexOrThrow("day_offset"));
                String condition = cursor.getString(cursor.getColumnIndexOrThrow("condition"));
                int high = cursor.getInt(cursor.getColumnIndexOrThrow("high"));
                int low = cursor.getInt(cursor.getColumnIndexOrThrow("low"));

                Calendar day = (Calendar) today.clone();
                day.add(Calendar.DAY_OF_MONTH, offset);
                String weekday;
                if (offset == 0) {
                    weekday = "今天";
                } else if (offset == 1) {
                    weekday = "明天";
                } else {
                    weekday = WEEK_NAMES[day.get(Calendar.DAY_OF_WEEK) - 1];
                }
                days.add(new WeatherDay(weekday, dateFormat.format(day.getTime()),
                        condition, high, low, WeatherIcons.iconOf(condition)));
            }
        } finally {
            cursor.close();
        }
        return days;
    }

    /** 实时天气。 */
    public WeatherNow getNow() {
        Cursor cursor = helper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_WEATHER_NOW, null, "id = 1", null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return new WeatherNow(
                        cursor.getString(cursor.getColumnIndexOrThrow("city")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("temp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("humidity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("wind")),
                        cursor.getString(cursor.getColumnIndexOrThrow("air")));
            }
            return null;
        } finally {
            cursor.close();
        }
    }
}
