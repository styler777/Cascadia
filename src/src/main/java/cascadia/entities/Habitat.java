package src.main.java.cascadia.entities;

import java.util.function.BiPredicate;


/**
 * Enum representing different types of habitats in the game.
 * Each habitat is associated with a name that is used to identify it.
 */
public enum Habitat implements BioUnit {
  
    SWAMP("swamp"), RIVER("river"), FOREST("forest"), DESERT("desert"), MOUNTAIN("mountain");

    private final String hab;

    /**
     * Constructor for the Habitat enum.
     * 
     * @param name The name associated with the habitat.
     */
    private Habitat(String name) {
        this.hab = name;
    }

    /**
     * Returns the name of the habitat.
     * 
     * @return The name of the habitat as a string.
     */
    @Override
    public String getName() {
        return hab;
    }

    /**
     * Recognizes if an entity's name is contained within an image name.
     * 
     * @return A BiPredicate that checks if the entity name (habitat) is contained in the image name.
     */
    @Override
    public BiPredicate<String, String> isRecognizeInNameImage() {
        return (entityName, nameImage) -> nameImage.contains(entityName);
    }
}
