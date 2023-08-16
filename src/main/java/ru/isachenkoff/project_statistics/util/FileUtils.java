package ru.isachenkoff.project_statistics.util;

import java.io.File;

public class FileUtils {

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return "";
        } else {
            return fileName.substring(i + 1);
        }
    }

}
