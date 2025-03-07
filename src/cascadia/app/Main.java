package cascadia.app;
import java.io.IOException;

import src.main.java.cascadia.ui.GameSession;
import src.main.java.cascadia.utils.Message;



public class Main {

	public static void main(String[] args) throws IOException {
		int nbTurn = 0;
		var display1D = GameSession.playDisplay();
		System.out.println(display1D);
		var play = GameSession.playInput();
		display1D.changeMessage(Message.COORDINATE);
		play = GameSession.initTextual(play); 
		System.out.println(play);
		for (nbTurn = 1; nbTurn < 20;) {
			System.out.println(display1D);
			nbTurn += play.playGame(nbTurn);
			System.out.println(play);
			
		}
		System.out.println(play.endTextual(display1D));
		System.out.println(play);
	}
}

