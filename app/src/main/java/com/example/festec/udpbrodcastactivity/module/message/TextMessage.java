package com.example.festec.udpbrodcastactivity.module.message;

/**
 * @author lanxian
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */

public class TextMessage {
    private short orderLength; // 命令参数长度
    private byte broadcastType; // 广播类型
    private byte level; // 事件级别
    private int startTime; // 开始时间
    private int endTime; // 结束时间
    private byte dataType; // 数据类型
    private int dataLength; // 数据长度
    private byte[] data; // 内容

    public TextMessage(String broadcastType, String broadcastLevel) {
        this.orderLength = 7;
        switch (broadcastType) {
            case "应急演练-发布系统演练":
                this.broadcastType = 0x01;
                break;
            case "应急演练-模拟演练":
                this.broadcastType = 0x02;
                break;
            case "应急演练-实际演练":
                this.broadcastType = 0x03;
                break;
            case "应急广播":
                this.broadcastType = 0x04;
                break;
            case "日常广播":
                this.broadcastType = 0x05;
                break;
            default:
                break;
        }
        switch (broadcastLevel) {
            case "特别重大":
                this.level = 0x01;
                break;
            case "重大":
                this.level = 0x02;
                break;
            case "较大":
                this.level = 0x03;
                break;
            case "一般":
                this.level = 0x04;
                break;
            default:
                break;
        }

    }

    public byte[] getTextMessageBytes() {
        byte[] bytes;

    }

    public short getOrderLength() {
        return orderLength;
    }

    public void setOrderLength(short orderLength) {
        this.orderLength = orderLength;
    }

    public byte getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(byte broadcastType) {
        this.broadcastType = broadcastType;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
