package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.common.Extras;
import com.example.myapplication.data.FriendDao;
import com.example.myapplication.data.MessageDao;
import com.example.myapplication.model.ChatMessage;
import com.example.myapplication.model.Friend;
import com.example.myapplication.widget.TitleBar;

import java.util.Random;

/**
 * 聊天活动：和某个好友聊天。
 *
 * <p>消息全部存在数据库 t_message 表里，本页面演示：
 * <ul>
 *   <li>查：进入页面从数据库读出历史记录</li>
 *   <li>增：发送消息写入数据库</li>
 *   <li>删：长按消息可以删除单条，或者清空聊天记录</li>
 * </ul>
 *
 * <p>为了演示双向聊天，发完消息后本地会"收到"一条模拟回复。
 * 回复里如果提到天气，会去数据库查这位好友所在城市的天气。
 */
public class ChatActivity extends Activity {

    private static final String[] DEFAULT_REPLIES = {
            "嗯嗯，我在呢。",
            "哈哈哈，确实是这样。",
            "好呀，都听你的。",
            "我今天还行，就是有点忙。",
            "晚点再聊，我先去忙会儿～",
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private MessageDao messageDao;
    private FriendDao friendDao;
    private ChatAdapter adapter;
    private ListView listView;
    private EditText etMessage;

    private long friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageDao = new MessageDao(this);
        friendDao = new FriendDao(this);

        friendId = getIntent().getLongExtra(Extras.FRIEND_ID, -1);
        String friendName = getIntent().getStringExtra(Extras.FRIEND_NAME);
        if (friendName == null || friendName.isEmpty()) {
            friendName = getString(R.string.default_user);
        }
        int friendAvatarRes =
                getIntent().getIntExtra(Extras.FRIEND_AVATAR_RES, R.drawable.avatar_1);

        // 自定义标题栏：标题是好友名字，右上角是当前登录的用户
        String username = getIntent().getStringExtra(Extras.USERNAME);
        if (username == null || username.isEmpty()) {
            username = getString(R.string.default_user);
        }
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setTitle(friendName);
        titleBar.setUserInfo(username,
                getIntent().getIntExtra(Extras.AVATAR_RES, R.drawable.avatar_1));

        listView = findViewById(R.id.lvMessages);
        adapter = new ChatAdapter(this, messageDao.listByFriend(friendId), friendAvatarRes);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.tvEmpty));

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showMessageOptions(adapter.getItem(position));
            return true;
        });

        etMessage = findViewById(R.id.etMessage);
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());

        scrollToBottom();
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) {
            toast(getString(R.string.toast_empty_message));
            return;
        }

        // 增：先把我说的话写进数据库
        messageDao.insert(new ChatMessage(friendId, text, true));
        etMessage.setText("");
        reload();

        // 模拟好友回复，演示双向聊天
        handler.postDelayed(() -> {
            messageDao.insert(new ChatMessage(friendId, buildReply(text), false));
            reload();
        }, 900);
    }

    /** 根据关键词生成回复，顺便用上数据库里好友的天气。 */
    private String buildReply(String text) {
        if (text.contains("天气")) {
            Friend friend = friendDao.findById(friendId);
            if (friend != null) {
                return "我这边" + friend.getCity() + friend.getCondition() + "，"
                        + friend.getTemp() + "°，你那边呢？";
            }
        }
        if (text.contains("你好") || text.contains("在吗") || text.toLowerCase().contains("hi")) {
            return "你好呀！我刚看完天气预报，今天挺舒服的。";
        }
        if (text.contains("吃")) {
            return "还没吃呢，你有什么推荐？";
        }
        if (text.contains("谢谢")) {
            return "客气啦～";
        }
        return DEFAULT_REPLIES[random.nextInt(DEFAULT_REPLIES.length)];
    }

    /** 长按消息：删除这条 / 清空聊天记录。 */
    private void showMessageOptions(ChatMessage message) {
        String[] items = {
                getString(R.string.action_delete_message),
                getString(R.string.action_clear_chat),
        };
        new AlertDialog.Builder(this)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        messageDao.delete(message.getId());
                        toast(getString(R.string.toast_message_deleted));
                    } else {
                        messageDao.deleteByFriend(friendId);
                        toast(getString(R.string.toast_chat_cleared));
                    }
                    reload();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /** 重新从数据库读一遍聊天记录。 */
    private void reload() {
        adapter.setMessages(messageDao.listByFriend(friendId));
        scrollToBottom();
    }

    private void scrollToBottom() {
        listView.post(() -> {
            if (adapter.getCount() > 0) {
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
