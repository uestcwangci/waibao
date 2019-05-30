package com.example.festec.udpbrodcastactivity.module.login_data;

import android.os.AsyncTask;

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
    public Result<LoggedInUser> login(String username, String ip, String mac) {
        try {
            LoggedInUser user = new LoggedInUser(username, ip, mac);

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
                    bw.write("quit");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    class TcpLinkTask extends AsyncTask<LoggedInUser, Void, Result<LoggedInUser>> {
        private LoggedInUser user;
        private SingleLocalSocket singleLocalSocket;
        @Override
        protected void onPreExecute() {
            singleLocalSocket = SingleLocalSocket.getInstance();
            // ignore
        }

        @Override
        protected Result<LoggedInUser> doInBackground(LoggedInUser... loggedInUsers) {
            try {
                user = loggedInUsers[0];

                singleLocalSocket.setSocket(new Socket(user.getIp(), 10041));

                if (singleLocalSocket.getSocket().isConnected()) {
                    return new Result.Success<>(user);
                } else {
                    return new Result.Failed<>(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new Result.Error(new IOException("Error logging in", e));
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // 处理UI更新
        }

        @Override
        protected void onPostExecute(Result<LoggedInUser> result) {
//            progressDialog.dismiss();
            if (result instanceof Result.Success) {
                 // 连接成功，发送本机mac地址
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedWriter bw = null;
                        try {
                            bw = singleLocalSocket.getBw();
                            bw.write(user.getMac());
                            bw.newLine();
                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                // 连接失败
            }
        }

    }

}

