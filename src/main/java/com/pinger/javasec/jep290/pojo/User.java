package com.pinger.javasec.jep290.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : p1n93r
 * @date : 2021/9/23 15:12
 */
@Data
public class User implements Serializable {
    private String name;
    private Integer age;
    private String address;
}
