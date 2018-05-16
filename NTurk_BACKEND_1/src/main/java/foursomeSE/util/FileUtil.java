package foursomeSE.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
//    public final static String uploadFilePath = new File("").getAbsolutePath() + "/src/main/resources/data/images/";
    public final static String jsonFilePath = "/data/jsons/";

    public static <T> List<T> readAll(Class<T[]> clazz, String tableName) {
        try (Scanner scanner = new Scanner(getInputStream(tableName))) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }
            String json = sb.toString();

            Gson gson = new Gson();
            List<T> result = new ArrayList<>(Arrays.asList(gson.fromJson(json, clazz)));
            return result;
        }
    }

    public static <T> void saveAll(List<T> tableList, String tableName) {
        try (PrintWriter printWriter = new PrintWriter(getOutputStream(tableName))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(tableList);
            printWriter.println(json);
            printWriter.flush();
        }
    }

    public static String getUploadImagePath() {
        if (FileUtil.class.getResource("/data/jsons/").toString().startsWith("jar:")) {
//            return "./data/images/";
            return "./";
        }
        return new File("").getAbsolutePath() + "/src/main/resources/data/images/";
    }

    /**
     * private
     * */

    private static InputStream getInputStream(String tableName) {
        try {
            return new FileInputStream(getFileName(tableName));
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            try {
                new File(getFileName(tableName)).createNewFile();
                try (PrintWriter printWriter = new PrintWriter(getOutputStream(tableName))) {
                    printWriter.println("[]");
                    printWriter.flush();
                }
                return getInputStream(tableName);
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    private static OutputStream getOutputStream(String tableName) {
        try {
            return new FileOutputStream(getFileName(tableName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(String tableName) {
        // TODO 感觉这样写很垃圾
        if (FileUtil.class.getResource("/data/jsons/").toString().startsWith("jar:")) {
//            return "./data/jsons/" + tableName + ".json";
            return "./" + tableName + ".json";
        }

        String filePath = new File("").getAbsolutePath(); // 可以这样看路径
        filePath += "/src/main/resources/data/jsons/" + tableName + ".json";
        return filePath;
    }

    public static void main(String[] args) throws IOException {
        getInputStream("test1");
    }
}
