package com.pinger.javasec.test;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Random;

public class Md5Utils {

   public static String generate(String password) {
      Random r = new Random();
      StringBuilder sb = new StringBuilder(16);
      sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
      int len = sb.length();
      if (len < 16) {
         for(int i = 0; i < 16 - len; ++i) {
            sb.append("0");
         }
      }

      String salt = sb.toString();
      System.out.println("加密盐" + salt);
      password = md5Hex(password + salt);
      char[] cs = new char[48];

      for(int i = 0; i < 48; i += 3) {
         cs[i] = password.charAt(i / 3 * 2);
         char c = salt.charAt(i / 3);
         cs[i + 1] = c;
         cs[i + 2] = password.charAt(i / 3 * 2 + 1);
      }

      return new String(cs);
   }

   public static boolean verify(String password, String md5) {
      try {
         char[] cs1 = new char[32];
         char[] cs2 = new char[16];

         for(int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
         }

         String salt = new String(cs2);
         return md5Hex(password + salt).equals(new String(cs1));
      } catch (Exception var5) {
         return false;
      }
   }

   public static String md5Hex(String src) {
      try {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         byte[] bs = md5.digest(src.getBytes());
         return new String((new Hex()).encode(bs));
      } catch (Exception var3) {
         return null;
      }
   }

   public static void main(String[] args) {
//      String mobile = "137s2345678";
//      String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
//      if (Pattern.matches(regex, mobile)) {
//         System.out.println("校验成功");
//      } else {
//         System.out.println("校验失败");
//      }
//
//      String phone = "18893sde9495861s";
//      String hidenPhone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
//      System.out.println(hidenPhone);

      String test = generate("test");
      System.out.println(test);


   }
}
