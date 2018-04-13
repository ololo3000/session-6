package ru.sbt.jschool.session6;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@FunctionalInterface
public interface RequestHandler {
    void handle(OutputStream outputStream, HttpRequest httpRequest, Map<String, Object> ctx) throws IOException;
}
