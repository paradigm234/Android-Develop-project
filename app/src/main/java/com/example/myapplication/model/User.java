package com.example.myapplication.model;

/**
 * 用户，对应数据库 t_user 表。
 */
public class User {

    private final long id;
    private final String username;
    private final String password;
    private final int avatarIndex;
    private final long createdAt;

    public User(long id, String username, String password, int avatarIndex, long createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.avatarIndex = avatarIndex;
        this.createdAt = createdAt;
    }

    /** 新增用户时用。 */
    public User(String username, String password, int avatarIndex) {
        this(0, username, password, avatarIndex, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
