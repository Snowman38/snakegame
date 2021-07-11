package server.game.managers.mapmanager;

import interfaces.PathConstants;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestMapManager implements PathConstants {

    JFXPanel panel = new JFXPanel();
    MapManager mapManager;

    private boolean nameInFiles(File[] listOfFiles, String name) {

        for (File file : listOfFiles) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testInitAndCreateMaps() {

        mapManager = new MapManager(30, 30);

        File folder = new File(MAP_PATH);
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        List<String> generatedFileNames = mapManager.getAllFileNames();
        for (String generatedFileName : generatedFileNames) {
            assertTrue(nameInFiles(listOfFiles, generatedFileName));
        }
    }

    @Test
    public void testGetAllFileNames() {

        mapManager = new MapManager(30, 30);
        List<String> generatedFileNames = mapManager.getAllFileNames();
        File folder = new File(MAP_PATH);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (String generatedFileName : generatedFileNames) {
            assertTrue(nameInFiles(listOfFiles, generatedFileName));
        }
    }

    @Test
    public void testGetMap() {

        mapManager = new MapManager(30, 30);

        assertNotNull(mapManager.getMap(1));

        BoxStatus[][] map = mapManager.getMap(1).getMap();

        assertEquals(map[0][0], BoxStatus.EMPTY);
        assertEquals(map[2][3], BoxStatus.WALL);
        assertEquals(map[3][3], BoxStatus.WALL);
    }

    @Test
    public void testGetAllMaps() {

        mapManager = new MapManager(30, 30);

        assertNotNull(mapManager.getAllMaps());
        assertEquals(mapManager.getAllMaps().size(), 8);
    }
}
