package src.main.java.cascadia.ui;

import java.util.Objects;

import src.main.java.cascadia.core.Pick;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.utils.Coordinate;

/**
 * A record representing the user's input in a 2D space, consisting of a coordinate, an index for a tile,
 * and an index for an animal. It provides methods for validation and updating the input.
 */
public record UserInput2D(Coordinate coordinate , int indexTile , int indexAnimal) {
	
	 /**
   * Constructs a UserInput2D record with the specified coordinate, tile index, and animal index.
   * Validates the coordinate to ensure it's non-null.
   * @param coordinate the coordinate of the user input
   * @param indexTile the index of the selected tile
   * @param indexAnimal the index of the selected animal
   */
	public UserInput2D {
		Objects.requireNonNull(coordinate);
	}
	 /**
   * Default constructor that initializes the UserInput2D with default values.
   * The coordinate is set to (-1, -1), and both the tile and animal indices are set to -1.
   */
	public UserInput2D() {
		this(new Coordinate(-1 , -1) , -1 , -1 );
	}


  /**
   * Validates the user input to check if it's within valid bounds.
   * @param boardWidth the width of the board
   * @param boardHeight the height of the board
   * @return true if the input is valid; false otherwise
   */
	public boolean validUserInput2D(int boardWidth ,int boardHeight) {
		if(!coordinate.validCoordinate(boardWidth , boardHeight))
			return false;
		if(indexTile == -1 && indexAnimal == -1)
			return false;
		return true;

	}

  /**
   * Creates a new UserInput2D with updated values. If a new coordinate is provided, it will replace the old one.
   * If a new index for the tile or animal is provided, it will replace the corresponding index; otherwise, it keeps the old index.
   * @param newCoordinate the new coordinate
   * @param newIndexAnimal the new index for the animal, or -1 to keep the existing one
   * @param newIndexTile the new index for the tile, or -1 to keep the existing one
   * @return a new UserInput2D with the updated values
   */
	public UserInput2D newUserInput2D(Coordinate newCoordinate , int newIndexAnimal , int newIndexTile) {
		Objects.requireNonNull(newCoordinate);
		return new UserInput2D(newCoordinate ,(newIndexTile == -1)? indexTile : newIndexTile ,(newIndexAnimal == -1)? indexAnimal : newIndexAnimal);

	}
	/**
   * Retrieves the BioUnit (either tile or animal) based on the user's input.
   * @param pick the pick object to retrieve the selected BioUnit
   * @return the selected BioUnit (either tile or animal)
   */
	public BioUnit getBioUnit(Pick pick) {
		Objects.requireNonNull(pick);
		return (indexTile != -1) ? pick.getPickTileIndex(indexTile) : pick.getPickAnimalIndex(indexAnimal);

	}
}
