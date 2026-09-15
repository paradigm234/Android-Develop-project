package com.example.myapplication.common;

import com.example.myapplication.R;

/**
 * 天气状况 -> 图标。图标不入库，只存文字，取图标时再映射。
 */
public final class WeatherIcons {

    private WeatherIcons() {
    }

    public static int iconOf(String condition) {
        if (condition == null) {
            return R.drawable.ic_weather_sunny;
        }
        if (condition.contains("雪")) {
            return R.drawable.ic_weather_snow;
        }
        if (condition.contains("雷")) {
            return R.drawable.ic_weather_thunder;
        }
        if (condition.contains("雨")) {
            return R.drawable.ic_weather_rain;
        }
        if (condition.contains("阴") || condition.contains("雾")) {
            return R.drawable.ic_weather_overcast;
        }
        if (condition.contains("云")) {
            return R.drawable.ic_weather_cloudy;
        }
        return R.drawable.ic_weather_sunny;
    }
}
