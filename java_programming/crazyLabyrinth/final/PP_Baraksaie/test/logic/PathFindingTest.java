package logic;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PathFindingTest {

    @Test
    public void testFindingWays_startAt00() {

        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_5), // y == 1

                }
        };
        // L T
        // T T
        Coordinate shouldBeInSideThePossibleWays = new Coordinate(1, 0);
        Coordinate startPosition = new Coordinate(0, 0);
        GameField logic = new GameField(new FakeGui(), test, new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, startPosition));
        List<Coordinate> possibleWays = logic.getCurrentPossibleWays();
        assertTrue(possibleWays.contains(shouldBeInSideThePossibleWays));
        assertTrue(possibleWays.contains(startPosition));
        assertEquals(2, possibleWays.size());
        List<Coordinate> shortestWayTo = logic.getCurrentShortestWayTo(shouldBeInSideThePossibleWays);
        List<Coordinate> result = Arrays.asList(startPosition, shouldBeInSideThePossibleWays);
        assertEquals(result, shortestWayTo);
    }

    @Test
    public void testFindingWaysBig_startAt00() {

        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1)

                }
        };

        Coordinate startCoord = new Coordinate(0, 0);
        GameField logic = new GameField(new FakeGui(), test, new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, startCoord));
        List<Coordinate> possibleWays = logic.getCurrentPossibleWays();
        Coordinate coord0 = new Coordinate(0, 1);
        Coordinate coord1 = new Coordinate(0, 2);
        Coordinate coord2 = new Coordinate(0, 3);
        Coordinate coord3 = new Coordinate(0, 4);
        Coordinate coord4 = new Coordinate(0, 5);
        Coordinate coord5 = new Coordinate(0, 6);
        assertTrue(possibleWays.contains(coord0));
        assertTrue(possibleWays.contains(coord1));
        assertTrue(possibleWays.contains(coord2));
        assertTrue(possibleWays.contains(coord3));
        assertTrue(possibleWays.contains(coord4));
        assertTrue(possibleWays.contains(coord5));
        assertFalse(possibleWays.contains(new Coordinate(0, -1)));
        List<Coordinate> shortestWayFromStartToCoord5 = logic.getCurrentShortestWayTo(coord5);
        List<Coordinate> result = Arrays.asList(startCoord,coord0, coord1, coord2, coord3, coord4, coord5);
        assertEquals(result, shortestWayFromStartToCoord5);

        List<Coordinate> shortestWayFromStartToCoord4 = logic.getCurrentShortestWayTo(coord4);
        List<Coordinate> result2 = Arrays.asList(startCoord,coord0, coord1, coord2, coord3, coord4);
        assertEquals(result2, shortestWayFromStartToCoord4);

        List<Coordinate> shortestWayFromStartToCoordStart = logic.getCurrentShortestWayTo(startCoord);
        List<Coordinate> resultStart = Arrays.asList(startCoord);
        assertEquals(resultStart, shortestWayFromStartToCoordStart);
    }

    @Test
    public void testFindingWays2_startAt00() {
        WayCard[][] test = {
                new WayCard[]{
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1)
                },

                new WayCard[]{
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.L, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_1)
                },

                // only x * x  && x == x game field is allowed. but we can do a trick
                new WayCard[]{
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_1),
                }
        };
        Coordinate start = new Coordinate(0, 0);
        GameField logic = new GameField(new FakeGui(), test, new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, start));
        List<Coordinate> possibleWays = logic.getCurrentPossibleWays();
        assertTrue(possibleWays.contains(new Coordinate(0, 1)));
        assertTrue(possibleWays.contains(new Coordinate(0, 2)));
        assertTrue(possibleWays.contains(new Coordinate(0, 3)));
        assertTrue(possibleWays.contains(new Coordinate(0, 4)));
        assertTrue(possibleWays.contains(new Coordinate(0, 5)));
        assertTrue(possibleWays.contains(new Coordinate(0, 6)));
        assertTrue(possibleWays.contains(new Coordinate(0, 7)));

        assertTrue(possibleWays.contains(new Coordinate(1, 0)));
        assertTrue(possibleWays.contains(new Coordinate(1, 1)));
        assertTrue(possibleWays.contains(new Coordinate(1, 2)));
        assertTrue(possibleWays.contains(new Coordinate(1, 3)));
        assertTrue(possibleWays.contains(new Coordinate(1, 4)));
        assertTrue(possibleWays.contains(new Coordinate(1, 5)));
        assertTrue(possibleWays.contains(new Coordinate(1, 6)));
        assertTrue(possibleWays.contains(new Coordinate(1, 7)));

        assertFalse(possibleWays.contains(new Coordinate(2, 0)));
        assertFalse(possibleWays.contains(new Coordinate(2, 1)));
        assertFalse(possibleWays.contains(new Coordinate(2, 2)));
        assertFalse(possibleWays.contains(new Coordinate(2, 3)));
        assertFalse(possibleWays.contains(new Coordinate(2, 4)));
        assertFalse(possibleWays.contains(new Coordinate(2, 5)));
        assertFalse(possibleWays.contains(new Coordinate(2, 6)));
        assertFalse(possibleWays.contains(new Coordinate(2, 7)));
    }

    @Test
    public void testFindWays_Loop_StartAt00() {


        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.TREASURE_5), // y == 1

                }
        };
        // T T
        // L T180
        // loop inside.
        GameField logic = new GameField(new FakeGui(), test, new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, new Coordinate(0, 0)));
        List<Coordinate> possibleWays = logic.getCurrentPossibleWays();
        assertTrue(possibleWays.contains(new Coordinate(1, 0)));
        assertTrue(possibleWays.contains(new Coordinate(1, 1)));
        assertTrue(possibleWays.contains(new Coordinate(0, 1)));
    }

    @Test
    public void testFindWays_startAtOtherPosition() {


        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.TREASURE_5), // y == 1

                }
        };
        // T T
        // L T180
        // loop inside.
        GameField logic = new GameField(new FakeGui(), test, new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, new Coordinate(1, 0)));
        List<Coordinate> possibleWays = logic.getCurrentPossibleWays();
        assertTrue(possibleWays.contains(new Coordinate(0, 0)));
        assertTrue(possibleWays.contains(new Coordinate(1, 1)));
        assertTrue(possibleWays.contains(new Coordinate(0, 1)));
        assertFalse(possibleWays.contains(new Coordinate(0, 1000)));
    }

}
