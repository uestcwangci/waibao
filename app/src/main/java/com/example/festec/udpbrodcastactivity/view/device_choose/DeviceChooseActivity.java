package com.example.festec.udpbrodcastactivity.view.device_choose;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.adapters.ChooseAdapter;
import com.example.festec.udpbrodcastactivity.view.broadcast.BroadcastActivity;

public class DeviceChooseActivity extends AppCompatActivity {
    private static final String TAG = "waibao";
    private FloatingActionButton nextBt;
    private Toolbar toolbar;
    private RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choose);
        initUI();
        GlobalValues.checkedMac.clear();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        ButtonListener buttonListener = new ButtonListener();
//        Button dell = findViewById(R.id.dell);
//        dell.setOnClickListener(buttonListener);
        toolbar = findViewById(R.id.choose_toolbar);
        setSupportActionBar(toolbar);
        nextBt = findViewById(R.id.bt_next);
        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceChooseActivity.this, BroadcastActivity.class));
            }
        });
        recyclerView = findViewById(R.id.choose_recycle_view);
//        recyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChooseAdapter chooseAdapter = new ChooseAdapter(GlobalValues.onlineList);
        recyclerView.setAdapter(chooseAdapter);





    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.dell:
//                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
