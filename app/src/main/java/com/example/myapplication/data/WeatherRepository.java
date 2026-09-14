package com.example.myapplication.data;

import com.example.myapplication.R;
import com.example.myapplication.model.Friend;
import com.example.myapplication.model.WeatherDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 天气数据源。
 *
 * <p>这里使用本地模拟数据，保证没有网络也能正常演示。
 * 以后要接真实接口，只要把这里换成网络请求即可。
 */
public final class WeatherRepository {

    private static final String[] WEEK_NAMES = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六",
    };

    private static final String[] CONDITIONS = {"晴", "多云", "阴", "小雨", "雷阵雨", "多云", "晴"};
    private static final int[] HIGHS = {30, 29, 27, 24, 23, 26, 28};
    private static final int[] LOWS = {21, 20, 19, 18, 17, 19, 20};
    private static final int[] ICONS = {
            R.drawable.ic_weather_sunny,
            R.drawable.ic_weather_cloudy,
            R.drawable.ic_weather_overcast,
            R.drawable.ic_weather_rain,
            R.drawable.ic_weather_thunder,
            R.drawable.ic_weather_cloudy,
            R.drawable.ic_weather_sunny,
    };

    private WeatherRepository() {
    }

    /** 未来 7 天预报，第 0 天是今天。 */
    public static List<WeatherDay> getWeeklyForecast() {
        List<WeatherDay> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        for (int i = 0; i < CONDITIONS.length; i++) {
            String weekday;
            if (i == 0) {
                weekday = "今天";
            } else if (i == 1) {
                weekday = "明天";
            } else {
                weekday = WEEK_NAMES[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            }
            days.add(new WeatherDay(weekday, dateFormat.format(calendar.getTime()),
                    CONDITIONS[i], HIGHS[i], LOWS[i], ICONS[i]));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    public static WeatherDay getToday() {
        return getWeeklyForecast().get(0);
    }

    public static int getCurrentTemp() {
        return 26;
    }

    public static String getHumidity() {
        return "45%";
    }

    public static String getWind() {
        return "东北风 3 级";
    }

    public static String getAirQuality() {
        return "优";
    }

    /** 好友和他们所在城市的天气。 */
    public static List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("林小雨", "上海 · 多云", "多云", 27, R.drawable.avatar_2));
        friends.add(new Friend("陈子昂", "广州 · 雷阵雨", "雷阵雨", 31, R.drawable.avatar_3));
        friends.add(new Friend("王思远", "成都 · 阴", "阴", 22, R.drawable.avatar_4));
        friends.add(new Friend("赵晓晓", "哈尔滨 · 小雪", "小雪", 3, R.drawable.avatar_1));
        friends.add(new Friend("刘一鸣", "西安 · 晴", "晴", 25, R.drawable.avatar_2));
        friends.add(new Friend("周未然", "杭州 · 小雨", "小雨", 20, R.drawable.avatar_3));
        return friends;
    }

    /** 根据天气状况取对应的图标。 */
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
        if (condition.contains("阴")) {
            return R.drawable.ic_weather_overcast;
        }
        if (condition.contains("云")) {
            return R.drawable.ic_weather_cloudy;
        }
        return R.drawable.ic_weather_sunny;
    }

    /** 根据今天的天气生成出行建议。 */
    public static List<String> getTravelAdvice() {
        WeatherDay today = getToday();
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
        advice.add("空气质量" + getAirQuality() + "，很适合户外活动或骑车通勤。");
        return advice;
    }
}
