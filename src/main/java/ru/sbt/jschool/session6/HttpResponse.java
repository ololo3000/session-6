package ru.sbt.jschool.session6;

public class HttpResponse {
    public static byte[] OK(String body) {
        return ("HTTP/1.1 200 OK\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + body.length() + "\n" +
                "Connection: close\n\n" +
                body).getBytes();
    }

    public static byte[] BadRequest(String body) {
        return ("HTTP/1.1 400 Bad Request\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + body.length() + "\n" +
                "Connection: close\n\n" +
                body).getBytes();
    }

    public static byte[] NotFound(String body) {
        return ("HTTP/1.1 404 Not Found\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + body.length() + "\n" +
                "Connection: close\n\n" +
                body).getBytes();
    }

    public static byte[] internalServerError(String body) {
        return ("HTTP/1.1 500 Internal Server Error\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + body.length() + "\n" +
                "Connection: close\n\n" +
                body).getBytes();
    }


}
