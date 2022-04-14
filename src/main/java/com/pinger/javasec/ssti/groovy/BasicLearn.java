package com.pinger.javasec.ssti.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author : p1n93r
 * @date : 2021/9/23 12:09
 */
public class BasicLearn {

    public static void testGroovyShell(){
        Binding bind = new Binding();
        bind.setVariable("name", "p1n93r");
        bind.setVariable("age", "18");
        GroovyShell shell = new GroovyShell(bind);
        Object obj = shell.evaluate("str = name+'\\r\\n'+age;return str");
        System.out.println(obj);
    }


    public static void main(String[] args) {
        testGroovyShell();
    }


}
