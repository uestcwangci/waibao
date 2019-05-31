package com.example.festec.udpbrodcastactivity.module.protocol;

import com.example.festec.udpbrodcastactivity.module.message.BaseMessage;
import com.example.festec.udpbrodcastactivity.module.message.HeatBeatMessage;
import com.example.festec.udpbrodcastactivity.module.message.QueryMessage;
import com.example.festec.udpbrodcastactivity.module.message.RegisterMessage;
import com.example.festec.udpbrodcastactivity.module.message.SettingsMessage;
import com.example.festec.udpbrodcastactivity.module.message.TextMessage;
import com.example.festec.udpbrodcastactivity.module.utils.AckUtils;
import com.example.festec.udpbrodcastactivity.module.utils.ByteUtils;

import java.util.List;



public class EmergencyBody<T>{
    private short destinationCount; //2个字节
    private List<byte[]> destinationAddressList;
    private short orderID; //2个字节

    private T t;

    public byte[] getEmergencyBodyBytes() {
        byte[] bytes;
        bytes = ByteUtils.short2Byte(destinationCount);
        for (int i = 0; i < destinationAddressList.size(); i++){
            bytes = ByteUtils.addBytes(bytes, destinationAddressList.get(i));
        }
        bytes = ByteUtils.addBytes(bytes, ByteUtils.short2Byte(orderID));
        if (t instanceof AckUtils) {
            bytes = ByteUtils.addBytes(bytes, ((AckUtils) t).getAckUtilsBytes());
        }else if (t instanceof RegisterMessage) {   //注册
            bytes = ByteUtils.addBytes(bytes, ((RegisterMessage) t).getRegisterMessageBytes());
        } else if (t instanceof HeatBeatMessage) {  //心跳包
            bytes = ByteUtils.addBytes(bytes, ((HeatBeatMessage) t).getHeartBeatUtilsBytes());
        }else if (t instanceof QueryMessage) {   //查询
            bytes = ByteUtils.addBytes(bytes, ((QueryMessage) t).getQueryMessageBytes());
        } else if (t instanceof SettingsMessage) {  //设置参数
            bytes = ByteUtils.addBytes(bytes, ((SettingsMessage) t).getSettingMessageBytes());
        } else if (t instanceof BaseMessage) {      //文本
            bytes = ByteUtils.addBytes(bytes, ((BaseMessage) t).getBaseMessageBytes());
        }

        return bytes;
    }

    public void setT(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public short getDestinationCount() {
        return destinationCount;
    }

    public void setDestinationCount(short destinationCount) {
        this.destinationCount = destinationCount;
    }

    public List<byte[]> getDestinationAddressList() {
        return destinationAddressList;
    }

    public void setDestinationAddressList(List<byte[]> destinationAddressList) {
        this.destinationAddressList = destinationAddressList;
    }

    public short getOrderID() {
        return orderID;
    }

    public void setOrderID(short orderID) {
        this.orderID = orderID;
    }

    @Override
    public String toString() {
        return "EmergencyBody{" +
                "destinationCount=" + destinationCount +
                ", destinationAddressList=" + destinationAddressList +
                ", orderID=" + orderID +
                ", t=" + t +
                '}';
    }
}
