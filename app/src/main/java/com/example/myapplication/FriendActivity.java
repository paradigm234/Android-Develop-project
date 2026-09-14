package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.common.Extras;
import com.example.myapplication.data.WeatherRepository;
import com.example.myapplication.widget.TitleBar;

/**
 * 好友列表活动：用 ListView 展示好友和他们所在城市的天气。
 */
public class FriendActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // 自定义标题栏，右上角显示从登录界面传过来的用户名和头像
        String username = getIntent().getStringExtra(Extras.USERNAME);
        if (username == null || username.isEmpty()) {
            username = getString(R.string.default_user);
        }
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setUserInfo(username,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));

        ListView listView = findViewById(R.id.lvFriends);
        listView.setAdapter(new FriendAdapter(this, WeatherRepository.getFriends()));
    }
}
