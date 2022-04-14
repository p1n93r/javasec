//package com.pinger.javasec.jep290;
//
//import com.pinger.javasec.jep290.filter.CustomObjectInputFilter;
//import sun.misc.ObjectInputFilter;
//
//import java.io.FileInputStream;
//import java.io.ObjectInputStream;
//
///**
// * @author : p1n93r
// * @date : 2021/9/23 14:16
// */
//public class BasicLearn {
//
//    public static void testOne()throws Exception{
//        ObjectInputFilter.Config.setSerialFilter(new CustomObjectInputFilter());
//
//        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("xxxx/xxx"));
//        ObjectInputFilter.Config.setObjectInputFilter(objectInputStream,new CustomObjectInputFilter());
//
//    }
//
//
//    public static void main(String[] args)throws Exception {
//        testOne();
//    }
//
//
//
//
//
//}
