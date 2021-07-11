package handlers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import logging.Logger;
import server.ai.Difficulty;
import server.game.Fruit;
import server.game.managers.mapmanager.Map;
import server.game.managers.mapmanager.MapManager;
import server.game.managers.mapmanager.MapSkinManager;
import server.game.managers.powerupsmanager.PowerUp;
import server.game.managers.snakemanager.AISnake;
import server.game.managers.snakemanager.PlayerSnake;
import server.game.managers.snakemanager.Snake;
import server.game.managers.snakemanager.SnakeSkinManager;
import server.game.usables.Direction;

/**
 * The main singleplayer class, handling behaviour, rendering, render timing and game updates for the client. It also
 * controls UI functionality.
 */
public class SinglePlayer implements PathConstants, GameConstants {

    //handleFruit method uses this
    public static Label balance;
    private Stage window;
    
    private int canvasOriginX;
    private int canvasOriginY;

    private static Group group;
    private Snake playerSnake;
    private Snake aiSnake;
    private final Text t = new Text();
    private Text countdownText;
    private StackPane countdownTextPane;
    private Timer countDownTimer;
    private MapSkinManager mapSkinManager;
    private SnakeSkinManager snakeSkinManager;
    private Map map;
    private Fruit fruit;
    private PowerUp powerUp1;
    private PowerUp powerUp2;
    private HBox playerLifeBox;
    private HBox aiLifeBox;
    private static Text powerUpText;
    private static ImageView powerUpBox;
    private int currentPlayerLives;
    private int currentAILives;
    private Difficulty difficulty;
    private GraphicsContext gc;
    // This thread renders the game
    private final Thread renderThread = new Thread() {

        @Override
        public void run() {

            map.drawMap(gc, SQUARE_SIZE);
            playerSnake.renderSnake(gc, SQUARE_SIZE);
            aiSnake.renderSnake(gc, SQUARE_SIZE);

        }
    };
    private boolean paused;
    private Direction lastPlayerDirection;
    // This thread updates the game
    private final Thread gameThread = new Thread() {

        @Override
        public void run() {

            if (lastPlayerDirection != null) {
                playerSnake.setDirection(lastPlayerDirection);
                lastPlayerDirection = null;
            }
            map.drawMap(gc, SQUARE_SIZE);

            if (!aiSnake.freezeOtherPlayer()) {
                handleSnakeFreeze(playerSnake, aiSnake);
                playerSnake.updateSnake();
            } else {
                aiSnake.freezeOtherPlayer();
            }

            if (!playerSnake.freezeOtherPlayer()) {
                handleSnakeFreeze(aiSnake, playerSnake);
                aiSnake.updateSnake(playerSnake, fruit.getFruitPos());
            } else {
                playerSnake.freezeOtherPlayer();
            }

            if (playerSnake.isDead() || aiSnake.isDead()) {

                playerSnake.setLives(INIT_LIVES);
                aiSnake.setLives(INIT_LIVES);

                timer.stop();

                AudioController.endMusic();
                AudioController.startMusic("menuMusic.mp3");
                Util.getWindow(group.getScene(), "EndScreen");
            }

            if(playerSnake.getLives() != currentPlayerLives){
                currentPlayerLives = playerSnake.getLives();
                updateLifeBox();
            }

            if(aiSnake.getLives() != currentAILives){
                currentAILives = aiSnake.getLives();
                updateLifeBox();
            }

        }
    };
    // This timer controls game updates and rendering every n nanoseconds, while started until stopped
    private final AnimationTimer timer = new AnimationTimer() {

        // Time wanted between updates, in nanoseconds
        private final long UPDATE_INTERVAL_NS = (long) (UPDATE_INTERVAL * Math.pow(10, 6));

        // Time of last call in nanoseconds
        private long lastThreadTimeNs = 0;

        @Override
        public void handle(long currentThreadTimeNs) {

            // Update and render if the below statement is true
            if (currentThreadTimeNs - lastThreadTimeNs >= UPDATE_INTERVAL_NS) {
                lastThreadTimeNs = currentThreadTimeNs;

                /* The order of updating and rendering here shouldn't matter here,
                 as they are handled concurrently in their own threads */
                gameThread.run();
                renderThread.run();
            }
        }
    };

