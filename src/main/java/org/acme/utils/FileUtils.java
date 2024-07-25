package org.acme.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private FileUtils() {
        throw new IllegalStateException("Utils class shouldn't be instantiated");
    }

    public static List<Path> getFilesFromDir(Path dirPath, String extension) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*." + extension)) {
            var pathList = new ArrayList<Path>();
            stream.forEach(pathList::add);
            return pathList;
        } catch (IOException | DirectoryIteratorException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static Optional<String> readFile(Path filePath) {
        try {
            return Optional.of(new String(Files.readAllBytes(filePath)));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static void writeFile(Path filePath, String content) {
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void append(Path filePath, String content) {
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                logger.info("Created new file: " + filePath);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        try (FileWriter fileWriter = new FileWriter(filePath.toString(), true)) {
            fileWriter.write(content);
            logger.info("Successfully appended to the file.");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
