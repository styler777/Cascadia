package src.main.java.cascadia.core;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import src.main.java.cascadia.data.ImageLoading;
import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.entities.Tile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

/**
 * La classe Pick gère les différents éléments du jeu qui sont tirés au hasard pendant la partie,
 * y compris les animaux et les tuiles. Elle assure la gestion des piles de tirage et de la logique
 * d'ajout et de retrait des éléments pendant le déroulement du jeu.
 */
public class Pick {

    private final LinkedList<Animal> animalDrawPile;
    private final LinkedList<Tile> tileDrawPile;
    private final LinkedList<Tile> pickTileGame;
    private final LinkedList<Animal> pickAnimalsGame;
    private final ImageLoading image;
    private final static int NUMBER_OF_TOKENS = 20;
    private final static int NUMBER_OF_TILE_TO_PICK = 4;
    private static final List<Animal> LIST_INIT_ANIMAL = List.of(Animal.BEAR, Animal.BUZZARD, Animal.ELK, Animal.FOX, Animal.SALMON);
    private static final List<Habitat> LIST_INIT_HABITAT = List.of(Habitat.DESERT, Habitat.FOREST, Habitat.MOUNTAIN, Habitat.RIVER, Habitat.SWAMP);
    private final HashMap<Animal, Integer> overcrowding;

    /**
     * Constructeur de la classe Pick.
     * Initialise les différentes piles et l'image.
     */
    public Pick() {
        this.animalDrawPile = new LinkedList<>();
        this.tileDrawPile = new LinkedList<>();
        this.pickAnimalsGame = new LinkedList<>();
        this.pickTileGame = new LinkedList<>();
        this.overcrowding = new HashMap<>();
        this.image = new ImageLoading();
    }

    /**
     * Initialise la pile d'animaux à partir des animaux de départ.
     * Chaque animal est ajouté un nombre de fois défini par NUMBER_OF_TOKENS.
     */
    public void initializeAnimalDrawPile() {
        LIST_INIT_ANIMAL.forEach(animal -> 
            animalDrawPile.addAll(Collections.nCopies(NUMBER_OF_TOKENS, animal))
        );
        Collections.shuffle(animalDrawPile);
    }

    /**
     * Renvoie un BiPredicate permettant de vérifier si un nom d'entité figure dans le nom de l'image.
     *
     * @return Le BiPredicate qui effectue la comparaison.
     */
    public BiPredicate<String, String> isRecognizeInNameImage() {
        return (entityName, nameImage) -> nameImage.contains(entityName);
    }

    /**
     * Initialise la liste des animaux autorisés en fonction des images dans le chemin spécifié.
     *
     * @param path Le chemin vers le dossier d'images.
     * @return La liste des animaux autorisés.
     */
    private List<Animal> initializeAllowedAnimal(Path path) {
        return LIST_INIT_ANIMAL.stream()
            .filter(animal -> isRecognizeInNameImage().test(animal.getName(), path.toString()))
            .collect(Collectors.toList());
    }

    /**
     * Initialise la liste des habitats autorisés en fonction des images dans le chemin spécifié.
     *
     * @param path Le chemin vers le dossier d'images.
     * @return La liste des habitats autorisés.
     */
    private List<Habitat> initializeDoubleHabitat(Path path) {
        return LIST_INIT_HABITAT.stream()
            .filter(habitat -> isRecognizeInNameImage().test(habitat.getName(), path.toString()))
            .collect(Collectors.toList());
    }