    // Handles the freezing of a snake
    private void handleSnakeFreeze(Snake aiSnake, Snake playerSnake) {

        try {
            aiSnake.handlePowerUp(powerUp1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aiSnake.handlePowerUp(powerUp2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aiSnake.handleFruit(fruit, playerSnake);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called on game start, initializes the game
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getGame(ActionEvent event) {

        this.mapSkinManager = new MapSkinManager();
        this.snakeSkinManager = new SnakeSkinManager();

        //retreives the selected skin from the shop
        int selectedMapSkin;
        int selectedSnakeSkin;

        try {
            selectedMapSkin = mapSkinManager.getSelectedMap();
        } catch (IOException e) {
            selectedMapSkin = 3;
        }

        try {
            selectedSnakeSkin = snakeSkinManager.getSelectedSkin();
        } catch (IOException e) {
            selectedSnakeSkin = 3;
        }

        this.map = new Map(MAP_CELLS_X, MAP_CELLS_Y, mapSkinManager.getMapSkin(selectedMapSkin));
        this.fruit = new Fruit(map);
        this.powerUp1 = new PowerUp(map);
        this.powerUp2 = new PowerUp(map);

        this.playerSnake = new PlayerSnake(SPEED, map, snakeSkinManager.getSnakeSkin(selectedSnakeSkin), INIT_LIVES);
        this.aiSnake = new AISnake(SPEED, map, snakeSkinManager.getSnakeSkin(4), INIT_LIVES, this.difficulty);

        this.paused = false;
        this.lastPlayerDirection = null;

        this.canvasOriginX = Math.floorDiv(X_SCREEN_WIDTH, 2) - Math.floorDiv(MAP_CELLS_X * SQUARE_SIZE, 2);
        this.canvasOriginY = Math.floorDiv(Y_SCREEN_WIDTH, 2) - Math.floorDiv(MAP_CELLS_Y * SQUARE_SIZE, 2);

        this.currentPlayerLives = playerSnake.getLives();
        this.currentAILives = aiSnake.getLives();

        window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        start(window);

    }

    /**
     * Initializes an the game with easy difficulty
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getGameEasy(ActionEvent event) {

        t.setText("Easy Mode");
        AudioController.clickSound();
        this.difficulty = Difficulty.EASY;
        Snake.valueOfFruit = 10;
        getGame(event);
    }

    /**
     * Initializes an the game with medium difficulty
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getGameMedium(ActionEvent event) {

        t.setText("Medium Mode");
        t.setTranslateX(500);
        AudioController.clickSound();
        this.difficulty = Difficulty.MEDIUM;
        Snake.valueOfFruit = 25;
        getGame(event);
    }

    /**
     * Initializes an the game with hard difficulty
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getGameHard(ActionEvent event) {

        t.setText("Hard Mode");
        AudioController.clickSound();
        this.difficulty = Difficulty.HARD;
        Snake.valueOfFruit = 50;
        getGame(event);
    }

    /**
     * Loads the difficulty selection screen
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getSinglePlayer(ActionEvent event) {

        this.timer.stop();
        Util.getWindow(event, "SinglePlayer");
    }

    /**
     * Loads the main menu
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getMain(ActionEvent event) {

        this.timer.stop();
        AudioController.endMusic();
        AudioController.startMusic("menuMusic.mp3");
        Util.getWindow(event, "MainMenu");
    }

    private void updateLifeBox(){

        this.playerLifeBox.getChildren().clear();

        for(int i = 0; i < this.playerSnake.getLives(); i++){
            ImageView lifeImageView = new ImageView(new Image(IMAGE_PATH + "life.png"));
            lifeImageView.setFitWidth(40);
            lifeImageView.setFitHeight(40);
            this.playerLifeBox.getChildren().add(lifeImageView);
        }

        this.aiLifeBox.getChildren().clear();

        for(int i = 0; i < this.aiSnake.getLives(); i++){
            ImageView lifeImageView = new ImageView(new Image(IMAGE_PATH + "life.png"));
            lifeImageView.setFitWidth(40);
            lifeImageView.setFitHeight(40);
            this.aiLifeBox.getChildren().add(lifeImageView);
        }
    }

    private void initLifeBox(){

        this.playerLifeBox = new HBox();
        this.aiLifeBox = new HBox();
        this.playerLifeBox.setSpacing(10);
        this.aiLifeBox.setSpacing(10);
        this.playerLifeBox.setTranslateX(canvasOriginX + 900);
        this.aiLifeBox.setTranslateX(canvasOriginX + 900);
        this.playerLifeBox.setTranslateY(canvasOriginY + 150);
        this.aiLifeBox.setTranslateY(canvasOriginY + 250);
        this.playerLifeBox.setAlignment(Pos.CENTER_LEFT);
        this.aiLifeBox.setAlignment(Pos.CENTER_LEFT);

        Text playerLifeText = new Text("Player Lives");
        playerLifeText.setFont(new Font("System", 30));
        playerLifeText.setFill(Color.WHITE);
        playerLifeText.setTextAlignment(TextAlignment.LEFT);
        playerLifeText.setTranslateX(canvasOriginX + 900);
        playerLifeText.setTranslateY(canvasOriginY + 125);

        group.getChildren().add(playerLifeText);


        Text aiLifeText = new Text("AI Lives");
        aiLifeText.setFont(new Font("System", 30));
        aiLifeText.setFill(Color.WHITE);
        aiLifeText.setTextAlignment(TextAlignment.LEFT);
        aiLifeText.setTranslateX(canvasOriginX + 900);
        aiLifeText.setTranslateY(canvasOriginY + 225);

        group.getChildren().add(aiLifeText);

        // Initialise the heart view for each player
        for(int i = 0; i < this.playerSnake.getLives(); i++){
            ImageView lifeImageView = new ImageView(new Image(IMAGE_PATH + "life.png"));
            lifeImageView.setFitWidth(40);
            lifeImageView.setFitHeight(40);
            this.playerLifeBox.getChildren().add(lifeImageView);
        }

        for(int i = 0; i < this.aiSnake.getLives(); i++){
            ImageView lifeImageView = new ImageView(new Image(IMAGE_PATH + "life.png"));
            lifeImageView.setFitWidth(40);
            lifeImageView.setFitHeight(40);
            this.aiLifeBox.getChildren().add(lifeImageView);
        }

        group.getChildren().add(this.playerLifeBox);
        group.getChildren().add(this.aiLifeBox);
    }

    private void initCanvas(){
        Canvas canvas = new Canvas(X_SCREEN_WIDTH, Y_SCREEN_WIDTH);

        // Translates the canvas to the center of the screen
        canvas.setTranslateX(canvasOriginX);
        canvas.setTranslateY(canvasOriginY);

        gc = canvas.getGraphicsContext2D();
        gc.setEffect(new Bloom(0.8));

        group.getChildren().add(canvas);
    }

    private void initBalance(){
        balance = new Label("Balance: " + FileHandler.getBalance());
        balance.setFont(new Font("System", 30));
        balance.setTextFill(Color.WHITE);
        group.getChildren().add(balance);
        balance.setTranslateX(canvasOriginX + 900);
        balance.setTranslateY(canvasOriginY - 25);
        balance.setMinSize(200, 80);
    }

    private void initCountdownScreen(){
        this.countdownText = new Text();
        countdownText.setFont(Font.font("Verdana", 200));
        countdownText.setFill(Color.WHITE);
        countdownText.setTextAlignment(TextAlignment.CENTER);
        countdownText.setEffect(new Glow(1.0f));

        this.countdownTextPane = new StackPane();
        countdownTextPane.setAlignment(Pos.CENTER);
        countdownTextPane.setMinWidth(X_SCREEN_WIDTH);
        countdownTextPane.setMinHeight(Y_SCREEN_WIDTH);
        countdownTextPane.getChildren().add(countdownText);

        countdownTextPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

        group.getChildren().add(countdownTextPane);

        this.countDownTimer = new Timer();
        countDownTimer.scheduleAtFixedRate(new TimerTask() {

            private int currentCountdownTime = 3;

            @Override
            public void run() {

                if (currentCountdownTime == 0) {
                    AudioController.gameStartSound();

                    // Select a song based on the selected difficulty
                    String songName = switch (difficulty) {
                        case EASY -> "gameMusicEasy.mp3";
                        case MEDIUM -> "gameMusicMedium.mp3";
                        case HARD -> "gameMusicHard.mp3";
                    };

                    AudioController.startMusic(songName);
                    countDownTimer.cancel();
                    timer.start();
                    countdownTextPane.setVisible(false);
                    return;
                }

                countdownTextPane.setVisible(true);
                // Temporarily draw a map over the screen while the countdown is active
                map.drawMap(gc, SQUARE_SIZE);
                countdownText.setText(String.valueOf(currentCountdownTime));
                currentCountdownTime--;
                AudioController.endMusic();
                AudioController.countDownSound();

            }
        }, 0, 1000);
    }

    private void initBackButton(){
        Button back = new Button("Back");
        back.setTranslateX(canvasOriginX - 250);
        back.setTranslateY(canvasOriginY);
        back.setMinSize(200, 80);
        back.setFocusTraversable(false);
        back.setOnAction(event -> {
            countDownTimer.cancel();
            gameThread.interrupt();
            renderThread.interrupt();
            timer.stop();
            AudioController.endMusic();
            AudioController.startMusic("menuMusic.mp3");
            getSinglePlayer(event);
        });
        group.getChildren().add(back);
    }

    private void initChooseMapButton(){
        ChoiceBox<String> cb = new ChoiceBox<>();
        group.getChildren().add(cb);
        cb.setTranslateX(canvasOriginX - 250);
        cb.setTranslateY(canvasOriginY + 300);
        cb.setMinSize(200, 40);
        cb.setFocusTraversable(false);
        MapManager mm = new MapManager(30, 30);
        String[] array = mm.getAllFileNames().toArray(new String[0]);
        for (String s : array) {
            cb.getItems().add(s);
        }
        cb.setValue("Choose Custom Map");
        cb.setOnAction(event -> {
            String value = cb.getValue();

            for (int i = 0; i < cb.getItems().size(); i++) {
                if (cb.getItems().get(i).equals(value)) {
                    timer.stop();
                    getGame(event);
                    map = mm.getMap(i);

                    fruit.despawnFruit(map);
                    fruit.spawnRandomFruit(map);
                    powerUp1.despawnPowerUp(map);
                    powerUp2.despawnPowerUp(map);
                    powerUp1.spawnPowerUp(map);
                    powerUp2.spawnPowerUp(map);
                    aiSnake.setMap(map);
                    aiSnake.setLives(INIT_LIVES);
                    aiSnake.changeSpawn();
                    aiSnake.respawnSnake();
                    playerSnake.setMap(map);
                    playerSnake.setLives(INIT_LIVES);
                    playerSnake.changeSpawn();
                    playerSnake.respawnSnake();
                    updatePowerUpBox(null);
                    break;
                }
            }

        });
    }

    private void initResetButton(){
        Button reset = new Button("Reset");
        group.getChildren().add(reset);
        reset.setTranslateX(canvasOriginX - 250);
        reset.setTranslateY(canvasOriginY + 100);
        reset.setMinSize(200, 80);
        reset.setFocusTraversable(false);
        reset.setOnAction(event -> {
            map.generateMap();
            map.generateRandomWalls();
            fruit.despawnFruit(map);
            fruit.spawnRandomFruit(map);
            powerUp1.despawnPowerUp(map);
            powerUp2.despawnPowerUp(map);
            powerUp1.spawnPowerUp(map);
            powerUp2.spawnPowerUp(map);
            aiSnake.setLives(INIT_LIVES);
            aiSnake.changeSpawn();
            aiSnake.respawnSnake();
            playerSnake.setLives(INIT_LIVES);
            playerSnake.changeSpawn();
            playerSnake.respawnSnake();

            timer.stop();
            getGame(event);
        });
    }

    private void initPauseButton(){
        Button pause = new Button("Pause");
        group.getChildren().add(pause);
        pause.setTranslateX(canvasOriginX - 250);
        pause.setTranslateY(canvasOriginY + 200);
        pause.setMinSize(200, 80);
        pause.setFocusTraversable(false);
        pause.setOnAction(event -> {
            this.paused = !this.paused;
            if (paused) {
                pause.setText("Unpause");
                timer.stop();
            } else {
                pause.setText("Pause");
                timer.start();
            }
        });
    }

    private void initPowerUpBox(){

        powerUpText = new Text("Power Up");
        powerUpText.setVisible(false);
        powerUpText.setFont(new Font("System", 30));
        powerUpText.setFill(Color.WHITE);
        powerUpText.setTranslateX(canvasOriginX + 900);
        powerUpText.setTranslateY(canvasOriginY + 350);

        group.getChildren().add(powerUpText);

        powerUpBox = new ImageView();
        powerUpBox.setFitWidth(100);
        powerUpBox.setFitHeight(100);
        powerUpBox.setTranslateX(canvasOriginX + 900);
        powerUpBox.setTranslateY(canvasOriginY + 375);

        group.getChildren().add(powerUpBox);
    }

    /**
     * Updates the power up display (Called by PowerUp.java
     * @param image the power up image to render
     */
    public static void updatePowerUpBox(Image image){
        powerUpText.setVisible(image != null);
        powerUpBox.setImage(image);
    }

    /**
     * Called during game initialisation
     *
     * @param primaryStage The main game rendering stage
     */
    public void start(Stage primaryStage) {

        try {

            group = new Group();

            initCanvas();
            initBalance();
            initLifeBox();
            initBackButton();
            initResetButton();
            initPauseButton();
            initChooseMapButton();
            initPowerUpBox();
            initCountdownScreen();


            Scene scene = new Scene(group, X_SCREEN_WIDTH, Y_SCREEN_WIDTH, mapSkinManager.getClosestBackgroundColor());
            scene.getStylesheets().add(getClass().getResource(CSS_PATH + "style.css").toExternalForm());

            // Handles movement for player snake
            scene.setOnKeyPressed(event -> {
                Direction inputDirection;

                switch (event.getCode()) {
                    case RIGHT -> inputDirection = Direction.RIGHT;
                    case LEFT -> inputDirection = Direction.LEFT;
                    case UP -> inputDirection = Direction.UP;
                    case DOWN -> inputDirection = Direction.DOWN;
                    case SPACE -> {
                        if (playerSnake.hasMine()) {

                            playerSnake.setMine();
                            Logger.debug("Used setMine");
                            updatePowerUpBox(null);
                            AudioController.powerupSound();
                        }
                        else if (playerSnake.hasFreeze()) {
                            playerSnake.initFreeze();
                            Logger.debug("Used Freeze");
                            updatePowerUpBox(null);
                            AudioController.powerupSound();
                        }
                        else if (playerSnake.hasWallSkipper()) {
                            playerSnake.initWallSkipper();
                            Logger.debug("Used WallSkip");
                            updatePowerUpBox(null);
                            AudioController.powerupSound();
                        }
                        else if (playerSnake.hasControlsInverter()) {
                            playerSnake.initControlsInverter();
                            Logger.debug("Used Controls");
                            updatePowerUpBox(null);
                            AudioController.powerupSound();
                        }
                        else if (playerSnake.hasSpeedUp()) {
                            playerSnake.speedUp();
                            Logger.debug("Used speedUp");
                            updatePowerUpBox(null);
                            AudioController.powerupSound();
                        }
                        else {
                            Logger.debug("No PowerUp");
                        }

                        if (powerUp1.getCoordinate() == null) {
                            powerUp1.spawnPowerUp(map);
                        } else if (powerUp2.getCoordinate() == null) {
                            powerUp2.spawnPowerUp(map);
                        }
                        return;
                    }
                    default -> {
                        return;
                    }
                }

                if (!playerSnake.isOppositeDirection(inputDirection)) {
                    this.lastPlayerDirection = inputDirection;
                }
            });

            primaryStage.setScene(scene);
            primaryStage.show();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
