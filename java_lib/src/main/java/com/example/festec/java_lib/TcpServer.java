package com.example.festec.java_lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TcpServer{
    private static final int PORT = 10014;
    private boolean isListen = true;   //线程监听标志位
    private static final int THREAD_POOL_NUM = 10;  // 线程池容量
    public ArrayList<ServerSocketThread> SST = new ArrayList<ServerSocketThread>();


    public static void main(String[] args) {
        new TcpServer().start();
    }

    //更改监听标志位
    public void setIsListen(boolean b){
        isListen = b;
    }

    public void closeSelf(){
        isListen = false;
        for (ServerSocketThread s : SST){
            s.isRun = false;
        }
        SST.clear();
    }


    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
//            serverSocket.setSoTimeout(5000);
            // 最大支持n条连接,核心线程数=最大线程数
            ExecutorService service = Executors.newFixedThreadPool(THREAD_POOL_NUM);
            while (isListen){
                System.out.println("开始监听....");
                Socket socket = serverSocket.accept();
                if (socket != null){
                    service.execute(new ServerSocketThread(socket));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    class ServerSocketThread extends Thread{
        Socket socket = null;
        private PrintWriter pw;
        private InputStream is = null;
        private OutputStream os = null;
        private String ip = null;
        private boolean isRun = true;

        ServerSocketThread(Socket socket){
            this.socket = socket;
            ip = socket.getInetAddress().toString();
            System.out.println("ServerSocketThread:检测到新的客户端联入,ip:" + ip);

            try {
                socket.setSoTimeout(5000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                pw = new PrintWriter(os,true);
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String msg){
            pw.println(msg);
            pw.flush(); //强制送出数据
        }

        @Override
        public void run() {
            byte[] buff = new byte[4096];
            String rcvMsg;
            int rcvLen;
            SST.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()) {
                try {
                    if ((rcvLen = is.read(buff)) != -1) {
                        rcvMsg = new String(buff, 0, rcvLen);
                        System.out.println("run:收到消息: " + rcvMsg);
                        if (rcvMsg.equals("QuitServer")) {
                            isRun = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
                SST.clear();
                System.out.println("run: 断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}