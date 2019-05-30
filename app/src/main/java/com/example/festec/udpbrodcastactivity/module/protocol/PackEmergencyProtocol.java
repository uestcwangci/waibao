package com.example.festec.udpbrodcastactivity.module.protocol;

import android.content.Context;

import com.example.festec.udpbrodcastactivity.module.message.HeatBeatMessage;
import com.example.festec.udpbrodcastactivity.module.message.MP3Message;
import com.example.festec.udpbrodcastactivity.module.message.PictureMessage;
import com.example.festec.udpbrodcastactivity.module.message.QueryMessage;
import com.example.festec.udpbrodcastactivity.module.message.RegisterMessage;
import com.example.festec.udpbrodcastactivity.module.message.SettingsMessage;
import com.example.festec.udpbrodcastactivity.module.message.StopMessage;
import com.example.festec.udpbrodcastactivity.module.message.TextMessage;
import com.example.festec.udpbrodcastactivity.module.utils.ByteUtils;
import com.example.festec.udpbrodcastactivity.module.utils.CRC32Utils;
import com.example.festec.udpbrodcastactivity.module.utils.PrefUtils;
import com.example.festec.udpbrodcastactivity.module.utils.ResourceEncodeUtils;

import java.util.ArrayList;
import java.util.List;


public class PackEmergencyProtocol<T> {
    private EmergencyProtocol<T> emergencyProtocol;
    private EmergencyHeader header;
    private EmergencyBody<T> body;
    private Context context;


    public PackEmergencyProtocol() {
        emergencyProtocol = new EmergencyProtocol<>();
        header = new EmergencyHeader();
        body = new EmergencyBody<>();
        emergencyProtocol.setHeader(header);
        emergencyProtocol.setBody(body);
    }

    public PackEmergencyProtocol(Context context) {
        this.context = context;
        emergencyProtocol = new EmergencyProtocol<>();
        header = new EmergencyHeader();
        body = new EmergencyBody<>();
        emergencyProtocol.setHeader(header);
        emergencyProtocol.setBody(body);
    }

    /**
     * 注册、心跳包等发送给服务器端的打包
     */
    public EmergencyProtocol<T> packPackEmergencyProtocol(T t) {
        //设置目的设备的数量
        body.setDestinationCount((short) 1);
        //设置目的设备逻辑地址
        List<byte[]> destinationList = new ArrayList<>();
        byte[] destinationAddress = new byte[]{0x01, 0x03, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03};
        destinationList.add(destinationAddress);
        body.setDestinationAddressList(destinationList);

        if (t instanceof RegisterMessage) {
            //命令ID
            body.setOrderID(new byte[]{0x00, 0x10});//注册
        }else if (t instanceof HeatBeatMessage) {
            //命令ID
            body.setOrderID(new byte[]{0x00, (byte) 0x80});  //心跳包
        } else if (t instanceof QueryMessage) {
            //命令ID
            body.setOrderID(new byte[]{0x00, (byte) 0x20});  //查询
        }else if (t instanceof SettingsMessage) {
            body.setOrderID(new byte[]{0x00, (byte) 0x30});  //设置参数命令
        } else if (t instanceof PictureMessage) {
            body.setOrderID(new byte[]{0x00, (byte) 0x01});
        } else if (t instanceof TextMessage) {
            body.setOrderID(new byte[]{0x00, (byte) 0x01});
        } else if (t instanceof MP3Message) {
            body.setOrderID(new byte[]{0x00, (byte) 0x01});
        } else if (t instanceof StopMessage) {
            body.setOrderID(new byte[]{0x00, (byte) 0x02});
        }
        //命令参数(包括命令参数长度与命令参数内容)
        body.setT(t);
        //消息头
        setHeaderMessage(t);
        byte[] packetBytes = ByteUtils.addBytes(header.getHeaderBytes(), body.getEmergencyBodyBytes());
        System.out.println("发送的CRC32：" + CRC32Utils.encode(packetBytes));
        emergencyProtocol.setCrc32(CRC32Utils.encode(packetBytes));
        return emergencyProtocol;
    }

    /****设置消息头的信息****/
    private void setHeaderMessage(T t) {
        //设置源设备逻辑地址
        byte[] sourceAddress = ResourceEncodeUtils.getSourceAddress(context);
        if (sourceAddress != null) {
            PrefUtils.setString(context, "sourceAddress", ByteUtils.bytesToHexString(sourceAddress));
            header.setSourceAddress(sourceAddress);
        }
        //设置数据包的消息编号
        header.setMessageId((short) 1);
        //设置数据包类型为请求
        header.setPacketType((short) 1);
        if (t instanceof RegisterMessage) {
            //设置数据包的长度
            //消息头长度24Bytes+目标设备数量2Bytes+目标设备逻辑地址12×nBytes+命令ID长度2Byte+命令参数长度4Bytes+命令参数内容长度+CRC校验码长度
            header.setPacketLength(24 + 2 + 12 * body.getDestinationCount() + 2 + 4 + ((RegisterMessage) t).getOrderLength() + 4);
        }else if (t instanceof HeatBeatMessage) {
            header.setPacketLength(24 + 2 + 12 * body.getDestinationCount() + 2 + 4 + ((HeatBeatMessage) t).getOrderLength() + 4);
        } else if (t instanceof QueryMessage) {
            header.setPacketLength(24 + 2 + 12 * body.getDestinationCount() + 2 + 4 + ((QueryMessage) t).getOrderLength() + 4);
        } else if (t instanceof SettingsMessage) {
            header.setPacketLength(24 + 2 + 12 * body.getDestinationCount() + 2 + 4 + ((SettingsMessage) t).getOrderLength() + 4);
        }
    }
}
