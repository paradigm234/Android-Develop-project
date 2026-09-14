package com.example.myapplication.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * 自定义控件：通用标题栏。
 *
 * <p>本作业要求"设计一个自定义控件，并在所有的活动中都进行应用"，这就是那个控件。
 * 三个活动（登录、今日天气、未来 7 天预报、好友列表）的顶部都是它。
 *
 * <p>它由若干个系统控件组合而成，并提供三个能力：
 * <ul>
 *   <li>显示标题（xml 属性 {@code tbTitle} 或 {@link #setTitle(CharSequence)}）</li>
 *   <li>可选地显示左上角返回按钮（xml 属性 {@code tbShowBack}），默认点击结束当前活动</li>
 *   <li>在右侧显示当前用户的头像和用户名（{@link #setUserInfo(String, int)}）</li>
 * </ul>
 */
public class TitleBar extends LinearLayout {

    private ImageView btnBack;
    private TextView tvTitle;
    private ImageView ivAvatar;
    private TextView tvUserName;

    private OnClickListener backClickListener;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = (int) dp(context, 16);
        setPadding(padding, 0, padding, 0);
        setBackgroundResource(R.drawable.bg_title_bar);

        // 把 view_title_bar.xml 里的子控件挂到当前控件上（布局根节点是 merge）
        View.inflate(context, R.layout.view_title_bar, this);

        btnBack = findViewById(R.id.tbBack);
        tvTitle = findViewById(R.id.tbTitle);
        ivAvatar = findViewById(R.id.tbAvatar);
        tvUserName = findViewById(R.id.tbUserName);

        String title = null;
        boolean showBack = false;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            title = array.getString(R.styleable.TitleBar_tbTitle);
            showBack = array.getBoolean(R.styleable.TitleBar_tbShowBack, false);
            array.recycle();
        }
        setTitle(title);
        setShowBack(showBack);

        btnBack.setOnClickListener(v -> {
            if (backClickListener != null) {
                backClickListener.onClick(v);
            } else if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        });
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title == null ? "" : title);
    }

    public void setShowBack(boolean show) {
        btnBack.setVisibility(show ? VISIBLE : GONE);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        this.backClickListener = listener;
    }

    /**
     * 在标题栏右侧显示当前用户的头像和用户名。
     *
     * @param userName  用户名，为空时只显示头像
     * @param avatarRes 头像资源 id
     */
    public void setUserInfo(String userName, int avatarRes) {
        ivAvatar.setImageResource(avatarRes);
        ivAvatar.setVisibility(VISIBLE);

        if (userName == null || userName.trim().isEmpty()) {
            tvUserName.setText("");
            tvUserName.setVisibility(GONE);
        } else {
            tvUserName.setText(userName);
            tvUserName.setVisibility(VISIBLE);
        }
    }

    private static float dp(Context context, float value) {
        return value * context.getResources().getDisplayMetrics().density;
    }
}
