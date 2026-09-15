package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.common.Extras;
import com.example.myapplication.data.WeatherRepository;
import com.example.myapplication.model.WeatherDay;
import com.example.myapplication.model.WeatherNow;
import com.example.myapplication.widget.TitleBar;

/**
 * 第二个活动：今日天气 + 出行建议。
 */
public class WeatherActivity extends Activity {

    private WeatherRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        repository = new WeatherRepository(this);

        showUserInfo();
        showTodayWeather();
        showTravelAdvice();

        findViewById(R.id.btnForecast).setOnClickListener(v -> openForecast());
        findViewById(R.id.btnFriends)
                .setOnClickListener(v -> startActivity(intentWithUser(FriendActivity.class)));
        findViewById(R.id.btnRefresh).setOnClickListener(v -> refreshWeather(currentCity()));
        findViewById(R.id.tvCity).setOnClickListener(v -> showCityDialog());

        // 进入页面先联网刷新一次；界面展示的始终是数据库里的最新数据
        refreshWeather(currentCity());
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
    }

    /** 今日天气卡片，数据全部来自数据库。 */
    private void showTodayWeather() {
        WeatherNow now = repository.getNow();
        if (now != null) {
            ((TextView) findViewById(R.id.tvCity))
                    .setText(getString(R.string.today_city_line, now.getCity()));
            ((TextView) findViewById(R.id.tvTodayTemp)).setText(now.getTemp() + "°");
            ((TextView) findViewById(R.id.tvHumidity)).setText(now.getHumidity());
            ((TextView) findViewById(R.id.tvWind)).setText(now.getWind());
            ((TextView) findViewById(R.id.tvAirQuality)).setText(now.getAir());
        }

        WeatherDay today = repository.getToday();
        if (today != null) {
            ((ImageView) findViewById(R.id.ivTodayIcon)).setImageResource(today.getIconRes());
            ((TextView) findViewById(R.id.tvTodayCondition)).setText(today.getCondition());
            ((TextView) findViewById(R.id.tvTodayRange))
                    .setText(getString(R.string.today_range, today.getHigh(), today.getLow()));
        }
    }

    /** 出行建议：按数据动态生成文字，塞进卡片里。 */
    private void showTravelAdvice() {
        LinearLayout container = findViewById(R.id.llAdvice);
        container.removeAllViews();
        for (String line : repository.getTravelAdvice()) {
            container.addView(createAdviceView(line));
        }
    }

    /** 当前显示的城市。 */
    private String currentCity() {
        WeatherNow now = repository.getNow();
        return now == null ? getString(R.string.city_name) : now.getCity();
    }

    /** 联网刷新：请求接口 -> 写进数据库 -> 重新读数据库刷新界面。 */
    private void refreshWeather(String cityName) {
        final TextView button = findViewById(R.id.btnRefresh);
        button.setEnabled(false);
        button.setText(R.string.action_refreshing);

        repository.refreshFromNetwork(cityName, new WeatherRepository.RefreshCallback() {
            @Override
            public void onSuccess(String city) {
                button.setEnabled(true);
                button.setText(R.string.action_refresh);
                showTodayWeather();
                showTravelAdvice();
                toast(getString(R.string.toast_refresh_ok, city));
            }

            @Override
            public void onFailure(String message) {
                button.setEnabled(true);
                button.setText(R.string.action_refresh);
                toast(getString(R.string.toast_refresh_failed, message));
            }
        });
    }

    /** 切换城市。 */
    private void showCityDialog() {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_input_city, null);
        EditText etCity = content.findViewById(R.id.etCity);
        etCity.setText(currentCity());

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_switch_city)
                .setView(content)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    String city = etCity.getText().toString().trim();
                    if (!city.isEmpty()) {
                        refreshWeather(city);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
