package com.example.festec.udpbrodcastactivity.view.broadcast;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.SingleLocalSocket;
import com.example.festec.udpbrodcastactivity.view.fragments.SendAudFragment;
import com.example.festec.udpbrodcastactivity.view.fragments.SendPicFragment;
import com.example.festec.udpbrodcastactivity.view.fragments.SendTextFragment;

import java.util.ArrayList;
import java.util.List;

public class BroadcastActivity extends AppCompatActivity
        implements SendTextFragment.OnTextFragmentInteractionListener,
        SendPicFragment.OnPicFragmentInteractionListener,
        SendAudFragment.OnAudFragmentInteractionListener {


    private Toolbar toolbar;
    private Spinner spinnerType, spinnerLevel, spinnerData;

    private String broadcastType;
    private String broadcastLevel;
    private String dataType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        initUI();

    }

    private void initUI() {
        ButtonListener buttonListener = new ButtonListener();
//        Button dell = findViewById(R.id.dell);
//        dell.setOnClickListener(buttonListener);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initSpinner();


    }

    private void initSpinner() {
        spinnerType = findViewById(R.id.spinner_type);
        spinnerLevel = findViewById(R.id.spinner_level);
        spinnerData = findViewById(R.id.spinner_data);
        List<String> typeList = new ArrayList<>();
        List<String> levelList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        typeList.add("应急演练-发布系统演练");
        typeList.add("应急演练-模拟演练");
        typeList.add("应急演练-实际演练");
        typeList.add("应急广播");
        typeList.add("日常广播");

        levelList.add("特别重大");
        levelList.add("重大");
        levelList.add("较大");
        levelList.add("一般");

        dataList.add("文本");
        dataList.add("图片");
        dataList.add("音频");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, typeList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, levelList);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        spinnerType.setAdapter(adapter1);
        spinnerLevel.setAdapter(adapter2);
        spinnerData.setAdapter(adapter3);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                broadcastType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                broadcastLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(BroadcastActivity.this,
//                        parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (parent.getItemAtPosition(position).toString()) {
                    case "文本":
                        transaction.replace(R.id.fragment_layout,
                                SendTextFragment.newInstance(broadcastType, broadcastLevel));
                        transaction.commit();
                        break;
                    case "图片":
                        transaction.replace(R.id.fragment_layout, new SendPicFragment());
                        transaction.commit();
                        break;
                    case "音频":
                        transaction.replace(R.id.fragment_layout, new SendAudFragment());
                        transaction.commit();
                        break;
                    default:
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onAudFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPicFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTextFragmentInteraction(Uri uri) {

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
