package src.main.java.cascadia.score;

import java.util.List;

import src.main.java.cascadia.entities.BioUnit;
import src.main.java.cascadia.utils.Coordinate;


public sealed  interface ScoreBioUnit permits HabitatScore , AnimalScore{
	int countNeighbors();
	void addAllNeighbor(List<Coordinate>coordinates , BioUnit bioUnit);
	boolean isNeighbor(Coordinate coordinate);
	boolean checkCoordinate(List<Coordinate> coordinates);
	String toString();
}
