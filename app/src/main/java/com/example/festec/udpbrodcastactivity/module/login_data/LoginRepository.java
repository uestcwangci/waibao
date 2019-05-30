package com.example.festec.udpbrodcastactivity.module.login_data;

import com.example.festec.udpbrodcastactivity.module.login_data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // 如果用户凭据将缓存在本地存储中，则建议对其进行加密
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // 单例
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    // 登录信息存储于本地
    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public Result<LoggedInUser> login(String username, String ip, String mac) {

        // handle login
        Result<LoggedInUser> result = dataSource.login(username, ip, mac);
        // 登陆成功将登录信息存至本地
//        if (result instanceof Result.Success) {
//            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
//        }
        return result;
    }
}
