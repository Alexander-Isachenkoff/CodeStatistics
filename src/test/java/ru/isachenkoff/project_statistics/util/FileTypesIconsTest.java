package ru.isachenkoff.project_statistics.util;

import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileTypesIconsTest {

    @Test
    void getIconForType() {
        Image image = FileTypesIcons.getIconForType("cpp");
        assertNotNull(image);
    }

}