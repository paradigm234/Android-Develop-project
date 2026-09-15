package com.example.myapplication.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.myapplication.model.Friend;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.model.WeatherNow;
import com.example.myapplication.net.WeatherApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 业务数据入口。
 *
 * <p>页面不直接写 SQL，统一通过这里读写数据库，
 * 数据来源全部是 SQLite。
 */
public class WeatherRepository {

    /** 联网放在后台线程，避免卡住界面。 */
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

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

    /** 联网刷新的回调。 */
    public interface RefreshCallback {
        void onSuccess(String city);

        void onFailure(String message);
    }

    /**
     * 联网获取真实天气：后台请求接口 -> 写入数据库 -> 回主线程通知界面。
     *
     * <p>界面本身仍然只读数据库，联网只是负责把数据库刷新成最新数据。
     */
    public void refreshFromNetwork(String cityName, RefreshCallback callback) {
        EXECUTOR.execute(() -> {
            try {
                WeatherApiClient.Place place = WeatherApiClient.geocode(cityName);
                if (place == null) {
                    String message = "没有找到城市：" + cityName;
                    MAIN_HANDLER.post(() -> callback.onFailure(message));
                    return;
                }

                WeatherApiClient.Result result =
                        WeatherApiClient.fetch(place.latitude, place.longitude, place.name);

                weatherDao.saveNow(new WeatherNow(result.city, result.temp,
                        result.humidity, result.wind, result.air));

                int size = result.days.size();
                String[] conditions = new String[size];
                int[] highs = new int[size];
                int[] lows = new int[size];
                for (int i = 0; i < size; i++) {
                    conditions[i] = result.days.get(i).condition;
                    highs[i] = result.days.get(i).high;
                    lows[i] = result.days.get(i).low;
                }
                weatherDao.saveForecast(conditions, highs, lows);

                MAIN_HANDLER.post(() -> callback.onSuccess(result.city));
            } catch (Exception e) {
                String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                MAIN_HANDLER.post(() -> callback.onFailure(message));
            }
        });
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