    /**
     * Crée une représentation des indices des animaux dans la liste d'animaux de départ.
     *
     * @return Une liste des indices des animaux dans LIST_INIT_ANIMAL.
     */
    public List<Integer> createImageAnimal() {
        return pickAnimalsGame.stream()
            .map(animal -> LIST_INIT_ANIMAL.indexOf(animal))
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retire un animal de la pile de tirage des animaux.
     *
     * @return Un Optional contenant l'animal retiré, ou Optional.empty() si la pile est vide.
     */
    private Optional<Animal> removeTileToPickAnimals() {
        return animalDrawPile.isEmpty() ? Optional.empty() : Optional.of(animalDrawPile.removeFirst());
    }

    /**
     * Ajoute un animal à la pile des animaux en jeu.
     * Si la pile d'animaux à tirer est vide, l'ajout est ignoré.
     */
    private void addTileToPickAnimal() {
        Optional<Animal> animal = removeTileToPickAnimals();
        animal.ifPresent(a -> {
            overcrowding.merge(a, 1, Integer::sum);
            pickAnimalsGame.add(a);
        });
    }

    /**
     * Crée un certain nombre d'animaux pour le jeu en fonction du nombre d'animaux à créer.
     *
     * @param animalNumber Le nombre d'animaux à créer.
     */
    private void createPickAnimal(int animalNumber) {
        IntStream.range(0, animalNumber).forEach(_ -> addTileToPickAnimal());
    }

    /**
     * Retire un animal spécifique de la pile d'animaux en jeu à un index donné.
     *
     * @param index L'index de l'animal à retirer.
     * @return L'animal retiré de la pile.
     * @throws IllegalArgumentException Si l'index est invalide.
     */
    public Optional<Animal> removePickAnimalGame(int index) {
        validateIndex(index, pickAnimalsGame.size());
        return Optional.ofNullable(pickAnimalsGame.remove(index));
    }

    /**
     * Récupère l'animal à un index donné de la pile d'animaux en jeu.
     *
     * @param index L'index de l'animal à récupérer.
     * @return L'animal à l'index donné.
     * @throws IllegalArgumentException Si l'index est invalide.
     */
    public Animal getPickAnimalIndex(int index) {
        validateIndex(index, pickAnimalsGame.size());
        return pickAnimalsGame.get(index);
    }

    /**
     * Crée toutes les tuiles en récupérant les images du dossier spécifié et en les ajoutant à la pile de tuiles.
     */
    private void createAllTiles() {
        image.retrieveFileImage(Paths.get("Images/Tiles")).forEach(path -> 
            tileDrawPile.add(new Tile(initializeAllowedAnimal(path), initializeDoubleHabitat(path)))
        );
        Collections.shuffle(tileDrawPile);
    }

    /**
     * Vérifie si un animal dans la pile d'animaux est en situation de surpopulation.
     *
     * @return L'animal en situation de surpopulation, ou Animal.NONE si aucun animal n'est en surpopulation.
     */
    private Animal isOvercrowding() {
        return overcrowding.entrySet().stream()
            .filter(entry -> entry.getValue() >= 4)
            .map(Entry::getKey)
            .findFirst()
            .orElse(Animal.NONE);
    }

    /**
     * Retire une tuile de la pile de tirage des tuiles.
     *
     * @return Un Optional contenant la tuile retirée, ou Optional.empty() si la pile est vide.
     */
    private Optional<Tile> removeTileToPickTiles() {
        return tileDrawPile.isEmpty() ? Optional.empty() : Optional.of(tileDrawPile.removeFirst());
    }

    /**
     * Ajoute une tuile à la pile des tuiles en jeu.
     * Si la pile de tuiles à tirer est vide, l'ajout est ignoré.
     */
    private void addTileToPickTile() {
        removeTileToPickTiles().ifPresent(pickTileGame::add);
    }

    /**
     * Retire une tuile de la pile des tuiles en jeu à un index donné.
     *
     * @param index L'index de la tuile à retirer.
     * @return La tuile retirée de la pile.
     * @throws IllegalArgumentException Si l'index est invalide.
     */
    public Optional<Tile> removePickTileGame(int index) {
        validateIndex(index, pickTileGame.size());
        return Optional.ofNullable(pickTileGame.remove(index));
    }

    /**
     * Récupère une tuile de la pile des tuiles en jeu.
     * Après récupération, une nouvelle tuile est ajoutée à la pile.
     *
     * @return Une Optional contenant la tuile retirée, ou Optional.empty() si la pile est vide.
     */
    public Optional<Tile> getTilePickGame() {
        if (pickTileGame.isEmpty()) {
            return Optional.empty();
        }
        Optional<Tile> tile = Optional.of(pickTileGame.removeFirst());
        addTileToPickTile();
        return tile;
    }

    /**
     * Méthode utilitaire pour valider l'index dans une liste.
     *
     * @param index L'index à valider.
     * @param size La taille de la liste.
     * @throws IllegalArgumentException Si l'index est invalide.
     */
    private void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index < 0 or index >= size");
        }
    }

    /**
     * Récupère la tuile à un index donné de la pile des tuiles en jeu.
     *
     * @param index L'index de la tuile à récupérer.
     * @return La tuile à l'index donné.
     * @throws IllegalArgumentException Si l'index est invalide.
     */
    public Tile getPickTileIndex(int index) {
        validateIndex(index, pickTileGame.size());
        return pickTileGame.get(index);
    }

    /**
     * Modifie la pile des tuiles ou des animaux en fonction du BioUnit passé en argument.
     *
     * @param bioUnit Le BioUnit qui détermine l'élément à modifier (Animal ou Tile).
     */
    private void changePickTile(BioUnit bioUnit) {
        removePickTileGame(pickTileGame.indexOf(bioUnit));
        addTileToPickTile();
    }

    /**
     * Gère la surpopulation en retirant les animaux en trop et en ajoutant de nouvelles tuiles.
     */
    private void fixOvercrowding() {
        Animal animal = isOvercrowding();
        if (animal != Animal.NONE) {
            pickAnimalsGame.removeIf(a -> a.equals(animal));
            overcrowding.merge(animal, -4, Integer::sum);
            IntStream.range(0, NUMBER_OF_TILE_TO_PICK).forEach(_ -> addTileToPickAnimal());
        }
    }

    /**
     * Modifie la pile des animaux en fonction du BioUnit passé en argument.
     * Gère également la surpopulation.
     *
     * @param bioUnit Le BioUnit qui détermine l'élément à modifier (Animal).
     */
    private void changePickAnimal(BioUnit bioUnit) {
        removePickAnimalGame(pickAnimalsGame.indexOf(bioUnit));
        addTileToPickAnimal();
        fixOvercrowding();
    }

    /**
     * Modifie le jeu en fonction du BioUnit passé en argument (Animal ou Tile).
     *
     * @param bioUnit Le BioUnit à traiter.
     */
    public void changePickGame(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit);
        switch (bioUnit) {
            case Tile t -> changePickTile(t);
            case Animal a -> changePickAnimal(a);
            default -> throw new IllegalArgumentException("Unexpected value: " + bioUnit);
        }
    }

    /**
     * Initialise le jeu en créant toutes les tuiles et animaux nécessaires pour commencer.
     */
    public void initializePickGame() {
        createAllTiles();
        initializeAnimalDrawPile();
        createPickAnimal(NUMBER_OF_TILE_TO_PICK);
        IntStream.range(0, NUMBER_OF_TILE_TO_PICK).forEach(_ -> addTileToPickTile());
    }

    /**
     * Renvoie une représentation sous forme de chaîne de caractères de l'état actuel du jeu.
     *
     * @return Une chaîne décrivant l'état des tuiles et des animaux en jeu.
     */
    @Override
    public String toString() {
        return "PICK:\n-------------------\n" + 
            pickTileGame.stream().map(Tile::toString).collect(Collectors.joining("\n")) + "\n" + 
            pickAnimalsGame.stream().map(Animal::toString).collect(Collectors.joining(", ", "Animal: ", "\n------------------\n"));
    }
}
