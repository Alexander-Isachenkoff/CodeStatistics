package ru.isachenkoff.project_statistics.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    public static void unzip(String zipPath, String destinationPath) {
        try (ZipFile zipFile = new ZipFile(zipPath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                byte[] bytes = new byte[(int) entry.getSize()];
                zipFile.getInputStream(entry).read(bytes);
                File file = new File(destinationPath, entry.getName());
                if (!entry.isDirectory()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(bytes);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }

}
