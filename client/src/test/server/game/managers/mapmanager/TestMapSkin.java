package server.game.managers.mapmanager;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static interfaces.PathConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestMapSkin {

    JFXPanel panel = new JFXPanel();
    MapSkin ms;

    @Test
    public void testLoadingSkinsA() throws MapImageLoadException {

        File folder = new File(MAP_SKIN_PATH);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> filesForTest = new ArrayList<>();

        for (int i = 0; i < 9; i++) {

            filesForTest.add("file:" + MAP_SKIN_PATH + listOfFiles[i].getName());
        }


        ms = new MapSkin(filesForTest.get(0), filesForTest.get(1), filesForTest.get(2), filesForTest.get(3), filesForTest.get(4), filesForTest.get(5),
                filesForTest.get(6), filesForTest.get(7), filesForTest.get(8));

        assertNotNull(ms.getBackgroundSkin());
        assertNotNull(ms.getCoinSkin());
        assertNotNull(ms.getMineSkin());
        assertNotNull(ms.getReverseSkin());
        assertNotNull(ms.getSkipSkin());
        assertNotNull(ms.getSpeedSkin());
        assertNotNull(ms.getWallSkin());
        assertNotNull(ms.getFreezeSkin());
        assertNotNull(ms.getFruitSkin());
    }

    @Test
    public void testLoadingSkinsB() throws MapImageLoadException {

        File folder = new File(MAP_SKIN_PATH);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> filesForTest = new ArrayList<>();

        for (int i = 0; i < 8; i++) {

            filesForTest.add("file:" + MAP_SKIN_PATH + listOfFiles[i].getName());
        }

        filesForTest.add("file:" + MAP_SKIN_PATH + "randomNonExistentFileHere.png.jpeg");

        assertThrows(MapImageLoadException.class,
                () -> ms = new MapSkin(filesForTest.get(0), filesForTest.get(1), filesForTest.get(2), filesForTest.get(3), filesForTest.get(4), filesForTest.get(5),
                        filesForTest.get(6), filesForTest.get(7), filesForTest.get(8)),
                "Checking for error while loading skins.");
    }

    @Test
    public void testLoadingSkinsC()  {

        File folder = new File(MAP_SKIN_PATH);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> filesForTest = new ArrayList<>();

        for (int i = 0; i < 8; i++) {

            filesForTest.add("file:" + MAP_SKIN_PATH + listOfFiles[i].getName());
        }

        filesForTest.add("DIJIOJE9 Uijkjdnam");

        assertThrows(IllegalArgumentException.class,
                () -> ms = new MapSkin(filesForTest.get(0), filesForTest.get(1), filesForTest.get(2), filesForTest.get(3), filesForTest.get(4), filesForTest.get(5),
                        filesForTest.get(6), filesForTest.get(7), filesForTest.get(8)),
                "Constructor call failed");
    }
}
