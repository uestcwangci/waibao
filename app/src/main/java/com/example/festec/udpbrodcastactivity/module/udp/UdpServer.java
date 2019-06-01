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

    private DatagramSocket ds = null;

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
                try {
                    ds = new DatagramSocket();
                    for (final Integer udpPort : portList) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runServerNow(udpPort, data);
                            }
                        }).start();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void start(final byte[] data){
        isUdpRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ds = new DatagramSocket();
                    for (final Integer udpPort : portList) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runServer(udpPort, data);
                            }
                        }).start();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runServer(int udpPort, byte[] data) {
        try {
            while (isUdpRun) {
                InetAddress adds = InetAddress.getByName(ip);
                DatagramPacket dp = new DatagramPacket(data, data.length, adds, udpPort);
                ds.send(dp);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "UdpTestServer run Exception: " + e.toString());
        }
    }

    private void runServerNow(int udpPort, byte[] data) {
        try {
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
//        if (ds != null) {
//            ds.close();
//        }
        Log.d(TAG, "UDP 服务器关闭");
    }


}
