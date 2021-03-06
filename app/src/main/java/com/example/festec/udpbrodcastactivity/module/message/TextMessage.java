package com.example.festec.udpbrodcastactivity.module.message;


public class TextMessage extends BaseMessage{


    public TextMessage(String broadcastType, String broadcastLevel, String dataType) {
        super(broadcastType, broadcastLevel, dataType);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "orderLength=" + orderLength +
                ", broadcastType=" + broadcastType +
                ", level=" + level +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dataType=" + dataType +
                ", dataLength=" + dataLength +
                ", data=" + new String(data) +
                '}';
    }
}
