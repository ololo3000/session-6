package ru.sbt.jschool.session6.handlers;

import ru.sbt.jschool.session6.HttpRequest;
import ru.sbt.jschool.session6.HttpResponse;
import ru.sbt.jschool.session6.RequestHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CreateHandler implements RequestHandler {

    @Override
    public void handle(OutputStream outputStream, HttpRequest httpRequest, Map<String, Object> ctx) throws IOException {
        String dataBaseDirPath = ctx.get("DB_DIRECTORY").toString();
        String extension = ctx.get("DB_FILE_EXTENSION").toString();
        Map<Long, File> fileMap = (HashMap<Long, File>) ctx.get("DB_FILE_MAP");
        Integer age = null;
        String name = null;
        DecimalFormat df = new DecimalFormat();
        df.setParseBigDecimal(true);
        BigDecimal salary = new BigDecimal(0);
        try {
            age = Integer.valueOf(httpRequest.getQuery().get("age"));
            name = httpRequest.getQuery().get("name");
            salary = (BigDecimal) df.parse(httpRequest.getQuery().get("salary"));
        } catch (Exception e) {
            outputStream.write(HttpResponse.BadRequest("Invalid parameter list"));
            return;
        }

        User user = new User(age, name, salary);
        Long id = 0l;
        while (true) {
            if (!fileMap.containsKey(id)) {
                break;
            }
            id++;
        }
        String path = dataBaseDirPath + "$" + id.toString() + extension;
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
            fileMap.put(id, new File(path));
        } catch (Exception e) {
            outputStream.write(HttpResponse.internalServerError(e.getMessage()));
            return;
        }
        outputStream.write(HttpResponse.OK(id.toString()));
    }
}
