package src.main.java.cascadia.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import src.main.java.cascadia.core.Pick;
import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.score.Card;
import src.main.java.cascadia.utils.Coordinate;
import src.main.java.cascadia.utils.Mode;




public record UserInputHandler(Card category, Mode visual, int nbPlayer) {
  /**
   * Constructor for UserInputHandler.
   * Ensures that category and visual are not null, and nbPlayer is between 0 and 4.
   *
   * @param category The Card category (FAMILY or INTERMEDIATE).
   * @param visual The Mode of the game (GRAPHIC or TEXTUAL).
   * @param nbPlayer The number of players (between 0 and 4).
   */
  public UserInputHandler {
      Objects.requireNonNull(category, "Category cannot be null");
      Objects.requireNonNull(visual, "Mode cannot be null");
      if (nbPlayer < 0 || nbPlayer > 4) {
          throw new IllegalArgumentException("Number of players must be between 0 and 4");
      }
  }
  /**
   * Creates a Card based on the provided card type string.
   * Throws an IllegalArgumentException if the input string is invalid.
   *
   * @param CardType A string representing the card type (F or I).
   * @return The corresponding Card.
   */
  public static Card createCard(String CardType) {
      Objects.requireNonNull(CardType, "Card type cannot be null");
      return switch (CardType) {
          case "F" -> Card.FAMILY;
          case "I" -> Card.INTERMEDIATE;
          default -> throw new IllegalArgumentException("Unexpected value: " + CardType);
      };
  }

  /**
   * Starts the game by prompting the user for the number of players, card choice, and mode choice.
   *
   * @return A new UserInputHandler instance with the user's input.
   */
  public static UserInputHandler playStart() {
  	int nbPlayerChoice = UserInputHandler.getInputIntegerFromConsole();
  	Card CardChoice = UserInputHandler.getInputCardFromConsole();
  	Mode modeChoice = UserInputHandler.getInputModeFromConsole();
  	return new UserInputHandler(CardChoice, 
								modeChoice,
								nbPlayerChoice);
  }
  
  
  /**
   * Creates a BioUnit based on the input bio unit type (either "T" for Tile or "A" for Animal) 
   * and retrieves the corresponding BioUnit from the provided deck.
   *
   * @param bioUnitType The type of the BioUnit (T for Tile, A for Animal).
   * @param deck The Pick deck to retrieve the BioUnit from.
   * @return The corresponding BioUnit.
   */
  public static BioUnit createBioUnit(String bioUnitType, Pick deck) {
      Objects.requireNonNull(bioUnitType, "BioUnit type cannot be null");
      Objects.requireNonNull(deck, "Deck cannot be null");

      return switch (bioUnitType) {
          case "T" -> deck.getPickTileIndex(getInputIntegerFromConsole());
          case "A" -> deck.getPickAnimalIndex(getInputIntegerFromConsole());
          default -> throw new IllegalArgumentException("Unexpected value: " + bioUnitType);
      };
  }
  /**
   * Creates a Mode based on the input string (either "G" for GRAPHIC or "Text" for TEXTUAL).
   * Throws an IllegalArgumentException if the input is invalid.
   *
   * @param modeType The string representing the mode (G or Text).
   * @return The corresponding Mode.
   */
  public static Mode createMode(String modeType) {
      Objects.requireNonNull(modeType, "Mode type cannot be null");
      return switch (modeType) {
          case "G" -> Mode.GRAPHIC;
          case "Text" -> Mode.TEXTUAL;
          default -> throw new IllegalArgumentException("Unexpected value: " + modeType);
      };
  }
  /**
   * Prompts the user to input a Card type from the console.
   * Retries if the input is invalid.
   *
   * @return The Card selected by the user.
   */
  public static Card getInputCardFromConsole() {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  	 try {
  	       
  	        String input = reader.readLine().trim();
  	        return createCard(input);
  	    } catch (IOException e) {
  	        throw new RuntimeException("Error reading input", e);
  	    } catch (IllegalArgumentException e) {
  	        
  	        return getInputCardFromConsole(); 
  	    }
  	
  }
  /**
   * Prompts the user to input a BioUnit type (Tile or Animal) from the console.
   * Retries if the input is invalid.
   *
   * @param deck The Pick deck from which the BioUnit will be selected.
   * @return The BioUnit selected by the user.
   */
  public static BioUnit getInputBioUnitFromConsole(Pick deck) {
      Objects.requireNonNull(deck, "Deck cannot be null");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      try {
          String input = reader.readLine().trim();
          return createBioUnit(input, deck);
      } catch (IOException e) {
          throw new RuntimeException("Error reading input", e);
      } catch (IllegalArgumentException e) {
          System.out.println(e.getMessage());
          return getInputBioUnitFromConsole(deck); 
      }
  }
  /**
   * Prompts the user to input a Mode from the console.
   * Retries if the input is invalid.
   *
   * @return The Mode selected by the user.
   */
  public static Mode getInputModeFromConsole() {
  	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      try {
          
          String input = reader.readLine().trim();
          return createMode(input);
      } catch (IOException e) {
          throw new RuntimeException("Error reading input", e);
      } catch (IllegalArgumentException e) {
          System.out.println(e.getMessage());
          return getInputModeFromConsole(); 
      }
  }
  /**
   * Creates a Coordinate object based on the user's input of two integers (x and y).
   *
   * @return A new Coordinate instance.
   */
  public static Coordinate createCoordinate() {
      return new Coordinate(getInputIntegerFromConsole(), getInputIntegerFromConsole());
  }
  /**
   * Prompts the user to input an integer from the console.
   * Retries if the input is invalid.
   *
   * @return The integer entered by the user.
   */
  public static int getInputIntegerFromConsole() {
  	 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  	    try {
  	       
  	        String input = reader.readLine().trim();
  	        return Integer.parseInt(input);
  	    } catch (IOException e) {
  	        throw new RuntimeException("Error reading input", e);
  	    } catch (NumberFormatException e) {
  	        return getInputIntegerFromConsole(); 
  	    }
  }
}