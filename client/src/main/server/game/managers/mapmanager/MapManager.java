package server.game.managers.mapmanager;

import interfaces.PathConstants;
import logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class that retrieves all maps from the default path and constructs Map tempnetworkingobjects from them.
 * Maps should have be stored in the default path as a txt file
 */
public class MapManager implements PathConstants {

    private final List<Map> maps;
    private final List<File> mapFiles;
    private final int mapCellsX;
    private final int mapCellsY;

    private final MapSkinManager msm;

    /**
     * Constructs a map manager from given dimensions
     *
     * @param mapCellsX The X dimensions of the map
     * @param mapCellsY The Y dimensions of the map
     */
    public MapManager(int mapCellsX, int mapCellsY) {

        this.maps = new ArrayList<>();
        this.mapFiles = new ArrayList<>();
        this.msm = new MapSkinManager();
        this.mapCellsX = mapCellsX;
        this.mapCellsY = mapCellsY;

        this.init();
        this.createMaps();
    }

    /* Loads all files  */
    private void init() {

        File folder = new File(MAP_PATH);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            this.mapFiles.add(new File(MAP_PATH + listOfFiles[i].getName()));
        }
    }

    // Creates all maps
    private void createMaps() {

        for (File mapFile : this.mapFiles) {
            try {
                maps.add(new Map(this.mapCellsX, this.mapCellsY, mapFile, this.msm.getMapSkin(msm.getSelectedMap())));
            } catch (IOException e) {
                Logger.debug("No user created map files.");
                e.printStackTrace();
            }

        }
    }

    /**
     * Used to retrieve a Map
     *
     * @param atIndex The index of the map to retrieve
     * @return the map at the supplied index
     */
    public Map getMap(int atIndex) {

        if (atIndex < maps.size()) {
            return this.maps.get(atIndex);
        }
        return null;
    }

    public List<Map> getAllMaps() {

        return this.maps;
    }

    /**
     * @return a List of file names currently loaded
     */
    public List<String> getAllFileNames() {

        List<String> strs = new ArrayList<>();

        for (File mapFile : this.mapFiles) {
            strs.add(mapFile.getName());
        }

        return strs;
    }

}
