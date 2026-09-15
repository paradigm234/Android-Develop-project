package com.example.myapplication.model;

import com.example.myapplication.common.AvatarUtils;

/**
 * 好友信息，对应数据库 t_friend 表。
 */
public class Friend {

    private final long id;
    private final String name;
    private final String city;
    private final String condition;
    private final int temp;
    private final int avatarIndex;

    public Friend(long id, String name, String city, String condition, int temp, int avatarIndex) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.condition = condition;
        this.temp = temp;
        this.avatarIndex = avatarIndex;
    }

    /** 新增好友时用（还没有数据库 id）。 */
    public Friend(String name, String city, String condition, int temp, int avatarIndex) {
        this(0, name, city, condition, temp, avatarIndex);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCondition() {
        return condition;
    }

    public int getTemp() {
        return temp;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public int getAvatarRes() {
        return AvatarUtils.resOf(avatarIndex);
    }
}
