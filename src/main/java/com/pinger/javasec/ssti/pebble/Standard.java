package com.pinger.javasec.ssti.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/8/9 15:11
 * 研究pebble安全机制
 */
public class Standard {

    public static void caseOne()throws Exception{

//        PebbleEngine engine = new PebbleEngine.Builder().methodAccessValidator(new NoOpMethodAccessValidator()).build();
        PebbleEngine engine = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate compiledTemplate = engine.getTemplate("templates/home.html");
        Writer writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        User user = new User();
        user.setName("p1n93r");
        context.put("user", user);
        context.put("content", "This is a basic test");
        compiledTemplate.evaluate(writer, context);
        String output = writer.toString();
        System.out.println(output);




    }


    public static void main(String[] args) throws Exception{
        caseOne();
    }

}
