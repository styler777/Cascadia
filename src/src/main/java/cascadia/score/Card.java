package src.main.java.cascadia.score;


/**
 * Enum représentant les différents types de cartes dans le jeu.
 * Chaque carte a un nom qui permet de la distinguer.
 */
public enum Card {
    FAMILY("Family"), INTERMEDIATE("Intermediate");

    private final String card;

    /**
     * Constructeur de l'énumération Card.
     * 
     * @param name Le nom de la carte.
     */
    private Card(String name) {
        this.card = name;
    }

    /**
     * Récupère le nom de la carte.
     * 
     * @return Le nom de la carte.
     */
    public String getName() {
        return card;
    }
}
