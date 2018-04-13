package ru.sbt.jschool.session6.handlers;

import ru.sbt.jschool.session6.HttpRequest;
import ru.sbt.jschool.session6.HttpResponse;
import ru.sbt.jschool.session6.RequestHandler;
import ru.sbt.jschool.session6.json.JsonFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListHandler implements RequestHandler {
    @Override
    public void handle(OutputStream outputStream, HttpRequest httpRequest, Map<String, Object> ctx) throws IOException {
        Map<Long, File> fileMap = (HashMap<Long, File>) ctx.get("DB_FILE_MAP");
        JsonFormatter jsonFormatter = new JsonFormatter();
        jsonFormatter.addTypeExtension(BigDecimal.class, (bigDecimal, formatter, c) -> {
            return bigDecimal.toString();
        });
        List<User> userList = new ArrayList<>();
        for (Long key : fileMap.keySet()) {
            try (
                    FileInputStream fis = new FileInputStream(fileMap.get(key).getAbsolutePath());
                    ObjectInputStream oin = new ObjectInputStream(fis)) {
                User u = (User) oin.readObject();
                userList.add(u);
            } catch (Exception e) {
                outputStream.write(HttpResponse.internalServerError(e.getMessage()));
            }
        }
        outputStream.write(HttpResponse.OK(jsonFormatter.marshall(userList)));

    }
}
