package src.main.java.cascadia.score;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.utils.Coordinate;



/**
 * Représente une carte unique dans le jeu, qui contient des entités biologiques (comme des animaux ou des habitats) et gère les scores associés.
 */
public class UniqueCard {
    private final Map<BioUnit, List<ScoreBioUnit>> entityNeighbors;
    private final Map<BioUnit, Integer> entityScores;
    private final Card card;

    /**
     * Constructeur de la carte unique.
     * 
     * @param card La carte associée (par exemple, Family ou Intermediate).
     * @throws NullPointerException Si la carte est null.
     */
    public UniqueCard(Card card) {
        Objects.requireNonNull(card, "Card cannot be null");
        this.entityNeighbors = new HashMap<>();
        this.entityScores = new HashMap<>();
        this.card = card;
    }

    /**
     * Retourne le type de ScoreBioUnit pour une entité biologique donnée.
     * 
     * @param bioUnit L'unité biologique dont on souhaite obtenir le type de ScoreBioUnit.
     * @return Un objet ScoreBioUnit correspondant à l'unité biologique.
     * @throws IllegalArgumentException Si le type de BioUnit n'est pas supporté.
     */
    private static ScoreBioUnit getScoreBioUnitType(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");
        return switch (bioUnit) {
            case Animal a -> new AnimalScore(a);
            case Habitat h -> new HabitatScore(h);
            case Tile _ -> throw new IllegalArgumentException("Unexpected value: " + bioUnit);
        };
    }

    /**
     * Retourne le type d'unité biologique associée à un BioUnit.
     * 
     * @param bioUnit L'unité biologique dont on souhaite obtenir le type.
     * @return L'unité biologique correspondante.
     * @throws IllegalArgumentException Si le type de BioUnit n'est pas supporté.
     */
    private static BioUnit getBioUnitType(BioUnit bioUnit) {
        return switch (bioUnit) {
            case Animal a -> a;
            case Habitat h -> h;
            case Tile _ -> throw new IllegalArgumentException("Unexpected value: " + bioUnit);
        };
    }

    /**
     * Retourne le score associé à une unité biologique donnée.
     * 
     * @param bioUnit L'unité biologique pour laquelle obtenir le score.
     * @return Le score de l'unité biologique, ou 0 si l'unité biologique n'est pas trouvée.
     */
    public int getEntityScore(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");
        return entityScores.getOrDefault(getBioUnitType(bioUnit), 0);
    }

