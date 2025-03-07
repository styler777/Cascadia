package src.main.java.cascadia.core;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.score.UniqueCard;
import src.main.java.cascadia.utils.Coordinate;
import src.main.java.cascadia.utils.Message;
import src.main.java.cascadia.utils.Mode;

/**
 * La classe Player représente un joueur dans le jeu. Elle gère les informations relatives à l'identité du joueur, 
 * ainsi que ses actions dans l'écosystème, y compris l'ajout d'animaux et de tuiles.
 */
public class Player {
    private final int id;  // Identifiant unique du joueur
    private final HashMap<Coordinate, Tile> tileOwners;  // Cartographie des coordonnées aux tuiles que le joueur possède
    private final UniqueCard card;  // Carte unique du joueur
    private final Mode mode;  // Mode de jeu du joueur
    private Message message;  // Message affiché pour le joueur

    /**
     * Constructeur de la classe Player.
     * Initialise un joueur avec un identifiant, une carte unique et un mode de jeu.
     * 
     * @param id L'identifiant du joueur
     * @param card La carte unique du joueur
     * @param mode Le mode de jeu du joueur
     */
    public Player(int id, UniqueCard card, Mode mode) {
        Objects.requireNonNull(card, "card cannot be null");
        Objects.requireNonNull(mode, "mode cannot be null");
        this.id = id;
        this.tileOwners = new HashMap<>();
        this.card = card;
        this.mode = mode;
        this.message = Message.START;
    }

