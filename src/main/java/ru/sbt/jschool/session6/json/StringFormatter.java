package ru.sbt.jschool.session6.json;

import java.util.HashMap;

public class StringFormatter implements Formatting<Object> {

    @Override
    public String format(Object object, JsonFormatter jsonFormatter, HashMap<String, Object> ctx) {
        return String.format("\"%s\"", object.toString());
    }
}
