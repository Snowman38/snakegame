package server.game.managers.mapmanager;

import interfaces.PathConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logging.Logger;
import server.game.usables.Coordinate;
import server.game.usables.Direction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class holds the status of the map, as well as it's dimensions and selected skin
 */
public class Map implements PathConstants {

    private int xSize;
    private int ySize;

    private BoxStatus[][] mapMatrix;
    private MapSkin skin;

    /**
     * Generates a map
     *
     * @param xSize The x Dimension of the map
     * @param ySize The y dimension of the map
     * @param file  The file containing the map data
     */
    public Map(int xSize, int ySize, File file) {

        this.xSize = xSize;
        this.ySize = ySize;
        this.mapMatrix = readMap(file);
    }

    /**
     * Generates a map
     *
     * @param xSize The x Dimension of the map
     * @param ySize The y dimension of the map
     * @param file  The file containing the map data
     * @param skin  The MapSkin to construct the map with
     */
    public Map(int xSize, int ySize, File file, MapSkin skin) {

        this.xSize = xSize;
        this.ySize = ySize;
        this.mapMatrix = readMap(file);
        this.skin = skin;
    }

    /**
     * Generates a map
     *
     * @param xSize The x Dimension of the map
     * @param ySize The y dimension of the map
     * @param skin  The MapSkin to construct the map with
     */
    public Map(int xSize, int ySize, MapSkin skin) {

        this.xSize = xSize;
        this.ySize = ySize;
        this.skin = skin;

        generateMap();
        generateRandomWalls();
    }

    /**
     * Generates a map
     *
     * @param xSize The x Dimension of the map
     * @param ySize The y dimension of the map
     */
    public Map(int xSize, int ySize) {

        this.xSize = xSize;
        this.ySize = ySize;

        generateMap();
        generateRandomWalls();

        //try {
        //this.skin = new MapSkin(MAP_SKIN_URL, FOOD_SKIN_URL, WALL_SKIN_URL, COIN_SKIN_URL, FREEZE_SKIN_URL, REVERSE_SKIN_URL, SKIP_SKIN_URL, SPEED_SKIN_URL, MINE_SKIN_URL);
        //} catch (MapImageLoadException e) {
        // Logger.debug("Missing default map skin files.");
        //}

    }

    /* Reads a map from a .txt file for later use */
    private BoxStatus[][] readMap(File file) {

        BoxStatus[][] matrix = new BoxStatus[xSize][ySize];

        try {

            FileReader fReader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fReader);
            Scanner scanner = new Scanner(bReader);

            while (scanner.hasNextLine()) {

                for (int y = 0; y < xSize; y++) {

                    String[] line = scanner.nextLine().trim().split(" ");

                    for (int x = 0; x < line.length; x++) {

                        String mapStringValue = line[x];
                        matrix[x][y] = stringToStatus(mapStringValue);
                    }
                }
            }

            scanner.close();
            return matrix;
        } catch (FileNotFoundException e) {
            Logger.error("Failed to read map in Map.java");
            e.printStackTrace();
        }

