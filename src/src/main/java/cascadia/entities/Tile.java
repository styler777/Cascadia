package src.main.java.cascadia.entities;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * A record representing a Tile, which contains a list of allowed animals, habitats,
 * and the animal currently occupying the tile.
 */
public record Tile(List<Animal> allowedAnimals, List<Habitat> habitats, Animal animalInTile) implements BioUnit {

    /**
     * Constructor for the Tile record.
     * 
     * @param allowedAnimals The list of animals allowed on this tile.
     * @param habitats The list of habitats associated with this tile.
     * @param animalInTile The animal currently occupying the tile.
     * @throws NullPointerException if animalInTile is null.
     */
    public Tile {
        Objects.requireNonNull(animalInTile, "animalInTile cannot be null");
        allowedAnimals = List.copyOf(allowedAnimals);
        habitats = List.copyOf(habitats);
    }

    /**
     * Constructor for the Tile record, setting the animalInTile to Animal.NONE by default.
     * 
     * @param allowedAnimals The list of animals allowed on this tile.
     * @param habitats The list of habitats associated with this tile.
     */
    public Tile(List<Animal> allowedAnimals, List<Habitat> habitats) {
        this(allowedAnimals, habitats, Animal.NONE);
    }

    /**
     * Adds an animal to the tile, replacing the current animal if it belongs to the allowed animals.
     * 
     * @param animal The animal to add to the tile.
     * @return A new Tile with the updated animal.
     * @throws NullPointerException if the animal is null.
     */
    public Tile addAnimalToTile(Animal animal) {
        Objects.requireNonNull(animal, "animal cannot be null");
        Animal chosenAnimal = animalbelongToAllowedAnimal(animal) ? animal : animalInTile;
        return new Tile(allowedAnimals, habitats, chosenAnimal);
    }

    /**
     * Checks if the given animal is allowed on this tile.
     * 
     * @param animal The animal to check.
     * @return True if the animal is allowed on the tile, false otherwise.
     * @throws NullPointerException if the animal is null.
     */
    public boolean animalbelongToAllowedAnimal(Animal animal) {
        Objects.requireNonNull(animal);
        return allowedAnimals.contains(animal);
    }

    /**
     * Checks if the image name corresponds to the names of animals and habitats on the tile.
     * 
     * @param nameImage The name of the image to check.
     * @return True if all animals and habitats on the tile match the image name, false otherwise.
     * @throws NullPointerException if nameImage is null.
     */
    public boolean nameImageCorresponding(String nameImage) {
        Objects.requireNonNull(nameImage, "nameImage cannot be null");
        
        // Check if all allowed animals match the nameImage
        boolean animalsMatch = allowedAnimals.stream().allMatch(animal -> isRecognizeInNameImage().test(animal.getName(), nameImage));
        
        // Check if all habitats match the nameImage
        boolean habitatsMatch = habitats.stream().allMatch(habitat -> isRecognizeInNameImage().test(habitat.getName(), nameImage));
        
        return animalsMatch && habitatsMatch;
    }

    /**
     * Recognizes if a name is part of an image name.
     * 
     * @return A BiPredicate that checks if an entity name is contained within an image name.
     */
    @Override
    public BiPredicate<String, String> isRecognizeInNameImage() {
        return (entityName, nameImage) -> nameImage.contains(entityName);
    }

    /**
     * Returns a string representation of the Tile.
     * 
     * @return A string representation of the Tile, including allowed animals, habitats, and the animal in the tile.
     */
    @Override
    public String toString() {
        String hasAnimal = animalInTile.equals(Animal.NONE) ? "" : " Animal In Tile: " + animalInTile;
        return "Tile: (" + "Allowed Animals: " + allowedAnimals + " Habitats: " + habitats + hasAnimal + ")";
    }

    /**
     * Returns the name of the entity.
     * 
     * @return The name of the entity ("Tile").
     */
    @Override
    public String getName() {
        return "Tile";
    }
}
