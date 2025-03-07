package src.main.java.cascadia.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import src.main.java.cascadia.core.EcosystemPlayerManager;
import src.main.java.cascadia.entities.Animal;
import src.main.java.cascadia.entities.Tile;



public record Coordinate(int x , int y) {
	/**
   * Checks if the coordinate is valid within the given board dimensions.
   * A coordinate is considered valid if it lies within the boundaries of the board width and height.
   *
   * @param boardWidth The width of the board.
   * @param boardHeight The height of the board.
   * @return true if the coordinate is within the board bounds, false otherwise.
   */
	public boolean validCoordinate(int boardWidth , int boardHeight) {
		/*tests if the coordinate is valid*/
		return x >= 0 && y >= 0 && x < boardWidth && y < boardHeight;
	}
	
	/**
   * Returns a list of coordinates along a line based on the current coordinate (x, y), considering
   * a starting point (beginning of the line) and the board limits. This method is used to get a line of 
   * coordinates based on a specific bio unit's position.
   *
   * @param ecosystem The ecosystem manager containing the game state.
   * @param nbBioUnit The number of bio units to consider for the line of coordinates.
   * @param coordinates The starting coordinates to form the line.
   * @param bioUnit The bio unit (either Animal or Tile) to be used for filtering.
   * @param <B> The type of bio unit (Animal or Tile).
   * @return A list of coordinates forming a line in the ecosystem.
   */
	private <B> List<Coordinate> getAllCoordinateLines(EcosystemPlayerManager ecosystem,int nbBioUnit ,List<Coordinate> coordinates, B bioUnit) {
		/*return  the list of bioUnit (Animal or Habitat) present on this line 
		 * start = starting by x coordinate of bioUnit  
		 * end 	 = limit of board  
		 * */
		
		Objects.requireNonNull(ecosystem , "board is null");
		Objects.requireNonNull(bioUnit , " bioUnit is null");
		BiFunction<Integer, Integer, List<Coordinate>> coordLine = (i, j) -> 
	    IntStream.range(j - nbBioUnit , j)
	             .mapToObj(c -> new Coordinate(i, c))
	             .collect(Collectors.toList());
		
			
	    return coordinates.stream()
                .flatMap(coord -> coordLine.apply(coord.x(), coord.y()).stream())
                .toList();

		}
	
  /**
   * Gets the list of neighboring coordinates of a given bio unit (Animal or Tile) 
   * in the ecosystem. Filters the coordinates based on the type of bio unit.
   *
   * @param ecosystem The ecosystem manager containing the game state.
   * @param bioUnit The bio unit (either Animal or Tile) used for filtering neighbors.
   * @param <B> The type of bio unit (Animal or Tile).
   * @return A list of coordinates of neighboring tiles or animals that satisfy the bio unit condition.
   */
	public <B> List<Coordinate> getCoordinatesToNeighbor(EcosystemPlayerManager ecosystem, B bioUnit) {
		Objects.requireNonNull(ecosystem);
		Objects.requireNonNull(bioUnit);
		
		var coordinates = List.<Coordinate>of(new Coordinate(x, y + 2) , 
																					new Coordinate(x + 1, y + 2),
																					new Coordinate(x - 1, y + 2));
		
		return getAllCoordinateLines(ecosystem, 3, coordinates, bioUnit).stream()
	    .filter(coord -> switch (bioUnit) {
	        case Animal a -> 
	            ecosystem.getTileFromEcosystem(coord)
	                     .map(tile -> !tile.animalInTile().equals(a))
	                     .orElse(false);
	        case Tile _ -> 
	           !ecosystem.getTileFromEcosystem(coord).isEmpty();
	        default -> 
	            throw new IllegalArgumentException("Unexpected value: " + bioUnit);
	    })
	    .toList();

	}
	
	 /**
   * Converts the coordinate into a string representation in the form of "[x, y]".
   *
   * @return A string representation of the coordinate.
   */
	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return "[" + x + "," + y + "]";
	}
	
	
}
