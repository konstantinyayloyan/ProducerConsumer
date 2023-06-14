package com.vmware.utils;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterUtils {
    public static void writeToFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(content);
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println("An IOException occurred when trying to write to " + fileName + ", please check");
        }
    }
}
