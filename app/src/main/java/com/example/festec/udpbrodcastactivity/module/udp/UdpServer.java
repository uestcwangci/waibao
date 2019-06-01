package com.example.festec.udpbrodcastactivity.module.udp;

import android.support.v4.app.NavUtils;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UdpServer {
    private static final String TAG = "waibao";


    private boolean isUdpRun = true;
    private String ip = "244.0.0.12";//组播地址 使用D类地址
    private int port = 8888; // 发送端口

    private MulticastSocket multicastSocket;
    private InetAddress address;



    public UdpServer() {
        // 侦听的端口
        try {
            multicastSocket = new MulticastSocket(port);
            // 使用D类地址，该地址为发起组播的那个ip段，
            address = InetAddress.getByName(ip);
            multicastSocket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void start(final byte[] data, final int delay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                isUdpRun = true;
                if (multicastSocket == null) {
                    return;
                }
                if (delay < 0) {
                    return;
                }
                try {
                    while (isUdpRun) {
                        try {
                            // 组报
                            DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                            // 向组播ID，即接收group /239.0.0.1  侦听端口 8888
                            multicastSocket.send(datagramPacket);
                            Thread.sleep(delay);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (multicastSocket != null) {
                        multicastSocket.close();
                    }
                }
            }
        }).start();
    }




    public void udpServerClose() {
        isUdpRun = false;
        Log.d(TAG, "UDP 服务器关闭");
    }


}
