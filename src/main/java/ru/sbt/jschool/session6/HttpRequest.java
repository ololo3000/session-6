package ru.sbt.jschool.session6;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String uri;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> query;
    private final Map<String, String> headers;

    public static class Builder {
        private final BufferedReader input;
        private String method = "";
        private String uri = "";
        private String path = "";
        private String httpVersion = "";
        private Map<String, String> query = new HashMap<>();
        private Map<String, String> headers = new HashMap<>();

        public Builder(BufferedReader input) {
            this.input = input;
        }

        public HttpRequest build() {
            String startLine;
            try {
                startLine = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException("");
            }

            if (startLine == null) {
                return null;
            }

            String[] strings = startLine.split(" ");
            if (strings.length < 3) {
                throw new RuntimeException("Invalid HTTP StartLine");
            }

            method = strings[0];
            uri = strings[1];
            httpVersion = strings[2];
            strings = uri.split("\\?");
            path = strings[0];

            if (strings.length > 1) {
                for (String arg : strings[1].split("&")) {
                    String[] entry = arg.split("=");
                    if (entry.length == 2) {
                        query.put(entry[0], entry[1]);
                    }
                }
            }

            String headerLine;
            do {
                try {
                    headerLine = input.readLine();
                    if (headerLine == null) {
                        return null;
                    }
                    String[] entry = headerLine.split(": ");
                    if (entry.length == 2) {
                        headers.put(entry[0], entry[1]);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("");
                }
            } while (!headerLine.equals(""));
            return new HttpRequest(this);
        }
    }

    public HttpRequest(Builder builder) {
        this.method = builder.method;
        this.query = builder.query;
        this.httpVersion = builder.httpVersion;
        this.uri = builder.uri;
        this.headers = builder.headers;
        this.path = builder.path;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getQuery() {
        return this.query;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public String getPath() {
        return this.path;
    }

    public String getUri() {
        return this.uri;
    }

    public String getMethod() {
        return this.method;
    }

}
