package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.WeatherDay;

import java.util.List;

/**
 * 未来 7 天预报的 ListView 适配器。
 */
public class ForecastAdapter extends BaseAdapter {

    private final Context context;
    private final List<WeatherDay> data;

    public ForecastAdapter(Context context, List<WeatherDay> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public WeatherDay getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false);
            holder = new ViewHolder();
            holder.tvWeekday = convertView.findViewById(R.id.tvWeekday);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.ivIcon = convertView.findViewById(R.id.ivIcon);
            holder.tvCondition = convertView.findViewById(R.id.tvCondition);
            holder.tvTemp = convertView.findViewById(R.id.tvTemp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WeatherDay day = getItem(position);
        holder.tvWeekday.setText(day.getWeekday());
        holder.tvDate.setText(day.getDate());
        holder.ivIcon.setImageResource(day.getIconRes());
        holder.tvCondition.setText(day.getCondition());
        holder.tvTemp.setText(day.getHigh() + "° / " + day.getLow() + "°");
        return convertView;
    }

    /** 缓存 item 里的控件，避免每次都 findViewById。 */
    private static class ViewHolder {
        TextView tvWeekday;
        TextView tvDate;
        ImageView ivIcon;
        TextView tvCondition;
        TextView tvTemp;
    }
}
