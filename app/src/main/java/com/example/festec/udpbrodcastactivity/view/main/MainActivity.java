package com.example.festec.udpbrodcastactivity.view.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.widget.Button;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.SingleLocalSocket;
import com.example.festec.udpbrodcastactivity.module.adapters.PortAdapter;
import com.example.festec.udpbrodcastactivity.view.device_choose.DeviceChooseActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int INIT_MAP_DONE = 69;
    private static final String TAG = "waibao";
    private SingleLocalSocket singleLocalSocket;

    private PortAdapter adapter;

    //UI
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private Button btStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        // initSocket
        singleLocalSocket = SingleLocalSocket.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();

    }


    private void initUI() {
        ButtonListener buttonListener = new ButtonListener();
        btStart = findViewById(R.id.btn_start);
        btStart.setOnClickListener(buttonListener);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

//        fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//
//            }
//        });


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("获取设备连接列表");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PortAdapter(GlobalValues.mac2IPandPortMap);
        recyclerView.setAdapter(adapter);


        // 下拉刷新
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMap();
            }
        });

    }





    class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start:
                    Intent intent = new Intent(MainActivity.this, DeviceChooseActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    private void initMap() {
        progressDialog.show();

        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = singleLocalSocket.getBw();
                    bw.write("queryMap");
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final Thread thread2 = new Thread(new Runnable() {
            private boolean isRun = true;
            @Override
            public void run() {
                try {
                    thread1.join();
                    BufferedReader br = singleLocalSocket.getBr();
                    GlobalValues.onlineList.clear();
                    GlobalValues.macList.clear();
                    String line = null;
                    while (isRun && ((line = br.readLine()) != null)) {
                        if (!"mapDone".equals(line)) {
                            String[] strings = line.split("\\|");
                            String mac = strings[0];
                            String ip = strings[1];
                            String udpPort = strings[2];
                            GlobalValues.mac2IPandPortMap.put(mac, ip + "|" + udpPort);
                            GlobalValues.macList.add(mac);
                            if (!"0".equalsIgnoreCase(ip)) {
                                GlobalValues.onlineList.add(mac);
                            }
                        } else {
                            Log.d(TAG, "map: " + GlobalValues.onlineList.toString());
                            isRun = false;
                            Message msg = Message.obtain();
                            msg.what = INIT_MAP_DONE;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread2.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_MAP_DONE:
                    adapter.notifyDataSetChanged();
                    if (swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
                    }
                    progressDialog.dismiss();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
