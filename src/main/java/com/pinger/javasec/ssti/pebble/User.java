package com.pinger.javasec.ssti.pebble;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : p1n93r
 * @date : 2021/8/9 15:36
 */
@Setter
@ToString
public class User {
    private Integer id;
    private String name;


    public String isName(){
        return this.name;
    }

}
