package ru.sbt.jschool.session6.json;

import java.util.HashMap;

@FunctionalInterface
public interface Formatting<T> {
    String format(T object, JsonFormatter jsonFormatter, HashMap<String, Object> ctx);


}
