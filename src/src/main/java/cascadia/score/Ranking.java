package src.main.java.cascadia.score;

import java.util.*;
import java.util.stream.Collectors;

import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.BioUnit;

/**
 * Classe représentant le classement des joueurs dans un jeu. Cette classe gère les scores des joueurs,
 * le nombre de joueurs, et les égalités lors du calcul du podium.
 */
public record Ranking(List<Integer> playerScores, BioUnit bioUnit, int nbPlayers) {

    /**
     * Constructeur de la classe Ranking.
     * 
     * @param playerScores Liste des scores des joueurs.
     * @param bioUnit Unité biologique associée à ce classement.
     * @param nbPlayers Nombre de joueurs, qui doit être entre 1 et 4 inclus.
     * @throws NullPointerException si la liste des scores ou l'unité biologique est null.
     * @throws IllegalArgumentException si le nombre de joueurs est inférieur à 1 ou supérieur à 4.
     */
    public Ranking {
        Objects.requireNonNull(playerScores, "Player scores cannot be null");
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");

        if (nbPlayers < 1 || nbPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 1 and 4.");
        }
        playerScores = List.copyOf(playerScores);  
    }

    /**
     * Constructeur par défaut. Crée un classement avec des scores vides, l'unité biologique NONE, et un seul joueur.
     */
    public Ranking() {
        this(new ArrayList<>(), Animal.NONE, 1);
    }

    /**
     * Calcule le coefficient de score d'un joueur en fonction de sa position sur le podium.
     * 
     * @param podiumPosition La position du joueur sur le podium (0 pour la première place, 1 pour la deuxième, etc.).
     * @return Le coefficient de score du joueur.
     */
    public int getPlayerCoefficient(int podiumPosition) {
        return switch (podiumPosition) {
            case 0 -> (nbPlayers >= 3) ? 3 : 2;  // 3 points pour la première place si 3 joueurs ou plus, sinon 2 points
            case 1 -> (nbPlayers == 2) ? 0 : 1;  // 0 point pour la deuxième place si 2 joueurs, sinon 1 point
            default -> 0;
        };
    }

    /**
     * Calcule le coefficient de score en cas d'égalité entre les joueurs.
     * 
     * @param numberOfTies Le nombre d'égalités dans le classement.
     * @param podiumPosition La position du joueur sur le podium.
     * @return Le coefficient de score ajusté en fonction des égalités.
     */
    public int calculateTieCoefficient(int numberOfTies, int podiumPosition) {
        return switch (numberOfTies) {
            case 1 -> getPlayerCoefficient(podiumPosition);  // Cas d'une seule égalité
            case 2, 4 -> (nbPlayers == 2) ? 0 : (podiumPosition == 2) ? 1 : (podiumPosition == 1) ? getPlayerCoefficient(podiumPosition) : 0;
            default -> 0;
        };
    }

    /**
     * Calcule le nombre d'égalités pour chaque score.
     * 
     * @return Une carte où la clé est le score et la valeur est le nombre d'occurrences de ce score.
     */
    public Map<Integer, Integer> countEqualities() {
        return playerScores.stream()
                .collect(Collectors.groupingBy(score -> score, Collectors.summingInt(_ -> 1)));
    }

    /**
     * Retourne les trois meilleurs scores parmi les joueurs.
     * 
     * @return Une liste contenant les trois meilleurs scores, ou moins si le nombre de joueurs est inférieur à 3.
     */
    public List<Integer> getTopScores() {
        return playerScores.stream()
                .sorted(Comparator.reverseOrder())  // Trie les scores dans l'ordre décroissant
                .limit(Math.min(3, playerScores.size()))  // Limite à 3 scores au maximum
                .collect(Collectors.toList());
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du classement.
     * 
     * @return Une chaîne représentant le classement avec les scores des joueurs, l'unité biologique et le nombre de joueurs.
     */
    @Override
    public String toString() {
        return "Ranking{" +
                "playerScores=" + playerScores +
                ", bioUnit=" + bioUnit.getName() +
                ", nbPlayers=" + nbPlayers +
                '}';
    }
}
