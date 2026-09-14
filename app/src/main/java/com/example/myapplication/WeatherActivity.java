package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.common.Extras;
import com.example.myapplication.data.WeatherRepository;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.widget.TitleBar;

/**
 * 第二个活动：今日天气 + 出行建议。
 */
public class WeatherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        showUserInfo();
        showTodayWeather();
        showTravelAdvice();

        findViewById(R.id.btnForecast).setOnClickListener(v -> openForecast());
        findViewById(R.id.btnFriends)
                .setOnClickListener(v -> startActivity(intentWithUser(FriendActivity.class)));
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

        ((TextView) findViewById(R.id.tvCity))
                .setText(getString(R.string.today_city_line, getString(R.string.city_name)));
    }

    /** 今日天气卡片。 */
    private void showTodayWeather() {
        WeatherDay today = WeatherRepository.getToday();

        ((ImageView) findViewById(R.id.ivTodayIcon)).setImageResource(today.getIconRes());
        ((TextView) findViewById(R.id.tvTodayTemp))
                .setText(WeatherRepository.getCurrentTemp() + "°");
        ((TextView) findViewById(R.id.tvTodayCondition)).setText(today.getCondition());
        ((TextView) findViewById(R.id.tvTodayRange))
                .setText(getString(R.string.today_range, today.getHigh(), today.getLow()));
        ((TextView) findViewById(R.id.tvHumidity)).setText(WeatherRepository.getHumidity());
        ((TextView) findViewById(R.id.tvWind)).setText(WeatherRepository.getWind());
        ((TextView) findViewById(R.id.tvAirQuality)).setText(WeatherRepository.getAirQuality());
    }

    /** 出行建议：按数据动态生成文字，塞进卡片里。 */
    private void showTravelAdvice() {
        LinearLayout container = findViewById(R.id.llAdvice);
        for (String line : WeatherRepository.getTravelAdvice()) {
            container.addView(createAdviceView(line));
        }
    }

    private TextView createAdviceView(String text) {
        TextView view = new TextView(this);
        view.setText("· " + text);
        view.setTextSize(14);
        view.setTextColor(getColor(R.color.text_secondary));
        view.setLineSpacing(dp(4), 1f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) dp(8);
        view.setLayoutParams(params);
        view.setGravity(Gravity.START);
        return view;
    }

    private void openForecast() {
        startActivity(intentWithUser(ForecastActivity.class));
    }

    /** 新建一个目标活动，并把用户名和头像一起带过去。 */
    private Intent intentWithUser(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.putExtra(Extras.USERNAME, getIntent().getStringExtra(Extras.USERNAME));
        intent.putExtra(Extras.AVATAR_RES,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));
        return intent;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
