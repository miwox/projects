package logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class GameFieldTest {

    @Test
    public void testCorrectNumberOfTreasuresEachPlayerAndPosition() {
        GameField testLogic = new GameField(new FakeGui(), 5, 3, new FakeLogger());
        List<Player> players = testLogic.getPlayers();
        Player one = players.get(0);
        Player two = players.get(1);
        Player three = players.get(2);
        assertEquals(one.getTreasures().size(), 5);
        assertEquals(two.getTreasures().size(), 5);
        assertEquals(three.getTreasures().size(), 5);
        assertEquals(GameField.PLAYER_START_COORDINATE[0], one.getPosition());
        assertEquals(GameField.PLAYER_START_COORDINATE[1], two.getPosition());
        assertEquals(GameField.PLAYER_START_COORDINATE[2], three.getPosition());
    }

    @Test
    public void testMovementOfWayCardsRight() {
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_2), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_3), // y == 2
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.NINETY, Treasure.TREASURE_5), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_6), // y == 2

                },
                new WayCard[]{ // x == 2
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_7), // y == 0
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_8), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_9), // y == 2

                }
        };

        WayCard freeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        GameField logic = new GameField(new FakeGui(), test, freeCard);
        //Before movement

        WayCard oldFreeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        WayCard card00 = test[0][0];
        WayCard card10 = test [1][0];
        WayCard newFreeCardAfterMovement = test[2][0];

        WayCard newFreeCard =  logic.moveCardHorizontalGetFreeCard(Direction.RIGHT, logic.getWayCards(), logic.getFreeCard(), 0);
        logic.setFreeCard(newFreeCard);
        WayCard positionOfFreeCardAfterMovement = logic.getWayCards()[0][0];

        assertEquals(newFreeCardAfterMovement.toString(), logic.getFreeCard().toString());
        assertEquals(positionOfFreeCardAfterMovement.toString(), oldFreeCard.toString());
        assertEquals(card00.toString(), logic.getWayCards()[1][0].toString());
        assertEquals(card10.toString(), logic.getWayCards()[2][0].toString());
        assertEquals(newFreeCardAfterMovement.toString(), logic.getFreeCard().toString());
    }

    @Test
    public void testMovementOfWayCardsLeft() {
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_2), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_3), // y == 2
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.NINETY, Treasure.TREASURE_5), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_6), // y == 2

                },
                new WayCard[]{ // x == 2
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_7), // y == 0
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_8), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_9), // y == 2

                }
        };

        WayCard freeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        GameField logic = new GameField(new FakeGui(), test, freeCard);
        //Before movement

        WayCard oldFreeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        WayCard card10 = test [1][0];
        WayCard card20 = test[2][0];
        WayCard newFreeCardAfterMovement = test[0][0];

        WayCard newFreeCard = logic.moveCardHorizontalGetFreeCard(Direction.LEFT, logic.getWayCards(), logic.getFreeCard(), 0);
        logic.setFreeCard(newFreeCard);
        WayCard positionOfFreeCardAfterMovement = logic.getWayCards()[0][2];

        assertEquals(card10.toString(), logic.getWayCards()[0][0].toString());
        assertEquals(card20.toString(), logic.getWayCards()[1][0].toString());
        assertEquals(newFreeCardAfterMovement.toString(), logic.getFreeCard().toString());
        assertEquals(oldFreeCard.toString(), logic.getWayCards()[2][0].toString());
        assertEquals(newFreeCardAfterMovement.toString(), logic.getFreeCard().toString());
    }

    @Test
    public void testMovementOfWayCardsUP() {
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_2), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_3), // y == 2
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.NINETY, Treasure.TREASURE_5), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_6), // y == 2

                },
                new WayCard[]{ // x == 2
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_7), // y == 0
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_8), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_9), // y == 2

                }
        };

        WayCard freeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        Player testPlayer = new Player(PlayerIndex.PLAYER_ONE,PlayerMode.NONE, new Coordinate(0,0));
        GameField logic = new GameField(new FakeGui(), test, freeCard);
        //Before movement
        WayCard oldFreeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        WayCard card00 = test [0][0];
        WayCard card01 = test[0][1];
        WayCard card02 = test [0][2];

        WayCard newFreeCard = logic.moveCardVerticalAndGetNewFreeCard(Direction.UP, logic.getWayCards(), freeCard, 0);
        logic.setFreeCard(newFreeCard);

        assertEquals(oldFreeCard.toString(), logic.getWayCards()[0][2].toString());
        assertEquals(card00.toString(), logic.getFreeCard().toString());
        assertNotEquals(card01.toString(), logic.getWayCards()[0][1].toString());
        assertEquals(card01.toString(), logic.getWayCards()[0][0].toString());
        assertEquals(card02.toString(), logic.getWayCards()[0][1].toString());

    }

    @Test
    public void testMovementOfWayCardsDown() {
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_2), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_3), // y == 2
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.NINETY, Treasure.TREASURE_5), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_6), // y == 2

                },
                new WayCard[]{ // x == 2
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_7), // y == 0
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_8), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_9), // y == 2

                }
        };

        WayCard freeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        GameField logic = new GameField(new FakeGui(), test, freeCard);
        //Before movement
        WayCard[][] test2 = logic.getDeepCopyOfWayCards(test);
        for(int x = 0 ; x < test2.length; x++){
            for(int y = 0; y < test2.length; y++){
                assertEquals(test2[x][y].toString(), test[x][y].toString());
            }
        }
        WayCard oldFreeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        WayCard card00 = test [0][0];
        WayCard card01 = test[0][1];
        WayCard card02 = test [0][2];
        WayCard newFreeCard = logic.moveCardVerticalAndGetNewFreeCard(Direction.DOWN, logic.getWayCards(), freeCard, 0);
        logic.setFreeCard(newFreeCard);
        assertEquals(oldFreeCard.toString(), logic.getWayCards()[0][0].toString());
        assertEquals(card02.toString(), logic.getFreeCard().toString());
        assertNotEquals(card00.toString(), logic.getWayCards()[0][0].toString());
        assertEquals(oldFreeCard.toString(), logic.getWayCards()[0][0].toString());
        assertEquals(card00.toString(), logic.getWayCards()[0][1].toString());
    }


    @Test
    public void testGetTargetToGo() {
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_1), // y == 0
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_2), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_3), // y == 2
                },
                new WayCard[]{ // x == 1
                        new WayCard(WayType.I, Rotation.ZERO, Treasure.TREASURE_4), // y == 0
                        new WayCard(WayType.T, Rotation.NINETY, Treasure.TREASURE_5), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_6), // y == 2

                },
                new WayCard[]{ // x == 2
                        new WayCard(WayType.T, Rotation.ZERO, Treasure.TREASURE_7), // y == 0
                        new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_8), // y == 1
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.TREASURE_9), // y == 2

                }
        };

        WayCard freeCard = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, Treasure.TREASURE_10);
        GameField logic = new GameField(new FakeGui(), test, freeCard);
        Player test_player = new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, new Coordinate(0,0));
        List<Treasure> treasures_player = Arrays.asList(Treasure.TREASURE_2, Treasure.TREASURE_11);
        test_player.addTreasures(treasures_player);
        Coordinate result = logic.getTargetToGo(test_player, test);
        Coordinate ex = new Coordinate(0,1);
        Assert.assertEquals(ex, result);
        Coordinate INVALID = new Coordinate(-1,-1);
        test_player.removeFirstTreasure();
        Coordinate result1 = logic.getTargetToGo(test_player, test);
        Assert.assertEquals(INVALID, result1);

    }
}
