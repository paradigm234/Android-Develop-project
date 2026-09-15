package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天记录的 ListView 适配器。
 *
 * <p>两种布局：自己发的靠右（蓝底白字），好友发的靠左（白底 + 头像）。
 */
public class ChatAdapter extends BaseAdapter {

    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;

    private final Context context;
    private final List<ChatMessage> data = new ArrayList<>();
    private final int friendAvatarRes;

    public ChatAdapter(Context context, List<ChatMessage> data, int friendAvatarRes) {
        this.context = context;
        this.friendAvatarRes = friendAvatarRes;
        this.data.addAll(data);
    }

    public void setMessages(List<ChatMessage> messages) {
        data.clear();
        data.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isFromMe() ? TYPE_ME : TYPE_FRIEND;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = getItem(position);

        if (getItemViewType(position) == TYPE_ME) {
            TextView bubble;
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_chat_me, parent, false);
                bubble = convertView.findViewById(R.id.tvMessage);
                convertView.setTag(bubble);
            } else {
                bubble = (TextView) convertView.getTag();
            }
            bubble.setText(message.getContent());
        } else {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_chat_friend, parent, false);
                holder = new ViewHolder();
                holder.avatar = convertView.findViewById(R.id.ivAvatar);
                holder.bubble = convertView.findViewById(R.id.tvMessage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.avatar.setImageResource(friendAvatarRes);
            holder.bubble.setText(message.getContent());
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView bubble;
    }
}
