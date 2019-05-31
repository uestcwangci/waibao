package com.example.festec.udpbrodcastactivity.module.message;

import java.util.Arrays;

public class MP3Message extends BaseMessage{


    public MP3Message(String broadcastType, String broadcastLevel, String dataType) {
        super(broadcastType, broadcastLevel, dataType);
    }

    @Override
    public String toString() {
        return "MP3Message{" +
                "orderLength=" + orderLength +
                ", broadcastType=" + broadcastType +
                ", level=" + level +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dataType=" + dataType +
                ", dataLength=" + dataLength +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}