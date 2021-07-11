package interfaces;

/**
 * Interface used to store path constants (e.g /client/src/main/...)
 */
public interface PathConstants {

    String RESOURCES_PATH = "client/src/main/resources/";

    String ROOT_MEDIA_PATH = RESOURCES_PATH + "audio/";

    String MUSIC_PATH = ROOT_MEDIA_PATH + "music/";

    String EFFECTS_PATH = ROOT_MEDIA_PATH + "effects/";

    String STORAGE_PATH = RESOURCES_PATH + "storage/";

    String DATA_PATH = STORAGE_PATH + "gamedata.txt";

    String DATA_PATH2 = STORAGE_PATH + "gamedata2.txt";

    String TRACKER_PATH = STORAGE_PATH + "tracker.txt";

    String FXML_PATH = "/resources/fxml/";

    String IMAGE_PATH = "/resources/img/";

    String VIDEO_PATH = "/resources/vid/";

    String CSS_PATH = "/resources/css/";

    String FONT_PATH = "/resources/font/";

    String ROOT_PATH = "client/src/main/server/resources";

    String MAP_SKIN_PATH = ROOT_PATH + "/mapskins/";
    String SNAKE_PATH = ROOT_PATH + "/snakeskins/";
    String MAP_PATH = ROOT_PATH + "/maps/";

    String MAP_SKIN_URL = MAP_SKIN_PATH + "skin0background.png";
    String FOOD_SKIN_URL = MAP_SKIN_PATH + "skin0food.png";
    String WALL_SKIN_URL = MAP_SKIN_PATH + "skin0wall.png";
    String COIN_SKIN_URL = MAP_SKIN_PATH + "skin0coin.png";
    String FREEZE_SKIN_URL = MAP_SKIN_PATH + "skin0freeze.png";
    String REVERSE_SKIN_URL = MAP_SKIN_PATH + "skin0reverse.png";
    String SKIP_SKIN_URL = MAP_SKIN_PATH + "skin0skip.png";
    String SPEED_SKIN_URL = MAP_SKIN_PATH + "skin0speed.png";
    String MINE_SKIN_URL = MAP_SKIN_PATH + "skin0mine.png";

    String SNAKE_HEAD_URL = SNAKE_PATH + "skin0head.png";
    String BODY_SKIN_URL = SNAKE_PATH + "skin0body.png";


}
