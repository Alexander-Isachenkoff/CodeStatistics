package ru.isachenkoff.project_statistics.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatFileTest {

    public static final String TEST_TREE_PATH = "src/test/resources/test_tree";
    private static StatFileRoot statFile;

    @BeforeAll
    static void beforeAll() {
        File file = new File(TEST_TREE_PATH);
        statFile = new StatFileRoot(file);
    }

    @Test
    void flatFiles() {
        assertEquals(26, statFile.flatFiles().size());
    }

    @Test
    void getChildren() {
        StatFile child2 = statFile.getChildren().get(2);
        assertEquals(9, child2.getChildren().size());
    }

    @Test
    void getAllFileTypes() {
        List<String> allFileTypes = statFile.getAllFileTypes();

        assertEquals(4, allFileTypes.size());
        assertTrue(allFileTypes.contains("c"));
        assertTrue(allFileTypes.contains("h"));
        assertTrue(allFileTypes.contains("exe"));
        assertTrue(allFileTypes.contains("json"));
    }

    @Test
    void getTotalLines() {
        statFile.setExtFilter(statFile.getAllFileTypes());
        int totalLines = statFile.getTotalLines();
        assertEquals(918, totalLines);
    }

    @Test
    void getTotalLinesFiltered() {
        statFile.setExtFilter(Arrays.asList("c", "h"));
        int totalLines = statFile.getTotalLines();
        int notEmptyLines = statFile.getNotEmptyLines();
        assertEquals(832, totalLines);
        assertEquals(690, notEmptyLines);
    }

    @Test
    void getNotEmptyLinesInfo() {
        String notEmptyLinesInfo = statFile.getNotEmptyLinesInfo();
        assertEquals("776 (85%)", notEmptyLinesInfo);
    }

    @Test
    void getFileTypesStatistics() {
        List<FileTypeStat> statList = statFile.getFileTypesStatistics();

        assertEquals("c", statList.get(0).getFileType().getExtension());
        assertEquals("C", statList.get(0).getFileType().getTypeName());
        assertEquals("c.png", statList.get(0).getFileType().getFileName());
        assertEquals(11, statList.get(0).getFilesCount());
        assertEquals(448, statList.get(0).getLinesCount());
        assertEquals("364 (81%)", statList.get(0).getNotEmptyLinesInfo());

        assertEquals("exe", statList.get(1).getFileType().getExtension());
        assertEquals("exe", statList.get(1).getFileType().getTypeName());
        assertEquals(null, statList.get(1).getFileType().getFileName());
        assertEquals(7, statList.get(1).getFilesCount());
        assertEquals(0, statList.get(1).getLinesCount());
        assertEquals("0 (0%)", statList.get(1).getNotEmptyLinesInfo());

        assertEquals("h", statList.get(2).getFileType().getExtension());
        assertEquals("H", statList.get(2).getFileType().getTypeName());
        assertEquals("c.png", statList.get(2).getFileType().getFileName());
        assertEquals(4, statList.get(2).getFilesCount());
        assertEquals(384, statList.get(2).getLinesCount());
        assertEquals("326 (85%)", statList.get(2).getNotEmptyLinesInfo());

        assertEquals("json", statList.get(3).getFileType().getExtension());
        assertEquals("JSON", statList.get(3).getFileType().getTypeName());
        assertEquals("json.png", statList.get(3).getFileType().getFileName());
        assertEquals(4, statList.get(3).getFilesCount());
        assertEquals(86, statList.get(3).getLinesCount());
        assertEquals("86 (100%)", statList.get(3).getNotEmptyLinesInfo());
    }

}