    /**
     * Ajoute un score pour une unité biologique spécifique.
     * 
     * @param bioUnit L'unité biologique à laquelle le score est ajouté.
     * @param score Le score à ajouter.
     */
    private void addEntityScore(BioUnit bioUnit, int score) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");
        entityScores.computeIfAbsent(bioUnit, _ -> score);
    }

    /**
     * Met à jour le score d'une unité biologique en ajoutant un bonus au score existant.
     * 
     * @param bioUnit L'unité biologique dont le score doit être mis à jour.
     * @param bonus Le bonus à ajouter au score actuel.
     */
    public void updateEntityScore(BioUnit bioUnit, int bonus) {
        entityScores.computeIfPresent(bioUnit, (_, currentScore) -> currentScore + bonus);
    }

    /**
     * Calcule le score total pour toutes les unités de type Animal.
     * 
     * @return Le score total pour toutes les unités de type Animal.
     */
    public int totalScoreAnimal() {
        return entityScores.entrySet().stream()
            .filter(entry -> entry.getKey() instanceof Animal)
            .mapToInt(Map.Entry::getValue)
            .sum();
    }

    /**
     * Calcule le score total pour toutes les unités de type Habitat.
     * 
     * @return Le score total pour toutes les unités de type Habitat.
     */
    public int totalScoreHabitat() {
        return entityScores.entrySet().stream()
            .filter(entry -> entry.getKey() instanceof Habitat)
            .mapToInt(Map.Entry::getValue)
            .sum();
    }

    /**
     * Calcule le score total de toutes les unités biologiques.
     * 
     * @return Le score total de toutes les unités biologiques.
     */
    public int totalScore() {
        return entityScores.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Retourne les unités ScoreBioUnit associées à une unité biologique donnée.
     * 
     * @param bioUnit L'unité biologique pour laquelle obtenir les ScoreBioUnits.
     * @return Une liste des ScoreBioUnits associées à l'unité biologique donnée.
     */
    private List<ScoreBioUnit> getScoreBioUnit(BioUnit bioUnit) {
        return entityNeighbors.getOrDefault(bioUnit, new ArrayList<>());
    }

    /**
     * Vérifie si une unité ScoreBioUnit existe pour une unité biologique donnée et une liste de coordonnées.
     * 
     * @param bioUnit L'unité biologique à vérifier.
     * @param coordinates La liste de coordonnées à vérifier.
     * @return Un objet Optional contenant le ScoreBioUnit trouvé, ou vide si aucun n'est trouvé.
     */
    private Optional<ScoreBioUnit> checkScoreBioUnit(BioUnit bioUnit, List<Coordinate> coordinates) {
        return getScoreBioUnit(bioUnit).stream()
            .filter(sB -> sB.checkCoordinate(coordinates))
            .findFirst();
    }

    /**
     * Ajoute un ScoreBioUnit à la carte, en le mettant à jour avec les nouvelles coordonnées.
     * 
     * @param optional Un ScoreBioUnit trouvé ou vide.
     * @param coordinates Les nouvelles coordonnées à ajouter.
     * @param bioUnit L'unité biologique associée au ScoreBioUnit.
     */
    private void addToMap(Optional<ScoreBioUnit> optional, List<Coordinate> coordinates, BioUnit bioUnit) {
        ScoreBioUnit newScoreBioUnit = optional.orElseThrow(() -> new IllegalArgumentException("ScoreBioUnit not found"));
        newScoreBioUnit.addAllNeighbor(coordinates, bioUnit);
        var existingList = entityNeighbors.get(bioUnit);
        if (existingList != null) {
            existingList.remove(optional.get());
        }
        existingList.add(newScoreBioUnit);

        if (!bioUnit.equals(Animal.NONE)) {
            entityNeighbors.computeIfAbsent(bioUnit, _ -> new ArrayList<>()).add(newScoreBioUnit);
            entityScores.computeIfAbsent(bioUnit, _ -> scoreBioUnit(bioUnit));
        }
    }

    /**
     * Ajoute un ScoreBioUnit à la carte pour une unité biologique donnée.
     * 
     * @param bioUnit L'unité biologique à ajouter.
     * @param scoreBioUnit Le ScoreBioUnit associé.
     */
    private void addBioUnit(BioUnit bioUnit, ScoreBioUnit scoreBioUnit) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");
        Objects.requireNonNull(scoreBioUnit, "ScoreBioUnit cannot be null");

        if (!bioUnit.equals(Animal.NONE)) {
            entityNeighbors.computeIfAbsent(bioUnit, _ -> new ArrayList<>()).add(scoreBioUnit);
            entityScores.computeIfAbsent(bioUnit, _ -> scoreBioUnit(bioUnit));
        }
    }

    /**
     * Ajoute un ScoreBioUnit à une unité biologique en fonction des coordonnées.
     * 
     * @param bioUnit L'unité biologique à ajouter.
     * @param coordinates La liste des coordonnées associées.
     */
    public void addScoreBioUnit(BioUnit bioUnit, List<Coordinate> coordinates) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");
        Objects.requireNonNull(coordinates, "Coordinates cannot be null");

        Optional<ScoreBioUnit> optional = checkScoreBioUnit(bioUnit, coordinates);
        if (optional.isEmpty()) {
            var newScoreBioUnit = getScoreBioUnitType(bioUnit);
            newScoreBioUnit.addAllNeighbor(coordinates, bioUnit);
            addBioUnit(bioUnit, newScoreBioUnit);
        } else {
            addToMap(optional, coordinates, bioUnit);
        }
    }

    /**
     * Calcule le coefficient de score pour une position sur le podium.
     * 
     * @param rank La position du joueur sur le podium.
     * @return Le coefficient associé à la position.
     */
    public int coefficient(int rank) {
        Objects.requireNonNull(card, "Card cannot be null");

        if (rank < 0) {
            throw new IllegalArgumentException("Unexpected value: " + rank);
        }

        UnaryOperator<Integer> coefficient = number -> switch (number) {
            case 1 -> 2 * number;
            case 2 -> 5 * number;
            case 3 -> (card.equals(Card.FAMILY)) ? 9 * number : 8 * number;
            default -> (card.equals(Card.FAMILY)) ? 9 * number : 12 * number;
        };

        return coefficient.apply(rank);
    }

    /**
     * Calcule le score pour une unité biologique donnée.
     * 
     * @param bioUnit L'unité biologique pour laquelle calculer le score.
     * @return Le score de l'unité biologique.
     */
    public int scoreBioUnit(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");

        return switch (bioUnit) {
            case Animal a -> entityNeighbors.getOrDefault(a, new ArrayList<>()).stream()
                .filter(_ -> !a.equals(Animal.NONE))
                .mapToInt(sB -> coefficient(sB.countNeighbors()))
                .sum();
            case Habitat h -> entityNeighbors.getOrDefault(h, new ArrayList<>()).stream()
                .mapToInt(ScoreBioUnit::countNeighbors)
                .max()
                .orElse(0);
            case Tile _ -> 0;
        };
    }

    /**
     * Calcule et ajoute le score pour toutes les unités biologiques.
     */
    public void scoreAllBioUnit() {
        entityNeighbors.keySet().forEach(bioUnit -> addEntityScore(bioUnit, scoreBioUnit(bioUnit)));
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'état de la carte.
     * 
     * @return Une chaîne représentant l'état de la carte et le score total.
     */
    @Override
    public String toString() {
        return entityNeighbors.entrySet().stream()
            .map(entry -> entry.getKey() + " : " + entry.getValue() + "\n" + scoreBioUnit(entry.getKey()) + " \n")
            .collect(Collectors.joining("Score:\n-----------------\n", "", "Total Score :\n" + totalScore() + "\n"));
    }
}
