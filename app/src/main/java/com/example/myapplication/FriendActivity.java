package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.common.AvatarUtils;
import com.example.myapplication.common.Extras;
import com.example.myapplication.data.FriendDao;
import com.example.myapplication.model.Friend;
import com.example.myapplication.widget.TitleBar;

/**
 * 好友列表活动。
 *
 * <p>好友数据全部存在 SQLite 里，本页面演示增删查改：
 * <ul>
 *   <li>查：列表从数据库读取</li>
 *   <li>增：右上角"添加"，弹窗输入姓名和城市</li>
 *   <li>改：长按某一行 -> 修改资料</li>
 *   <li>删：长按某一行 -> 删除好友</li>
 * </ul>
 */
public class FriendActivity extends Activity {

    /** 新增好友时随便给一组天气，省得手工填。 */
    private static final String[] SAMPLE_CONDITIONS = {"晴", "多云", "阴", "小雨", "雷阵雨"};
    private static final int[] SAMPLE_TEMPS = {28, 26, 22, 19, 24};

    private FriendDao friendDao;
    private FriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        friendDao = new FriendDao(this);

        // 自定义标题栏，右上角显示从登录界面传过来的用户名和头像
        String username = getIntent().getStringExtra(Extras.USERNAME);
        if (username == null || username.isEmpty()) {
            username = getString(R.string.default_user);
        }
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setUserInfo(username,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));

        adapter = new FriendAdapter(this, friendDao.getAll());
        ListView listView = findViewById(R.id.lvFriends);
        listView.setAdapter(adapter);

        // 长按 -> 修改 / 删除
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showFriendOptions(adapter.getItem(position));
            return true;
        });

        // 单击 -> 进入聊天
        listView.setOnItemClickListener((parent, view, position, id) -> openChat(adapter.getItem(position)));

        // 增
        findViewById(R.id.btnAddFriend).setOnClickListener(v -> showFriendDialog(null));
    }

    /** 进入和这位好友的聊天界面。 */
    private void openChat(Friend friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Extras.FRIEND_ID, friend.getId());
        intent.putExtra(Extras.FRIEND_NAME, friend.getName());
        intent.putExtra(Extras.FRIEND_AVATAR_RES, friend.getAvatarRes());
        intent.putExtra(Extras.USERNAME, getIntent().getStringExtra(Extras.USERNAME));
        intent.putExtra(Extras.AVATAR_RES,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));
        startActivity(intent);
    }

    private void showFriendOptions(Friend friend) {
        String[] items = {
                getString(R.string.action_edit_friend),
                getString(R.string.action_delete_friend),
        };
        new AlertDialog.Builder(this)
                .setTitle(friend.getName())
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        showFriendDialog(friend);
                    } else {
                        confirmDelete(friend);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /** friend 为 null 表示新增，否则是修改。 */
    private void showFriendDialog(Friend friend) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_edit_friend, null);
        EditText etName = content.findViewById(R.id.etFriendName);
        EditText etCity = content.findViewById(R.id.etFriendCity);
        if (friend != null) {
            etName.setText(friend.getName());
            etCity.setText(friend.getCity());
        }

        new AlertDialog.Builder(this)
                .setTitle(friend == null ? R.string.dialog_add_friend : R.string.dialog_edit_friend)
                .setView(content)
                .setPositiveButton(R.string.action_save,
                        (dialog, which) -> saveFriend(friend, etName, etCity))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void saveFriend(Friend friend, EditText etName, EditText etCity) {
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        if (name.isEmpty() || city.isEmpty()) {
            toast(getString(R.string.toast_friend_empty));
            return;
        }

        if (friend == null) {
            int avatarIndex = friendDao.count() % AvatarUtils.AVATARS.length;
            int pick = (int) (System.currentTimeMillis() % SAMPLE_CONDITIONS.length);
            friendDao.insert(new Friend(name, city,
                    SAMPLE_CONDITIONS[pick], SAMPLE_TEMPS[pick], avatarIndex));
            toast(getString(R.string.toast_friend_added));
        } else {
            friendDao.update(new Friend(friend.getId(), name, city,
                    friend.getCondition(), friend.getTemp(), friend.getAvatarIndex()));
            toast(getString(R.string.toast_friend_updated));
        }
        reload();
    }

    private void confirmDelete(Friend friend) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete_friend)
                .setMessage(getString(R.string.dialog_delete_friend, friend.getName()))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    friendDao.delete(friend.getId());
                    toast(getString(R.string.toast_friend_deleted));
                    reload();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /** 重新从数据库读一遍列表。 */
    private void reload() {
        adapter.setFriends(friendDao.getAll());
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
