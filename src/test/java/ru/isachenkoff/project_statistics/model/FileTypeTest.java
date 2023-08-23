package ru.isachenkoff.project_statistics.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileTypeTest {

    @Test
    void load() {
        Map<String, FileType> fileTypeMap = FileType.getRegisteredFileTypes();
        FileType fileType = fileTypeMap.get("cpp");
        assertEquals("C++", fileType.getTypeName());
    }

}