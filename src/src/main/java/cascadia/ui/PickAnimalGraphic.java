package src.main.java.cascadia.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
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
import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.utils.Coordinate;

/**
 * The PickAnimalGraphic class is responsible for handling the visual aspects
 * of animal selection in the game. It provides functionality for creating
 * animal selection circles, rendering animal images, and detecting mouse
 * interactions with the animal graphics.
 */
public class PickAnimalGraphic {
	private final HashMap<Ellipse2D, Integer> ellipses;
	private final ImageLoading image;
	private final Pick pick;
	
	/**
   * Constructs a PickAnimalGraphic instance with the given image loader and pick handler.
   * @param image the image loader responsible for loading animal images.
   * @param pick the pick handler responsible for managing the animal selection.
   */
	public PickAnimalGraphic(ImageLoading image , Pick pick) {
		Objects.requireNonNull(image);
		Objects.requireNonNull(pick);
		this.ellipses = new HashMap<>();
		this.pick = pick;
		this.image = image;
	
	}
	
  /**
   * Returns a BiFunction that checks if a given coordinate is inside any of the animal selection circles.
   * @return a BiFunction for detecting mouse interaction with animal circles.
   */
	 public BiFunction<Set<Ellipse2D>,Coordinate , Optional<Ellipse2D>> isInCercle() {
		 BiFunction<Set<Ellipse2D>,Coordinate , Optional<Ellipse2D>>  biFunction = (list,coord) -> 
		 list.stream().filter(cercle -> cercle.contains(coord.x() , coord.y())).findFirst();
		 return biFunction;
	}

	 /**
    * Retrieves the index of the selected animal based on the given ellipse.
    * @param cercle an Optional containing the ellipse representing the selected animal.
    * @return the index of the selected animal, or -1 if none is selected.
    */
	 public int getIndexPickAnimal(Optional<Ellipse2D> cercle) {
		Objects.requireNonNull(cercle);
		return cercle.map(c -> ellipses.getOrDefault(c, -1)).orElse(-1);
	}
	 /**
    * Retrieves a list of animals to be displayed in the selection interface.
    * @return a list of animals available for selection.
    */
	 private List<Animal> getpickAnimal() {
		 return IntStream.range(0, 4).mapToObj(i -> pick.getPickAnimalIndex(i)).toList();

	}
	 

   /**
    * Creates the animal selection interface, rendering animal selection circles and their images.
    * @param context the application context used for rendering.
    * @param color the color of the selection circles.
    * @param width the width of the display area.
    * @param height the height of the display area.
    * @return a set of Ellipse2D objects representing the animal selection circles.
    */
	 
	 public Set<Ellipse2D> createPickAnimalInterface(ApplicationContext context, Color color, int width, int height) {
		 	var list = image.getListAnimalImage(getpickAnimal());
		 
	        context.renderFrame(graphics -> {
	           

	            int diameter = 60;
	            int spacing = 10;
	            int startX = width / 4 ;
	            int startY = height / 4 + height - 350;

	            
	            IntStream.range(0, 4).forEach(i -> {
	                int x = startX + i * (diameter + spacing);
	                int y = startY;

	              createCircle(graphics, color, new Coordinate(x, y), diameter);
	              graphics.drawImage(list.get(i), x, y , diameter, diameter, null);
	                
	               
	                
	                
	                ellipses.put(new Ellipse2D.Double(x, y, diameter, diameter), i);
	            });
	        });
	        return Set.copyOf(ellipses.keySet());
	    }
	 
	  /**
    * Helper method to create a filled circle for the animal selection interface.
    * @param graphics the graphics object used for rendering.
    * @param color the color of the circle.
    * @param coordinate the center coordinates of the circle.
    * @param diameter the diameter of the circle.
    */
	 private static void createCircle(Graphics2D graphics, Color color,Coordinate coordinate, int diameter) {
	     graphics.setColor(color);   
	     graphics.fillOval(coordinate.x(), coordinate.y(), diameter, diameter);  
	 }
	 
	 /**
    * Detects if the mouse click is inside any of the animal selection circles.
    * @param biFunction the BiFunction used to check if the mouse is inside any circle.
    * @param mouse the mouse coordinates.
    * @param ellipses the set of Ellipse2D objects representing the animal selection circles.
    * @return the index of the selected animal, or -1 if none is selected.
    */
	 public int mouseOnPickAnimal(BiFunction<Set<Ellipse2D>,Coordinate , Optional<Ellipse2D>> biFunction  , Coordinate mouse, Set<Ellipse2D> ellipses) {
			Objects.requireNonNull(mouse);
			Objects.requireNonNull(biFunction);
			Objects.requireNonNull(ellipses);
			Optional<Ellipse2D> ellipse = biFunction.apply(ellipses, mouse);
			return getIndexPickAnimal(ellipse);

		}
}