package src.main.java.cascadia.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import com.github.forax.zen.ApplicationContext;

import src.main.java.cascadia.core.Pick;
import src.main.java.cascadia.data.ImageLoading;
import src.main.java.cascadia.entities.Tile;
import src.main.java.cascadia.utils.Coordinate;

/**
 * The PickTileGraphic class is responsible for managing the graphical rendering of the tile
 * selection interface. It handles drawing rectangular areas for tile selection, loading
 * images for tiles, and detecting mouse interactions with these tiles.
 */

public class PickTileGraphic {
	private final HashMap<Rectangle, Integer> rectangles;
	private final ImageLoading image;
	private final Pick pick;
	

  /**
   * Constructs a PickTileGraphic instance with the given image loader and pick handler.
   * @param image the image loader responsible for loading tile images.
   * @param pick the pick handler responsible for managing the tile selection.
   */
	public PickTileGraphic(ImageLoading image,Pick pick) {
		Objects.requireNonNull(pick);
		Objects.requireNonNull(image);
		this.rectangles = new HashMap<>();
		this.image = image;
		this.pick = pick;
	}
	 /**
   * Helper method to create a filled rectangle on the graphics context.
   * @param graphics the graphics object used for rendering.
   * @param color the color of the rectangle.
   * @param coordinate the top-left corner coordinates of the rectangle.
   * @param width the width of the rectangle.
   */
	private static void createRectangle(Graphics2D graphics, Color color, Coordinate coordinate, int width) {
			
			graphics.setColor(color);  
			graphics.fillRect(coordinate.x(), coordinate.y(), width, width); 
		        
		    }
  /**
   * Returns a BiFunction that checks if a given coordinate is inside any of the tile selection rectangles.
   * @return a BiFunction for detecting mouse interaction with tile rectangles.
   */
	 public BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>> isInRectangle() {
		 BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>>  biFunction = (list,coord) -> 
		 list.stream().filter(rect -> rect.contains(coord.x() , coord.y())).findFirst();
		 return biFunction;
	}
	 
	 public int getIndexPickTile(Optional<Rectangle> rectangle) {
		Objects.requireNonNull(rectangle);
		return rectangle.map(rect -> rectangles.getOrDefault(rect, -1)).orElse(-1);
		
	}
	  /**
    * Retrieves the index of the selected tile based on the given rectangle.
    * @param rectangle an Optional containing the rectangle representing the selected tile.
    * @return the index of the selected tile, or -1 if none is selected.
    */
	 private List<Tile> getpickTile() {
		 return IntStream.range(0, 4).mapToObj(i -> pick.getPickTileIndex(i)).toList();

	}
	 /**
    * Retrieves a list of tiles to be displayed in the selection interface.
    * @return a list of tiles available for selection.
    */
	 public Set<Rectangle> createPickHabitatInterface(ApplicationContext context, Color color, int width, int height) { 

		 var list =image.getListImage(getpickTile());	
		    context.renderFrame(graphics -> {
		        int widthRect = 50 , startX = width /4,heightRect = 50 ,spacing = 10;
		        int startY = height / 4 + height - 400;  

		        

		        IntStream.range(0, 4).forEach(i -> {
		            int x = startX + i * (widthRect + spacing);  
		            int y = startY; 

		            createRectangle(graphics, color, new Coordinate(x , y), widthRect);
		            graphics.drawImage(list.get(i), x, y , widthRect, heightRect, null);
		            
		            rectangles.put(new Rectangle(x, y, widthRect, heightRect) , i);
		        });
		    });

		   return Set.copyOf(rectangles.keySet());
		}
	 
	  /**
    * Creates the tile selection interface, rendering rectangular areas for tiles and their images.
    * @param context the application context used for rendering.
    * @param color the color of the selection rectangles.
    * @param width the width of the display area.
    * @param height the height of the display area.
    * @return a set of Rectangle objects representing the tile selection areas.
    */
	 public int mouseOnPickTile(BiFunction<Set<Rectangle>,Coordinate , Optional<Rectangle>> biFunction  , Coordinate mouse, Set<Rectangle> rectangles) {
			Objects.requireNonNull(mouse);
			Objects.requireNonNull(biFunction);
			Objects.requireNonNull(rectangles);
			Optional<Rectangle> rectangle = biFunction.apply(rectangles, mouse);
			return getIndexPickTile(rectangle);

		}
}