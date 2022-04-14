package com.pinger.javasec.jackson.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : p1n93r
 * @date : 2021/8/4 9:07
 */
@Getter
@Setter
public class User {

    private Height height;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Object object;

}
