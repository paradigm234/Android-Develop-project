package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 第一个活动：登录界面。
 *
 * <p>包含用户名输入框、密码输入框，以及可选择用户头像的头像控件。
 * 登录成功后会把用户名和头像传给第二个活动（下一步实现）。
 */
public class LoginActivity extends Activity {

    /** 保存已选头像，避免屏幕旋转后丢失。 */
    private static final String STATE_AVATAR = "state_avatar";

    /** 可选头像资源。 */
    private static final int[] AVATAR_RES = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
    };

    private ImageView ivAvatar;
    private EditText etUsername;
    private EditText etPassword;

    /** 当前选中的头像资源 id。 */
    private int selectedAvatarRes = AVATAR_RES[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivAvatar = findViewById(R.id.ivAvatar);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        if (savedInstanceState != null) {
            selectedAvatarRes = savedInstanceState.getInt(STATE_AVATAR, AVATAR_RES[0]);
        }
        ivAvatar.setImageResource(selectedAvatarRes);

        ivAvatar.setOnClickListener(v -> showAvatarPicker());
        findViewById(R.id.btnLogin).setOnClickListener(v -> onLoginClicked());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_AVATAR, selectedAvatarRes);
    }

    /** 弹出对话框选择头像。 */
    private void showAvatarPicker() {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_select_avatar, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.select_avatar)
                .setView(content)
                .setNegativeButton(R.string.action_cancel, null)
                .create();

        int[] optionIds = {
                R.id.avatarOption1,
                R.id.avatarOption2,
                R.id.avatarOption3,
                R.id.avatarOption4,
        };
        for (int i = 0; i < optionIds.length; i++) {
            int avatarRes = AVATAR_RES[i];
            content.findViewById(optionIds[i]).setOnClickListener(v -> {
                selectedAvatarRes = avatarRes;
                ivAvatar.setImageResource(avatarRes);
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    /** 点击登录按钮：先做非空校验，再跳转到下一个活动。 */
    private void onLoginClicked() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUsername.requestFocus();
            toast(getString(R.string.toast_input_username));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.requestFocus();
            toast(getString(R.string.toast_input_password));
            return;
        }

        // 下一步：跳转到第二个活动，并把 username 和 selectedAvatarRes 传过去。
        toast(getString(R.string.toast_login_ok, username));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
