package com.example.festec.udpbrodcastactivity.module.login_data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String ip;

    public LoggedInUser(String displayName, String ip) {
        this.displayName = displayName;
        this.ip = ip;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getIp() {
        return ip;
    }
}
