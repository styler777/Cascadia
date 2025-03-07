package src.main.java.cascadia.entities;

import java.util.function.BiPredicate;


public sealed interface BioUnit permits Animal , Habitat , Tile {
	String getName();
	BiPredicate<String, String> isRecognizeInNameImage();
}
