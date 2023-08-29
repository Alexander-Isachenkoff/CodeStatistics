package ru.isachenkoff.project_statistics.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void getExtension() {
        String extension = FileUtils.getExtension(new File("hello.png"));
        assertEquals("png", extension);
    }

    @Test
    void getExtension2() {
        String extension = FileUtils.getExtension(new File("hello.png.jpg"));
        assertEquals("jpg", extension);
    }

    @Test
    void getExtension3() {
        String extension = FileUtils.getExtension(new File("hello.png."));
        assertEquals("", extension);
    }

    @Test
    void getExtension4() {
        String extension = FileUtils.getExtension(new File("hello"));
        assertEquals("", extension);
    }

}