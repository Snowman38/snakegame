package server.game.managers.snakemanager;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static interfaces.PathConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestSnakeSkin {

    JFXPanel panel = new JFXPanel();
    File folder = new File(SNAKE_PATH);
    File[] listOfFiles = folder.listFiles();
    SnakeSkin ss;

    @Test
    public void testLoadingSkinsA() throws SnakeImageLoadException {

        ArrayList<String> filesForTest = new ArrayList<>();

        for (int i = 0; i < 2; i++) {

            filesForTest.add("file:" + SNAKE_PATH + listOfFiles[i].getName());
        }


        ss = new SnakeSkin(filesForTest.get(0), filesForTest.get(1));

        assertNotNull(ss.getBodyImage());
        assertNotNull(ss.getHeadImage());
    }

    @Test
    public void testLoadingSkinsB() {

        ArrayList<String> filesForTest = new ArrayList<>();

        filesForTest.add("file:" + SNAKE_PATH + listOfFiles[0].getName());
        filesForTest.add("file:" + SNAKE_PATH + "randomNonExistentFileHere.png.jpeg");

        assertThrows(SnakeImageLoadException.class,
                () -> ss = new SnakeSkin(filesForTest.get(0), filesForTest.get(1)),
                "Catches failed skin loading process.");
    }

    @Test
    public void testLoadingSkinsC() {

        ArrayList<String> filesForTest = new ArrayList<>();

        filesForTest.add("file:" + SNAKE_PATH + listOfFiles[0].getName());
        filesForTest.add("DIJIOJE9 Uijkjdnam");

        assertThrows(IllegalArgumentException.class,
                () -> ss = new SnakeSkin(filesForTest.get(0), filesForTest.get(1)),
                "Constructor call failed");
    }
}
