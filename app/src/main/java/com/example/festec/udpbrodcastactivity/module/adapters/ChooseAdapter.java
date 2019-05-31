package com.example.festec.udpbrodcastactivity.module.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.festec.udpbrodcastactivity.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ViewHolder> {
    private List<Integer> clients;

    public ChooseAdapter(List<Integer> clients) {
        this.clients = clients;
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
        String text = "设备" + clients.get(i);
        viewHolder.checkBox.setText(text);
    }


    @Override
    public int getItemCount() {
        return clients.size();
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
