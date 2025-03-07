package src.main.java.cascadia.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.utils.Coordinate;


/**
 * Gestionnaire de l'écosystème du joueur. Cette classe permet de gérer l'état de l'écosystème,
 * incluant l'ajout et la récupération de tuiles dans un tableau représentant l'écosystème.
 */
public class EcosystemPlayerManager {
    
    private final Tile[][] ecosystem;
    
    /**
     * Constructeur de la classe EcosystemPlayerManager.
     *
     * @param width Largeur de l'écosystème (nombre de colonnes).
     * @param height Hauteur de l'écosystème (nombre de lignes).
     * @throws IllegalArgumentException si width ou height sont inférieurs à 0.
     */
    public EcosystemPlayerManager(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width and height must be non-negative");
        }
        this.ecosystem = new Tile[width][height];
    }
    
    /**
     * Récupère la tuile située à la position donnée dans l'écosystème.
     *
     * @param coordinate Les coordonnées de la tuile à récupérer.
     * @return Un {@link Optional} contenant la tuile si elle existe, ou un {@link Optional#empty()} si la tuile n'existe pas.
     * @throws NullPointerException si les coordonnées sont nulles.
     */
    public Optional<Tile> getTileFromEcosystem(Coordinate coordinate) {
        Objects.requireNonNull(coordinate, "Coordinate cannot be null");
        
        Function<Coordinate, Optional<Tile>> getTile = coord -> 
            coordinate.validCoordinate(playerBoardWidth(), playerBoardHeight()) ?
                Optional.ofNullable(ecosystem[coord.x()][coord.y()]) : Optional.empty();
        
        return getTile.apply(coordinate);
    }
    
    /**
     * Ajoute une tuile à l'écosystème à la position spécifiée.
     *
     * @param coordinate Les coordonnées où la tuile doit être ajoutée.
     * @param tile Une {@link Optional} contenant la tuile à ajouter. Si elle est vide, la position est nettoyée.
     * @throws NullPointerException si les coordonnées ou la tuile sont nulles.
     */
    public void addTileToEcosystem(Coordinate coordinate, Optional<Tile> tile) {
        Objects.requireNonNull(coordinate, "Coordinate cannot be null");
        Objects.requireNonNull(tile, "Tile cannot be null");
        
        Consumer<Optional<Tile>> addTile = tileOp -> 
            ecosystem[coordinate.x()][coordinate.y()] = tileOp.orElse(null);
        
        addTile.accept(tile);
    }
    
    /**
     * Récupère une liste de toutes les tuiles présentes dans l'écosystème.
     *
     * @return Une liste contenant toutes les tuiles présentes dans l'écosystème.
     */
    public List<Tile> getListTile() {
        var list = new ArrayList<Tile>();
        for (int i = 0; i < ecosystem.length; i++) {
            for (int j = 0; j < ecosystem[0].length; j++) {
                // Si la tuile est présente, on l'ajoute à la liste.
                getTileFromEcosystem(new Coordinate(i, j)).ifPresent(list::add);
            }
        }
        return List.copyOf(list);
    }
    
    /**
     * Récupère la largeur de l'écosystème (nombre de colonnes).
     *
     * @return La largeur de l'écosystème.
     */
    public int playerBoardWidth() {
        return ecosystem.length;
    }
    
    /**
     * Récupère la hauteur de l'écosystème (nombre de lignes).
     *
     * @return La hauteur de l'écosystème.
     */
    public int playerBoardHeight() {
        return ecosystem[0].length;
    }

    /**
     * Représentation sous forme de chaîne de caractères de l'écosystème,
     * affichant une grille de tuiles avec des symboles "#" pour les tuiles présentes et "" pour les tuiles vides.
     *
     * @return La chaîne de caractères représentant l'écosystème.
     */
    @Override
    public String toString() {
        Function<Optional<Tile>, String> display = op -> op.map(_ -> "#").orElse("");
        
        return IntStream.range(0, playerBoardWidth())
            .mapToObj(i -> IntStream.range(0, playerBoardHeight())
                .mapToObj(j -> display.apply(Optional.ofNullable(ecosystem[i][j])))
                .collect(Collectors.joining(",", "[", "]")))
            .collect(Collectors.joining("\n", "\nEcosystem:\n\n-------------------\n", "\n"));
    }
}
