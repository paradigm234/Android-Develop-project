package com.example.myapplication.common;

/**
 * Activity 之间传递数据用的 key。
 */
public final class Extras {

    /** 用户名，String。 */
    public static final String USERNAME = "extra_username";

    /** 头像资源 id，int。 */
    public static final String AVATAR_RES = "extra_avatar_res";

    /** 好友 id，long。 */
    public static final String FRIEND_ID = "extra_friend_id";

    /** 好友名字，String。 */
    public static final String FRIEND_NAME = "extra_friend_name";

    /** 好友头像资源 id，int。 */
    public static final String FRIEND_AVATAR_RES = "extra_friend_avatar_res";

    private Extras() {
    }
}
