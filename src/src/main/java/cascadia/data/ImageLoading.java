package src.main.java.cascadia.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.utils.ImageMapping;

public class ImageLoading {
    
    private final BufferedImage[] picturesAnimal;
    private final BufferedImage[] picturesTile;
    private final BufferedImage[] pictureCard;
    private final static int LENGHT = 120;
    private final HashMap<Animal , ImageMapping> imagesAnimal; 
    private final HashMap<Tile, ImageMapping> imagesTile;
    
    private final static Map<Card, Path> IMAGE_CARD = Map.of(
            Card.FAMILY , Path.of("Images/card/Famille.jpg"),
            Card.INTERMEDIATE , Path.of("Images/card/Intermediaire.jpg")
    );
    private final static Path scorePath = Path.of("Images/score/score.png");
    private int indexImageTile;
    private int indexAnimal;
    
    /**
     * Constructor to initialize image arrays and maps for animals, tiles, and cards.
     */
    public ImageLoading() {
        this.picturesAnimal = new BufferedImage[LENGHT];
        this.picturesTile = new BufferedImage[LENGHT];
        this.pictureCard = new BufferedImage[LENGHT];
        this.imagesAnimal = new HashMap<>();
        this.imagesTile = new HashMap<>();
        this.indexImageTile = 0;
        this.indexAnimal = 0;
    }

    /**
     * Loads images for animals into the picturesAnimal array.
     */
    public void loadImageAnimal() {
        imagesAnimal.keySet().forEach(animal -> { 
            setImage(imagesAnimal.getOrDefault(animal, null).numberImage(), 
                     imagesAnimal.getOrDefault(animal, null).path(), 
                     picturesAnimal);
        });  
    }
    
    /**
     * Returns the image for the Family card.
     * @return BufferedImage for the Family card.
     */
    private BufferedImage imageFamily() {
        setImage(0, IMAGE_CARD.getOrDefault(Card.FAMILY, Path.of("")) , pictureCard);
        return pictureCard[0];
    }
    
    /**
     * Returns the image for the Intermediate card.
     * @return BufferedImage for the Intermediate card.
     */
    private BufferedImage imageIntermediate() {
        setImage(1, IMAGE_CARD.getOrDefault(Card.INTERMEDIATE, Path.of("")) , pictureCard);
        return pictureCard[1];
    }
    
    /**
     * Returns the image for the score.
     * @return BufferedImage for the score.
     */
    public BufferedImage imageScore() {
        setImage(2, scorePath , pictureCard);
        return pictureCard[2];
    }

    /**
     * Returns the appropriate image for a given card.
     * @param card The card type (Family, Intermediate, etc.)
     * @return BufferedImage for the specified card.
     */
    public BufferedImage imageCard(Card Card) {
        Objects.requireNonNull(Card);
        return switch (Card) {
            case FAMILY -> imageFamily();
            case INTERMEDIATE -> imageIntermediate();
            default -> throw new IllegalArgumentException("Unexpected value: " + Card);
        };
    }
    
    /**
     * Loads images for tiles into the picturesTile array.
     */
    public void loadImageTile() {
        imagesTile.keySet().forEach(tile -> { 
            setImage(imagesTile.getOrDefault(tile, null).numberImage(), 
                     imagesTile.getOrDefault(tile, null).path(), 
                     picturesTile);
        });    
    }

    /**
     * Returns a list of BufferedImages corresponding to the given animals.
     * @param animals List of Animal objects.
     * @return List of BufferedImage objects for each animal.
     */
    public List<BufferedImage> getListAnimalImage(List<Animal> animals) {
        Objects.requireNonNull(animals);
        animals.forEach(animal -> addMapImageAnimal(animal));
        loadImageAnimal();
        return animals.stream()
                      .map(animal -> picturesAnimal[imagesAnimal.getOrDefault(animal, null).numberImage()])
                      .toList();
    }
    
    /**
     * Returns a list of BufferedImages corresponding to the given tiles.
     * @param tiles List of Tile objects.
     * @return List of BufferedImage objects for each tile.
     */
    public List<BufferedImage> getListImage(List<Tile> tiles) {
        Objects.requireNonNull(tiles);
        tiles.forEach(tile -> addMapImageTile(tile));
        loadImageTile();
        return tiles.stream()
                    .map(tile -> picturesTile[imagesTile.getOrDefault(tile, null).numberImage()])
                    .toList();
    }

    /**
     * Returns a BufferedImage for a specific tile.
     * @param tile The tile to retrieve the image for.
     * @return BufferedImage for the tile.
     */
    public BufferedImage getBufferedImageTile(Tile tile) {
        Objects.requireNonNull(tile);
        return picturesTile[imagesTile.getOrDefault(tile, null).numberImage()];
    }

    /**
     * Returns a BufferedImage for a specific animal.
     * @param animal The animal to retrieve the image for.
     * @return BufferedImage for the animal.
     */
    public BufferedImage getBufferedImageAnimal(Animal animal) {
        Objects.requireNonNull(animal);
        return picturesAnimal[imagesAnimal.getOrDefault(animal, null).numberImage()];
    }

    /**
     * Loads an image into the specified position of the pictures array.
     * @param position Position in the image array to load the image into.
     * @param path Path to the image file.
     * @param pictures The array of images to load the image into.
     */
    private void setImage(int position, Path path, BufferedImage[] pictures) {
        Objects.requireNonNull(path);
        try (var input = Files.newInputStream(path)) {
            pictures[position] = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Image file not found: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the list of image files from a given directory.
     * @param directory Path to the directory to retrieve images from.
     * @return List of paths to the image files in the directory.
     */
    public List<Path> retrieveFileImage(Path directory) {
        Objects.requireNonNull(directory);
        var imagesName = new ArrayList<Path>();
        
        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(file -> imagesName.add(Path.of(directory.toString(), file.getFileName().toString())));
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }
        
        return List.copyOf(imagesName);
    }

    /**
     * Maps and loads images for animals based on file names.
     * @param animal The animal to add to the image map.
     */
    private void addMapImageAnimal(Animal animal) {
        retrieveFileImage(Paths.get("Images/animals")).forEach(path -> {
            if (animal.isRecognizeInNameImage().test(animal.getName(), path.toString())) {
                imagesAnimal.computeIfAbsent(animal, _ -> new ImageMapping(path, indexAnimal++));
            }
        });
    }

    /**
     * Maps and loads images for tiles based on file names.
     * @param tile The tile to add to the image map.
     */
    private void addMapImageTile(Tile tile) {
        Objects.requireNonNull(tile, "The tiles list cannot be null.");
        retrieveFileImage(Paths.get("Images/Tiles")).forEach(path -> {
            if (tile.nameImageCorresponding(path.toString())) {
                imagesTile.computeIfAbsent(tile, _ -> new ImageMapping(path, indexImageTile++));
            }
        });
    }

    /**
     * Returns a string representation of the picturesTile array.
     * @return String representation of the picturesTile array.
     */
    @Override
    public String toString() {
        return "" + Arrays.toString(picturesTile);
    }
}
