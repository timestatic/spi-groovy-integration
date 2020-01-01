package com.github.sgi.core.scanner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @FileName: FileScanner.java
 * @Description: FileScanner.java类说明
 * @Author: timestatic
 * @Date: 2020/1/1 17:20
 */
public class FileScanner {

    public static String readFile(String path) {
        File textFile = new File(path);
        if (!textFile.getName().endsWith(".groovy")) {
            throw new IllegalArgumentException("only support .groovy source code file");
        }
        StringBuilder content = new StringBuilder();

        try (RandomAccessFile file = new RandomAccessFile(path, "rw")) {
            String line;
            while ((line = file.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
