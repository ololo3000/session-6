package ru.sbt.jschool.session6.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DateFormatter implements Formatting<Date> {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String format(Date date, JsonFormatter jsonFormatter, HashMap<String, Object> ctx) {
        return String.format("\"%s\"", DATE_FORMAT.format(date));
    }
}
