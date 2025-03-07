package src.main.java.cascadia.core;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.score.Ranking;
import src.main.java.cascadia.score.UniqueCard;
import src.main.java.cascadia.ui.MessageDisplay;
import src.main.java.cascadia.utils.Coordinate;
import src.main.java.cascadia.utils.Message;
import src.main.java.cascadia.utils.Mode;

/**
 * Représente une partie du jeu, incluant la gestion des joueurs, des tuiles, du score et des classements.
 * Cette classe est responsable de l'initialisation, du déroulement du jeu et de la mise à jour des scores.
 */
public class Game {
    
    private final int nbPlayer;
    private final HashMap<Player, EcosystemPlayerManager> players;
    private final Pick deck;
    private final int width;
    private final int height;
    private final HashMap<Habitat, Ranking> rankings;
    private final List<Coordinate> coordinates;
    private static final List<Habitat> LIST_INIT_HABITAT = List.of(Habitat.DESERT, Habitat.FOREST, Habitat.MOUNTAIN, Habitat.RIVER, Habitat.SWAMP);
    private final Card category;
    private final Mode mode;
    private Message message;
    private int index;

    /**
     * Constructeur de la classe Game.
     *
     * @param nbPlayer Le nombre de joueurs dans la partie (entre 1 et 4).
     * @param category La catégorie de cartes utilisées dans le jeu.
     * @param mode Le mode du jeu (GRAPHIC ou TEXTUAL).
     * @param width La largeur de la grille de l'écosystème.
     * @param height La hauteur de la grille de l'écosystème.
     * @param deck Le paquet de cartes utilisé dans le jeu.
     * @throws IllegalArgumentException Si nbPlayer n'est pas compris entre 1 et 4.
     */
    public Game(int nbPlayer, Card category, Mode mode, int width, int height, Pick deck) {
        Objects.requireNonNull(deck, "Deck cannot be null");
        Objects.requireNonNull(mode, "Mode cannot be null");
        
        if (nbPlayer <= 0 || nbPlayer > 4) {
            throw new IllegalArgumentException("Number of players must be between 1 and 4");
        }
        
        this.nbPlayer = nbPlayer;
        this.players = new HashMap<>();
        this.deck = deck;
        this.category = category;
        this.width = width;
        this.height = height;
        this.index = 0;
        this.mode = mode;
        this.message = (mode.equals(Mode.GRAPHIC)) ? Message.NOTHING : Message.START;
        this.coordinates = List.of(
            new Coordinate(width / 2, height / 2),
            new Coordinate((width / 2) - 1, height / 2),
            new Coordinate(width / 2, height / 2 - 1)
        );
        this.rankings = new HashMap<>();
    }

