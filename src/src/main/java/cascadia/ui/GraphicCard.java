package src.main.java.cascadia.ui;

import com.github.forax.zen.ApplicationContext;
import java.util.Objects;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.data.ImageLoading;

/**
 * The GraphicCard class is responsible for handling the display of cards in the game.
 * It uses the provided Card category to load and display an image representation of the card.
 */
public class GraphicCard {
    private final Card category;
    private final ImageLoading image;

    /**
     * Constructs a new GraphicCard instance for displaying a specific card category.
     * @param category the card category that will be displayed.
     */
    public GraphicCard(Card category) {
        Objects.requireNonNull(category);
        this.category = category;
        this.image = new ImageLoading();
    }

    /**
     * Displays the image of the card in the game window.
     * The image is drawn at a specified position in the context and is sized according to a fixed cell size.
     * @param context the application context used to render the image.
     * @param width the width of the game window.
     * @param height the height of the game window.
     */
    public void displayImageCard(ApplicationContext context, int width, int height) {
        Objects.requireNonNull(context);
        context.renderFrame(graphics -> {
            int cellsize = 300; // Set the size for the card image
            graphics.drawImage(image.imageCard(category), width - width / 4, height / 2 - height / 3, cellsize, cellsize, null);
        });
    }
}