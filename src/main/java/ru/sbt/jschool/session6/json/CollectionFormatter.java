package ru.sbt.jschool.session6.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;


public class CollectionFormatter implements Formatting<Collection<?>> {
    public String repeatNTimes(String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    @Override
    public String format(Collection<?> collection, JsonFormatter jsonFormatter, HashMap<String, Object> ctx) {
        ArrayList<String> listElements = new ArrayList<>();
        ctx.put("nstLvl", (Integer) ctx.get("nstLvl") + 1);
        for (Object obj : collection) {
            listElements.add(repeatNTimes("\t", (Integer) ctx.get("nstLvl")) + jsonFormatter.marshall(obj));

        }
        ctx.put("nstLvl", (Integer) ctx.get("nstLvl") - 1);
        return String.format("[\n%s\n" + repeatNTimes("\t", (Integer) ctx.get("nstLvl")) + "]", listElements.stream().collect(Collectors.joining(",\n")));
    }
}
