package server.networking;

import interfaces.PathConstants;
import logging.Logger;
import server.game.Fruit;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.powerupsmanager.MultiplayerPowerUp;
import server.game.usables.Coordinate;
import server.interfaces.NetworkingConstants;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

/**
 * A class which sets up a server allowing two users to connect to it and start the game.
 * The server is responsible for retrieving and sending information to and from clients, as well as collision detection.
 * <p>
 * Within this class there are two private classes:
 * ReadClient : This class is responsible for reading information from each client and storing this data in a variable
 * WriteClient : This class is responsible for sending data to the relevant client
 *
 * @author Dilpreet Kang
 */

public class Server implements PathConstants, NetworkingConstants {

    private static ServerSocket serverSocket;
    private static Map map;
    private static int numLives;
    private final CyclicBarrier barrier = new CyclicBarrier(2);
    private final int port = PORT;
    private int numPlayers;
    private int maxNumPlayers;
    private ReadClient player1RC;
    private ReadClient player2RC;
    private WriteClient player1WC;
    private WriteClient player2WC;
    private CopyOnWriteArrayList<Coordinate> p1Coordinates;
    private CopyOnWriteArrayList<Coordinate> p2Coordinates;
    private Fruit fruit;
    private MultiplayerPowerUp powerUp1;
    private MultiplayerPowerUp powerUp2;
    private Boolean invertP1 = false;
    private Boolean invertP2 = false;
    private Boolean finishedInvert1 = false;
    private Boolean finishedInvert2 = false;
    private Boolean freezeP1 = false;
    private Boolean freezeP2 = false;
    private Boolean finishedFreeze1 = false;
    private Boolean finishedFreeze2 = false;
    private Coordinate plantedMine;
    private Boolean collisionOnP1 = true;
    private Boolean collisionOnP2 = true;
    private int snakeCode1;
    private int snakeCode2;
    private Boolean gameEnded = false;

