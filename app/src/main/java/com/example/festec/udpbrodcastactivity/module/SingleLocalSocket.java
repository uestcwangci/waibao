package com.example.festec.udpbrodcastactivity.module;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 单例模式，统一管理socket
 */
public class SingleLocalSocket {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;

    private static SingleLocalSocket INSTANCE = null;

    private SingleLocalSocket() {
    }

    public static SingleLocalSocket getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingleLocalSocket();
        }
        return INSTANCE;
    }


    public Socket getSocket() {
        return socket;
    }

    public void initSocket(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
            bw = new BufferedWriter(new OutputStreamWriter(os));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public OutputStream getOs() {
        return os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(SingleLocalSocket.getInstance().getOs()));
                    bw.write("quit");
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
