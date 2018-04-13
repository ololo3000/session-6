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
import java.util.HashMap;
import java.util.Map;

public class UserHandler implements RequestHandler {
    @Override
    public void handle(OutputStream outputStream, HttpRequest request, Map<String, Object> ctx) throws IOException {
        String[] strings = request.getPath().split("/");

        if (strings.length > 3 || strings.length == 2) {
            outputStream.write(HttpResponse.NotFound(request.getPath()));
            return;
        }
        if (!strings[2].startsWith("$")) {
            outputStream.write(HttpResponse.NotFound(request.getPath()));
            return;
        }
        Long id;
        try {
            id = Long.valueOf(strings[2].substring(1));
        } catch (Exception e) {
            outputStream.write(HttpResponse.BadRequest("Invalid user id"));
            return;
        }

        Map<Long, File> fileMap = (HashMap<Long, File>) ctx.get("DB_FILE_MAP");
        JsonFormatter jsonFormatter = new JsonFormatter();
        jsonFormatter.addTypeExtension(BigDecimal.class, (bigDecimal, formatter, c) -> {
            return bigDecimal.toString();
        });

        if (fileMap.containsKey(id)) {
            try (
                    FileInputStream fis = new FileInputStream(fileMap.get(id).getAbsolutePath());
                    ObjectInputStream oin = new ObjectInputStream(fis)) {
                User u = (User) oin.readObject();
                outputStream.write(HttpResponse.OK(jsonFormatter.marshall(u)));
            } catch (Exception e) {
                outputStream.write(HttpResponse.internalServerError(e.getMessage()));
            }
        } else {
            outputStream.write(HttpResponse.NotFound(id.toString()));
        }
    }
}
