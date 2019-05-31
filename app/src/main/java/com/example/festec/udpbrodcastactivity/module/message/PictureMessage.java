package com.example.festec.udpbrodcastactivity.module.message;

import java.util.Arrays;

public class PictureMessage extends BaseMessage {


    public PictureMessage(String broadcastType, String broadcastLevel, String dataType) {
        super(broadcastType, broadcastLevel, dataType);
    }

    @Override
    public String toString() {
        return "PictureMessage{" +
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
