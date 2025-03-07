package src.main.java.cascadia.score;

import java.util.*;
import java.util.stream.Collectors;

import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.utils.Coordinate;

/**
 * Classe représentant le score d'un animal dans le jeu.
 * Cette classe gère les voisins de l'animal et calcule son score basé sur la proximité d'autres entités.
 */
public final class AnimalScore implements ScoreBioUnit {
    private final Animal animal;
    private final HashMap<Coordinate, Boolean> neighbors;

    /**
     * Constructeur de la classe AnimalScore.
     * 
     * @param animal L'animal associé à ce score.
     * @throws NullPointerException si l'animal est nul.
     */
    public AnimalScore(Animal animal) {
        Objects.requireNonNull(animal);
        this.neighbors = new HashMap<>();
        this.animal = animal;
    }

    /**
     * Ajoute tous les voisins de l'animal à la liste des voisins.
     * 
     * @param coordinates Liste des coordonnées des voisins.
     * @param bioUnit L'unité biologique avec laquelle les voisins sont comparés.
     * @throws NullPointerException si les coordonnées ou l'unité biologique sont nulles.
     */
    @Override
    public void addAllNeighbor(List<Coordinate> coordinates, BioUnit bioUnit) {
        Objects.requireNonNull(coordinates);
        Objects.requireNonNull(bioUnit);
        if (animal.equals(bioUnit))
            coordinates.stream().forEach(coord -> neighbors.computeIfAbsent(coord, _ -> true));
    }

    /**
     * Vérifie si une coordonnée donnée est un voisin de l'animal.
     * 
     * @param coordinate La coordonnée à vérifier.
     * @return true si la coordonnée est un voisin, false sinon.
     * @throws NullPointerException si la coordonnée est nulle.
     */
    public boolean isNeighbor(Coordinate coordinate) {
        Objects.requireNonNull(coordinate);
        return neighbors.getOrDefault(coordinate, false);
    }

    /**
     * Récupère la liste de toutes les coordonnées des voisins de l'animal.
     * 
     * @return Une liste des coordonnées des voisins.
     */
    public List<Coordinate> getNeighbors() {
        return neighbors.keySet().stream().toList();
    }

    /**
     * Vérifie si au moins une des coordonnées données est un voisin de l'animal.
     * 
     * @param coordinates Liste des coordonnées à vérifier.
     * @return true si au moins une coordonnée est un voisin, false sinon.
     * @throws NullPointerException si la liste des coordonnées est nulle.
     */
    @Override
    public boolean checkCoordinate(List<Coordinate> coordinates) {
        Objects.requireNonNull(coordinates);
        return coordinates.stream().anyMatch(coord -> neighbors.getOrDefault(coord, false));
    }

    /**
     * Compte le nombre de voisins de l'animal.
     * 
     * @return Le nombre de voisins.
     */
    @Override
    public int countNeighbors() {
        return neighbors.keySet().size();
    }

    /**
     * Vérifie l'égalité entre deux objets AnimalScore.
     * 
     * @param o L'objet à comparer.
     * @return true si les objets sont égaux, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof AnimalScore score && animal.equals(score.animal)
                && neighbors.equals(score.neighbors);
    }

    /**
     * Calcule le code de hachage pour l'AnimalScore.
     * 
     * @return Le code de hachage de l'objet.
     */
    @Override
    public int hashCode() {
        return Objects.hash(animal, neighbors);
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du score de l'animal.
     * 
     * @return Une chaîne de caractères représentant le score de l'animal et ses voisins.
     */
    @Override
    public String toString() {
        return neighbors.keySet().stream()
                .map(Coordinate::toString)
                .collect(Collectors.joining(animal.toString(), ",", "\n"));
    }
}
