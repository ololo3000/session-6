package ru.sbt.jschool.session6;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HttpServer {
    private final int port;
    private final Map<String, RequestHandler> requestHandlerMap = new HashMap<>();
    private final Map<String, Object> ctx = new HashMap<>();

    public static class Builder {
        private final String DEFAULT_PROP_FILE_PATH = "/app.properties";
        private int port = 8080;
        private String dataBaseDirPath = "/home/fl/DB/";
        private String propFilePath = null;
        private String fileExtension = ".bin";

        public Builder propFilePath(String propFilePath) {
            this.propFilePath = propFilePath;
            return this;
        }

        public HttpServer build() {
            try (InputStream stream = this.propFilePath == null ?
                    HttpServer.class.getResourceAsStream(DEFAULT_PROP_FILE_PATH) :
                    Files.newInputStream(Paths.get(propFilePath))) {

                Properties prop = new Properties();
                prop.load(stream);
                Object portObj = prop.get("PORT");
                Object dirPathObj = prop.get("DB_DIRECTORY");
                Object extensionObj = prop.get("DB_FILE_EXTENSION");
                if (portObj != null) {
                    this.port = Integer.valueOf(portObj.toString());
                }
                if (dirPathObj != null) {
                    this.dataBaseDirPath = dirPathObj.toString();
                }

                if (extensionObj != null) {
                    this.fileExtension = extensionObj.toString();
                }

                return new HttpServer(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HttpServer(Builder builder) {
        this.port = builder.port;
        this.ctx.put("DB_DIRECTORY", builder.dataBaseDirPath);
        File[] db = new File(builder.dataBaseDirPath).listFiles();
        HashMap<Long, File> fileMap = Arrays.stream(db).collect(
                HashMap<Long, File>::new,
                (map, f) -> {
                    map.put(Long.valueOf(f.getName()
                            .substring(1, f.getName().length() - builder.fileExtension.length())), f);
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                });

        this.ctx.put("DB_FILE_MAP", fileMap);
        this.ctx.put("DB_FILE_EXTENSION", builder.fileExtension);

    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(String path, RequestHandler requestHandler) {
        requestHandlerMap.put(path, requestHandler);
    }

    private RequestHandler getRequestHandler(HttpRequest request) {
        RequestHandler requestHandler = requestHandlerMap.get(request.getPath());
        if (requestHandler != null) {
            return requestHandler;
        }

        String[] strings = request.getPath().split("/");
        for (int i = 1; i < strings.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < strings.length - i; j++) {
                stringBuilder.append(strings[j] + "/");
            }
            requestHandler = requestHandlerMap.get(stringBuilder.toString());
            if (requestHandler != null) {
                return requestHandler;
            }
        }
        return null;
    }

    private void handleConnection(Socket clientSocket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream os = clientSocket.getOutputStream()) {
            HttpRequest request = new HttpRequest.Builder(br).build();
            if (request == null) {
                return;
            }
            RequestHandler requestHandler = getRequestHandler(request);
            if (requestHandler != null) {
                requestHandler.handle(os, request, ctx);
            } else {
                os.write(HttpResponse.NotFound(request.getPath()));
            }
        } catch (IOException e) {
            System.err.println("Connection unexpectedly " + clientSocket.getPort() + "closed");
        }
    }
}
