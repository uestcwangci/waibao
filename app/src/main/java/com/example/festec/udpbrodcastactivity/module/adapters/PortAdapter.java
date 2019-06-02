package com.example.festec.udpbrodcastactivity.module.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;

import java.util.Map;

public class PortAdapter extends RecyclerView.Adapter<PortAdapter.ViewHolder> {
    private Map<String, String> mac2IPandPortMap;

    public PortAdapter(Map<String, String> clients) {
        this.mac2IPandPortMap = clients;
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

        String ip = mac2IPandPortMap.get(GlobalValues.macList.get(i)).split("\\|")[0];
        if ("0".equalsIgnoreCase(ip)) {
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
        return mac2IPandPortMap.size();
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
