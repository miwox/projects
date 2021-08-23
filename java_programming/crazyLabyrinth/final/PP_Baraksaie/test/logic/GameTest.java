package logic;

import org.junit.Test;
import static org.junit.Assert.*;



public class GameTest {

    @Test
    public void testCorrectRowWasPushing() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testChangePlayerTurn.json");

        //check way card
        WayCard w01 = logic.getWayCards()[0][1];
        logic.pushRowColumn(0,1, Direction.RIGHT);
        WayCard w11 = logic.getWayCards()[1][1];
        assertEquals(w01, w11);

        logic.pushRowColumn(0,1, Direction.RIGHT);
        WayCard w21 = logic.getWayCards()[2][1];
        assertEquals(w01, w21);

        logic.pushRowColumn(0,1, Direction.RIGHT);
        WayCard w31 = logic.getWayCards()[3][1];
        assertEquals(w01, w31);

        WayCard w41 = logic.getWayCards()[4][1];
        assertNotEquals(w01, w41);
        logic.pushRowColumn(0,1, Direction.RIGHT);
        w41 = logic.getWayCards()[4][1];
        assertEquals(w01, w41);
        assertNotEquals(w01, logic.getWayCards()[5][1]);

        //fake gui doesn't change the state of the logic
        logic.setStateTwoCheckTreasureRespawn();
    }

    @Test
    public void testCorrectColumnWasPushed() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testChangePlayerTurn.json");

        //check way card
        WayCard w10 = logic.getWayCards()[1][0];
        WayCard w11 = logic.getWayCards()[1][1];
        assertNotEquals(w10, w11);
        logic.pushRowColumn(1,0, Direction.DOWN);
        w11 = logic.getWayCards()[1][1];
        assertEquals(w10, w11);
    }

    @Test
    public void testCorrectRowWasPushed2() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testChangePlayerTurn.json");

        //check way card
        WayCard w61 = logic.getWayCards()[6][1];
        WayCard w51 = logic.getWayCards()[5][1];
        assertNotEquals(w61, w51);
        logic.pushRowColumn(6,1, Direction.LEFT);
        w51 = logic.getWayCards()[5][1];
        assertEquals(w61, w51);
    }

    @Test
    public void testCorrectColumnWasPushed2() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
            logic.startGame();
            logic = logic.loadGame("test/logic/testChangePlayerTurn.json");
            logic.startGame();

        //check way card
        WayCard w10 = logic.getWayCards()[1][0];
        WayCard freeCard = logic.getFreeCard();
        assertNotEquals(w10, freeCard);
        logic.pushRowColumn(1,6, Direction.UP);
        freeCard = logic.getFreeCard();
        assertEquals(w10, freeCard);
    }

    @Test
    public void testChangePlayerAfterMoving() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testChangePlayerTurn.json");
        logic.startGame();
        // set state two directly without pushing any card.
        logic.setStateTwoCheckTreasureRespawn();
        Player currentPlayerFirstRound = logic.getCurrentTurn();
        logic.setStateOneCheckTreasureAfterMoving();
        Player currentPlayerSecondRound = logic.getCurrentTurn();
        assertNotEquals(currentPlayerFirstRound, currentPlayerSecondRound);
        assertEquals(currentPlayerFirstRound, logic.getPlayers().get(0));
        assertEquals(currentPlayerSecondRound, logic.getPlayers().get(1));
    }

    @Test
    public void testStateOfAi() throws IllegalInputException {
        GameField logic = new GameField(new FakeGui(), 1, new PlayerMode[]{PlayerMode.HUMAN},
                new String[]{"init"}, new FakeLogger());
        logic.startGame();
        logic = logic.loadGame("test/logic/testAiAndHuman.json");
        // before starting the game, check that the current turn is the Normal Ai
        // after starting the game, the programm must automatically will push coordinate.
        PlayerMode result_ai = logic.getCurrentTurn().getPlayerMode();
        assertEquals(PlayerMode.COMPUTER_1, result_ai);
        // start game
        logic.startGame();
        assertEquals(PlayerMode.COMPUTER_1, result_ai);
        // still the same
        Coordinate exp_startPosition = new Coordinate(0,0);
        assertEquals(exp_startPosition, logic.getCurrentTurn().getPosition());

        System.out.println(logic.getCurrentTurn());
        // will move the player
        logic.setStateTwoCheckTreasureRespawn();
        logic.setStateOneCheckTreasureAfterMoving();
        assertEquals(PlayerMode.HUMAN, logic.getCurrentTurn().getPlayerMode());
    }

}