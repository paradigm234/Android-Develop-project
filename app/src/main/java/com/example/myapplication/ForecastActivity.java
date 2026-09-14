package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.adapter.ForecastAdapter;
import com.example.myapplication.common.Extras;
import com.example.myapplication.data.WeatherRepository;
import com.example.myapplication.widget.TitleBar;

/**
 * 第三个活动：未来 7 天预报，用 ListView 展示。
 */
public class ForecastActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        showUserInfo();

        ListView listView = findViewById(R.id.lvForecast);
        listView.setAdapter(new ForecastAdapter(this, WeatherRepository.getWeeklyForecast()));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /** 把从登录界面传过来的用户名和头像交给自定义标题栏显示。 */
    private void showUserInfo() {
        String username = getIntent().getStringExtra(Extras.USERNAME);
        if (username == null || username.isEmpty()) {
            username = getString(R.string.default_user);
        }
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setUserInfo(username,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));

        ((TextView) findViewById(R.id.tvSubtitle))
                .setText(getString(R.string.city_forecast_subtitle, getString(R.string.city_name)));
    }
}
