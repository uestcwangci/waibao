package com.example.festec.udpbrodcastactivity.module.udp;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class UdpServer {
    private static final String TAG = "waibao";


    private boolean isUdpRun = true;
    private String ip = "239.0.1.25";//组播地址 使用D类地址
    private int sendPort = 10012; // 发送端口

    private MulticastSocket multicastSocket;
    private InetAddress address;



    public UdpServer() {

    }



    public void start(final byte[] data, final int delay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 侦听的端口
                try {
//                    multicastSocket = new MulticastSocket(sendPort);
                    multicastSocket = new MulticastSocket();
                    // 使用D类地址，该地址为发起组播的那个ip段，
                    address = InetAddress.getByName(ip);
//                    multicastSocket.joinGroup(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isUdpRun = true;
                if (multicastSocket == null) {
                    return;
                }
                if (delay < 0) {
                    return;
                }
                try {
                    while (isUdpRun) {
                        DatagramPacket datagramPacket;
                        try {
                            // 组报
                            datagramPacket = new DatagramPacket(data, data.length,
                                    address, sendPort);
                            // 向组播ID，即接收group /239.0.0.1  侦听端口 8888
                            multicastSocket.send(datagramPacket);
                            Log.d(TAG, "发送" + Arrays.toString(data));
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
