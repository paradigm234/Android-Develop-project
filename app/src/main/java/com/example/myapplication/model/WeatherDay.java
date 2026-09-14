package com.example.myapplication.model;

/**
 * 一天的天气数据。
 */
public class WeatherDay {

    private final String weekday;
    private final String date;
    private final String condition;
    private final int high;
    private final int low;
    private final int iconRes;

    public WeatherDay(String weekday, String date, String condition,
                      int high, int low, int iconRes) {
        this.weekday = weekday;
        this.date = date;
        this.condition = condition;
        this.high = high;
        this.low = low;
        this.iconRes = iconRes;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getDate() {
        return date;
    }

    public String getCondition() {
        return condition;
    }

    public int getHigh() {
        return high;
    }

    public int getLow() {
        return low;
    }

    public int getIconRes() {
        return iconRes;
    }
}
