package com.example.festec.udpbrodcastactivity.view.test;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.utils.PermissionUtil;
import com.example.festec.udpbrodcastactivity.view.login.LoginActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class UdpP2pActivity extends AppCompatActivity {
    private static final String TAG = "waibao";
    // 要申请的权限
    private final String[] permissions = new String[]{
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO};

    private int minBufferSize;
    private AudioRecord audioRec;
    private byte[] audBuffer;

    private DatagramSocket datagramSocket;
    private InetAddress address;

    private boolean isRun = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_p2p);
        PermissionUtil.getInstance().chekPermissions(this, permissions, permissionsResult);
        initUI();
        initAudio();
    }

    private void initUI() {
        ToggleButton toggleButton = findViewById(R.id.startBt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            datagramSocket = new DatagramSocket();
            address = InetAddress.getByName("192.168.99.161");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        final Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (datagramSocket == null)
                    return;
                try {
                    audioRec.startRecording();
                    while (isRun) {
                        try {
                            int length = audioRec.read(audBuffer, 0, minBufferSize);
                            Log.d(TAG, "length:" + length);
                            // 组报
                            DatagramPacket datagramPacket = new DatagramPacket(audBuffer, length, address, 7777);
                            datagramSocket.send(datagramPacket);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRun = true;
                    sendThread.start();
                } else {
                    isRun = false;
                    if (audioRec.getState() == AudioRecord.STATE_INITIALIZED) {
                        audioRec.stop();
                        audioRec.release();
                    }
                }
            }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initAudio() {
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = 44100;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;

        minBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig, AudioFormat.ENCODING_PCM_8BIT);
        Log.d(TAG, "****RecordMinBufferSize = " + minBufferSize);
        audioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize);
        audBuffer = new byte[minBufferSize];

        if (audioRec == null) {
            return;
        }
        //声学回声消除器 AcousticEchoCanceler 消除了从远程捕捉到音频信号上的信号的作用
        if (AcousticEchoCanceler.isAvailable()) {
            AcousticEchoCanceler aec = AcousticEchoCanceler.create(audioRec.getAudioSessionId());
            if (aec != null) {
                aec.setEnabled(true);
            }
        }

        //自动增益控制 AutomaticGainControl 自动恢复正常捕获的信号输出
        if (AutomaticGainControl.isAvailable()) {
            AutomaticGainControl agc = AutomaticGainControl.create(audioRec.getAudioSessionId());
            if (agc != null) {
                agc.setEnabled(true);
            }
        }

        //噪声抑制器 NoiseSuppressor 可以消除被捕获信号的背景噪音
        if (NoiseSuppressor.isAvailable()) {
            NoiseSuppressor nc = NoiseSuppressor.create(audioRec.getAudioSessionId());
            if (nc != null) {
                nc.setEnabled(true);
            }
        }
    }


    //创建监听权限的接口对象
    PermissionUtil.IPermissionsResult permissionsResult = new PermissionUtil.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Toast.makeText(UdpP2pActivity.this, "权限通过", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(UdpP2pActivity.this, "权限不通过", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


}
