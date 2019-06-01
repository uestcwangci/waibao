package com.example.festec.udpbrodcastactivity.module.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;

import java.util.List;

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ViewHolder> {
    private List<String> onlineClients;

    public ChooseAdapter(List<String> clients) {
        this.onlineClients = clients;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.choose_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String mac = onlineClients.get(i);
        String text = "设备mac地址：" + mac;
        viewHolder.checkBox.setText(text);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GlobalValues.checkedMac.add(mac);
                } else {
                    GlobalValues.checkedMac.remove(mac);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return onlineClients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.choose_card);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

}
