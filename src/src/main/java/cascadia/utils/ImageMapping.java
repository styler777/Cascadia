package src.main.java.cascadia.utils;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a mapping between an image file (given by its path) and a number that represents the image.
 * The image can be used in various parts of the application (like UI or game logic) based on the provided 
 * number that maps to a specific image.
 * 
 * This class is immutable and its fields are set via the constructor.
 */
public record ImageMapping(Path path, int numberImage) {

    /**
     * Constructor for the ImageMapping record. Ensures that the provided path is not null.
     * 
     * @param path The path to the image file.
     * @param numberImage The number representing the image.
     * @throws NullPointerException If the path is null.
     */
    public ImageMapping {
        Objects.requireNonNull(path, "Path cannot be null");
    }
}