package com.pinger.javasec.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author : p1n93r
 * @date : 2021/3/31 11:52
 * 序列化、反序列化工具
 */
public class SerialUtil {


    public static void runPayload(Object object) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        System.out.println(outputStream);
        new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();
    }


}
