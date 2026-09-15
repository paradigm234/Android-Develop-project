package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.common.WeatherIcons;
import com.example.myapplication.model.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表的 ListView 适配器。
 */
public class FriendAdapter extends BaseAdapter {

    private final Context context;
    private final List<Friend> data = new ArrayList<>();

    public FriendAdapter(Context context, List<Friend> data) {
        this.context = context;
        this.data.addAll(data);
    }

    /** 数据库里的好友变化后，刷新列表。 */
    public void setFriends(List<Friend> friends) {
        data.clear();
        data.addAll(friends);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Friend getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
            holder = new ViewHolder();
            holder.ivAvatar = convertView.findViewById(R.id.ivAvatar);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvCity = convertView.findViewById(R.id.tvCity);
            holder.ivIcon = convertView.findViewById(R.id.ivIcon);
            holder.tvCondition = convertView.findViewById(R.id.tvCondition);
            holder.tvTemp = convertView.findViewById(R.id.tvTemp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Friend friend = getItem(position);
        holder.ivAvatar.setImageResource(friend.getAvatarRes());
        holder.tvName.setText(friend.getName());
        holder.tvCity.setText(friend.getCity());
        holder.ivIcon.setImageResource(WeatherIcons.iconOf(friend.getCondition()));
        holder.tvCondition.setText(friend.getCondition());
        holder.tvTemp.setText(friend.getTemp() + "°");
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvCity;
        ImageView ivIcon;
        TextView tvCondition;
        TextView tvTemp;
    }
}
