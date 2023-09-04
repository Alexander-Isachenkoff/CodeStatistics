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

    public static final Image DIRECTORY_IMAGE;
    public static final Image DEFAULT_FILE_IMAGE;
    private static final String IMAGES_FILE_TYPES_DIR = "images/file_types/";
    private static final Map<String, Image> typeImageMap = new HashMap<>();
    private static Map<String, FileType> fileTypes;

    static {
        InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + "default file.png");
        DEFAULT_FILE_IMAGE = (resource != null) ? new Image(resource) : null;

        InputStream dirImgRes = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + "folder.png");
        DIRECTORY_IMAGE = (dirImgRes != null) ? new Image(dirImgRes) : null;
    }

    private final String extension;
    private final String typeName;
    private final String imageFileName;

    public static FileType of(String extension) {
        Map<String, FileType> types = getRegisteredFileTypes();
        if (types.containsKey(extension)) {
            return types.get(extension);
        } else {
            return new FileType(extension, extension, null);
        }
    }

    synchronized static Map<String, FileType> getRegisteredFileTypes() {
        if (fileTypes == null) {
            fileTypes = load();
        }
        return fileTypes;
    }

    private static Map<String, FileType> load() {
        long l = System.currentTimeMillis();
        InputStream resource = Main.class.getResourceAsStream("file_types.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
        Map<String, FileType> fileTypesMap = reader.lines()
                .skip(1)
                .map(s -> s.split(";"))
                .collect(Collectors.toMap(s -> s[0], s -> new FileType(s[0], s[1], s[2])));
        System.out.printf("load:\t\t%d%n", System.currentTimeMillis() - l);
        return fileTypesMap;
    }

    public Image getImage() {
        if (typeImageMap.containsKey(extension)) {
            return typeImageMap.get(extension);
        } else {
            if (imageFileName != null) {
                InputStream resource = Main.class.getResourceAsStream(IMAGES_FILE_TYPES_DIR + imageFileName);
                Image image = (resource != null) ? new Image(resource) : DEFAULT_FILE_IMAGE;
                typeImageMap.put(extension, image);
                return image;
            } else {
                return DEFAULT_FILE_IMAGE;
            }
        }
    }

}
