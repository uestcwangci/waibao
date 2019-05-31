package com.example.festec.udpbrodcastactivity.view.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.example.festec.udpbrodcastactivity.module.login_data.LoginRepository;
import com.example.festec.udpbrodcastactivity.module.login_data.Result;
import com.example.festec.udpbrodcastactivity.module.login_data.model.LoggedInUser;
import com.example.festec.udpbrodcastactivity.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    /**
     * 异步处理登录
     *
     * @param username 用户名
     * @param ipAdd    ip地址
     */
    public void login(String username, String ipAdd) {

        Result<LoggedInUser> result = loginRepository.login(username, ipAdd);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void logout(Context context) {
        if (loginRepository.isLoggedIn()) {
            loginRepository.logout();
        } else {
            Toast.makeText(context, "未登录", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginDataChanged(String username, String ipAdd) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(ipAdd)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_ip_add));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // 用户名验证检查
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.trim().length() == 0) {
            return false;
        }
        // 开头不能是下划线
        if (username.trim().toCharArray()[0] == '_') {
            return false;
        }
        return true;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String ipAdd) {
        return isIpAddress(ipAdd.trim());
    }

    /**
     * 正则判断ip地址是否合法
     * @param addr 地址
     * @return
     */
    private boolean isIpAddress(String addr) {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String patten = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(patten);

        Matcher mat = pat.matcher(addr);

        return mat.find();
    }
}
