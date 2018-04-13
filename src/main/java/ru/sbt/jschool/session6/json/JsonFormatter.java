package ru.sbt.jschool.session6.json;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonFormatter {
    private final static Formatting<Object> PRIMITIVE_TYPE_FORMATTER = new PrimitiveTypeFormatter();
    private final static Formatting<Collection<?>> COLLECTION_FORMATTER = new CollectionFormatter();
    private final static Formatting<Object> STRING_FORMATTER = new StringFormatter();
    private final static Formatting<Date> DATE_FORMATTER = new DateFormatter();
    private final static Formatting<Calendar> CALENDAR_FORMATTER = new CalendarFormatter();

    private HashMap<Class<?>, Formatting> typeExtensions = new HashMap<>();
    private HashMap<Class<?>, Formatting> baseTypeExtensions = new HashMap<>();
    private HashMap<String, Object> ctx = new HashMap<>();

    public JsonFormatter() {
        ctx.put("nstLvl", 0);
        typeExtensions.put(Boolean.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Character.class, STRING_FORMATTER);
        typeExtensions.put(Byte.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Short.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Integer.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Long.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Float.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Double.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(Void.class, PRIMITIVE_TYPE_FORMATTER);
        typeExtensions.put(String.class, STRING_FORMATTER);
        baseTypeExtensions.put(Date.class, DATE_FORMATTER);
        baseTypeExtensions.put(Calendar.class, CALENDAR_FORMATTER);
        baseTypeExtensions.put(List.class, COLLECTION_FORMATTER);
        baseTypeExtensions.put(Set.class, COLLECTION_FORMATTER);
    }


    private static ArrayList<Field> getFields(Object obj) {
        Class<?> clazz = obj.getClass();
        ArrayList<Field> fields = new ArrayList<>();
        while (!clazz.equals(Object.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private Formatting<?> getBaseTypeFormatter(Class<?> clazz) {
        for (Class<?> cls : baseTypeExtensions.keySet()) {
            if (cls.isAssignableFrom(clazz)) {
                return baseTypeExtensions.get(cls);
            }
        }
        return null;
    }

    private String fieldToString(Object obj, String fieldName) {
        String resultStr = "";
        if (obj == null) {
            return String.format("\"%s\":%s", fieldName, null);
        }
        Class<?> clazz = obj.getClass();

        Formatting baseTypeFormatter = getBaseTypeFormatter(clazz);
        if (typeExtensions.containsKey(clazz)) {
            resultStr = typeExtensions.get(clazz).format(obj, this, ctx);
        } else if (baseTypeFormatter != null) {
            resultStr = baseTypeFormatter.format(obj, this, ctx);
        } else {
            resultStr = marshall(obj);
        }
        return String.format("\"%s\":%s", fieldName, resultStr);
    }


    public <T> void addTypeExtension(Class<T> clazz, Formatting<T> formatter) {
        typeExtensions.put(clazz, formatter);
    }

    public <T> void addBaseTypeExtension(Class<T> clazz, Formatting<T> formatter) {
        baseTypeExtensions.put(clazz, formatter);
    }

    public String repeatNTimes(String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    public String marshall(Object object) {
        if (object == null) {
            return null;
        }

        if (typeExtensions.containsKey(object.getClass())) {
            return typeExtensions.get(object.getClass()).format(object, this, ctx);
        }

        Formatting baseTypeFormatter = getBaseTypeFormatter(object.getClass());

        if (baseTypeFormatter != null) {
            return baseTypeFormatter.format(object, this, ctx);
        }

        ArrayList<String> stringedFields = new ArrayList<>();

        for (Field field : getFields(object)) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Annotation setterAnnotation = field.getAnnotation(JsonProperty.class);
            String fieldName;
            if (setterAnnotation != null) {
                fieldName = ((JsonProperty) setterAnnotation).name();
            } else {
                fieldName = field.getName();
            }

            try {
                ctx.put("nstLvl", (Integer) ctx.get("nstLvl") + 1);
                stringedFields.add(repeatNTimes("\t", (Integer) ctx.get("nstLvl")) + fieldToString(field.get(object), fieldName));
                ctx.put("nstLvl", (Integer) ctx.get("nstLvl") - 1);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable access to field: " + field.getName() + " of class: " + field.getType().getName(), e);
            }
        }
        return String.format("{\n%s\n" + repeatNTimes("\t", (Integer) ctx.get("nstLvl")) + "}", stringedFields.stream().collect(Collectors.joining(",\n")));
    }


}
