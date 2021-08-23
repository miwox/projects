package gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import logic.PlayerIndex;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class handles the display on the right side of
 * the graphical interface
 * For more comments see interface GUIOutputConnector
 * @author Miwand Baraksaie inf104162
 */
public class GuiDisplay implements GUIOutputConnector {
    private Label lblCurrentPlayer;
    /**
     * Label of player names and treasures left, in a map.
     */
    private Map<PlayerIndex, Label> labelsOfPlayerNames;
    private Map<PlayerIndex, Label> labelsOfPlayerTreasuresLeft;
    private final Label lblAction;
    private final Button btnNewGame;
    private final Button btnCloseGame;
    private final Button btnStartGame;

    /**
     * Action text
     */
    private final String PUSH_CARD = " please push a card!";
    private final String MOVE_PLAYER = " please move your player!";
    private final String HAS_WON = " has WON! Start a new game, close or load a game!";

    /**
     * Color of the action text. Maps to the color of the buttons
     */
    private final Color[] COLOR_LABELS = new Color[]{Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED};

    /**
     * Constructor for the right part of the gui
     * @param lblCurrentPlayer - shows the current player name
     * @param lblPlayerOne - shows name of player one
     * @param lblPlayerTwo - shows name of player two
     * @param lblPlayerThree - shows name of player three
     * @param lblPlayerFour - shows name of player four
     * @param lblPlayerOneTreasureLeft - show treasure amount to  collect of player one
     * @param lblPlayerTwoTreasureLeft - show treasure amount to  collect of player one
     * @param lblThreeTreasureLeft - show treasure amount to  collect of player one
     * @param lblPlayerFourTreasureLeft - show treasure amount to  collect of player one
     * @param lblAction - shows the action text, what to do
     * @param btnNewGame - shows new game button after finishing the game
     * @param btnCloseGame - shows close game button after finishing the game
     * @param btnStartGame - shows the btn start game
     */
    public GuiDisplay(Label lblCurrentPlayer,
                      Label lblPlayerOne,
                      Label lblPlayerTwo,
                      Label lblPlayerThree,
                      Label lblPlayerFour,
                      Label lblPlayerOneTreasureLeft,
                      Label lblPlayerTwoTreasureLeft,
                      Label lblThreeTreasureLeft,
                      Label lblPlayerFourTreasureLeft,
                      Label lblAction,
                      Button btnNewGame,
                      Button btnCloseGame,
                      Button btnStartGame) {

        this.lblCurrentPlayer = lblCurrentPlayer;
        labelsOfPlayerNames = new LinkedHashMap<>();
        labelsOfPlayerNames.put(PlayerIndex.PLAYER_ONE, lblPlayerOne);
        labelsOfPlayerNames.put(PlayerIndex.PLAYER_TWO, lblPlayerTwo);
        labelsOfPlayerNames.put(PlayerIndex.PLAYER_THREE, lblPlayerThree);
        labelsOfPlayerNames.put(PlayerIndex.PLAYER_FOUR, lblPlayerFour);

        labelsOfPlayerTreasuresLeft = new LinkedHashMap<>();
        labelsOfPlayerTreasuresLeft.put(PlayerIndex.PLAYER_ONE, lblPlayerOneTreasureLeft);
        labelsOfPlayerTreasuresLeft.put(PlayerIndex.PLAYER_TWO, lblPlayerTwoTreasureLeft);
        labelsOfPlayerTreasuresLeft.put(PlayerIndex.PLAYER_THREE, lblThreeTreasureLeft);
        labelsOfPlayerTreasuresLeft.put(PlayerIndex.PLAYER_FOUR, lblPlayerFourTreasureLeft);
        this.btnNewGame = btnNewGame;
        this.btnCloseGame = btnCloseGame;
        this.lblAction = lblAction;
        this.btnStartGame = btnStartGame;
    }


    @Override
    public void updateDisplayCurrentTurn(PlayerIndex index) {
        assert labelsOfPlayerNames.get(index) != null;
        lblCurrentPlayer.setText(labelsOfPlayerNames.get(index).getText());
        lblCurrentPlayer.setTextFill(COLOR_LABELS[index.ordinal()]);
    }

    @Override
    public void updateDisplayTreasuresLeft(PlayerIndex index, int amountOfTreasure) {
        assert labelsOfPlayerNames.get(index) != null;
        assert amountOfTreasure >= 0;
        labelsOfPlayerTreasuresLeft.get(index).setText(Integer.toString(amountOfTreasure));
    }

    @Override
    public void initNameAndTreasure(PlayerIndex index, String name, int amountOfTreasure) {
        assert labelsOfPlayerNames.get(index) != null;
        labelsOfPlayerNames.get(index).setText(name);
        Label label = labelsOfPlayerNames.get(index);
        label.setText(name);
        label.setTextFill(COLOR_LABELS[index.ordinal()]);
        labelsOfPlayerTreasuresLeft.get(index).setTextFill(COLOR_LABELS[index.ordinal()]);
        updateDisplayTreasuresLeft(index, amountOfTreasure);
    }

    @Override
    public void remove(PlayerIndex index) {
        labelsOfPlayerNames.get(index).setText("");
        labelsOfPlayerTreasuresLeft.get(index).setText("");
    }

    @Override
    public void showWinner(PlayerIndex index) {
        lblAction.setText(labelsOfPlayerNames.get(index).getText() + HAS_WON);
        lblAction.setTextFill(COLOR_LABELS[index.ordinal()]);
        btnCloseGame.setVisible(true);
        btnNewGame.setVisible(true);
    }

    @Override
    public void displayPushCard(PlayerIndex index) {
        lblAction.setText(labelsOfPlayerNames.get(index).getText() + PUSH_CARD);
        lblAction.setTextFill(COLOR_LABELS[index.ordinal()]);
    }

    @Override
    public void displayMovePlayer(PlayerIndex index) {
        lblAction.setText(labelsOfPlayerNames.get(index).getText() + MOVE_PLAYER);
        lblAction.setTextFill(COLOR_LABELS[index.ordinal()]);
    }

    @Override
    public void invisibleButtons() {
        btnNewGame.setVisible(false);
        btnCloseGame.setVisible(false);
    }

    @Override
    public void invisibleActionText() {
        lblAction.setText("");
    }

    @Override
    public void showBtnStartGame() {
        btnStartGame.setVisible(true);
    }

}
