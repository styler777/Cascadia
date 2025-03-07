package src.main.java.cascadia.score;

import java.util.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.utils.Coordinate;

/**
 * Classe représentant le score d'un habitat. Cette classe gère les voisins d'un habitat
 * et permet de calculer le nombre de voisins, ainsi que de vérifier les coordonnées des voisins.
 */
public final class HabitatScore implements ScoreBioUnit {

    private final Habitat habitat;
    private final HashMap<Coordinate, Boolean> neighbors;

    /**
     * Constructeur de la classe HabitatScore.
     * 
     * @param habitat L'habitat associé à ce score.
     * @throws NullPointerException si l'habitat est null.
     */
    public HabitatScore(Habitat habitat) {
        Objects.requireNonNull(habitat);
        this.neighbors = new HashMap<>();
        this.habitat = habitat;
    }

    /**
     * Ajoute tous les voisins d'un habitat à la liste des voisins.
     * 
     * @param coordinates Liste des coordonnées des voisins.
     * @param bioUnit L'unité biologique à vérifier pour l'ajout.
     * @throws NullPointerException si les coordonnées ou l'unité biologique sont nulles.
     */
    @Override
    public void addAllNeighbor(List<Coordinate> coordinates, BioUnit bioUnit) {
        Objects.requireNonNull(coordinates);
        Objects.requireNonNull(bioUnit);
        if (habitat.equals(bioUnit)) {
            coordinates.forEach(coord -> neighbors.computeIfAbsent(coord, _ -> true));
        }
    }

    /**
     * Vérifie si une coordonnée est un voisin de l'habitat.
     * 
     * @param coordinate La coordonnée à vérifier.
     * @return true si la coordonnée est un voisin, sinon false.
     * @throws NullPointerException si la coordonnée est null.
     */
    @Override
    public boolean isNeighbor(Coordinate coordinate) {
        Objects.requireNonNull(coordinate);
        return neighbors.getOrDefault(coordinate, false);
    }

    /**
     * Compte le nombre de voisins de l'habitat.
     * 
     * @return Le nombre de voisins.
     */
    @Override
    public int countNeighbors() {
        return neighbors.keySet().size();
    }

    /**
     * Vérifie si l'une des coordonnées spécifiées est un voisin.
     * 
     * @param coordinates Liste des coordonnées à vérifier.
     * @return true si au moins une coordonnée est un voisin, sinon false.
     * @throws NullPointerException si la liste de coordonnées est null.
     */
    @Override
    public boolean checkCoordinate(List<Coordinate> coordinates) {
        Objects.requireNonNull(coordinates);
        return coordinates.stream().anyMatch(coord -> neighbors.getOrDefault(coord, false));
    }

    /**
     * Vérifie l'égalité entre deux objets HabitatScore.
     * 
     * @param o L'objet à comparer avec cet HabitatScore.
     * @return true si les deux objets sont égaux, sinon false.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof HabitatScore score && habitat.equals(score.habitat)
                && neighbors.equals(score.neighbors);
    }

    /**
     * Calcule le code de hachage de l'objet HabitatScore.
     * 
     * @return Le code de hachage de cet objet.
     */
    @Override
    public int hashCode() {
        return Objects.hash(habitat, neighbors);
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères des voisins de l'habitat.
     * 
     * @return La chaîne représentant l'habitat et ses voisins.
     */
    @Override
    public String toString() {
        return neighbors.keySet().stream()
                .map(Coordinate::toString)
                .collect(Collectors.joining(habitat.toString(), ",", "\n"));
    }
}