    /**
     * Calcule le score d'un BioUnit (animal ou habitat) pour ce joueur.
     * 
     * @param bioUnit Le BioUnit dont le score doit être calculé
     * @return Le score associé à ce BioUnit
     */
    public int playerScore(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit, "bioUnit cannot be null");
        return card.getEntityScore(bioUnit);
    }

    /**
     * Calcule le score total de tous les animaux que possède le joueur.
     * 
     * @return Le score total des animaux du joueur
     */
    public int playerAnimalScore() {
        return card.totalScoreAnimal();
    }

    /**
     * Calcule le score total de tous les habitats du joueur.
     * 
     * @return Le score total des habitats du joueur
     */
    public int playerHabitatScore() {
        return card.totalScoreHabitat();
    }

    /**
     * Calcule le score total du joueur en combinant les scores des animaux et des habitats.
     * 
     * @return Le score total du joueur
     */
    public int playerScoreTotal() {
        return card.totalScore();
    }

    /**
     * Met à jour le score d'un BioUnit spécifique en fonction du bonus donné.
     * 
     * @param bioUnit Le BioUnit dont le score doit être mis à jour
     * @param bonus Le bonus à ajouter au score du BioUnit
     */
    public void updateScore(BioUnit bioUnit, int bonus) {
        Objects.requireNonNull(bioUnit, "bioUnit cannot be null");
        card.updateEntityScore(bioUnit, bonus);
    }

    /**
     * Met à jour les scores du joueur en fonction des BioUnits dans l'écosystème et des tuiles possédées.
     * 
     * @param ecosystem L'écosystème dans lequel les tuiles sont situées
     */
    public void playerAddBioUnitScore(EcosystemPlayerManager ecosystem) {
        Objects.requireNonNull(ecosystem, "ecosystem cannot be null");
        tileOwners.forEach((coord, tile) -> {
            var coordinates = new ArrayList<>(coord.getCoordinatesToNeighbor(ecosystem, tile.animalInTile()));
            coordinates.add(coord);
            card.addScoreBioUnit(tile.animalInTile(), coordinates);
            playerScoreHabitat(ecosystem, tile, coordinates);
        });
    }

    /**
     * Met à jour le score des habitats associés à une tuile et aux coordonnées voisines.
     * 
     * @param ecosystem L'écosystème contenant les tuiles
     * @param tile La tuile dont le score doit être mis à jour
     * @param coordinates Les coordonnées des tuiles voisines
     */
    private void playerScoreHabitat(EcosystemPlayerManager ecosystem, Tile tile, List<Coordinate> coordinates) {
        tile.habitats().forEach(habitat -> card.addScoreBioUnit(habitat, coordinates));
    }

    /**
     * Définit la condition pour insérer une tuile dans l'écosystème en fonction de si la tuile a des voisins 
     * ou si l'ajout de la tuile est manuel.
     * 
     * @return Le BiPredicate définissant la condition d'insertion d'une tuile
     */
    private BiPredicate<Boolean, Boolean> conditionInsertTileToEcosystem() {
        return (hasNeighbors, isManualTileAddition) -> isManualTileAddition || hasNeighbors;
    }

    /**
     * Ajoute une tuile à l'écosystème si la condition est remplie.
     * 
     * @param ecosystem L'écosystème où la tuile doit être ajoutée
     * @param bioUnit La tuile à ajouter
     * @param coord Les coordonnées de la tuile
     * @param isManualTileAddition Si l'ajout de la tuile est manuel
     * @return true si la tuile a été ajoutée, false sinon
     */
    private boolean addTile(EcosystemPlayerManager ecosystem, BioUnit bioUnit, Coordinate coord, boolean isManualTileAddition) {
        BiPredicate<Boolean, Boolean> addTileIfPossible = conditionInsertTileToEcosystem();
        if (addTileIfPossible.test(!coord.getCoordinatesToNeighbor(ecosystem, bioUnit).isEmpty(), isManualTileAddition)) {
            var tile = tileOwners.computeIfAbsent(coord, _ -> (Tile) bioUnit);
            ecosystem.addTileToEcosystem(coord, Optional.ofNullable(tile));
            return true;
        }
        return false;
    }

    /**
     * Ajoute un animal à une tuile dans l'écosystème si l'animal est autorisé à être ajouté.
     * 
     * @param ecosystem L'écosystème où l'animal doit être ajouté
     * @param bioUnit L'animal à ajouter
     * @param coord Les coordonnées de la tuile où l'animal doit être ajouté
     * @return true si l'animal a été ajouté, false sinon
     */
    private boolean addAnimal(EcosystemPlayerManager ecosystem , BioUnit bioUnit , Coordinate coord) {
        Optional<Tile> optionalTile = Optional.ofNullable(tileOwners.getOrDefault(coord, null));
        
        if(optionalTile.isPresent() && optionalTile.get().animalbelongToAllowedAnimal((Animal )bioUnit)) {
            Tile newTile = optionalTile.get().addAnimalToTile((Animal) bioUnit); 
            tileOwners.merge(coord, newTile, (_ , _)-> newTile);
            ecosystem.addTileToEcosystem(coord, Optional.of(newTile));
            return true;
        }
        return false;
    }

    /**
     * Ajoute une tuile ou un animal à la carte du joueur en fonction du type de BioUnit (Animal ou Tile).
     * 
     * @param ecosystem L'écosystème où l'élément doit être ajouté
     * @param bioUnit L'élément à ajouter (Animal ou Tile)
     * @param coord Les coordonnées où l'élément doit être ajouté
     * @param isManualTileAddition Si l'ajout de la tuile est manuel
     * @return true si l'élément a été ajouté, false sinon
     */
    public boolean addTileInMapTiles(EcosystemPlayerManager ecosystem, BioUnit bioUnit, Coordinate coord, boolean isManualTileAddition) {
        Objects.requireNonNull(bioUnit, "tile is null");
        Objects.requireNonNull(coord, "coord is null");
        Objects.requireNonNull(ecosystem, "tile is null");
        
        return switch (bioUnit) {
            case Tile _ -> addTile(ecosystem, bioUnit, coord, isManualTileAddition);
            case Animal _ -> addAnimal(ecosystem, bioUnit, coord);
            default -> throw new IllegalArgumentException("Unexpected value: " + bioUnit);
        };
    }

    /**
     * Récupère toutes les tuiles que possède le joueur.
     * 
     * @return Une liste des tuiles que le joueur possède
     */
    public List<Tile> getTiles() {
        return tileOwners.values().stream().toList();
    }

    /**
     * Récupère tous les habitats associés aux tuiles que possède le joueur.
     * 
     * @return Une liste des habitats associés aux tuiles du joueur
     */
    public List<Habitat> getBioUnits() {
        return tileOwners.values().stream().flatMap(tile -> tile.habitats().stream()).toList();
    }

    /**
     * Vérifie si l'identifiant du joueur correspond à l'index donné.
     * 
     * @param index L'index à comparer à l'identifiant du joueur
     * @return true si l'identifiant correspond, false sinon
     */
    public boolean isIdentifier(int index) {
        return id == index;
    }

    /**
     * Change le message affiché au joueur.
     * 
     * @param message Le message à afficher au joueur
     */
    public void changeMessage(Message message) {
        Objects.requireNonNull(message);
        this.message = message;
    }

    /**
     * Compare deux joueurs pour vérifier s'ils sont égaux.
     * 
     * @param o L'objet à comparer
     * @return true si les deux joueurs sont égaux, false sinon
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Player player && id == player.id && tileOwners.equals(player.tileOwners);
    }

    /**
     * Calcule le code de hachage pour ce joueur.
     * 
     * @return Le code de hachage du joueur
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Renvoie une représentation sous forme de chaîne de caractères de l'état actuel du joueur.
     * 
     * @return Une chaîne représentant l'état du joueur
     */
    @Override
    public String toString() {
        if(mode.equals(Mode.GRAPHIC))
            return "Player : " + id;

        return switch (message) {
            case START -> tileOwners.entrySet().stream().map(entry -> entry.getKey() + "-> " + entry.getValue())
                .collect(Collectors.joining("\n", "Player " + id + "\n--------------\n", "\n--------------\n"));
            case END -> "\nPlayer :\n" + card.toString() + "\n";
            case COORDINATE, NOTHING -> "";
        };
    }
}
