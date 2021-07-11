package server.game.managers.mapmanager;

import handlers.FileHandler;
import handlers.FileHandler2;
import interfaces.PathConstants;
import javafx.scene.paint.Color;
import logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class that retrieves all skins from the default path and constructs MapSkin tempnetworkingobjects from them.
 * <p>
 * Map skins should have a background image, a food image, a powerup image, and a wall image.
 * <p>
 * The default skin is made of:
 * skin0background.png
 * skin0food.png
 * skin0wall.png
 * skin0powerup.png
 * <p>
 * All new skins should follow the same naming style with the only toggleButton being the 0 replaced by the skin number.
 */
public class MapSkinManager implements PathConstants {

    private final List<MapSkin> mapSkins;
    private final List<String> backgroundFiles;
    private final List<String> foodFiles;
    private final List<String> wallFiles;
    private final List<String> coinFiles;
    private final List<String> freezeFiles;
    private final List<String> reverseFiles;
    private final List<String> skipFiles;
    private final List<String> speedFiles;
    private final List<String> mineFiles;

    /**
     * Constructs a MapSkinManager and loads skins into files
     */
    public MapSkinManager() {

        this.mapSkins = new ArrayList<>();
        this.backgroundFiles = new ArrayList<>();
        this.foodFiles = new ArrayList<>();
        this.wallFiles = new ArrayList<>();
        this.coinFiles = new ArrayList<>();
        this.freezeFiles = new ArrayList<>();
        this.reverseFiles = new ArrayList<>();
        this.skipFiles = new ArrayList<>();
        this.speedFiles = new ArrayList<>();
        this.mineFiles = new ArrayList<>();
        this.init();
        this.createSkins();
    }

    /* Loads all skins into backgroundFiles, foodFiles, wallFiles */
    private void init() {

        File folder = new File(MAP_SKIN_PATH);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            if (listOfFiles[i].getName().contains("background")) {
                this.backgroundFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("food")) {
                this.foodFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("wall")) {
                this.wallFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("coin")) {
                this.coinFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("freeze")) {
                this.freezeFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("reverse")) {
                this.reverseFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("skip")) {
                this.skipFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("speed")) {
                this.speedFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            } else if (listOfFiles[i].getName().contains("mine")) {
                this.mineFiles.add(("file:" + MAP_SKIN_PATH + listOfFiles[i].getName()));
            }
        }
    }

    /* Creates MapSkins tempnetworkingobjects from the image files and stores them in the object */
    private void createSkins() {

        for (int i = 0; i < this.backgroundFiles.size(); i++) {
            try {
                mapSkins.add(new MapSkin(this.backgroundFiles.get(i), this.foodFiles.get(i), this.wallFiles.get(i), this.coinFiles.get(i), this.freezeFiles.get(i), this.reverseFiles.get(i), this.skipFiles.get(i), this.speedFiles.get(i), this.mineFiles.get(i)));
            } catch (MapImageLoadException e) {
                Logger.error("Missing map skin files.");
                e.printStackTrace();
            }

        }
    }

    // Returns a colour from a map index
    private Color getColor(int mapIndex) {

        Color[] mapColors = new Color[]{Color.web("#3B3B3B"), Color.web("#5682AD"), Color.web("#F8B8B8")};
        if (mapIndex == 0 || mapIndex == 1 || mapIndex == 2) {
            return mapColors[mapIndex];
        } else {
            return Color.web("964B00");
        }
    }

    /**
     * Used to retrieve a skin
     *
     * @param atIndex the index of the skin to retrieve
     * @return the MapSkin at the supplied index
     */
    public MapSkin getMapSkin(int atIndex) {

        if (atIndex < mapSkins.size()) {
            return this.mapSkins.get(atIndex);
        }
        try {
            return this.mapSkins.get(3);
        } catch (Exception e) {
            Logger.error("the default map skin should be called skin3 and is not found");
        }
        return null;

    }

    /* returns all loaded skins */
    public List<MapSkin> getAllSkins() {

        return this.mapSkins;
    }

    /**
     * Returns the currently equipped map
     *
     * @return the index of the selected map
     * @throws IOException if the file reader fails to get file content
     */
    public int getSelectedMap() throws IOException {

        String[] full = (FileHandler.getFileContent()).split(" ");
        String second = full[5].substring(1, 2);
        return Integer.parseInt(second) - 4;
    }

    /**
     * returns equipped map for specified player in multiplayer
     *
     * @param ID the index of the required map
     * @return the index of the selected map
     * @throws IOException if the file reader fails to get file content
     */
    public int getSelectedMap(int ID) throws IOException {

        if (ID == 1) {
            String[] full = (FileHandler.getFileContent()).split(" ");
            String second = full[5].substring(1, 2);
            return Integer.parseInt(second) - 4;
        } else {
            String[] full = (FileHandler2.getFileContent()).split(" ");
            String second = full[5].substring(1, 2);
            return Integer.parseInt(second) - 4;
        }
    }

    /**
     * Returns 0,1,2 for shop maps
     * If 5 is returned, then the DEFAULT map is used (original map, not paid for)
     *
     * @return a colour to use as a surrounding background based on the selected map
     */
    public Color getClosestBackgroundColor() {

        int mapIndex;
        try {
            mapIndex = getSelectedMap();
        } catch (IOException e) {
            return Color.web("2E4052");
        }

        return getColor(mapIndex);
    }

    /**
     * Returns 0,1,2 for shop maps
     * If 5 is returned, then the DEFAULT map is used (original map, not paid for)
     *
     * @param ID the id of the map to retrieve
     * @return a colour to use as a surrounding background based on the selected map
     */
    public Color getClosestBackgroundColor(int ID) {

        int mapIndex;
        if (ID == 1 || ID == 2) {
            try {
                mapIndex = getSelectedMap(ID);
                return getColor(mapIndex);
            } catch (IOException ignored) {
            }
        }
        return Color.web("2E4052");
    }
}