    /**
     * Ajoute un joueur et son gestionnaire d'écosystème à la partie.
     *
     * @param player Le joueur à ajouter.
     * @param ecosystem Le gestionnaire d'écosystème du joueur.
     * @throws NullPointerException Si l'un des paramètres est nul.
     */
    private void addPlayers(Player player, EcosystemPlayerManager ecosystem) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(ecosystem, "EcosystemPlayerManager cannot be null");
        players.put(player, ecosystem);
    }

    /**
     * Initialise les joueurs dans la partie.
     */
    private void initPlayers() {
        IntStream.range(0, nbPlayer).forEach(id -> 
            addPlayers(new Player(id, new UniqueCard(category), mode), 
                       new EcosystemPlayerManager(width, height))
        );
    }

    /**
     * Initialise une tuile de départ pour un joueur à une position donnée.
     *
     * @param player Le joueur à qui la tuile appartient.
     * @param ecosystem Le gestionnaire d'écosystème du joueur.
     * @param tile La tuile à ajouter.
     * @param coordinateIndex L'indice des coordonnées où la tuile doit être ajoutée.
     */
    private void initTileStart(Player player, EcosystemPlayerManager ecosystem, Tile tile, int coordinateIndex) {
        // Assure que la tuile est ajoutée sur la carte du joueur aux coordonnées spécifiées
        player.addTileInMapTiles(ecosystem, tile, coordinates.get(coordinateIndex), true);
    }

    /**
     * Initialise le jeu en créant les joueurs et en distribuant les tuiles de départ.
     */
    public void initGame() {
        initPlayers();
        deck.initializePickGame();
        players.entrySet().forEach(entry -> 
            IntStream.range(0, 3).forEach(i -> 
                initTileStart(entry.getKey(), entry.getValue(), deck.getTilePickGame().get(), i)
            )
        );
    }

    /**
     * Change le message de fin pour tous les joueurs.
     */
    private void changeMessageAllPlayers() {
        players.keySet().forEach(player -> player.changeMessage(Message.END));
    }

    /**
     * Ajoute un classement pour un habitat donné.
     *
     * @param habitat L'habitat auquel le classement est associé.
     * @param ranking Le classement à ajouter.
     * @throws NullPointerException Si l'habitat ou le classement sont nuls.
     */
    private void addRanking(Habitat habitat, Ranking ranking) {
        Objects.requireNonNull(ranking, "Ranking cannot be null");
        Objects.requireNonNull(habitat, "Habitat cannot be null");
        if (!ranking.playerScores().isEmpty()) {
            rankings.put(habitat, ranking);
        }
    }

    /**
     * Crée les classements pour chaque habitat initial.
     */
    private void createRankings() {
        LIST_INIT_HABITAT.forEach(habitat -> {
            addRanking(habitat, new Ranking(listScoreBioUnit(habitat), habitat, nbPlayer));
        });
    }

    /**
     * Calcule la liste des scores pour un BioUnit spécifique.
     *
     * @param bioUnit L'unité biologique pour laquelle les scores sont calculés.
     * @return La liste des scores des joueurs pour cet bioUnit.
     * @throws NullPointerException Si le BioUnit est nul.
     */
    private List<Integer> listScoreBioUnit(BioUnit bioUnit) {
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");

        return players.keySet().stream()
                    .map(player -> player.playerScore(bioUnit))
                    .filter(nb -> nb > 0)
                    .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Met à jour les scores des joueurs selon le classement.
     *
     * @param ranking Le classement à utiliser pour la mise à jour des scores.
     * @throws NullPointerException Si le classement est nul.
     */
    public void updateScore(Ranking ranking) {
        Objects.requireNonNull(ranking);
        players.keySet().forEach(player -> 
            player.updateScore(ranking.bioUnit(), 
                ranking.calculateTieCoefficient(ranking.countEqualities()
                    .getOrDefault(player.playerScore(ranking.bioUnit()), 0), 
                    ranking.getTopScores().indexOf(player.playerScore(ranking.bioUnit()))))
        );
    }

    /**
     * Calcule et récupère le bonus pour un joueur dans un habitat donné.
     *
     * @param habitat L'habitat pour lequel le bonus est calculé.
     * @param player Le joueur pour lequel le bonus est calculé.
     * @return Le bonus calculé.
     * @throws NullPointerException Si l'habitat ou le joueur sont nuls.
     */
    public int getBonus(Habitat habitat, Player player) {
        Objects.requireNonNull(habitat, "Habitat cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");
        var ranking = rankings.getOrDefault(habitat, new Ranking());
        return ranking.calculateTieCoefficient(
            ranking.countEqualities()
                .getOrDefault(player.playerScore(ranking.bioUnit()), 0), 
            ranking.getTopScores().indexOf(player.playerScore(ranking.bioUnit()))
        );
    }

    /**
     * Met à jour les scores pour tous les classements.
     */
    public void updateScoreRanking() {
        rankings.values().forEach(rank -> updateScore(rank));
    }

    /**
     * Initialise et affiche les résultats des joueurs après la fin du jeu.
     *
     * @param display1d L'affichage des résultats pour le mode graphique ou textuel.
     * @return L'affichage des résultats modifié.
     * @throws NullPointerException Si l'affichage est nul.
     */
    public MessageDisplay initResultPlayers(MessageDisplay display1d) {
        Objects.requireNonNull(display1d, "Display1D cannot be null");
        players.entrySet().forEach(entry -> entry.getKey().playerAddBioUnitScore(entry.getValue()));
        createRankings();
        
        if (mode.equals(Mode.TEXTUAL)) {
            rankings.values().forEach(rank -> updateScore(rank));
        }
        
        this.message = Message.END;
        display1d.changeMessage((mode.equals(Mode.GRAPHIC)) ? Message.NOTHING : Message.END);
        changeMessageAllPlayers();
        return display1d;
    }

    /**
     * Récupère l'écosystème du joueur courant.
     *
     * @return L'écosystème du joueur courant.
     * @throws IllegalArgumentException Si aucun joueur n'est trouvé.
     */
    public EcosystemPlayerManager getEcosystemPlayer() {
        if (getPlayer().isEmpty()) {
            throw new IllegalArgumentException("No player found");
        }
        return players.getOrDefault(getPlayer().get(), new EcosystemPlayerManager(10, 10));
    }

    /**
     * Récupère le joueur courant en fonction de l'index.
     *
     * @return Un {@link Optional} contenant le joueur courant, ou {@link Optional#empty()} si aucun joueur n'est trouvé.
     */
    public Optional<Player> getPlayer() {
        return players.keySet().stream().filter(p -> p.isIdentifier(index)).findFirst();
    }

    /**
     * Récupère la liste de tous les joueurs.
     *
     * @return La liste des joueurs dans la partie.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    /**
     * Permet à un joueur de faire un choix concernant une tuile à placer sur l'écosystème.
     *
     * @param player Le joueur qui fait le choix.
     * @param ecosystem Le gestionnaire d'écosystème du joueur.
     * @param bioUnit L'unité biologique que le joueur veut ajouter.
     * @param coordinate Les coordonnées où la tuile doit être placée.
     * @return True si l'ajout de la tuile a réussi, false sinon.
     * @throws NullPointerException Si l'un des paramètres est nul.
     */
    public boolean playerChoice(Player player, EcosystemPlayerManager ecosystem, BioUnit bioUnit, Coordinate coordinate) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(ecosystem, "EcosystemPlayerManager cannot be null");
        Objects.requireNonNull(coordinate, "Coordinate cannot be null");
        Objects.requireNonNull(bioUnit, "BioUnit cannot be null");

        if (player.addTileInMapTiles(ecosystem, bioUnit, coordinate, false)) {
            deck.changePickGame(bioUnit);
            index = (index + 1 < nbPlayer && nbPlayer != 1) ? index + 1 : 0;
            return true;
        }
        return false;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'état actuel du jeu.
     *
     * @return La chaîne de caractères représentant l'état du jeu.
     */
    @Override
    public String toString() {
        return switch (message) {
            case START -> deck + "\n" + getPlayer().get() + getEcosystemPlayer();
            case NOTHING, COORDINATE -> "";
            case END -> players.keySet().stream().map(Player::toString).collect(Collectors.joining("\n"));
        };
    }
}
