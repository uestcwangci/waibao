package com.example.festec.udpbrodcastactivity.module.login_data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.SingleLocalSocket;
import com.example.festec.udpbrodcastactivity.module.login_data.model.LoggedInUser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 处理具有登录凭据的身份验证并检索用户信息的类。
 */
public class LoginDataSource {
    public Result<LoggedInUser> login(String username, String ip) {
        try {
            LoggedInUser user = new LoggedInUser(username, ip);

            // 异步处理Tcp连接
            AsyncTask<LoggedInUser, Void, Result<LoggedInUser>> execute = new TcpLinkTask().execute(user);
            return execute.get();

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(SingleLocalSocket.getInstance().getOs()));
                    bw.write("quit" + GlobalValues.udpPort);
                    Log.d("waibao", "send quit");
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    static class TcpLinkTask extends AsyncTask<LoggedInUser, Void, Result<LoggedInUser>> {
        private LoggedInUser user;
        SingleLocalSocket localSocket;
        @Override
        protected void onPreExecute() {

            // ignore
        }

        @Override
        protected Result<LoggedInUser> doInBackground(LoggedInUser... loggedInUsers) {
            try {
                user = loggedInUsers[0];
                SingleLocalSocket.getInstance().initSocket(user.getIp(), GlobalValues.tcpServerPort);
                localSocket = SingleLocalSocket.getInstance();
                if (localSocket.getSocket().isConnected()) {
                    BufferedWriter bw = null;
                    try {
                        bw = localSocket.getBw();
                        bw.write(GlobalValues.udpPort + "|" + GlobalValues.localMac);
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new Result.Success<>(user);
                } else {
                    return new Result.Failed<>(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
                localSocket.disconnect();
                return new Result.Error(new IOException("Error logging in", e));
            }
        }


        @Override
        protected void onPostExecute(Result<LoggedInUser> result) {
//            progressDialog.dismiss();
            if (result instanceof Result.Success) {

            } else {
                // 连接失败
            }
        }

    }

}

