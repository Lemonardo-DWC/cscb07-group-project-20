package com.example.smartair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BadgeAdapter extends BaseAdapter {

    private Context context;
    private List<BadgeItem> badges;

    public BadgeAdapter(Context context, List<BadgeItem> badges) {
        this.context = context;
        this.badges = badges;
    }

    @Override
    public int getCount() {
        return badges.size();
    }

    @Override
    public Object getItem(int position) {
        return badges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_badge, parent, false);
        }

        ImageView ivBadge = convertView.findViewById(R.id.ivBadge);
        TextView tvTitle = convertView.findViewById(R.id.tvBadgeTitle);

        BadgeItem badge = badges.get(position);
        tvTitle.setText(badge.title);

        if(badge.unlocked){
            switch (badge.title) {
                case "First Perfect Week":
                    ivBadge.setImageResource(R.drawable.ic_badge_perfect_week);
                    break;
                case "10 High-Quality Technique Sessions":
                    ivBadge.setImageResource(R.drawable.ic_badge_technique_10);
                    break;
                case "Low Rescue Month":
                    ivBadge.setImageResource(R.drawable.ic_badge_low_rescue);
                    break;
            }
            convertView.setAlpha(1f);
        } else {
            ivBadge.setImageResource(R.drawable.ic_badge_locked);
            convertView.setAlpha(0.45f);
        }

        return convertView;
    }
}
