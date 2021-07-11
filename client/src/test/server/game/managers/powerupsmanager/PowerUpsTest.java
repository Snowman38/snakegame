package server.game.managers.powerupsmanager;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PowerUpsTest {

    JFXPanel panel = new JFXPanel();

    Map map = new Map(30, 30, new File("client/src/test/server/game/managers/mapmanager/TestMap.txt"));
    PowerUp pu = new PowerUp(map);
    ControlsInverter ci = new ControlsInverter(map);
    WallSkipper ws = new WallSkipper(map);
    Mine mine = new Mine(map);
    Freeze freeze = new Freeze(map);

    @Test
    public void testGetPowerType() {

        assertNotNull(pu.getPowerType());

        assertTrue(pu.getPowerType().equals(BoxStatus.COIN) ||
                pu.getPowerType().equals(BoxStatus.MINE) ||
                pu.getPowerType().equals(BoxStatus.FREEZE) ||
                pu.getPowerType().equals(BoxStatus.REVERSE) ||
                pu.getPowerType().equals(BoxStatus.SKIP) ||
                pu.getPowerType().equals(BoxStatus.SPEED));
    }

    @Test
    public void testSpawnPowerUp() {

        pu.spawnPowerUp(map);

        assertTrue(pu.getCoordinate().getX() < 30 && pu.getCoordinate().getX() > 0 &&
                pu.getCoordinate().getY() < 30 && pu.getCoordinate().getY() > 0);

        assertTrue(map.getMapValue(pu.getCoordinate()).equals(pu.getPowerType()));
    }

    @Test
    public void testDespawnPowerUp() {

        Coordinate coord = pu.getCoordinate();
        pu.despawnPowerUp(map);

        assertNull(pu.getCoordinate());
        assertTrue(map.getMapValue(coord).equals(BoxStatus.EMPTY));
    }

    @Test
    public void setControlsInvertTimer() {

        ci.setInvertTimer(5);
        assertEquals(ci.getInvertTimer(), 5);
    }

    @Test
    public void getControlsInvertTimer() {

        ci.setInvertTimer(10);
        assertEquals(ci.getInvertTimer(), 10);
    }

    @Test
    public void setFreezeTimer() {

        freeze.setFreezeTimer(5);
        assertEquals(freeze.getFreezeTimer(), 5);
    }

    @Test
    public void getFreezeTimer() {

        freeze.setFreezeTimer(10);
        assertEquals(freeze.getFreezeTimer(), 10);
    }
    @Test
    public void setWallSkipTimer() {

        ws.setSkipTimer(5);
        assertEquals(ws.getSkipTimer(), 5);
    }

    @Test
    public void getWallSkipTimer() {

        ws.setSkipTimer(10);
        assertEquals(ws.getSkipTimer(), 10);
    }

    @Test
    public void testPlantMine() {

        Coordinate coord = new Coordinate(10, 10);
        mine.plantMine(coord);

        assertEquals(mine.getMineCoordinate(), coord);
        assertTrue(map.getMapValue(coord).equals(BoxStatus.PLACED_MINE));
    }

}