        return null;
    }

    // Converts from the text data in the map file to a box status
    private BoxStatus stringToStatus(String squareString) {

        switch (squareString) {
            case "1":
                return BoxStatus.PLAYER_A;
            case "2":
                return BoxStatus.PLAYER_B;
            case "/":
                return BoxStatus.WALL;
            case "0":
                return BoxStatus.EMPTY;
            case "F":
                return BoxStatus.FRUIT;
            case "C":
                return BoxStatus.COIN;
            case "FR":
                return BoxStatus.FREEZE;
            case "R":
                return BoxStatus.REVERSE;
            case "SK":
                return BoxStatus.SKIP;
            case "SP":
                return BoxStatus.SPEED;
            case "M":
                return BoxStatus.MINE;
            default:
                Logger.error("Error while evaluating stringToStatus() in Map class");
                return null;
        }
    }

    // Checks whether a coordinate is in range of the dimensions of the map
    private boolean isValidCoordinate(Coordinate coord) {

        int x = coord.getX();
        int y = coord.getY();

        return x >= 0 &&
                x < this.xSize &&
                y >= 0 &&
                y < this.ySize;
    }

    //Generates random wall blocks for testing

    /* Checks if the randomly chosen coordinate is valid (is not out of bounds or has a Wall object nearby) */
    private boolean checkValidRandomCoordinate(Coordinate coord) {

        for (int i = coord.getX() - 1; i < coord.getX() + 1; i++) {

            for (int j = coord.getY() - 1; j < coord.getY() + 1; j++) {

                if (i != coord.getX() && j != coord.getY() &&
                        isValidCoordinate(new Coordinate(i, j))) {

                    if (mapMatrix[i][j] == BoxStatus.WALL) {

                        return false;
                    }
                }
            }
        }

        return true;
    }

    /* Checks all the four possible directions before randomly assigning one.
        If one of the surrounding Coordinates is a Wall object then that direction is invalid (so that the map doesn't
        create dead ends).
     */
    // TODO - optimise this
    private ArrayList<Coordinate> validDirections(Coordinate coord) {

        ArrayList<Coordinate> availableCoordinates = new ArrayList<>();
        Coordinate newCoord = new Coordinate(coord.getX(), coord.getY() - 1);
        ArrayList<Coordinate> validCoordinates = new ArrayList<>();

        /* Case left */

        if (isValidCoordinate(newCoord) &&
                mapMatrix[newCoord.getX()][newCoord.getY()] != BoxStatus.WALL) {

            //TODO: make the next few lines more efficient in all cases

            validCoordinates.add(new Coordinate(newCoord.getX() - 2, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 2, newCoord.getY()));

            if (validCoordinates(validCoordinates)) {

                availableCoordinates.add(newCoord);
                // Logger.debug("Added case left");
            }
            validCoordinates.clear();
        }

        /* Case right */

        newCoord = new Coordinate(coord.getX(), coord.getY() + 1);

        if (isValidCoordinate(newCoord) &&
                mapMatrix[newCoord.getX()][newCoord.getY()] != BoxStatus.WALL) {

            validCoordinates.add(new Coordinate(newCoord.getX() - 2, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 2, newCoord.getY()));

            if (validCoordinates(validCoordinates)) {

                availableCoordinates.add(newCoord);
                //Logger.debug("Added case right");
            }
            validCoordinates.clear();
        }

        /* Case up */

        newCoord = new Coordinate(coord.getX() - 1, coord.getY());

        if (isValidCoordinate(newCoord) &&
                mapMatrix[newCoord.getX()][newCoord.getY()] != BoxStatus.WALL) {

            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() + 2));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() - 2));

            if (validCoordinates(validCoordinates)) {

                availableCoordinates.add(newCoord);
                //Logger.debug("Added case up");
            }
            validCoordinates.clear();
        }

        /* Case down */

        newCoord = new Coordinate(coord.getX() + 1, coord.getY());

        if (isValidCoordinate(newCoord) &&
                mapMatrix[newCoord.getX()][newCoord.getY()] != BoxStatus.WALL) {

            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() + 2));
            validCoordinates.add(new Coordinate(newCoord.getX() - 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() - 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY()));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX() + 1, newCoord.getY() + 1));
            validCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY() - 2));

            if (validCoordinates(validCoordinates)) {

                availableCoordinates.add(newCoord);
                //Logger.debug("Added case down");
            }
            validCoordinates.clear();
        }

        return availableCoordinates;
    }

    /* Checks if the given set of coordinates contains a Wall object */
    private boolean validCoordinates(ArrayList<Coordinate> coordinates) {

        /* used to remove out of bounds coordinates */
        for (int i = 0; i < coordinates.size(); i++) {

            if (!isValidCoordinate(coordinates.get(i))) {

                coordinates.remove(i);
                i--;
            }
        }

        for (Coordinate coordinate : coordinates) {

            if (mapMatrix[coordinate.getX()][coordinate.getY()] == BoxStatus.WALL) {

                return false;
            }
        }

        return true;
    }

    /* Creates an image 2D array that will be drawn later on screen */
    private Image[][] skinMap() {

        Image[][] imagedMap = new Image[xSize][ySize];

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {

                BoxStatus status = mapMatrix[x][y];

                switch (status) {
                    case WALL -> imagedMap[x][y] = this.skin.getWallSkin();
                    case FRUIT -> imagedMap[x][y] = this.skin.getFruitSkin();
                    case COIN -> imagedMap[x][y] = this.skin.getCoinSkin();
                    case FREEZE -> imagedMap[x][y] = this.skin.getFreezeSkin();
                    case REVERSE -> imagedMap[x][y] = this.skin.getReverseSkin();
                    case SKIP -> imagedMap[x][y] = this.skin.getSkipSkin();
                    case SPEED -> imagedMap[x][y] = this.skin.getSpeedSkin();
                    case MINE -> imagedMap[x][y] = this.skin.getMineSkin();
                    default -> imagedMap[x][y] = this.skin.getBackgroundSkin();
                }
            }
        }

        return imagedMap;
    }

    /*
    Generates an empty map
     */
    public void generateMap() {

        BoxStatus[][] map = new BoxStatus[xSize][ySize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                map[i][j] = BoxStatus.EMPTY;
            }
        }
        this.mapMatrix = map;
    }

    /**
     * Generates random walls in the map
     */
    public void generateRandomWalls() {

        BoxStatus[][] map = this.mapMatrix;

        Random rand = new Random();

        int approxNoOfWalls = 80;
        int lengthOfWall;
        int directionRand;
        int xRand;
        int yRand;
        Coordinate addedCoord;
        ArrayList<Coordinate> availableDirections;
        int x;
        int y;

        for (int i = 0; i < approxNoOfWalls; i++) {

            x = rand.nextInt(xSize - 1);
            y = rand.nextInt(ySize - 1);
            addedCoord = new Coordinate(x, y);

            if (checkValidRandomCoordinate(addedCoord)) {

                mapMatrix[x][y] = BoxStatus.WALL;
                lengthOfWall = rand.nextInt(20);

                for (int j = 0; j < lengthOfWall; j++) {

                    availableDirections = validDirections(addedCoord);

                    if (availableDirections.size() > 0) {

                        directionRand = rand.nextInt(availableDirections.size());
                        xRand = availableDirections.get(directionRand).getX();
                        yRand = availableDirections.get(directionRand).getY();

                        mapMatrix[xRand][yRand] = BoxStatus.WALL;
                        addedCoord = new Coordinate(xRand, yRand);
                        i++;
                    } else {

                        j = lengthOfWall;
                    }
                }
            } else {

                i--;
            }
        }

        this.mapMatrix = map;
    }

    /* Draws the map on the screen with its given skins */
    public void drawMap(GraphicsContext gc, int squareSize) {

        Image[][] images = this.skinMap();

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                gc.drawImage(images[x][y], x * squareSize,
                        y * squareSize, squareSize, squareSize);
            }
        }
    }

    public int getX() {

        return xSize;
    }

    public void setX(int xSize) throws Exception {

        if(xSize > 0) {
            this.xSize = xSize;
        }
        else {
            throw new Exception("Invalid map length.");
        }
    }

    public int getY() {

        return ySize;
    }

    public void setY(int ySize) throws Exception {

        if(ySize > 0) {
            this.ySize = ySize;
        }
        else {
            throw new Exception("Invalid map width.");
        }
    }

    public BoxStatus getMapValue(Coordinate coord) {

        return mapMatrix[coord.getX()][coord.getY()];
    }

    public BoxStatus getMapValue(int x, int y) {

        return mapMatrix[x][y];
    }

    public void setMapValue(Coordinate coord, BoxStatus value) {

        mapMatrix[coord.getX()][coord.getY()] = value;
    }

    public BoxStatus[][] getMap() {

        return mapMatrix;
    }

    public void setMatrix(BoxStatus[][] matrix) {

        this.mapMatrix = matrix;
    }

    /**
     * Initialises a MapSkinManager and sets the current map skin to the provided one
     *
     * @param skin The index of the skin required
     */
    public void setSkin(int skin) {

        MapSkinManager msm = new MapSkinManager();
        this.skin = msm.getMapSkin(skin);
    }

}
