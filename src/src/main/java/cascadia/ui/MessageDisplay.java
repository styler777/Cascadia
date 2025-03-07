package src.main.java.cascadia.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import src.main.java.cascadia.utils.Message;



/**
 * The MessageDisplay class is responsible for managing and displaying messages
 * for different game states such as start, coordinate display, and end.
 */
public class MessageDisplay {
	private Message message;
	private final ArrayList<String> texts;
	 /**
   * Constructs a MessageDisplay instance with the given initial message.
   * @param message the initial message type to be displayed.
   */
	public MessageDisplay(Message message) {
		Objects.requireNonNull(message);
		this.texts = new ArrayList<>();
		this.message = message;
		
	}
	
	 /**
   * Returns a predicate that filters message text based on the message type
   * and index range.
   * @return a BiPredicate for filtering messages.
   */
	public static BiPredicate<Message, Integer> messagePredicateForIndex() {
	    return (msg, nb) -> {
	        return switch (msg) {
	            case START -> nb >= 0 && nb <= 33;
	            case COORDINATE -> nb >= 38 && nb <= 47;
	            case END ->nb >= 50 && nb <= 52;
	            case NOTHING -> true;
			
	        };
	    };
	}
	
	 /**
   * Filters a list of strings based on the message type and a given predicate.
   * @param text the list of message strings to filter.
   * @param message the message type to filter by.
   * @param predicate the filtering condition based on message type and index.
   * @return a list of filtered message strings.
   */
	public static List<String> filterMessages(List<String> text  , Message message, BiPredicate<Message , Integer> predicate) {
		Objects.requireNonNull(text);
		Objects.requireNonNull(predicate);
		Objects.requireNonNull(message);
		return  text.stream().filter(s -> predicate.test(message, text.indexOf(s))).toList();

	}
  /**
   * Loads a list of strings from a file at the given path.
   * @param path the path of the file to load.
   * @return a list of strings loaded from the file.
   * @throws IOException if an error occurs while reading the file.
   */
	public static List<String> load(Path path) throws IOException {
	   
	    Objects.requireNonNull(path, "path is null");
	    try (var stream = Files.lines(path)) {
	        return stream.map(s -> s).toList();
	              
	    }
	}
	
	/**
   * Displays a list of text messages by adding them to the internal list of texts.
   * @param textFile the list of text messages to display.
   */
	public void displayText(List<String> textFile) {
		Objects.requireNonNull(textFile);
		texts.addAll(textFile);
		
	}
	

  /**
   * Changes the current message type.
   * @param message the new message type to set.
   */
	public void changeMessage(Message message) {
		Objects.requireNonNull(message);
		this.message = message;

	}
	/**
   * Returns a string representation of the current message, filtered by the
   * message type and indices.
   * @return the string representation of the current message.
   */
	
	@Override
	public String toString() {
		
		return switch (message) {
		case START ->MessageDisplay.filterMessages(texts, Message.START, MessageDisplay.messagePredicateForIndex() ).stream().collect(Collectors.joining("\n", "", "\n")).toString();
		case COORDINATE ->MessageDisplay.filterMessages(texts, Message.COORDINATE, MessageDisplay.messagePredicateForIndex() ).stream().collect(Collectors.joining("\n", "", "\n")).toString();
		case END -> MessageDisplay.filterMessages(texts, Message.END, MessageDisplay.messagePredicateForIndex() ).stream().collect(Collectors.joining("\n", "", "\n")).toString();
		case NOTHING -> "";
		};
	}


}
