package src.main.java.cascadia.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;

import src.main.java.cascadia.core.Game;
import src.main.java.cascadia.core.Player;
import src.main.java.cascadia.data.ImageLoading;
import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.Habitat;
import src.main.java.cascadia.utils.Message;

/**
 * The ScoreGraphic class is responsible for displaying the game score, including player scores,
 * animal and habitat scores, and bonuses on the screen. It handles rendering score information
 * in various formats and positioning it appropriately on the UI.
 */
public class ScoreGraphic {
	private final ImageLoading imageLoading;
	private final Game game;
	private final static List<Animal> ANIMALS = List.of(Animal.BEAR ,  Animal.ELK , Animal.SALMON , Animal.BUZZARD , Animal.FOX);
	private final static List<Habitat> HABITATS = List.of(Habitat.MOUNTAIN , Habitat.FOREST , Habitat.DESERT , Habitat.SWAMP , Habitat.RIVER);
	public ScoreGraphic(Game game) {
		Objects.requireNonNull(game);
		this.imageLoading = new ImageLoading();
		this.game = game;
	}

  /**
   * Constructs a ScoreGraphic instance that manages score display for the given game.
   * @param game the game instance containing the game data and player information.
   */
	public Rectangle2D displayScore(ApplicationContext context , int width , int height) {
		Objects.requireNonNull(context);
	    context.renderFrame(graphics -> {
	        graphics.setColor(Color.ORANGE);
	        graphics.fill(new Rectangle2D.Float(0, 0, width, height));
            graphics.drawImage(imageLoading.imageScore(), width / 4, height / 4, width /2 , height - height/ 2, null);

	    });
	    return new Rectangle(width/4, height/4, width/2, height - height/2);
	}
	 /**
   * Displays the score background and score images on the screen.
   * @param context the application context used for rendering.
   * @param width the width of the display area.
   * @param height the height of the display area.
   * @return a Rectangle2D representing the area where the score is displayed.
   */
	public void displayScore(ApplicationContext context , String score , int width , int height) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(score);
		context.renderFrame(graphics -> {

	       
	        Font font = new Font("Helvetica", Font.BOLD | Font.ITALIC, 18);

	        
	        FontRenderContext frc = graphics.getFontRenderContext();

	        TextLayout layout = new TextLayout(score, font, frc);
	        layout.draw(graphics,width - 90, height/2 + 85);
	        

	    });
	}
	
	
	 /**
   * Displays the score of a specific bio unit for a player at a calculated position.
   * @param context the application context used for rendering.
   * @param bioUnitScore the score to display for the bio unit.
   * @param iPlayer the index of the player.
   * @param jBioUnit the index of the bio unit.
   * @param width the width of the display area.
   * @param height the height of the display area.
   */
	public void displayScorePlayerBioUnit(ApplicationContext context , String bioUnitScore , int iPlayer , int jBioUnit, int width , int height) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(bioUnitScore);
		int spacingX = 50 , spacingY = 20, x = width + iPlayer * (spacingX  + 80), y = height + jBioUnit * (spacingY + 40);
		displayScore(context , bioUnitScore, x, y); 
		
	}
	/**
   * Displays the individual scores of each bio unit (animal and habitat) for the given player.
   * @param context the application context used for rendering.
   * @param player the player whose bio unit scores are being displayed.
   * @param players the list of players.
   * @param rect the rectangle representing the score display area.
   */
	private void diplayBioUnitScore(ApplicationContext context , Player player , List<Player> players, Rectangle2D rect) {
		ANIMALS.forEach(animal ->{
				displayScorePlayerBioUnit(context , "" + player.playerScore(animal),players.indexOf(player) , ANIMALS.indexOf(animal), (int) rect.getWidth() ,(int) rect.getHeight());	
				
			});
		HABITATS.forEach(habitat -> {
				displayScorePlayerBioUnit(context , "" +player.playerScore(habitat) , players.indexOf(player) , HABITATS.indexOf(habitat), (int) rect.getWidth() - 20 , (int) (rect.getHeight() + rect.getHeight() / 2 + 90) );
				displayScorePlayerBioUnit(context , "" +  game.getBonus(habitat , player),players.indexOf(player) , HABITATS.indexOf(habitat), (int) rect.getWidth() + 50 ,(int) (rect.getHeight()+ rect.getHeight() / 2 + 90));
			});

	}
  /**
   * Displays the total score for a player, including the animal, habitat, and overall total score.
   * @param context the application context used for rendering.
   * @param player the player whose total score is being displayed.
   * @param players the list of players.
   * @param rect the rectangle representing the score display area.
   */
	
	private void displayTotalScoreBioUnit(ApplicationContext context , Player player , List<Player> players , Rectangle2D rect) {
			displayScorePlayerBioUnit(context , "" + player.playerAnimalScore(),players.indexOf(player) , 5, (int) rect.getWidth() ,(int) rect.getHeight());
			displayScorePlayerBioUnit(context , "" + player.playerHabitatScore(),players.indexOf(player) , 11, (int) rect.getWidth() ,(int) rect.getHeight());
			displayScorePlayerBioUnit(context , "" +  player.playerScoreTotal(),players.indexOf(player) , 13, (int) rect.getWidth() ,(int) rect.getHeight());
	}

  /**
   * Displays the complete score for all players, including individual bio unit scores, total scores, and ranking.
   * @param context the application context used for rendering.
   * @param width the width of the display area.
   * @param height the height of the display area.
   */
	public void scorePlayerBioUnit(ApplicationContext context , int width , int height) {
		Objects.requireNonNull(context);
		game.initResultPlayers(new MessageDisplay(Message.NOTHING));
		var players = game.getPlayers();
		var rect = displayScore(context, width, height);
		game.getPlayers().forEach(player -> {

			displayScorePlayerBioUnit(context , player.toString() , players.indexOf(player)  , 0,(int) rect.getWidth() , (int) rect.getHeight() - 40);
			diplayBioUnitScore(context , player , players,rect);

		});
		game.updateScoreRanking();
		game.getPlayers().forEach(player -> 
			displayTotalScoreBioUnit(context ,player , players ,rect)
			);
		
	}
	
	
	
	
	
	
}
