package com.pinger.javasec;

/**
 * @author : p1n93r
 * @date : 2021/10/16 16:17
 */
public class ReverseTest {

    public static String ALLATORIxDEMO(String a) {
        final int n = 1 << 3;
        final int n2 = 5 << 4 ^ (0x2 ^ 0x5) << 1;
        final int length = (a = a).length();
        final char[] array = new char[length];
        int n3;
        int i = n3 = length - 1;
        final char[] array2 = array;
        final char c = (char)n2;
        final int n4 = n;
        while (i >= 0) {
            final char[] array3 = array2;
            final String s = a;
            final int n5 = n3;
            final char char1 = s.charAt(n5);
            --n3;
            array3[n5] = (char)(char1 ^ n4);
            if (n3 < 0) {
                break;
            }
            final char[] array4 = array2;
            final String s2 = a;
            final int n6 = n3--;
            array4[n6] = (char)(s2.charAt(n6) ^ c);
            i = n3;
        }
        return new String(array2);
    }







}
