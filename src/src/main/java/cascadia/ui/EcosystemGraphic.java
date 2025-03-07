package src.main.java.cascadia.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import com.github.forax.zen.ApplicationContext;

import src.main.java.cascadia.core.Game;
import src.main.java.cascadia.data.ImageLoading;
import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.utils.Coordinate;



public class EcosystemGraphic {
	private final HashMap<Rectangle, Coordinate> rectangles;
	private final ImageLoading imageLoading;
	private final Game game;
	
	/**
   * Constructor to initialize the EcosystemGraphic with image loading and game instances.
   * 
   * @param image The image loading manager that handles the loading of game images.
   * @param game The game instance that provides game data.
   * @throws NullPointerException if either the image or game is null.
   */
	public EcosystemGraphic(ImageLoading image , Game game) {
		Objects.requireNonNull(image);
		Objects.requireNonNull(game);
		this.imageLoading = image;
		this.rectangles = new HashMap<>();
		this.game = game;
	}

  /**
   * Returns a BiFunction that checks whether a given coordinate is inside any of the rectangles.
   * 
   * @return A BiFunction that checks if a coordinate is within any of the rectangles in the grid.
   */
	 public BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>> isInRectangle() {
		 BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>>  biFunction = (list,coord) -> 
		 list.stream().filter(rect -> rect.contains(coord.x() , coord.y())).findFirst();
		 return biFunction;
	}
	

   /**
    * Creates a rectangle with a specified color and position.
    * 
    * @param graphics The graphics object to draw the rectangle.
    * @param color The color of the rectangle.
    * @param coordinate The coordinate where the rectangle should be drawn.
    * @param width The width and height of the rectangle.
    */
	
	private static void createRectangle(Graphics2D graphics, Color color, Coordinate coordinate, int width) {
			
			graphics.setColor(color);  
			
			graphics.draw(new Rectangle2D.Float(coordinate.x(), coordinate.y(), width, width)); 
		        
	}
	

  /**
   * Gets the index (coordinate) of a tile from a rectangle.
   * 
   * @param rectangle The rectangle representing a tile on the grid.
   * @return The corresponding coordinate of the tile in the ecosystem.
   */
	public Coordinate getIndexBoard(Optional<Rectangle> rectangle) {
	    Objects.requireNonNull(rectangle);
	    
	    return rectangle.map(rect -> {
	        
	        return rectangles.getOrDefault(rect, new Coordinate(-1, -1));
	    }).orElse(new Coordinate(-1,-1));
		}
	
	 /**
   * Checks if a tile contains an animal and draws the animal on the graphical grid.
   * 
   * @param tile The tile to check for an animal.
   * @param graphics The graphics object to render the animal.
   * @param i The x-coordinate where the tile is drawn.
   * @param j The y-coordinate where the tile is drawn.
   * @param cellSize The size of each tile in the grid.
   */
	
	private void tileHasAnimal(Tile tile , Graphics2D graphics , int i ,int  j ,int cellSize) {
		if(!tile.animalInTile().equals(Animal.NONE)) {
			graphics.drawImage(imageLoading.getBufferedImageAnimal(tile.animalInTile()), i + cellSize/3, j + cellSize/3, cellSize/2, cellSize/2, null);
	        
            
		}
			
		
		

	}
	
	 /**
   * Displays a message on the screen within the game context.
   * 
   * @param context The application context to render the message.
   * @param identifier The message to display on the screen.
   * @param width The width of the display area.
   * @param height The height of the display area.
   */
	
	public void displayPlay(ApplicationContext context , String identifier , int width , int height) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(identifier);
		context.renderFrame(graphics -> {

		       
	        Font font = new Font("Helvetica", Font.BOLD | Font.ITALIC, 18);

	        
	        FontRenderContext frc = graphics.getFontRenderContext();

	        TextLayout layout = new TextLayout(identifier, font, frc);
	        layout.draw(graphics,width - width /4, 50);
	        

	    });

	}
	 /**
   * Creates and renders a grid of rectangles, placing tiles and animals where applicable.
   * 
   * @param context The application context to render the grid.
   * @param cellSize The size of each cell in the grid.
   * @param gridWidth The width of the grid in terms of cells.
   * @param gridHeight The height of the grid in terms of cells.
   * @param color The color of the grid cells.
   * @return A set containing all the rectangles created for the grid.
   */
	
	public  Set<Rectangle> createGrid(ApplicationContext context, int cellSize, int gridWidth, int gridHeight, Color color) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(color);
		 imageLoading.getListImage(game.getPlayer().get().getTiles());
		context.renderFrame(graphics -> {
	   
	       
	       int i , j , x , y;
	        for (i = 10, x = 0; i < gridWidth + gridWidth/3; i += cellSize , x++) { 
	            for( j =  10 , y = 0; j < gridHeight + gridHeight / 2; j += cellSize, y++) {
	                
	                createRectangle(graphics, color,new Coordinate(i, j), cellSize);
	                rectangles.put(new Rectangle(i, j, cellSize, cellSize) , new Coordinate(x, y));
	           
	                if(game.getEcosystemPlayer().getTileFromEcosystem(new Coordinate(x, y)).isPresent()) {
	                	var tile = game.getEcosystemPlayer().getTileFromEcosystem(new Coordinate(x, y)).get();
	                	
	                	
	                	graphics.drawImage(imageLoading.getBufferedImageTile(tile),i, j , cellSize, cellSize, null);
	                	tileHasAnimal(tile , graphics , i ,j ,cellSize);
	                	
	                }
	          
	               
	            }
	            
	            
	       }
	    });
	    return Set.copyOf(rectangles.keySet());
	}
	


}
