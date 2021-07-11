package server.game.managers.snakemanager;

import handlers.FileHandler;
import handlers.FileHandler2;
import interfaces.PathConstants;
import logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class that retrieves all skins from the default path and constructs SnakeSkin tempnetworkingobjects from them.
 * <p>
 * Snake skins should have a body image and a head image.
 * <p>
 * The default skin is made of:
 * skin0body.png
 * skin0head.png
 * <p>
 * All new skins should follow the same naming style with the only toggleButton being the 0 replaced by the skin number.
 */
public class SnakeSkinManager implements PathConstants {

    private final List<SnakeSkin> snakeSkins;
    private final List<String> headFiles;
    private final List<String> bodyFiles;

    /**
     * Constructs a SnakeSkinManager and loads skins into files
     */
    public SnakeSkinManager() {

        this.snakeSkins = new ArrayList<>();
        this.headFiles = new ArrayList<>();
        this.bodyFiles = new ArrayList<>();
        this.init();
        this.createSkins();
    }

    /* Loads all skins into headFiles and bodyFiles */
    private void init() {

        File folder = new File(SNAKE_PATH);
        File[] listOfFiles = folder.listFiles();

        try {

            for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
                if (listOfFiles[i].getName().contains("body")) {
                    this.bodyFiles.add(("file:" + SNAKE_PATH + listOfFiles[i].getName()));
                } else if (listOfFiles[i].getName().contains("head")) {
                    this.headFiles.add(("file:" + SNAKE_PATH + listOfFiles[i].getName()));
                }
            }

        } catch (Exception e) {
            Logger.error("Snake Skin folder not found or empty. Error while initialising map skin manager:");
            e.printStackTrace();
        }

    }

    /* Creates SnakeSkin tempnetworkingobjects from the image files and stores them in the object */
    private void createSkins() {

        for (int i = 0; i < this.headFiles.size(); i++) {

            Logger.debug(headFiles.get(i));
            Logger.debug(bodyFiles.get(i));

            try {
                snakeSkins.add(new SnakeSkin(this.headFiles.get(i), this.bodyFiles.get(i)));
            } catch (Exception e) {
                Logger.error("Missing snake skin files.");
            }

        }
    }

    private List<SnakeSkin> getAllSkins() {

        return this.snakeSkins;
    }

    /**
     * Used to retrieve a skin
     *
     * @param atIndex the index of the skin to retrieve
     * @return the SnakeSkin at the supplied index
     */
    public SnakeSkin getSnakeSkin(int atIndex) {

        if (atIndex < snakeSkins.size()) {
            return this.snakeSkins.get(atIndex);
        }

        return this.snakeSkins.get(3);

    }

    /**
     * returns equipped skin for specified player in multiplayer
     *
     * @param ID the index of the required skin
     * @return the index of the selected skin
     * @throws IOException if the file reader fails to get file content
     */
    public int getSelectedSkin(int ID) throws IOException {

        if (ID == 1) {
            String[] full = (FileHandler.getFileContent()).split(" ");
            String first = full[5].substring(0, 1);
            return Integer.parseInt(first) - 1;
        } else {
            String[] full = (FileHandler2.getFileContent()).split(" ");
            String first = full[5].substring(0, 1);
            return Integer.parseInt(first) - 1;
        }

    }

    /**
     * Returns the currently equipped map
     *
     * @return the index of the selected map
     * @throws IOException if the file reader fails to get file content
     */
    public int getSelectedSkin() throws IOException {

        String[] full = (FileHandler.getFileContent()).split(" ");
        String first = full[5].substring(0, 1);
        return Integer.parseInt(first) - 1;
    }
}