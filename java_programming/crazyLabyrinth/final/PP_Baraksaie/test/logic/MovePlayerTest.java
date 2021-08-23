package logic;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MovePlayerTest {

    @Test
    public void testMovePlayer(){
        WayCard[][] test = {
                new WayCard[]{ // x == 0
                        new WayCard(WayType.L, Rotation.ZERO, Treasure.NONE), // y == 0
                        new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.TREASURE_1), // y == 1
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

        List<Treasure> treasures = Arrays.asList(Treasure.TREASURE_1, Treasure.TREASURE_2);
        Player test_player = new Player(PlayerIndex.PLAYER_ONE, PlayerMode.HUMAN, new Coordinate(0,0), treasures);
        // set logic direct to state two to move player. its required because of the private methods.
        GameField logic = new GameField(new FakeGui(), test, new WayCard(WayType.L, Rotation.NINETY, Treasure.TREASURE_10), test_player);
        WayCard newFreeCard = logic.moveCardVerticalAndGetNewFreeCard(Direction.DOWN, logic.getWayCards(), logic.getFreeCard(), 0);
        logic.setFreeCard(newFreeCard);
        logic.updatePlayerPosAfterPushVerticalGetRespawn(Direction.DOWN, logic.getPlayers(), 0);
        //check new coordinate of the player
        assertEquals(new Coordinate(0,1), logic.getPlayers().get(0).getPosition());

        newFreeCard = logic.moveCardVerticalAndGetNewFreeCard(Direction.DOWN, logic.getWayCards(), logic.getFreeCard(), 0);
        logic.setFreeCard(newFreeCard);
        logic.updatePlayerPosAfterPushVerticalGetRespawn(Direction.DOWN, logic.getPlayers(), 0);
        assertEquals(new Coordinate(0,2), logic.getPlayers().get(0).getPosition());

        // Respawn
        newFreeCard = logic.moveCardVerticalAndGetNewFreeCard(Direction.DOWN, logic.getWayCards(), logic.getFreeCard(), 0);
        logic.setFreeCard(newFreeCard);
        logic.updatePlayerPosAfterPushVerticalGetRespawn(Direction.DOWN, logic.getPlayers(), 0);
        assertEquals(new Coordinate(0,0), logic.getPlayers().get(0).getPosition());
    }


    @Test
    public void playerIsChanging() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testChangePlayerTurn.json");
        //check way card
        WayCard w01 = logic.getWayCards()[0][1];
        logic.pushRowColumn(0,1, Direction.RIGHT);
        WayCard w11 = logic.getWayCards()[1][1];

        assertEquals(w01, w11);
        //fake gui doesn't change the state of the logic
        logic.setStateTwoCheckTreasureRespawn();

    }
}
