package com.pinger.javasec.ssti.ognl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : p1n93r
 * @date : 2021/9/14 9:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String name;
    Integer age;
    Address address;
}
