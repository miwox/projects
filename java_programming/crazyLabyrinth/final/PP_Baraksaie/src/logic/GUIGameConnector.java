package logic;
import java.util.List;
import java.util.Map;

/**
 * Connects the logic with graphical interface
 * @author Miwand Baraksaie inf104162
 */
public interface GUIGameConnector {

    /**
     * Show the free card
     * @param freeCard - free card to show
     */
    void showFreeCard(WayCard freeCard);

    /**
     * Rotates the free card
     * @param freeCard - free card to roteate
     */
    void rotateFreeCard(WayCard freeCard);

    /**
     * Shows way card, to a logical coordinate
     * Add off set to get fx coordinates
     * @param wayCard - way card to show
     * @param x_logical - logical position
     * @param y_logical - logical position
     */
    void showWayCard(WayCard wayCard, int x_logical, int y_logical);

    /**
     * Init the arrows
     */
    void initializeArrows();

    /**
     * Init the player on the fx interface
     * @param player - player to init
     */
    void initializeAndShowPlayer(Player player);

    /**
     * Animates and push the free card to the at y_logical + 1 row
     * And contact the game state, to set the next state
     * Animate also respawn of players, when there is respawn
     * @param y_logical - coordinate, add off set to get fx coordinate
     * @param respawnCoordinates - respawn coordian
     * @param gameState - current logical state
     */
    void animatePushHorizontalLeftToRightAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState);

    /**
     * Animates and push the free card to the at y_logical + 1 row
     * Right to left
     * And contact the game state, to set the next state
     * Animate also respawn of players, when there is respawn
     * @param y_logical - coordinate, add off set to get fx coordinate
     * @param respawnCoordinates - respawn coordinates
     * @param gameState - current logical state
     */
    void animatePushHorizontalRightToLeftAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState);

    /**
     * Animates and push the free card to the x_logical + 1 column
     * Up to down
     * And contact the game state, to set the next state
     * Animate also respawn of players, when there is respawn
     * @param x_logical - coordinate, add off set to get fx coordinate
     * @param respawnCoordinates - respawn coordinates
     * @param gameState - current logical state
     */
    void animatePushVerticalUpToDownAndRespawnPlayers(int x_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState);

    /**
     * Animates and push the free card to the x_logical + 1 column.
     * Down to up.
     * And contact the game state, to set the next state
     * Animate also respawn of players, when there is respawn
     * @param x_logical - coordinate, add off set to get fx coordinate
     * @param respawnCoordinates - respawn coordinates
     * @param gameState - current logical state
     */
    void animatePushVerticalDownToUpAndRespawnPlayers(int x_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState);

    /**
     * Set the duration for the animation, to push card
     * @param index - index is mapping to DURATIONS in JavaFXGameGUI.java
     */
    void setDurationPushCardAnimation(int index);

    /**
     * Set the duration for the animation, to move player
     * @param index - index is mapping to DURATIONS in JavaFXGameGUI.java
     */
    void setDurationPlayerMovementAnimation(int index);

    /**
     * Highlights the way card, which is possible to move on it, when mouse is entering
     * @param pos_logical - logical position add off set to get fx coordinates
     */
    void highLightAvailable(Coordinate pos_logical);

    /**
     * Highlights the way card, which is not possible to move on it, when mouse is entering
     * @param pos_logical - logical position add off set to get fx coordinates
     */
    void highLightNotAvailable(Coordinate pos_logical);

    /**
     * Highlights the way card with the next target treasure
     * @param pos_logical - logical position add off set to get fx coordinates
     */
    void highLightTreasureCard(Coordinate pos_logical);

    /**
     * Highlights the free way card with treasure on it
     */
    void highLightFreeWayCard();

    /**
     * Disable highlighting after key released
     * @param pos_logical - logical position add off set to get fx coordinates
     */
    void highLightDisable(Coordinate pos_logical);

    /**
     * Animates and moves a player to another position
     * The first element of wayListToGo is the start position and the last element is the destination.
     * Contacts the logic to set the next state after finishing the animation
     * @param playerIndex - player to move, maps to the HashMap with the player buttons in it
     * @param wayListToGo - all way card steps between start position and destination position
     * @param state - logical the state to set the next state, after fishing
     */
    void animateMovePlayerFromTo(PlayerIndex playerIndex, List<Coordinate> wayListToGo, GameField state);

    /**
     * Removes the treasure of the way card, after collecting
     * Updates the treasures left score
     * @param x_logical - position of the way card, add off set to get fx coordinate
     * @param y_logical - position of the way card, add off set to get fx coordinate
     * @param index - player index of collecting player
     * @param treasuresLeft - number of treasures that left to collect
     */
    void removeTreasureCardUpdateTreasuresLeft(int x_logical, int y_logical, PlayerIndex index, int treasuresLeft);

    /**
     * Displays the name of current player
     * @param player - information about the player
     */
    void displayShowCurrentPlayer(Player player);

    /**
     * Removes the way card image. Add off set to get fx coordinates
     * If the way card has a treasure, also remove the treasure
     * @param x_logical - position of the way card, add off set to get fx coordinate
     * @param y_logical - position of the way card, add off set to get fx coordinate
     * @param hasTreasure - has way card a treasure on it
     */
    void deleteWayCardImage(int x_logical, int y_logical, boolean hasTreasure);

    /**
     * Removes way card image of free card, and the treasure if one is on it
     * @param hasTreasure - has the to remove way card a treasure
     */
    void deleteFreeCardImage(boolean hasTreasure);

    /**
     * Removes the player from the way cards
     * @param index - which player
     * @param pos_logical - logical position of the player
     */
    void removePlayer(PlayerIndex index, Coordinate pos_logical);

    /**
     * Displays text the name of the winner
     * @param index - which player won
     */
    void displayHasWon(PlayerIndex index);

    /**
     * Displays text to push a card
     * @param index - which player shall push a card
     */
    void displayActionPushCard(PlayerIndex index);

    /**
     * Displays text to move the player
     * @param index - which player
     */
    void displayActionMovePlayer(PlayerIndex index);

    /**
     * Will push an alert with an specific error
     * @param exception - thrwon exception
     */
    void displayError(Exception exception);

    /**
     * Set the buttons on the right of gui to default.
     */
    void textAndButtonsToDefault();

    /**
     * After highlighting the treasure, on the free card,
     * this method will disable the highlighting.
     */
    void highLightDisableFreeWayCard();

    /**
     * Will disable all highlighting, of the gui.
     */
    void disableAllHighLighting();

    /**
     * Set the visibility of the arrow, after blocking
     * @param dir - direction
     * @param pos_blocked - blocked - position
     * @param isVisible - able or disable visibility
     */
    void setArrowVisibility(Direction dir, Coordinate pos_blocked, boolean isVisible);

    /**
     * Shows the start button, after loading a new game.
     */
    void showBtnStartGame();
}
