package ru.isachenkoff.project_statistics.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileTypeTest {

    @Test
    void getRegisteredFileTypes() {
        Map<String, FileType> fileTypeMap = FileType.getRegisteredFileTypes();
        FileType fileType = fileTypeMap.get("cpp");
        assertEquals("C++", fileType.getTypeName());
        assertEquals("cpp", fileType.getExtension());
        assertEquals("cpp.png", fileType.getImageFileName());
    }

    @Test
    void instanceOf() {
        FileType fileType = FileType.of("java");
        assertEquals("Java", fileType.getTypeName());
        assertEquals("java", fileType.getExtension());
        assertEquals("java.png", fileType.getImageFileName());
        assertNotNull(fileType.getImage());

        // cached
        assertNotNull(fileType.getImage());
    }

    @Test
    void instanceOf_notRegistered() {
        FileType fileType = FileType.of("not");
        assertEquals("not", fileType.getTypeName());
        assertEquals("not", fileType.getExtension());
        assertNull(fileType.getImageFileName());
        assertEquals(FileType.DEFAULT_FILE_IMAGE, fileType.getImage());
    }

}