package server.game.managers.mapmanager;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestMapSkinManager {

    JFXPanel panel = new JFXPanel();
    MapSkinManager msm;

    @Test
    public void testInit() {

        assertDoesNotThrow(() -> msm = new MapSkinManager());
    }

    @Test
    public void testGetMapSkin() {

        msm = new MapSkinManager();

        assertNotNull(msm.getMapSkin(2));
        assertNotNull(msm.getMapSkin(4));
    }

    @Test
    public void testGetAllSkins() {

        msm = new MapSkinManager();

        assertEquals(msm.getAllSkins().size(), 4);
    }

    @Test
    public void testGetSelectedMap() throws IOException {

        msm = new MapSkinManager();

        assertEquals(msm.getSelectedMap(), 1);
    }

    @Test
    public void testGetSelectedMapID() throws IOException {

        msm = new MapSkinManager();

        assertEquals(msm.getSelectedMap(3), 5);
    }

    @Test
    public void testClosestBgColor() {

        msm = new MapSkinManager();

        assertEquals(msm.getClosestBackgroundColor().toString(), "0x5682adff");
    }

    @Test
    public void testClosestBgColorID() {

        msm = new MapSkinManager();

        System.out.println(msm.getClosestBackgroundColor(2).toString());

        assertEquals(msm.getClosestBackgroundColor(2).toString(), "0x964b00ff");
    }
}
