package com.example.festec.udpbrodcastactivity.module.utils;

public class ByteUtils {
    //合并两个byte数组
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    //将整数转化为字节数组,注意转型
    public static byte[] getBytes(int data1){
        byte[] data2 = new byte[4];
        data2[3] = (byte) (data1 & 0xFF);
        data2[2] = (byte) (data1 >> 8 & 0xFF);
        data2[1] = (byte) (data1 >> 16 & 0xFF);
        data2[0] = (byte) (data1 >> 24 & 0xFF);
        return data2;
    }

    //将数组转化为十六进制
    public static String bytesToHexString(byte[] data1){
        StringBuilder stringBuilder = new StringBuilder("");
        if (data1 == null || data1.length <= 0) {
            return null;
        }
        for (int i = 0; i < data1.length; i++) {
            int v = data1[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}

