package server.interfaces;

import server.game.usables.Coordinate;
import server.game.usables.Direction;

import java.util.List;

public interface AIIF {

    Direction getNextMove(Coordinate[][] board, List<Coordinate> snakeAI, List<Coordinate> snakeP, List<Coordinate> fruits);

}
