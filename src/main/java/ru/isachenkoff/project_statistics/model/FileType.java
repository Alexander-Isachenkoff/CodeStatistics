package ru.isachenkoff.project_statistics.model;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.isachenkoff.project_statistics.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class FileType {

    private static final String IMAGES_FILE_TYPES_DIR = "images/file_types/";
    private static final Map<String, Image> typeImageMap = new HashMap<>();
    private static final Image DEFAULT_FILE_IMAGE;
    private static Map<String, FileType> fileTypes;

    static {
        InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + "default file.png");
        DEFAULT_FILE_IMAGE = (resource != null) ? new Image(resource) : null;
    }

    private final String extension;
    private final String typeName;
    private final String fileName;

    public static FileType of(String extension) {
        Map<String, FileType> types = getRegisteredFileTypes();
        if (types.containsKey(extension)) {
            return types.get(extension);
        } else {
            return new FileType(extension, extension, null);
        }
    }

    static Map<String, FileType> getRegisteredFileTypes() {
        if (fileTypes == null) {
            fileTypes = load();
        }
        return fileTypes;
    }

    private static Map<String, FileType> load() {
        InputStream resource = Main.class.getResourceAsStream("file_types.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
        return reader.lines()
                .skip(1)
                .map(s -> s.split(";"))
                .collect(Collectors.toMap(s -> s[0], s -> new FileType(s[0], s[1], s[2])));
    }

    public Image getImage() {
        if (typeImageMap.containsKey(extension)) {
            return typeImageMap.get(extension);
        } else {
            if (fileName != null) {
                InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + fileName);
                Image image = (resource != null) ? new Image(resource) : DEFAULT_FILE_IMAGE;
                typeImageMap.put(extension, image);
                return image;
            } else {
                return DEFAULT_FILE_IMAGE;
            }
        }
    }

}
