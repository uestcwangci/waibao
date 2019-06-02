package com.example.festec.udpbrodcastactivity.view.test;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudService extends Service {
    private int minBufferSize;
    private AudioRecord audioRec;
    private byte[] audBuffer;

    private DatagramSocket datagramSocket;
    private InetAddress address;

    private boolean isRun = false;


    public AudService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAudio();
        try {
            datagramSocket = new DatagramSocket();
            address = InetAddress.getByName("192.168.0.161");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRun = true;
        sendThread.start();
        return super.onStartCommand(intent, flags, startId);
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


    @Override
    public void onDestroy() {
        isRun = false;
        if (audioRec.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRec.stop();
            audioRec.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
}
