package src.main.java.cascadia.entities;


import java.util.function.BiPredicate;


/**
 * Enum representing different types of animals in the game.
 * Each animal is associated with a name that is used to identify it.
 */
public enum Animal implements BioUnit {
  
    BEAR("bear"), FOX("fox"), BUZZARD("buzzard"), SALMON("salmon"), ELK("elk"), NONE("");

    private final String animal;

    /**
     * Constructor for the Animal enum.
     * 
     * @param name The name associated with the animal.
     */
    private Animal(String name) {
        this.animal = name;
    }

    /**
     * Returns the name of the animal.
     * 
     * @return The name of the animal as a string.
     */
    @Override
    public String getName() {
        return animal;
    }

    /**
     * Recognizes if an entity's name is contained within an image name.
     * 
     * @return A BiPredicate that checks if the entity name (animal) is contained in the image name.
     */
    @Override
    public BiPredicate<String, String> isRecognizeInNameImage() {
        return (entityName, nameImage) -> nameImage.contains(entityName);
    }
}


