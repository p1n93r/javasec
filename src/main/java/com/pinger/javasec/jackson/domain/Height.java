package com.pinger.javasec.jackson.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : p1n93r
 * @date : 2021/8/4 9:39
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Height {
    int h = 0;
}
