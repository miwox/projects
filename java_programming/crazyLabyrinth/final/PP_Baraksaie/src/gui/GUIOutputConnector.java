package gui;

import logic.PlayerIndex;

/**
 * Interface which connects the right part of graphical interface,
 * with the logic
 * @author Miwand Baraksaie inf104162
 */
public interface GUIOutputConnector {
    /**
     * Shows name of the current player
     * @param index - player
     */
    void updateDisplayCurrentTurn(PlayerIndex index);

    /**
     * Shows and updates the treasures left of a player.
     * @param index - player
     * @param amountOfTreasure - number of treasures
     */
    void updateDisplayTreasuresLeft(PlayerIndex index, int amountOfTreasure);

    /**
     * Init the names of player and the number of players.
     * @param index - player
     * @param name - name of player
     * @param amountOfTreasure - number of treasures
     */
    void initNameAndTreasure(PlayerIndex index, String name, int amountOfTreasure);

    /**
     * Resets the labels of player
     * @param index - player
     */
    void remove(PlayerIndex index);

    /**
     * Shows the winning player
     * @param index - player
     */
    void showWinner(PlayerIndex index);

    /**
     * Display to push a card
     * @param index - player
     */
    void displayPushCard(PlayerIndex index);

    /**
     * Display to move the player
     * @param index - player
     */
    void displayMovePlayer(PlayerIndex index);

    /**
     * Makes the button invisible
     */
    void invisibleButtons();

    /**
     * Make the action text invisible
     */
    void invisibleActionText();

    /**
     * Shows the start game button
     */
    void showBtnStartGame();
}
