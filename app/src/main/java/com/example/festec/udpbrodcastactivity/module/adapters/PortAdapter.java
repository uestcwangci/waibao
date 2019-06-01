package com.example.festec.udpbrodcastactivity.module.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PortAdapter extends RecyclerView.Adapter<PortAdapter.ViewHolder> {
    private Map<Integer, String> portMacMap;

    public PortAdapter(Map<Integer, String> clients) {
        this.portMacMap = clients;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.port_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String mac = portMacMap.get(GlobalValues.portList.get(i));
        if ("0".equals(mac)) {
            viewHolder.tvOnline.setText("离线");
            viewHolder.imgIsOnline.setImageResource(R.drawable.ic_offline_icon2);
        } else {
            viewHolder.tvOnline.setText("在线");
            viewHolder.imgIsOnline.setImageResource(R.drawable.ic_online_icon2);

        }
        viewHolder.tvPosition.setText("四川省成都市顺江小区");
    }

    @Override
    public int getItemCount() {
        return portMacMap.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOnline;
        TextView tvPosition;
        ImageView imgIsOnline;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOnline = itemView.findViewById(R.id.tv_is_online);
            tvPosition = itemView.findViewById(R.id.tv_position);
            imgIsOnline = itemView.findViewById(R.id.img_is_online);
        }
    }

}
