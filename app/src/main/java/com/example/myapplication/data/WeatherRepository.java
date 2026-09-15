package com.example.myapplication.data;

import android.content.Context;

import com.example.myapplication.model.Friend;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.model.WeatherNow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 业务数据入口。
 *
 * <p>页面不直接写 SQL，统一通过这里读写数据库，
 * 数据来源全部是 SQLite。
 */
public class WeatherRepository {

    private final WeatherDao weatherDao;
    private final FriendDao friendDao;

    public WeatherRepository(Context context) {
        weatherDao = new WeatherDao(context);
        friendDao = new FriendDao(context);
    }

    public List<WeatherDay> getWeeklyForecast() {
        return weatherDao.getForecast();
    }

    public WeatherDay getToday() {
        List<WeatherDay> days = getWeeklyForecast();
        return days.isEmpty() ? null : days.get(0);
    }

    public WeatherNow getNow() {
        return weatherDao.getNow();
    }

    public List<Friend> getFriends() {
        return friendDao.getAll();
    }

    /** 根据今天的天气生成出行建议。 */
    public List<String> getTravelAdvice() {
        WeatherDay today = getToday();
        if (today == null) {
            return Collections.emptyList();
        }
        WeatherNow now = getNow();
        String air = now == null ? "优" : now.getAir();
        String condition = today.getCondition();

        List<String> advice = new ArrayList<>();
        if ("晴".equals(condition)) {
            advice.add("今天晴，最高 " + today.getHigh() + "°，紫外线较强，出门记得涂防晒、戴帽子。");
        } else if (condition.contains("雨")) {
            advice.add("今天有" + condition + "，出门记得带伞，路面湿滑注意慢行。");
        } else {
            advice.add("今天" + condition + "，气温 " + today.getLow() + "° ~ " + today.getHigh()
                    + "°，适合正常出行。");
        }
        advice.add("早晚温度在 " + today.getLow() + "° 左右，建议随身带一件薄外套。");
        advice.add("空气质量" + air + "，很适合户外活动或骑车通勤。");
        return advice;
    }
}
