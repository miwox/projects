package logic;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AiWorkTest {

    @Test
    public void testAi() throws IllegalInputException {
        GameField testLogic = new GameField(new FakeGui(),
                6,
                new PlayerMode[]{PlayerMode.HUMAN}, new String[]{"Test"},
                new FakeLogger());
        testLogic.startGame();
        assertEquals(6, testLogic.getPlayers().get(0).getTreasures().size());
        testLogic = testLogic.loadGame("test/logic/testNormalAi.json");

        // will push the way card in.
        testLogic.startGame();
        testLogic.setStateTwoUpdatePossibleWays();
        List<Coordinate> listOfPossibleWaysAfterPushing = testLogic.getCurrentPossibleWays();
        Coordinate target = testLogic.getTargetToGo(testLogic.getPlayers().get(0), testLogic.getWayCards());
        assertTrue(listOfPossibleWaysAfterPushing.contains(target));
        // will move the AI player to the target

        testLogic.setStateTwoCheckTreasureRespawn();
        testLogic.setStateOneCheckTreasureAfterMoving();

        List<Coordinate> listOfPossibleWaysAfterPushing1 = testLogic.getCurrentPossibleWays();
        Coordinate target1 = testLogic.getTargetToGo(testLogic.getPlayers().get(0), testLogic.getWayCards());

        //start position is next top and reachable
        assertTrue(listOfPossibleWaysAfterPushing1.contains(target1));
    }


}
