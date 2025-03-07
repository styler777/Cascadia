package src.main.java.cascadia.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import src.main.java.cascadia.core.Game;
import src.main.java.cascadia.core.Pick;
import src.main.java.cascadia.data.ImageLoading;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.utils.Coordinate;
import src.main.java.cascadia.utils.Mode;

import com.github.forax.zen.KeyboardEvent.Action;



public class GameMouseHandler {
	private final PickAnimalGraphic pickAnimalGraphic;
	private final PickTileGraphic pickTileGraphic;
	private final EcosystemGraphic ecosystemGraphic;
	private final ScoreGraphic scoreGraphic;
	private final GraphicCard graphicCard;
	private final ImageLoading image;
	private final static int WIDTH_GAME = 26;
	private final static int HEIGHT_GAME = 16;
	private final Pick pick;
	private final Game game;
	private final int width;
	private final int height;
	/**
   * Constructs a new GameMouseHandler instance for handling mouse events in the game.
   * @param nbPlayer the number of players in the game.
   * @param width the width of the game window.
   * @param height the height of the game window.
   * @param card the card used in the game.
   */
	
	public GameMouseHandler(int nbPlayer , int width , int height , Card card) {
		Objects.requireNonNull(card);
		this.image = new ImageLoading();
		this.graphicCard = new GraphicCard(card);
		this.pick = new Pick();
		this.width = width;
		this.height = height;
		this.game = new Game(nbPlayer, Card.FAMILY , Mode.GRAPHIC,  WIDTH_GAME , HEIGHT_GAME , pick);
		this.pickAnimalGraphic = new PickAnimalGraphic(image , pick);
		this.pickTileGraphic = new PickTileGraphic(image , pick);
		this.ecosystemGraphic = new EcosystemGraphic(image ,game);
		this.scoreGraphic = new ScoreGraphic(game);

	}
	
	
	 /**
   * Determines the coordinates on the game board based on the mouse location.
   * @param biFunction a BiFunction that checks if the mouse is within a rectangle on the board.
   * @param mouse the coordinates of the mouse.
   * @param rectangles a set of rectangles representing the game grid.
   * @return the coordinates on the board corresponding to the mouse location.
   */
	
	public Coordinate mouseOnBoard(BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>> biFunction  , Coordinate mouse, Set<Rectangle> rectangles) {
		Objects.requireNonNull(mouse);
		Objects.requireNonNull(biFunction);
		Objects.requireNonNull(rectangles);
		Optional<Rectangle> rectangle = biFunction.apply(rectangles, mouse);
		return ecosystemGraphic.getIndexBoard(rectangle);

	}
	
	 /**
   * Initializes the game, sets up necessary components like the card, animal and habitat pickers, 
   * and the ecosystem grid, then displays them on the screen.
   * @param context the application context for rendering the game.
   */
	private void initGame(ApplicationContext context) {
		pick.initializePickGame();
		game.initGame();
		graphicCard.displayImageCard(context, width, height);
		pickAnimalGraphic.createPickAnimalInterface(context, Color.WHITE,  width, height);
		pickTileGraphic.createPickHabitatInterface(context , Color.WHITE, width, height);
		ecosystemGraphic.createGrid(context, 50, width/2, height/2, Color.BLACK);
		ecosystemGraphic.displayPlay(context , game.getPlayer().orElseThrow()+ "" , width , height);
	}
	 /**
   * Clears the game screen by filling it with a background color.
   * @param context the application context for rendering the game.
   * @param width the width of the game window.
   * @param height the height of the game window.
   */
	
	private void clean(ApplicationContext context , int width , int height) {
		Objects.requireNonNull(context);
    context.renderFrame(graphics -> {
        graphics.setColor(Color.ORANGE);
        graphics.fill(new Rectangle2D.Float(0, 0, width, height));
        

    });
	}
	/**
   * Processes the mouse events for selecting animals and habitats and updates the user input.
   * @param context the application context for rendering the game.
   * @param x the x-coordinate of the mouse.
   * @param y the y-coordinate of the mouse.
   * @param userInput2D the current user input.
   * @return the updated user input.
   */
	private UserInput2D play(ApplicationContext context,int x , int y , UserInput2D userInput2D) {
	 int indexPickTile = pickTileGraphic.mouseOnPickTile(pickTileGraphic.isInRectangle(), new Coordinate(x,y), pickTileGraphic.createPickHabitatInterface(context,Color.WHITE, width, height));
   int indexPickAnimal = pickAnimalGraphic.mouseOnPickAnimal(pickAnimalGraphic.isInCercle(), new Coordinate(x, y), pickAnimalGraphic.createPickAnimalInterface(context, Color.white, width, height));
   var coordinate = mouseOnBoard(ecosystemGraphic.isInRectangle(), new Coordinate(x, y), ecosystemGraphic.createGrid(context, 50, width/2, height/2, Color.BLACK));
   ecosystemGraphic.displayPlay(context , game.getPlayer().orElseThrow() + "" , width , height);
   graphicCard.displayImageCard(context, width, height);
   return userInput2D.newUserInput2D(coordinate, indexPickAnimal, indexPickTile);
	}
	 /**
   * Checks if the user input is valid and performs the corresponding player choice.
   * @param context the application context for rendering the game.
   * @param userInput the user input to check.
   * @return 1 if the input is valid, 0 otherwise.
   */
	private int checkUserInput2D(ApplicationContext context, UserInput2D userInput) {
		if(userInput.validUserInput2D(game.getEcosystemPlayer().playerBoardWidth() , game.getEcosystemPlayer().playerBoardHeight())) {
      	  game.playerChoice(game.getPlayer().orElseThrow(), game.getEcosystemPlayer(), userInput.getBioUnit(pick),userInput.coordinate());
      	  clean(context , width , height);
      	  return 1;
        }
		
		return 0;
	}
	
	/**
   * Main game loop that handles mouse events, updates the game state, and renders the game components.
   * It listens for Pointer and Keyboard events and updates the game based on user interactions.
   * @param context the application context for rendering the game.
   */
	public void MouseHandler(ApplicationContext context) {
			Objects.requireNonNull(context);
			initGame(context);
			int nbTurn = 0;
			var userInput = new UserInput2D();
			  
		  while (nbTurn != 10) {
	        var event = context.pollOrWaitEvent(10);
	        if (event == null) {
	          continue;
	        }
	        switch (event) {
	        case PointerEvent e:
	          var location = e.location();
	          
	          userInput = play(context,location.x() , location.y() , userInput);
	          nbTurn += checkUserInput2D(context,userInput);
	          if(userInput.validUserInput2D(game.getEcosystemPlayer().playerBoardWidth() , game.getEcosystemPlayer().playerBoardHeight()))
	        	  userInput = new UserInput2D();
	          
	          break;
	        case KeyboardEvent e:
	          switch (e.action()) {
	          case Action.KEY_PRESSED:
	        	
	            return ;
	          default:
	          }
	          
	        }
		        
		      }
		  scoreGraphic.scorePlayerBioUnit(context , width , height);
          
	}
}
