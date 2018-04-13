package ru.sbt.jschool.session6.handlers;

import ru.sbt.jschool.session6.HttpRequest;
import ru.sbt.jschool.session6.HttpResponse;
import ru.sbt.jschool.session6.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class DeleteHandler implements RequestHandler {

    @Override
    public void handle(OutputStream outputStream, HttpRequest request, Map<String, Object> ctx) throws IOException {
        String[] strings = request.getPath().split("/");
        Map<Long, File> fileMap = (HashMap<Long, File>) ctx.get("DB_FILE_MAP");

        if (strings.length > 4 || strings.length == 3) {
            outputStream.write(HttpResponse.NotFound(request.getPath()));
            return;
        }
        Long id;
        try {
            id = Long.valueOf(strings[3]);
        } catch (Exception e) {
            outputStream.write(HttpResponse.BadRequest("Invalid user id"));
            return;
        }
        if (fileMap.containsKey(id)) {
            fileMap.get(id).delete();
            fileMap.remove(id);
            outputStream.write(HttpResponse.OK(id.toString()));
        } else {
            outputStream.write(HttpResponse.NotFound(id.toString()));

        }
    }
}
