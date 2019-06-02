package com.example.festec.udpbrodcastactivity.module.udp;

import android.util.Log;

import com.example.festec.udpbrodcastactivity.module.GlobalValues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UdpServer {
    private static final String TAG = "waibao";


    private boolean isUdpRun = true;

    private InetAddress address;


    private Map<String, String> udpIPandPort = new HashMap<>();



    public UdpServer() {
        for (Map.Entry<String, String> entry : GlobalValues.mac2IPandPortMap.entrySet()) {
            String[] s;
            if (GlobalValues.checkedMac.contains(entry.getKey())) {
                s = entry.getValue().split("\\|");
                if (!"0".equalsIgnoreCase(s[0])) {
                    udpIPandPort.put(s[0], s[1]);
                }
            }
        }

    }


    public void start(byte[] data, int delay) {
        isUdpRun = true;
        for (Map.Entry<String, String> entry : udpIPandPort.entrySet()) {
            new UdpThread(entry.getKey(), entry.getValue(), data, delay).start();
        }
    }

    class UdpThread extends Thread {
        private String ip;
        private int udpPort;
        private int delay;
        private byte[] data;
        private DatagramSocket socket;
        private InetAddress address;

        public UdpThread(String ip, String port, byte[] data, int delay) {
            this.data = data;
            this.delay = delay;
            this.ip = ip;
            this.udpPort = Integer.parseInt(port);
        }
        @Override
        public void run() {
            // 侦听的端口
            try {
                socket = new DatagramSocket();
                address = InetAddress.getByName(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (delay < 0) {
                return;
            }
            try {
                DatagramPacket datagramPacket;
                while (isUdpRun) {
                    datagramPacket = new DatagramPacket(data, data.length,
                            address, udpPort);
                    socket.send(datagramPacket);
                    Log.d(TAG, "发送" + Arrays.toString(data));
                    Thread.sleep(delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void udpServerClose() {
        isUdpRun = false;
        Log.d(TAG, "UDP 服务器关闭");
    }


}