    private Server() {

        initialise();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Logger.error("Issue creating server socket");
        }
    }

    /**
     * Starts the server for clients to connect to
     *
     * @param chosenMap The map selected by the host
     * @param lives     The number of lives for players to start with selected by the host
     * @author Dilpreet Kang
     */

    public static void main(Map chosenMap, int lives) {

        try {
            String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
            int activeServers = Integer.parseInt(splitted[1]) + 1;
            FileWriter myWriter = new FileWriter(TRACKER_PATH);
            myWriter.write(String.valueOf(splitted[0]) + activeServers);
            myWriter.close();
        } catch (IOException e) {
            Logger.error("Error writing to tracker file");
        }

        numLives = lives;
        map = chosenMap;
        Server server = new Server();
        server.startServer();
    }

    /**
     * Closes the server socket as long as the server socket is not null
     *
     * @author Dilpreet Kang
     */

    public static void turnOffServer() {

        try {
            if (serverSocket != null) {
                Logger.debug("server off");
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Issue ending the server");
        }
    }

    //sets default values and initialises map
    private void initialise() {

        numPlayers = 0;
        maxNumPlayers = 2;

        p1Coordinates = new CopyOnWriteArrayList<>();
        p2Coordinates = new CopyOnWriteArrayList<>();
        p1Coordinates.add(new Coordinate(0, 0));
        p2Coordinates.add(new Coordinate(10, 10));
        fruit = new Fruit(map);
        powerUp1 = new MultiplayerPowerUp(map);
        powerUp2 = new MultiplayerPowerUp(map);

    }

    private void startServer() {

        try {

            Logger.debug("Server is listening on port " + port);
            while (numPlayers < maxNumPlayers) {

                Socket newSocket = serverSocket.accept();
                newSocket.setTcpNoDelay(true);
                numPlayers += 1;
                Logger.debug("New user connected");

                ObjectOutputStream objOutput = new ObjectOutputStream(newSocket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(newSocket.getInputStream());

                //the current number of players establishes the players id (1 or 2) and sends it to the client
                objOutput.writeInt(numPlayers);

                ReadClient readFromClient = new ReadClient(numPlayers, objInput);
                WriteClient writeToClient = new WriteClient(numPlayers, objOutput);

                if (numPlayers == 1) {
                    player1RC = readFromClient;
                    player1WC = writeToClient;

                    player1WC.sendMap();
                    player1WC.sendLives();

                    snakeCode1 = player1RC.recieveSnakeCode();

                } else if (numPlayers == 2) {
                    player2RC = readFromClient;
                    player2WC = writeToClient;
                    player2WC.sendMap();
                    player2WC.sendLives();

                    snakeCode2 = player2RC.recieveSnakeCode();

                    player1WC.sendSnakeCodes();
                    player2WC.sendSnakeCodes();
                    player1WC.sendPowerUps();
                    player2WC.sendPowerUps();

                    player1WC.signalReady();
                    player2WC.signalReady();

                    new Thread(player1RC).start();
                    new Thread(player2RC).start();
                    new Thread(player1WC).start();
                    new Thread(player2WC).start();

                }

            }
            Logger.debug("2 Players have connected");

        } catch (IOException e) {
            Logger.error("Multiplayer server cannot be started");
            turnOffServer();
        }
    }

    private boolean checkForFood1() {

        return p1Coordinates.get(0).getX() == fruit.getFruitPos().getX() &&
                p1Coordinates.get(0).getY() == fruit.getFruitPos().getY();
    }

    private boolean checkForFood2() {

        return p2Coordinates.get(0).getX() == fruit.getFruitPos().getX() &&
                p2Coordinates.get(0).getY() == fruit.getFruitPos().getY();
    }

    private boolean checkCollision1() {

        int headX = p1Coordinates.get(0).getX();
        int headY = p1Coordinates.get(0).getY();

        for (Coordinate p2Coordinate : p2Coordinates) {
            if (headX == (p2Coordinate.getX()) && (headY == (p2Coordinate.getY()))) {
                return true;
            }
        }

        if (collisionOnP1 && map.getMap()[headX][headY].equals(BoxStatus.WALL)) {
            return true;
        }

        if (map.getMap()[headX][headY].equals(BoxStatus.PLACED_MINE)) {
            map.setMapValue(new Coordinate(headX, headY), BoxStatus.EMPTY);
            return true;
        }

        return false;

    }

    private boolean checkCollision2() {

        int headX = p2Coordinates.get(0).getX();
        int headY = p2Coordinates.get(0).getY();

        for (Coordinate p1Coordinate : p1Coordinates) {
            if (headX == (p1Coordinate.getX()) && (headY == (p1Coordinate.getY()))) {
                return true;
            }
        }

        if (collisionOnP2 && map.getMap()[headX][headY].equals(BoxStatus.WALL)) {
            return true;
        }

        if (map.getMap()[headX][headY].equals(BoxStatus.PLACED_MINE)) {
            map.setMapValue(new Coordinate(headX, headY), BoxStatus.EMPTY);
            return true;
        }

        return false;
    }

    //reads attributes of current player
    private class ReadClient extends Thread {

        private final int ID;
        private final ObjectInputStream objInput;

        private ReadClient(int ID, ObjectInputStream objInput) {

            this.ID = ID;
            this.objInput = objInput;

        }

        private int recieveSnakeCode() {

            try {
                return objInput.readInt();
            } catch (IOException e) {
                Logger.error("Cannot recieve snake code");
            }
            return 0;
        }

        @SuppressWarnings("unchecked")
        public void run() {

            try {

                while (true) {
                    if (ID == 1) {
                        p1Coordinates = (CopyOnWriteArrayList<Coordinate>) objInput.readObject();

                    } else if (ID == 2) {
                        p2Coordinates = (CopyOnWriteArrayList<Coordinate>) objInput.readObject();
                    }

                    if (objInput.readBoolean()) {
                        powerUp1.despawnPowerUp(map);
                        powerUp1 = new MultiplayerPowerUp(map);
                    }

                    if (objInput.readBoolean()) {
                        powerUp2.despawnPowerUp(map);
                        powerUp2 = new MultiplayerPowerUp(map);
                    }

                    if (ID == 1) {
                        invertP2 = objInput.readBoolean();
                        finishedInvert1 = objInput.readBoolean();
                        freezeP2 = objInput.readBoolean();
                        finishedFreeze1 = objInput.readBoolean();
                        collisionOnP1 = objInput.readBoolean();
                    }

                    if (ID == 2) {
                        invertP1 = objInput.readBoolean();
                        finishedInvert2 = objInput.readBoolean();
                        freezeP1 = objInput.readBoolean();
                        finishedFreeze2 = objInput.readBoolean();
                        collisionOnP2 = objInput.readBoolean();
                    }

                    if (objInput.readBoolean()) {
                        plantedMine = (Coordinate) objInput.readObject();
                        map.setMapValue(plantedMine, BoxStatus.PLACED_MINE);
                    }

                    if (objInput.readBoolean()) {
                        gameEnded = true;
                    }

                    Thread.sleep(0);
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                Logger.error("Cannot read data from client");
            }

        }

    }

    //writes attributes of the other player to the current client
    private class WriteClient extends Thread {

        private final int ID;
        private final ObjectOutputStream objOutput;

        private WriteClient(int ID, ObjectOutputStream objOutput) {

            this.ID = ID;
            this.objOutput = objOutput;
        }

        //tells clients that the game can begin as two players have connected
        private void signalReady() {

            try {
                objOutput.writeByte(0);
                objOutput.flush();
            } catch (IOException e) {
                Logger.error("Cannot signal ready");
            }
        }

        //sends an identical map
        private void sendMap() {

            try {
                objOutput.writeObject(map.getMap());
            } catch (IOException e) {
                Logger.error("Cannot send map");
            }
        }

        //sends the initial number of lives
        private void sendLives() {

            try {
                objOutput.writeObject(numLives);
            } catch (IOException e) {
                Logger.error("Cannot send number of lives");
            }
        }

        //sends each players snake skin code
        private void sendSnakeCodes() {

            try {
                objOutput.writeInt(snakeCode1);
                objOutput.writeInt(snakeCode2);
            } catch (IOException e) {
                Logger.error("Cannot send snake codes");
            }

        }

        private void sendPowerUps() {

            try {
                objOutput.writeObject(powerUp1);
                objOutput.writeObject(powerUp2);
            } catch (IOException e) {
                Logger.error("Cannot send powerups");
            }
        }

        public void run() {

            try {

                while (true) {

                    //barrier used to ensure both threads are in sync
                    barrier.await();
                    boolean collision1 = checkCollision1();
                    boolean collision2 = checkCollision2();
                    boolean foodCheck1 = checkForFood1();
                    boolean foodCheck2 = checkForFood2();
                    barrier.reset();

                    if (ID == 1) {

                        objOutput.writeObject(p2Coordinates);

                        objOutput.writeBoolean(collision2);

                        objOutput.writeBoolean(collision1);

                        objOutput.writeBoolean(foodCheck2);

                        if (foodCheck1) {
                            objOutput.writeBoolean(true);
                            fruit.despawnFruit(map);
                            fruit.spawnRandomFruit(map);

                        } else objOutput.writeBoolean(false);

                        objOutput.writeObject(powerUp1);
                        objOutput.writeObject(powerUp2);

                        objOutput.writeBoolean(invertP1);
                        objOutput.writeBoolean(finishedInvert2);

                        objOutput.writeBoolean(freezeP1);
                        objOutput.writeBoolean(finishedFreeze2);
                        sendMap();

                    } else if (ID == 2) {

                        objOutput.writeObject(p1Coordinates);

                        objOutput.writeBoolean(collision1);

                        objOutput.writeBoolean(collision2);

                        objOutput.writeBoolean(foodCheck1);

                        if (foodCheck2) {
                            objOutput.writeBoolean(true);
                            fruit.despawnFruit(map);
                            fruit.spawnRandomFruit(map);
                        } else objOutput.writeBoolean(false);

                        objOutput.writeObject(powerUp1);
                        objOutput.writeObject(powerUp2);

                        objOutput.writeBoolean(invertP2);
                        objOutput.writeBoolean(finishedInvert1);

                        objOutput.writeBoolean(freezeP2);
                        objOutput.writeBoolean(finishedFreeze1);
                        sendMap();
                    }

                    objOutput.writeBoolean(gameEnded);

                    if (gameEnded) serverSocket.close();
                    objOutput.reset();

                    //helps with lag
                    Thread.sleep(130);
                }

            } catch (IOException | InterruptedException | BrokenBarrierException e) {
                Logger.error("Cannot write data to client");
            }
        }

    }

}