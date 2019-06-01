package com.example.festec.udpbrodcastactivity.module.udp;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UdpServer {
    private static final String TAG = "waibao";


    private boolean isUdpRun = true;
    private String ip = "255.255.255.255";//广播地址
    private Set<Integer> portList; //指定广播接收数据端口


    public UdpServer(Set<Integer> clients) {
        this.portList = clients;
    }



    public void startNow(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (final Integer udpPort : portList) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runServerNow(udpPort, data);
                        }
                    }).start();
                }
            }
        }).start();
    }
    public void start(final byte[] data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (final Integer udpPort : portList) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runServer(udpPort, data);
                        }
                    }).start();
                }
            }
        }).start();
    }

    private void runServer(int udpPort, byte[] data) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            isUdpRun = true;
            while (isUdpRun) {
                InetAddress adds = InetAddress.getByName(ip);
                DatagramPacket dp = new DatagramPacket(data, data.length, adds, udpPort);
                ds.send(dp);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "UdpTestServer run Exception: " + e.toString());
        } finally {
            if (ds != null) {
                ds.close();
            }

        }
    }

    private void runServerNow(int udpPort, byte[] data) {
        try {
            DatagramSocket ds = new DatagramSocket();
            isUdpRun = true;
            while (isUdpRun) {
                InetAddress adds = InetAddress.getByName(ip);
                DatagramPacket dp = new DatagramPacket(data, data.length, adds, udpPort);
                ds.send(dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "UdpTestServer run Exception: " + e.toString());
        }
    }



    public void udpServerClose() {
        isUdpRun = false;
        Log.d(TAG, "UDP 服务器关闭");
    }


}
