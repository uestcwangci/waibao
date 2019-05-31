package com.example.festec.udpbrodcastactivity.view.login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.utils.PermissionUtil;
import com.example.festec.udpbrodcastactivity.view.main.MainActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.TreeMap;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private ProgressDialog dialog;


    // 要申请的权限
    private final String[] permissions = new String[]{
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WAKE_LOCK};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        PermissionUtil.getInstance().chekPermissions(this, permissions, permissionsResult);


        initGlobalValues();


        final EditText usernameEditText = findViewById(R.id.username);
        final EditText ipAddEditText = findViewById(R.id.ip_add);
        final Button loginButton = findViewById(R.id.login);
        final Button logoutButton = findViewById(R.id.logout);

//        logoutButton.setVisibility(View.GONE);
//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("建立Tcp连接");
        dialog.setMessage("连接中...");
        dialog.setCancelable(false);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    ipAddEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
//                loadingProgressBar.setVisibility(View.GONE);
                dialog.dismiss();
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        ipAddEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        ipAddEditText.addTextChangedListener(afterTextChangedListener);
        ipAddEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // 添加回车输入完毕
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loadingProgressBar.setVisibility(View.VISIBLE);
                    dialog.show();
                    loginViewModel.login(usernameEditText.getText().toString(),
                            ipAddEditText.getText().toString().trim());

                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                dialog.show();
                loginViewModel.login(usernameEditText.getText().toString(),
                        ipAddEditText.getText().toString().trim());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.logout(LoginActivity.this);
            }
        });

    }

    private void initGlobalValues() {
        GlobalValues.localMac = getMacAddress();
        GlobalValues.tcpServerPort = 10041;
        GlobalValues.portList = new ArrayList<>();
        GlobalValues.portMacMap = new TreeMap<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据IP地址获取MAC地址
     * @return
     */
    private static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // ignore
        }
        return strMacAddr;
    }
    /**
     * 获取移动设备本地IP
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":"))
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }


    private void updateUiWithUser(LoggedInUserView model) {
        // 登录成功的操作
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));


    }

    private void showLoginFailed(@StringRes Integer errorString) {
        // 登录错误的操作
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //创建监听权限的接口对象
    PermissionUtil.IPermissionsResult permissionsResult = new PermissionUtil.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Toast.makeText(LoginActivity.this, "权限通过", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(LoginActivity.this, "权限不通过", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }



}
