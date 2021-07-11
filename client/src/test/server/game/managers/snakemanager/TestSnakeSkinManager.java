package server.game.managers.snakemanager;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;
import server.game.managers.mapmanager.MapSkinManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSnakeSkinManager {

    JFXPanel panel = new JFXPanel();
    SnakeSkinManager ssm;

    @Test
    public void testInit() {

        assertDoesNotThrow(() -> ssm = new SnakeSkinManager());
    }

    @Test
    public void testGetMapSkin() {

        ssm = new SnakeSkinManager();
        assertNotNull(ssm.getSnakeSkin(2));
        assertNotNull(ssm.getSnakeSkin(4));
    }

    @Test
    public void testGetSelectedMap() throws IOException {

        ssm = new SnakeSkinManager();

        assertEquals(ssm.getSelectedSkin(), 1);
    }

    @Test
    public void testGetSelectedMapID() throws IOException {

        ssm = new SnakeSkinManager();

        assertEquals(ssm.getSelectedSkin(3), 8);
    }

}
