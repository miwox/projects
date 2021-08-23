package logic;

/**
 * Interface to log a game.
 * Allows implementing various types of logging.
 * Like a normal text logger or also a HTML logger
 */
public interface GameLogger{

    /**
     * Logs playing player
     * @param player - information
     */
    void logPlayingPlayer(Player player);

    /**
     * Logs the left treasures of player
     * @param player - information
     */
    void logPlayerHasTreasures(Player player);

    /**
     * Logs the total amount of treasures, which have to be collected in this
     * @param amount - of treasures
     */
    void logTotalTreasures(int amount);

    /**
     * Logs the turn of the player
     * @param currentTurn - player is on turn
     */
    void logCurrentTurnStateOne(Player currentTurn);

    /**
     * Logs the location of the pushed card
     * Horizontal from left to right.
     * @param x_fx - the arrow position, in fx coordinate
     * @param y_logical - the logical column which is moving
     * @param player - information of player
     * @param freeCard
     */
    void logPlayerPushedWayCardsHorizontalFromLeftToRight(int x_fx, int y_logical, Player player, WayCard freeCard);

    /**
     * Logs the location of the pushed card
     * Horizontal right to left
     * @param x_fx - the arrow position, in fx coordinate
     * @param y_logical - the logical column which is moving
     * @param player - information of player
     * @param freeCard
     */
    void logPlayerPushedWayCardsHorizontalFromRightToLeft(int x_fx, int y_logical, Player player, WayCard freeCard);

    /**
     * Logs the location of the pushed card.
     * Vertical from up to down
     * @param x_logical - the logical row which is moving
     * @param y_fx - the arrow position, in fx coordinate
     * @param player  information of player
     * @param freeCard
     */
    void logPlayerPushedWayCardsVerticalFromUpToDown(int x_logical, int y_fx, Player player, WayCard freeCard);

    /**
     * Logs the location of the pushed card.
     * Vertical from down to up
     * @param x_logical - the logical row which is moving
     * @param y_fx - the arrow position, in fx coordinate
     * @param player  information of player
     * @param freeCard
     */
    void logPlayerPushedWayCardsVerticalFromDownToUp(int x_logical, int y_fx, Player player, WayCard freeCard);

    /**
     * Logs  the movement of the player
     * @param player - player is moving
     * @param pos - from
     * @param newPos - to
     */
    void logMovePlayerTo(Player player, Coordinate pos, Coordinate newPos);

    /**
     * Logs the information about the collected treasure
     * @param treasure - treasure which got collected
     * @param player - information about the player, that collected the treasure
     */
    void logPlayerCollectedTreasure(Treasure treasure, Player player);

    /**
     * Logs after finishing collecting, the treasures.
     * The player has to go to the start position to win the game.
     * @param player - information
     * @param coordinate - start position to move to
     */
    void logPlayerGoToStartPosition(Player player, Coordinate coordinate);

    /**
     * Logs the winning player, after reaching the start position
     * @param player
     */
    void logHasWon(Player player);

    void logPlayerNotInvolved(PlayerIndex index);
}
