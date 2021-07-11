package server.networking;

import handlers.AudioController;
import handlers.FileHandler;
import handlers.FileHandler2;
import handlers.MultiPlayer;
import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logging.Logger;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.mapmanager.MapImageLoadException;
import server.game.managers.mapmanager.MapSkinManager;
import server.game.managers.powerupsmanager.MultiplayerPowerUp;
import server.game.managers.snakemanager.SnakeSkinManager;
import server.game.usables.Coordinate;
import server.game.usables.Direction;
import server.interfaces.NetworkingConstants;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A class which enables a user to connect to a running server, through the use of a socket.
 * <p>
 * Within this class there are two private classes:
 * ReadServer : This class is responsible for retrieving data from the server through the use of input streams.
 * If necessary this class may also perform actions as a result of this data.
 * <p>
 * WriteServer: This class is responsible for writing data to the server through the use of output streams.
 *
 * @author Dilpreet Kang
 */
public class Client implements PathConstants, NetworkingConstants, GameConstants {

    private static final SnakeSkinManager ssm = new SnakeSkinManager();
    private static final Label balance1 = new Label("Balance: " + FileHandler.getBalance());
    private static final Label balance2 = new Label("Balance: " + FileHandler2.getBalance());
    private static Map map = new Map(30, 30);
    private static Snake snake1 = new Snake(1, 3, new Coordinate(0, 0), map, ssm.getSnakeSkin(0));
    private static Snake snake2 = new Snake(1, 3, new Coordinate(10, 10), map, ssm.getSnakeSkin(0));
    private static int snakeSkinCode1 = 0;
    private static int snakeSkinCode2 = 0;
    private static Boolean start = false;
    private final int squareSize = SQUARE_SIZE;
    //method to update the number of lives of the current player on the users screen
    private final int upperLeftX = X_TRANSLATE + 350;
    private final int upperLeftY = Y_TRANSLATE + 100;
    private MapSkinManager msm;
    private Socket socket;
    private ReadServer player1RS;
    private WriteServer player1WS;
    private ReadServer player2RS;
    private WriteServer player2WS;
    private int ID;
    private GraphicsContext gc;
    private Timeline timeline;
    private MultiplayerPowerUp powerUp1;
    private MultiplayerPowerUp powerUp2;
    private Boolean changeControls = false;
    private Boolean invertOtherPlayer = false;
    private Boolean invertThisPlayer = false;
    private Boolean finishedInvertOther = false;
    private Boolean finishedInvertThis = false;
    private Boolean freezeControls = false;
    private Boolean freezeThisPlayer = false;
    private Boolean freezeOtherPlayer = false;
    private Boolean finishedFreezeOther = false;
    private Boolean finishedFreezeThis = false;
    private Coordinate plantedMineCoord = null;
    private Boolean plantedMine = false;
    private Boolean collisionOn = true;
    private Boolean gameEnded = false;
    private int numLives = NO_OF_LIVES;
    private int initialLives = 3;
    private Color backgroundColor = Color.web("964B00");
    private int counter = 0;
    private int counter2 = 0;
    private int counter3 = 0;
    private Font pixelFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH + "PressStart2P-Regular.ttf"), 50);
    private Font pixelFont2 = Font.loadFont(getClass().getResourceAsStream(FONT_PATH + "PressStart2P-Regular.ttf"), 25);

    /**
     * Establishes a connection with the server and starts the rendering of the client
     *
     * @param stage The stage that the game should be displayed on
     * @author Dilpreet Kang
     */

    public static void main(Stage stage) {

        Client client = new Client();
        client.connection();
        if (start == true) {
            Platform.runLater(() -> {
                try {
                    client.start(stage);
                } catch (MapImageLoadException e) {
                    Logger.error("Client cannot be started");
                }
            });
        }
    }

    //creates the socket used to communicate with the server, as well as input/output streams to retrieve initial data from the server
    private void connection() {

        try {
            start = false;
            socket = new Socket(HOSTNAME, PORT);
            socket.setTcpNoDelay(true);

            ObjectInputStream objInput = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objOutput = new ObjectOutputStream(socket.getOutputStream());

            //the server has sent the players id and here we read it
            ID = objInput.readInt();

            //here the clients and server are able to retrieve and send data needed before the game starts e.g. the map being used
            if (ID == 1) {
                player1RS = new ReadServer(objInput);
                player1WS = new WriteServer(objOutput);

                getMap().setMatrix(player1RS.getMap());

                numLives = player1RS.getLives();
                initialLives = numLives;
                int selectedSnakeSkin;
                try {

                    selectedSnakeSkin = ssm.getSelectedSkin(1);
                } catch (IOException e) {
                    selectedSnakeSkin = 3;
                }

                player1WS.sendSnakeCode(selectedSnakeSkin);
                int snakeCode1 = objInput.readInt();
                int snakeCode2 = objInput.readInt();
                setSnakeCode1(snakeCode1);
                setSnakeCode2(snakeCode2);

                powerUp1 = (MultiplayerPowerUp) objInput.readObject();
                powerUp2 = (MultiplayerPowerUp) objInput.readObject();

                //blocks until player 1 is ready so clients are rendered at the same time
                player1RS.ready();
                new Thread(player1RS).start();
                new Thread(player1WS).start();

                begin(ID);

            } else if (ID == 2) {

                player2RS = new ReadServer(objInput);
                player2WS = new WriteServer(objOutput);

                getMap().setMatrix(player2RS.getMap());
                numLives = player2RS.getLives();
                initialLives = numLives;

                int selectedSnakeSkin;
                try {
                    selectedSnakeSkin = ssm.getSelectedSkin(2);
                } catch (IOException e) {
                    selectedSnakeSkin = 3;
                }

                player2WS.sendSnakeCode(selectedSnakeSkin);
                int snakeCode1 = objInput.readInt();
                int snakeCode2 = objInput.readInt();
                setSnakeCode1(snakeCode2);
                setSnakeCode2(snakeCode1);

                powerUp1 = (MultiplayerPowerUp) objInput.readObject();
                powerUp2 = (MultiplayerPowerUp) objInput.readObject();

                //block until player 2 is ready so neither GUI will show up until this point
                player2RS.ready();

                new Thread(player2RS).start();
                new Thread(player2WS).start();

                begin(ID);
            }

            start = true;
        } catch (IOException | ClassNotFoundException e) {
            Logger.error("Client connection with server cannot be made");
        }

    }

    //this method is responsible for rendering the map and snake, as well as listening for keyboard inputs
    private void start(Stage primaryStage) throws MapImageLoadException {

        try {
            Group root = new Group();
            Canvas canvas = new Canvas(X_SCREEN_WIDTH, Y_SCREEN_WIDTH);
            canvas.setTranslateX(X_TRANSLATE - 100);
            canvas.setTranslateY(Y_TRANSLATE - 50);

            gc = canvas.getGraphicsContext2D();

            //sets up the back button so the user can return to the main menu
            MultiPlayer multi = new MultiPlayer();
            Button back = new Button("Leave Game");
            root.getChildren().add(back);
            back.setTranslateX(100);
            back.setTranslateY(150);
            back.setMinSize(200, 80);
            back.setFocusTraversable(false);
            back.setOnAction(event -> {
                gameEnded = true;

                try {
                    String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
                    FileWriter myWriter = new FileWriter(TRACKER_PATH);
                    myWriter.write(splitted[0] + "0");
                    myWriter.close();

                } catch (IOException e) {
                    Logger.error("Error writing to tracker file");
                }

                multi.getMain(event);

            });

            root.getChildren().add(canvas);

            Coordinate spawnSnake1 = snake1.spawnSnake();
            Coordinate spawnSnake2 = snake2.spawnSnake();

            //initialise player 1 window
            if (ID == 1) {
                snake1 = new Snake(1, numLives, spawnSnake1, map, ssm.getSnakeSkin(snakeSkinCode1));
                snake2 = new Snake(1, numLives, spawnSnake2, map, ssm.getSnakeSkin(snakeSkinCode2));

                this.msm = new MapSkinManager();
                int selectedMapSkin;

                try {
                    selectedMapSkin = msm.getSelectedMap(1);
                } catch (IOException e) {
                    selectedMapSkin = 3;
                }

                map.setSkin(selectedMapSkin);

                backgroundColor = msm.getClosestBackgroundColor(1);

                balance1.setFont(new Font("System", 30));
                balance1.setTextFill(Color.WHITE);
                root.getChildren().add(balance1);
                balance1.setTranslateX(X_TRANSLATE + 900);
                balance1.setTranslateY(Y_TRANSLATE - 25);
                balance1.setMinSize(200, 80);

            }

            //initialise player 2 window
            else if (ID == 2) {
                snake1 = new Snake(1, numLives, spawnSnake2, map, ssm.getSnakeSkin(snakeSkinCode1));
                snake2 = new Snake(1, numLives, spawnSnake1, map, ssm.getSnakeSkin(snakeSkinCode2));

                this.msm = new MapSkinManager();
                int selectedMapSkin;

                try {
                    selectedMapSkin = msm.getSelectedMap(2);
                } catch (IOException e) {
                    selectedMapSkin = 3;
                }

                map.setSkin(selectedMapSkin);

                backgroundColor = msm.getClosestBackgroundColor(2);

                balance2.setFont(new Font("System", 30));
                balance2.setTextFill(Color.WHITE);
                root.getChildren().add(balance2);
                balance2.setTranslateX(X_TRANSLATE + 900);
                balance2.setTranslateY(Y_TRANSLATE - 25);
                balance2.setMinSize(200, 80);
            }

            Scene scene = new Scene(root, 1550, 800, backgroundColor);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH + "style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.setOnKeyPressed(event -> {
                //controls toggleButton of direction based on input, follows snake rules: you can't go left if you're going right at the moment etc
                KeyCode key = event.getCode();
                if (key == KeyCode.RIGHT) {
                    if (changeControls && snake1.getDirection() != Direction.RIGHT) {
                        snake1.setDirection(Direction.LEFT);
                    } else if (snake1.getDirection() != Direction.LEFT) {
                        snake1.setDirection(Direction.RIGHT);
                    }
                } else if (key == KeyCode.LEFT) {
                    if (changeControls && snake1.getDirection() != Direction.LEFT) {
                        snake1.setDirection(Direction.RIGHT);
                    } else if (snake1.getDirection() != Direction.RIGHT) {
                        snake1.setDirection(Direction.LEFT);
                    }
                } else if (key == KeyCode.UP) {
                    if (changeControls && snake1.getDirection() != Direction.UP) {
                        snake1.setDirection(Direction.DOWN);
                    } else if (snake1.getDirection() != Direction.DOWN) {
                        snake1.setDirection(Direction.UP);
                    }
                } else if (key == KeyCode.DOWN) {
                    if (changeControls && snake1.getDirection() != Direction.DOWN) {
                        snake1.setDirection(Direction.UP);
                    } else if (snake1.getDirection() != Direction.UP) {
                        snake1.setDirection(Direction.DOWN);
                    }
                }

                //controls what happens if the user tries to use a powerup
                else if (key == KeyCode.SPACE) {
                    if (snake1.hasMine()) {
                        AudioController.powerupSound();
                        snake1.setMine(false);
                        plantedMine = true;
                        plantedMineCoord = snake1.getSnakeBodyCoordinates().get(snake1.getSnakeBodyCoordinates().size() - 1);
                        Logger.debug("Used setMine");
                    } else if (snake1.hasFreeze()) {
                        AudioController.powerupSound();
                        snake1.setFreeze(false);
                        freezeOtherPlayer = true;
                        finishedFreezeOther = false;
                        Logger.debug("Used Freeze");
                    } else if (snake1.hasWallSkipper()) {
                        AudioController.powerupSound();
                        snake1.setSkip(false);
                        collisionOn = false;
                        Logger.debug("Used WallSkip");
                    } else if (snake1.hasControlsInverter()) {
                        AudioController.powerupSound();
                        snake1.setInvert(false);
                        invertOtherPlayer = true;
                        finishedInvertOther = false;
                        Logger.debug("Used Controls");
                    } else if (snake1.hasSpeedUp()) {
                        AudioController.powerupSound();
                        snake1.speedUp();
                        Logger.debug("Used speedUp");
                    } else {
                        Logger.debug("No PowerUp");
                    }
                }
            });

            timeline = new Timeline(new KeyFrame(Duration.millis(NetworkingConstants.UPDATE_INTERVAL), e -> play(gc)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

        } catch (Exception e) {
            Logger.error("Issue starting multiplayer GUI");
        }
    }

    //the main game loop which is responsible for rendering and checking client side attributes
    private void play(GraphicsContext gc) {

        map.drawMap(gc, squareSize);
        snake1.drawSnake(gc, squareSize);
        snake2.drawSnake(gc, squareSize);
        displayLives(gc);
        displayPowerup(gc);

        try {
            checkGameEnded(gc);
            checkCoin();
            checkInversion();
            checkFreeze();
            checkWallSkip();
        } catch (IOException e) {
            Logger.error("Issue handling powerups");
        }

        if (ID == 1) {
            balance1.setText("Balance: " + FileHandler.getBalance());
        } else {
            balance2.setText("Balance: " + FileHandler2.getBalance());
        }
    }

    private void displayLives(GraphicsContext gc) {

        gc.setFill(backgroundColor);
        gc.fillRect(upperLeftX, upperLeftY, 600, 60);
        int snakeLives = snake1.getLives();
        int incrementer = 75;
        int counter = 0;
        for (int i = 0; i < snakeLives; i++) {
            Image life = new Image(IMAGE_PATH + "life.png");
            gc.drawImage(life, upperLeftX + (incrementer * i), upperLeftY, 60, 60);
            counter = i + 1;
        }

        for (int j = 0; j < (initialLives - snakeLives); j++) {
            Image nullLife = new Image(IMAGE_PATH + "life_null.png");
            gc.drawImage(nullLife, upperLeftX + (incrementer * counter), upperLeftY, 60, 60);
            counter++;
        }

    }
    
    private void displayPowerup(GraphicsContext gc) {
    	int xCoord = 800;
    	int yCoord = 600;
    	int size = 150;
    	gc.fillRect(xCoord, yCoord, size, size);
    	
    	if (snake1.hasControlsInverter()) {
    		Image reverse= new Image(IMAGE_PATH + "reverse.png");
    		gc.drawImage(reverse, xCoord ,yCoord,size,size);
    	}
    	else if (snake1.hasFreeze()) {
    		Image freeze = new Image(IMAGE_PATH + "freeze.png");
    		gc.drawImage(freeze, xCoord , yCoord,size,size);
    	}
    	else if (snake1.hasMine()) {
    		Image mine = new Image(IMAGE_PATH + "mine.png");
    		gc.drawImage(mine, xCoord , yCoord,size,size);
    	}
    	else if (snake1.hasSpeedUp()) {
    		Image speed = new Image(IMAGE_PATH + "speed.png");
    		gc.drawImage(speed, xCoord , yCoord,size,size);
    	}
    	else if (snake1.hasWallSkipper()) {
    		Image skip = new Image(IMAGE_PATH + "skip.png");
    		gc.drawImage(skip, xCoord , yCoord,size,size);
    	}
    }

    //method to communicate win/lose/draw with the client
    private void checkGameEnded(GraphicsContext gc) {
        if(getSnake1().isDead() || getSnake2().isDead() || gameEnded) {
        	int xPosition = 750;
        	int yPosition = 400;
        	
        	if (getSnake1().isDead() && !getSnake2().isDead()) {
        		gc.setFill(Color.RED);
        		gc.setFont(pixelFont);
        		gc.fillText("YOU LOSE", xPosition, yPosition);
        	}
        	
        	else if (getSnake2().isDead() && !getSnake1().isDead()) {
        		
                if (ID == 1) FileHandler.updateBalance(WIN_PRIZE);
                else FileHandler2.changeBalance(WIN_PRIZE);
        		
        		gc.setFill(Color.FORESTGREEN);
        		gc.setFont(pixelFont);
        		gc.fillText("YOU WIN!", xPosition, yPosition);
        	}
        	
        	else if (!getSnake2().isDead() && !getSnake1().isDead()) {
        		gc.setFill(Color.WHEAT);
        		gc.setFont(pixelFont2);
        		gc.fillText("A PLAYER HAS LEFT", xPosition, yPosition);
        	}
        	
        	timeline.stop();
        	try {
				socket.close();
				Server.turnOffServer();
			} catch (IOException e) {
				Logger.error("Issue closing client socket");
			}
        }
    }

    private void checkCoin() throws IOException {

        if (snake1.hasCoin()) {
            Random rand = new Random();
            int balancePrize = rand.nextInt(500);

            if (ID == 1) {
                FileHandler.updateBalance(balancePrize);
            } else {
                FileHandler2.changeBalance(balancePrize);
            }

            snake1.setCoin(false);
            Logger.debug("Received prize " + balancePrize);
        }

    }

    private void checkInversion() {

        if (invertThisPlayer) {
            if (counter == 0) {
                changeControls = true;
                snake1.initControlsInverter();
                counter++;
            } else if (!snake1.invertControls()) {
                counter--;
                changeControls = false;
                invertThisPlayer = false;
                finishedInvertThis = true;
            }

        } else {
            finishedInvertThis = false;
            counter = 0;
            changeControls = false;
            invertThisPlayer = false;
        }
    }

    private void checkFreeze() {

        if (freezeControls) snake1.setSpeed(0);
        else snake1.setSpeed(1);

        if (freezeThisPlayer) {
            if (counter2 == 0) {
                freezeControls = true;
                snake1.setSpeed(0);
                snake1.initFreeze();
                counter2++;
            } else if (!snake1.freezeOtherPlayer()) {
                snake1.setSpeed(1);
                counter2--;
                freezeControls = false;
                freezeThisPlayer = false;
                finishedFreezeThis = true;
            }
        } else {
            finishedFreezeThis = false;
            counter2 = 0;
            freezeControls = false;
            freezeThisPlayer = false;
        }
    }

    private void checkWallSkip() {

        if (!collisionOn) {
            if (counter3 == 0) {
                collisionOn = false;
                snake1.initWallSkipper();
                counter3++;
            } else if (!snake1.skipWall()) {
                collisionOn = true;
                counter3--;
            }

        } else {
            collisionOn = true;
            counter3 = 0;
        }
    }
    


    private void begin(int id) {

        ID = id;
    }

    //reads data from server about both players
    private class ReadServer extends Thread {

        private final ObjectInputStream objInput;

        private ReadServer(ObjectInputStream objInput) {

            this.objInput = objInput;
        }

        private void ready() {

            try {
                objInput.readByte();
            } catch (IOException e) {
                Logger.error("Cannot signal ready");
            }
        }

        private BoxStatus[][] getMap() throws ClassNotFoundException, IOException {

            return (BoxStatus[][]) objInput.readObject();
        }

        private Integer getLives() throws ClassNotFoundException, IOException {

            return (Integer) objInput.readObject();
        }

        @SuppressWarnings("unchecked")
        public void run() {

            try {
                while (true) {
                    if (getSnake2() != null) {

                        getSnake2().setSnakeBodyCoordinates((CopyOnWriteArrayList<Coordinate>) objInput.readObject());

                        //checks if the other player has collided
                        if (objInput.readBoolean()) {
                            getSnake2().shrink(1);
                            getSnake2().respawnSnake();
                        }

                        //checks if current player has collided
                        if (objInput.readBoolean()) {
                            AudioController.hitWallSound();
                            getSnake1().shrink(1);
                            getSnake1().respawnSnake();
                        }

                        //checks if the other player has eaten food
                        if (objInput.readBoolean()) {
                            getSnake2().grow(1);
                            getSnake1().shrink(1);
                        }

                        //checks if current player has eaten food
                        if (objInput.readBoolean()) {
                            AudioController.consumeSound();
                            if (ID == 1) {
                                FileHandler.updateBalance(APPLE_COIN_VALUE);
                            } else {
                                FileHandler2.changeBalance(APPLE_COIN_VALUE);
                            }
                            getSnake1().grow(1);
                            getSnake2().shrink(1);
                        }

                        //obtains any updates to the state of each powerup
                        powerUp1 = (MultiplayerPowerUp) objInput.readObject();
                        powerUp2 = (MultiplayerPowerUp) objInput.readObject();

                        //checks whether this player needs their controls inverted
                        invertThisPlayer = objInput.readBoolean();
                        finishedInvertOther = objInput.readBoolean();
                        if (finishedInvertOther) invertOtherPlayer = false;

                        //checks whether this player need to be freezed
                        freezeThisPlayer = objInput.readBoolean();
                        finishedFreezeOther = objInput.readBoolean();
                        if (finishedFreezeOther) freezeOtherPlayer = false;

                        map.setMatrix((BoxStatus[][]) objInput.readObject());
                        if (objInput.readBoolean()) gameEnded = true;
                        Thread.sleep(0);
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                Logger.error("Cannot read from server");
            }
        }

    }

    //writes this players attributes to the server
    private class WriteServer extends Thread {

        private final ObjectOutputStream objOutput;

        private WriteServer(ObjectOutputStream objOutput) {

            this.objOutput = objOutput;
        }

        private void sendSnakeCode(int code) {

            try {
                objOutput.writeInt(code);
                objOutput.flush();
            } catch (IOException e) {
                Logger.error("Cannot send snake codes");
            }
        }

        public void run() {

            try {
                while (true) {
                    if (getSnake1() != null) {
                        objOutput.writeObject(getSnake1().getSnakeBodyCoordinates());

                        objOutput.writeBoolean(getSnake1().handlePowerUp(powerUp1));

                        objOutput.writeBoolean(getSnake1().handlePowerUp(powerUp2));

                        objOutput.writeBoolean(invertOtherPlayer);
                        objOutput.writeBoolean(finishedInvertThis);

                        objOutput.writeBoolean(freezeOtherPlayer);
                        objOutput.writeBoolean(finishedFreezeThis);

                        objOutput.writeBoolean(collisionOn);

                        objOutput.writeBoolean(plantedMine);
                        if (plantedMine) {
                            objOutput.writeObject(plantedMineCoord);
                            plantedMine = false;
                        }

                        objOutput.writeBoolean(gameEnded);
                        objOutput.reset();
                        //helps with lag
                        Thread.sleep(10);
                    }
                }
            } catch (IOException | InterruptedException e) {
                Logger.error("Cannot write to server");
            }
        }

    }

    public Snake getSnake1() {

        return snake1;
    }

    public void setSnake1(Snake snake) {

        Client.snake1 = snake;
    }

    public Snake getSnake2() {

        return snake2;
    }

    public void setSnake2(Snake snake) {

        Client.snake2 = snake;
    }

    public Map getMap() {

        return Client.map;
    }

    public void setMap(Map map) {

        Client.map = map;
    }

    public void setSnakeCode1(int code) {

        Client.snakeSkinCode1 = code;
    }

    public void setSnakeCode2(int code) {

        Client.snakeSkinCode2 = code;
    }

}
