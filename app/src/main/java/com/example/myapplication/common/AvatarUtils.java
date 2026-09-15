package com.example.myapplication.common;

import com.example.myapplication.R;

/**
 * 头像工具类。
 *
 * <p>数据库里存的是头像的序号（0~3），而不是资源 id。
 * 因为资源 id 在每次重新编译后都可能变化，存序号才可靠。
 */
public final class AvatarUtils {

    public static final int[] AVATARS = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
    };

    private AvatarUtils() {
    }

    /** 序号 -> 资源 id。 */
    public static int resOf(int index) {
        if (index < 0 || index >= AVATARS.length) {
            return AVATARS[0];
        }
        return AVATARS[index];
    }

    /** 资源 id -> 序号。 */
    public static int indexOf(int resId) {
        for (int i = 0; i < AVATARS.length; i++) {
            if (AVATARS[i] == resId) {
                return i;
            }
        }
        return 0;
    }
}
