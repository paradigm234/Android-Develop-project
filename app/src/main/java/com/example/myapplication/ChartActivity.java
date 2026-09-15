package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.common.Extras;
import com.example.myapplication.data.MessageDao;
import com.example.myapplication.data.WeatherRepository;
import com.example.myapplication.model.Friend;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.widget.BarChartView;
import com.example.myapplication.widget.TitleBar;

import java.util.List;

/**
 * 数据统计活动：用自定义控件 BarChartView 把数据库里的数据画成柱状图。
 *
 * <p>两张图的数据都来自 SQLite：
 * <ul>
 *   <li>未来 7 天温度 —— 来自 t_weather_day</li>
 *   <li>和好友的聊天条数 —— 来自 t_message</li>
 * </ul>
 */
public class ChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        String username = getIntent().getStringExtra(Extras.USERNAME);
        if (username == null || username.isEmpty()) {
            username = getString(R.string.default_user);
        }
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setUserInfo(username,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));

        showTemperatureChart();
        showMessageChart();
    }

    /** 图一：未来 7 天最高 / 最低温。 */
    private void showTemperatureChart() {
        List<WeatherDay> days = new WeatherRepository(this).getWeeklyForecast();
        if (days.isEmpty()) {
            return;
        }

        String[] labels = new String[days.size()];
        float[] highs = new float[days.size()];
        float[] lows = new float[days.size()];

        int hottest = 0;
        for (int i = 0; i < days.size(); i++) {
            WeatherDay day = days.get(i);
            labels[i] = day.getWeekday();
            highs[i] = day.getHigh();
            lows[i] = day.getLow();
            if (day.getHigh() > days.get(hottest).getHigh()) {
                hottest = i;
            }
        }

        BarChartView chart = findViewById(R.id.chartTemp);
        chart.setData(labels, highs, lows,
                getString(R.string.chart_legend_high), getString(R.string.chart_legend_low));

        TextView summary = findViewById(R.id.tvSummary);
        summary.setText(getString(R.string.chart_summary,
                new MessageDao(this).count(),
                days.get(hottest).getHigh(),
                days.get(hottest).getWeekday()));
    }

    /** 图二：和每个好友的聊天条数。 */
    private void showMessageChart() {
        List<Friend> friends = new WeatherRepository(this).getFriends();
        MessageDao messageDao = new MessageDao(this);

        String[] labels = new String[friends.size()];
        float[] counts = new float[friends.size()];
        for (int i = 0; i < friends.size(); i++) {
            labels[i] = friends.get(i).getName();
            counts[i] = messageDao.countByFriend(friends.get(i).getId());
        }

        BarChartView chart = findViewById(R.id.chartMessages);
        chart.setData(labels, counts, null,
                getString(R.string.chart_legend_count), null);
    }
}
