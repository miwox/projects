package logic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Load game test
 * @author Miwand Baraksaie inf104162
 */
public class LoadDataTest {

    @Test
    public void testLoadGame0() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());
        testLogic = testLogic.loadGame("test/logic/testLoadGame.json");
        assertEquals(1, testLogic.getPlayers().size());
        assertEquals(24, testLogic.getPlayers().get(0).getTreasures().size());
    }

    @Test
    public void testLoadGame_falsePath() {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());

        // throw exception no changes, file not found
        try {
            testLogic = testLogic.loadGame("");
        } catch (IllegalInputException e) {
            assertTrue(e instanceof IllegalInputException);
        }

        assertEquals(1, testLogic.getPlayers().size());
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());
    }

    @Test
    public void testLoadGame_fourPlayerOnStart() throws IllegalInputException {
        GameField testLogic =
                new GameField(new FakeGui(),
                        6,
                        new PlayerMode[]{PlayerMode.HUMAN,
                                PlayerMode.HUMAN,
                                PlayerMode.HUMAN,
                                PlayerMode.HUMAN}, new String[]{
                        "Test0",
                        "Test1",
                        "Test2",
                        "Test3"},
                        new FakeLogger());

        testLogic.startGame();
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());
        assertEquals(6, testLogic.getPlayers().get(1).getTreasures().size());
        assertEquals(6, testLogic.getPlayers().get(2).getTreasures().size());
        assertEquals(6, testLogic.getPlayers().get(3).getTreasures().size());

        testLogic.loadGame("test/logic/testLoadGame.json");
        assertEquals(1, testLogic.getPlayers().size());
        assertEquals(24, testLogic.getPlayers().get(0).getTreasures().size());
    }

    @Test
    public void testLoadGame3() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());
        testLogic = testLogic.loadGame("test/logic/testLoadGame.json");
        Coordinate blocked = testLogic.getBlockedCoordinate();
        WayCard free = testLogic.getFreeCard();
        assertEquals(new Coordinate(-1,-1), blocked);
        assertEquals(WayType.L, free.getWayType());
        assertEquals(Rotation.HUNDRED_EIGHTY, free.getRotation());
        assertEquals(Treasure.NONE, free.getTreasure());
    }

    @Test
    public void testLoadGameBlockedCoordinateIsLogical10() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        Coordinate blockedBeforeLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(-1,-1), blockedBeforeLoad);

        testLogic = testLogic.loadGame("test/logic/testBlockedCoordinateIs10.json");
        Coordinate blockedCoordinateAfterLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(1,0), blockedCoordinateAfterLoad);
    }

    @Test
    public void testLoadGameBlockedCoordinateIsLogical30() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        Coordinate blockedBeforeLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(-1,-1), blockedBeforeLoad);

        testLogic = testLogic.loadGame("test/logic/testBlockedCoordinateIs30.json");
        Coordinate blockedCoordinateAfterLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(3,0), blockedCoordinateAfterLoad);
    }

    @Test
    public void testLoadGameBlockedCoordinateIsLogical01() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        Coordinate blockedBeforeLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(-1,-1), blockedBeforeLoad);

        testLogic = testLogic.loadGame("test/logic/testBlockedCoordinateIs01.json");
        Coordinate blockedCoordinateAfterLoad = testLogic.getBlockedCoordinate();
        assertEquals(new Coordinate(0,1), blockedCoordinateAfterLoad);
    }





}
