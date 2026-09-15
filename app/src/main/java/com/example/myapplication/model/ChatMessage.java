package com.example.myapplication.model;

/**
 * 一条聊天消息，对应数据库 t_message 表。
 */
public class ChatMessage {

    private final long id;
    private final long friendId;
    private final String content;
    private final boolean fromMe;
    private final long time;

    public ChatMessage(long id, long friendId, String content, boolean fromMe, long time) {
        this.id = id;
        this.friendId = friendId;
        this.content = content;
        this.fromMe = fromMe;
        this.time = time;
    }

    /** 新发一条消息时用。 */
    public ChatMessage(long friendId, String content, boolean fromMe) {
        this(0, friendId, content, fromMe, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public long getFriendId() {
        return friendId;
    }

    public String getContent() {
        return content;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public long getTime() {
        return time;
    }
}
