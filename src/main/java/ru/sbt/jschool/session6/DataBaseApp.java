package ru.sbt.jschool.session6;

import ru.sbt.jschool.session6.handlers.CreateHandler;
import ru.sbt.jschool.session6.handlers.DeleteHandler;
import ru.sbt.jschool.session6.handlers.ListHandler;
import ru.sbt.jschool.session6.handlers.UserHandler;

public class DataBaseApp {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer.Builder()
                .propFilePath("/home/fl/app.properties")
                .build();
        httpServer.addHandler("/user/delete/", new DeleteHandler());
        httpServer.addHandler("/user/create", new CreateHandler());
        httpServer.addHandler("/user/list", new ListHandler());
        httpServer.addHandler("/user/", new UserHandler());

        httpServer.run();
    }
}
