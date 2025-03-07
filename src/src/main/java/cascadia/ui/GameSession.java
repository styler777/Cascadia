package src.main.java.cascadia.ui;

import java.util.Objects;

import com.github.forax.zen.Application;

import src.main.java.cascadia.core.Game;
import src.main.java.cascadia.core.Pick;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.utils.Coordinate;
import src.main.java.cascadia.utils.Message;
import src.main.java.cascadia.utils.Mode;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;

public class GameSession {

    // Constants
    private static final int MIN_PLAYERS = 0;
    private static final int MAX_PLAYERS = 4;
    private static final int DEFAULT_BOARD_SIZE = 10;

    // Attributes
    private final Pick deck;
    private final Game game;
    private final Mode mode;
    private final Card category;
    private final int nbPlayer;

    // Constructor
    public GameSession(Mode mode, Card category, int nbPlayer) {
        Objects.requireNonNull(mode, "Mode cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");

        if (nbPlayer < MIN_PLAYERS || nbPlayer > MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }

        this.deck = new Pick();
        this.mode = mode;
        this.category = category;
        this.nbPlayer = nbPlayer;
        this.game = new Game(nbPlayer, category, mode, DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE, deck);
    }

    // Display the initial game message
    public static MessageDisplay playDisplay() throws IOException {
        MessageDisplay messageDisplay = new MessageDisplay(Message.START);
        messageDisplay.displayText(MessageDisplay.load(Path.of("File/text.txt")));
        return messageDisplay;
    }

    // Initialize a new Play instance based on user input
    public static GameSession playInput() {
        UserInputHandler input = UserInputHandler.playStart();
        return new GameSession(input.visual(), input.category(), input.nbPlayer());
    }

    // Update message based on the game mode
    public MessageDisplay changeMessage(MessageDisplay MessageDisplay) {
        Objects.requireNonNull(MessageDisplay, "MessageDisplay cannot be null");
        Message newMessage = isGraphic() ? Message.NOTHING : Message.COORDINATE;
        MessageDisplay.changeMessage(newMessage);
        return MessageDisplay;
    }

    // Check if the game is in graphic mode
    public boolean isGraphic() {
        return mode.equals(Mode.GRAPHIC);
    }

    // Initialize the game in textual mode
    public void initGameTextual() {
        game.initGame();
    }

    // Static method to initialize textual mode for an existing GameSession instance
    public static GameSession initTextual(GameSession GameSession) {
        Objects.requireNonNull(GameSession, "GameSession instance cannot be null");
        if (!GameSession.isGraphic()) {
            GameSession.initGameTextual();
        }
        return GameSession;
    }

    // End the textual game and disGameSession results
    public MessageDisplay endTextual(MessageDisplay MessageDisplay) {
        Objects.requireNonNull(MessageDisplay, "MessageDisplay cannot be null");
        return game.initResultPlayers(MessageDisplay);
    }

    // Main game loop for both graphic and textual modes
    public int playGame(int nbTurn) {
        return switch (mode) {
            case GRAPHIC -> playGraphic();
            case TEXTUAL -> playTextual(nbTurn);
        };
    }

    // Handle the graphic mode gameplay
    private int playGraphic() {
        Application.run(Color.ORANGE, context -> {
            var screenInfo = context.getScreenInfo();
            int width = screenInfo.width();
            int height = screenInfo.height();
            GameMouseHandler mouse = new GameMouseHandler(nbPlayer, width, height, category);
            mouse.MouseHandler(context);
        });
        return 20; // Indicates successful completion in graphic mode
    }

    // Handle the textual mode gameplay
    private int playTextual(int nbTurn) {
        Coordinate coordinate = UserInputHandler.createCoordinate();
        BioUnit bioUnit = UserInputHandler.getInputBioUnitFromConsole(deck);
        boolean validMove = game.playerChoice(
            game.getPlayer().orElseThrow(), 
            game.getEcosystemPlayer(), 
            bioUnit, 
            coordinate
        );
        return validMove ? 1 : 0;
    }

    // String representation of the game state
    @Override
    public String toString() {
        return game.toString();
    }
}
