package com.pinger.javasec.util;

import java.io.*;

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
        System.out.println("[+] 已序列化对象...");
        System.out.println("[+] 准备反序列化对象...");
        new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();
    }

    public static void savePayload(Object object,String targetPath)throws Exception{
        FileOutputStream out = new FileOutputStream(new File(targetPath));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        System.out.println("[+] 保存payload成功，文件位置："+targetPath);
    }





}
