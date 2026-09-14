package com.example.myapplication.model;

/**
 * 好友信息：好友所在城市的天气。
 */
public class Friend {

    private final String name;
    private final String city;
    private final String condition;
    private final int temp;
    private final int avatarRes;

    public Friend(String name, String city, String condition, int temp, int avatarRes) {
        this.name = name;
        this.city = city;
        this.condition = condition;
        this.temp = temp;
        this.avatarRes = avatarRes;
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

    public int getAvatarRes() {
        return avatarRes;
    }
}
