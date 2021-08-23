package logic;

public class FakeLogger implements GameLogger{
    @Override
    public void logPlayingPlayer(Player player) {

    }

    @Override
    public void logPlayerHasTreasures(Player player) {

    }

    @Override
    public void logTotalTreasures(int amount) {

    }

    @Override
    public void logCurrentTurnStateOne(Player currentTurn) {

    }

    @Override
    public void logPlayerPushedWayCardsHorizontalFromLeftToRight(int x_fx, int y_logical, Player respawnCoordinates, WayCard freeCard) {

    }

    @Override
    public void logPlayerPushedWayCardsHorizontalFromRightToLeft(int x_fx, int y_logical, Player player, WayCard freeCard) {

    }

    @Override
    public void logPlayerPushedWayCardsVerticalFromUpToDown(int x_logical, int y_fx, Player player, WayCard freeCard) {

    }

    @Override
    public void logPlayerPushedWayCardsVerticalFromDownToUp(int x_logical, int y_fx, Player player, WayCard freeCard) {

    }

    @Override
    public void logMovePlayerTo(Player player, Coordinate pos, Coordinate newPos) {

    }

    @Override
    public void logPlayerCollectedTreasure(Treasure treasure, Player player) {

    }

    @Override
    public void logPlayerGoToStartPosition(Player player, Coordinate coordinate) {

    }

    @Override
    public void logHasWon(Player player) {

    }

    @Override
    public void logPlayerNotInvolved(PlayerIndex index) {

    }
}
