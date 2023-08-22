package ru.isachenkoff.project_statistics.util;

import javafx.scene.image.Image;
import ru.isachenkoff.project_statistics.Main;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileTypesIcons {

    private static final String IMAGES_FILE_TYPES_DIR = "images/file_types/";
    private static final Map<String, Image> typeImageMap = new HashMap<>();
    private static final Image DEFAULT_FILE_IMAGE;

    static {
        InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + "default file.png");
        DEFAULT_FILE_IMAGE = (resource != null) ? new Image(resource) : null;
    }

    public static Image getIconForType(String fileType) {
        if (typeImageMap.containsKey(fileType)) {
            return typeImageMap.get(fileType);
        } else {
            InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + fileType + ".png");
            Image image = (resource != null) ? new Image(resource) : DEFAULT_FILE_IMAGE;
            typeImageMap.put(fileType, image);
            return image;
        }
    }

}
