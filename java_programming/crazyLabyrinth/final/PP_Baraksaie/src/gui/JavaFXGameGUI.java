package gui;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;

import java.util.*;

/**
 * Main class for the graphical interface
 *
 * @author Miwand Baraksaie inf104162
 */
public class JavaFXGameGUI implements GUIGameConnector {

    /**
     * Path to way card image
     */
    private static final Image[] IMAGES_OF_WAY_CARDS = new Image[]{
            new Image("gui/img/I.png"),
            new Image("gui/img/L.png"),
            new Image("gui/img/T.png")
    };

    /**
     * Path to treasure images
     */
    private static final Image[] IMAGES_TREASURES = new Image[]{
            new Image("gui/img/s0.png"),
            new Image("gui/img/s1.png"),
            new Image("gui/img/s2.png"),
            new Image("gui/img/s3.png"),
            new Image("gui/img/s4.png"),
            new Image("gui/img/s5.png"),
            new Image("gui/img/s6.png"),
            new Image("gui/img/s7.png"),
            new Image("gui/img/s8.png"),
            new Image("gui/img/s9.png"),
            new Image("gui/img/s10.png"),
            new Image("gui/img/s11.png"),
            new Image("gui/img/s12.png"),
            new Image("gui/img/s13.png"),
            new Image("gui/img/s14.png"),
            new Image("gui/img/s15.png"),
            new Image("gui/img/s16.png"),
            new Image("gui/img/s17.png"),
            new Image("gui/img/s18.png"),
            new Image("gui/img/s19.png"),
            new Image("gui/img/s20.png"),
            new Image("gui/img/s21.png"),
            new Image("gui/img/s22.png"),
            new Image("gui/img/s23.png"),
            new Image("gui/img/s24.png")
    };

    /**
     * Path to arrow image
     */
    private static final Image ARROW = new Image("gui/img/arrow.png");

    /**
     * Styles for buttons
     */
    private static final String[] STYLE_CLASS_CSS_BUTTON = {
            "button-PlayerZero",
            "button-PlayerOne",
            "button-PlayerTwo",
            "button-PlayerThree"
    };

    /**
     * Button positions on grid pane of each button player
     * Mapped to player index
     */
    private static final Coordinate[] POSITION_OF_BUTTON_ON_WAY_CARD = {
            new Coordinate(0, 0), // mapped to Player Index
            new Coordinate(2, 0),
            new Coordinate(2, 2),
            new Coordinate(0, 2),
    };

    /**
     * Effects for highlighting a way card
     */
    private static final InnerShadow SHADOW_POSSIBLE_MOVEMENT = new InnerShadow(25.0f, Color.GREENYELLOW);
    private static final Effect NONE_EFFECT = null;
    private static final InnerShadow SHADOW_NOT_POSSIBLE_MOVEMENT = new InnerShadow(25.0f, Color.DARKRED);
    private static final InnerShadow SHADOW_NEXT_TREASURE = new InnerShadow(25.0f, Color.DARKVIOLET);

    /**
     * Error Text
     */

    private static String ERROR_TITLE = "ERROR!";
    private static String ERROR_ACTION = "What?";

    /**
     * Durations for animation
     */
    private Duration ANIMATION_DURATION_WAY_CARD;
    private Duration ANIMATION_DURATION_PLAYER;
    private static final Duration[] DURATIONS = {
            Duration.millis(0.0),   // none
            Duration.millis(100),  // fast
            Duration.millis(1000), // slow
            Duration.millis(750),  // debug
    };

    /**
     * Makes the treasure on the way card bigger, by using a negative margin
     */
    private static final int MARGIN_TREASURE = -11;
    private static final Insets TREASURE_INSET =
            new Insets(MARGIN_TREASURE, MARGIN_TREASURE, MARGIN_TREASURE, MARGIN_TREASURE);

    /**
     * Text above the free card
     */
    private static final String FREE_CARD_TEXT = "Free\nCard";

    /**
     * Constants used for fx operations
     */
    private final int Y_FREE_CARD = 4;
    private final int X_FREE_CARD = 9;
    private final int X_FIRST_WAY_CARD = 1;
    private final int X_FIRST_ARROW = 0;
    private final int X_LAST_ARROW = 8;
    private final int Y_FIRST_ARROW = 0;
    private final int Y_LAST_ARROW = 8;
    private final int X_LAST_WAY_CARD = 7;
    private final int Y_FIRST_WAY_CARD = 1;
    private final int Y_LAST_WAY_CARD = 7;
    private static final int POSITION_TREASURE_IN_GRID = 1;
    private int SIZE_GRID_ON_WAY_CARD = 3;
    private final int SIZE_PLAY_FIELD = 9;
    private final int SIZE_GRID_GAME_FIELD = 10;
    // multiply the duration of the player, when the player should respawn, if not the respawn is too fast.
    private final int MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD = 2;

    /**
     * Very important to map logical coordinates to fx coordinates,
     * it's necessary to add an offset
     */
    private static final int OFFSET_WAY_CARD = 1;

    /**
     * The stage is needed to handle able/disable resizing the window
     * when an animation is activated, it's not allowed to resize the window.
     */
    private final Stage currentStage;

    /**
     * fx class instances to make fx operations
     */
    private final GridPane grdPnGame;
    private ImageView[][] imageViews;
    private ImageView imgVwFreeCard;
    private final Label freeCardText;
    private final GridPane[][] gridPanesOnWayCards;
    private GridPane grdPnOnFreeCard;
    private GUIOutputConnector guiDisplay;
    /**
     * A button represents a player
     */
    private Map<PlayerIndex, Button> players;


    /**
     * Constructor, get called from the game controller
     *
     * @param stage               - current stage (resizing window)
     * @param grdPnGame           - grid pane mapped to imageViews
     * @param imageViews          - image views mapped to gridPanesOnWayCards
     * @param imgVwFreeCard       - image view of the free way card
     * @param freeCardText        - free card text above the free card
     * @param gridPanesOnWayCards - grid panes on the way card mapped to imageViews
     * @param gridOnFreeCard      - the grid pane on the free card
     * @param guiDisplay          - the gui display right
     */
    public JavaFXGameGUI(Stage stage, GridPane grdPnGame,
                         ImageView[][] imageViews,
                         ImageView imgVwFreeCard,
                         Label freeCardText,
                         GridPane[][] gridPanesOnWayCards,
                         GridPane gridOnFreeCard, GuiDisplay guiDisplay) {
        this.currentStage = stage;
        this.grdPnGame = grdPnGame;
        this.imageViews = imageViews;
        this.imgVwFreeCard = imgVwFreeCard;
        this.freeCardText = freeCardText;
        this.gridPanesOnWayCards = gridPanesOnWayCards;
        this.grdPnOnFreeCard = gridOnFreeCard;

        this.ANIMATION_DURATION_WAY_CARD = DURATIONS[2];
        this.ANIMATION_DURATION_PLAYER = DURATIONS[2];
        this.players = new HashMap<>();
        this.SIZE_GRID_ON_WAY_CARD = 3;
        this.guiDisplay = guiDisplay;
    }

    @Override
    public void setDurationPushCardAnimation(int index) {
        assert index >= 0 && index < DURATIONS.length;
        this.ANIMATION_DURATION_WAY_CARD = DURATIONS[index];
    }

    @Override
    public void setDurationPlayerMovementAnimation(int index) {
        assert index >= 0 && index < DURATIONS.length;
        this.ANIMATION_DURATION_PLAYER = DURATIONS[index];
    }

    @Override
    public void highLightAvailable(Coordinate pos_logical) {
        int x_fx = pos_logical.getX() + OFFSET_WAY_CARD;
        int y_fx = pos_logical.getY() + OFFSET_WAY_CARD;
        GridPane grdPn = gridPanesOnWayCards[x_fx][y_fx];
        ImageView imgVw = imageViews[x_fx][y_fx];
        setEffectOnCard(grdPn, imgVw, SHADOW_POSSIBLE_MOVEMENT);
    }

    @Override
    public void highLightTreasureCard(Coordinate pos_logical) {
        int x_fx = pos_logical.getX() + OFFSET_WAY_CARD;
        int y_fx = pos_logical.getY() + OFFSET_WAY_CARD;
        GridPane grdPn = gridPanesOnWayCards[x_fx][y_fx];
        ImageView imgVw = imageViews[x_fx][y_fx];
        setEffectOnCard(grdPn, imgVw, SHADOW_NEXT_TREASURE);
    }

    @Override
    public void highLightDisable(Coordinate pos_logical) {
        int x_fx = pos_logical.getX() + OFFSET_WAY_CARD;
        int y_fx = pos_logical.getY() + OFFSET_WAY_CARD;
        GridPane grdPn = gridPanesOnWayCards[x_fx][y_fx];
        ImageView imgVw = imageViews[x_fx][y_fx];
        setEffectOnCard(grdPn, imgVw, NONE_EFFECT);
    }

    /**
     * Set an effect to a way card
     *
     * @param pn     - grid pane
     * @param imgVw  - image view
     * @param effect - selected effect
     */
    private void setEffectOnCard(GridPane pn, ImageView imgVw, Effect effect) {
        pn.setEffect(effect);
        imgVw.setEffect(effect);
    }

    @Override
    public void highLightNotAvailable(Coordinate pos_logical) {
        gridPanesOnWayCards[pos_logical.getX() + OFFSET_WAY_CARD][pos_logical.getY() + OFFSET_WAY_CARD].setEffect(SHADOW_NOT_POSSIBLE_MOVEMENT);
        imageViews[pos_logical.getX() + OFFSET_WAY_CARD][pos_logical.getY() + OFFSET_WAY_CARD].setEffect(SHADOW_NOT_POSSIBLE_MOVEMENT);
    }

    @Override
    public void animateMovePlayerFromTo(PlayerIndex playerIndex, List<Coordinate> wayListToGo, GameField state) {
        // After clicking we are not anymore at state two.
        isResizeWindowAllowed(false);
        final int cellWidth = (int) (grdPnGame.getWidth() / SIZE_GRID_GAME_FIELD);
        int index = 0;
        Button currPlayer = players.get(playerIndex);
        // by deactivate this, the button will move behind the other cards
        currPlayer.getParent().toFront();
        SequentialTransition finish = new SequentialTransition();
        Coordinate moveFirst = POSITION_OF_BUTTON_ON_WAY_CARD[playerIndex.ordinal()];
        double distanceXCenter = (POSITION_TREASURE_IN_GRID - moveFirst.getX()) * (float) cellWidth / SIZE_GRID_ON_WAY_CARD;
        double distanceYCenter = (POSITION_TREASURE_IN_GRID - moveFirst.getY()) * (float) cellWidth / SIZE_GRID_ON_WAY_CARD;
        finish.getChildren().add(addParallelAnimationTo(new ParallelTransition(), distanceXCenter, distanceYCenter, this.ANIMATION_DURATION_PLAYER, currPlayer));
        // add the ways to the animation
        while (index < wayListToGo.size() - 1) {
            Coordinate curr = wayListToGo.get(index);
            Coordinate next = wayListToGo.get(index + 1);
            double distanceX = (next.getX() - curr.getX()) * cellWidth;
            double distanceY = (next.getY() - curr.getY()) * cellWidth;
            finish.getChildren().add(addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY, this.ANIMATION_DURATION_PLAYER, currPlayer));
            index++;
        }
        Coordinate finalPosition = wayListToGo.get(wayListToGo.size() - 1);
        highLightDisable(finalPosition);
        //add position in grid pane to animation
        double distanceXPos = (moveFirst.getX() - POSITION_TREASURE_IN_GRID) * (float) cellWidth / SIZE_GRID_ON_WAY_CARD;
        double distanceYPos = (moveFirst.getY() - POSITION_TREASURE_IN_GRID) * (float) cellWidth / SIZE_GRID_ON_WAY_CARD;
        finish.getChildren().add(addParallelAnimationTo(new ParallelTransition(), distanceXPos, distanceYPos, this.ANIMATION_DURATION_PLAYER, currPlayer));

        finish.onFinishedProperty().set(actionEvent -> {
            isResizeWindowAllowed(true);
            Coordinate coordinateOnWayCard = POSITION_OF_BUTTON_ON_WAY_CARD[playerIndex.ordinal()];
            int newX = finalPosition.getX();
            int newY = finalPosition.getY();
            // set values to default
            currPlayer.setManaged(false);
            GridPane grd = gridPanesOnWayCards[newX + OFFSET_WAY_CARD][newY + OFFSET_WAY_CARD];
            grd.getChildren().remove(currPlayer);
            grd.add(currPlayer, coordinateOnWayCard.getX(), coordinateOnWayCard.getY());
            currPlayer.setTranslateX(0);
            currPlayer.setTranslateY(0);
            currPlayer.setManaged(true);
            state.setStateOneCheckTreasureAfterMoving();
        });
        finish.play();
    }

    /**
     * Will respawn the player, to the new position after pushing it out.
     *
     * @param respawnPos_logical - respawn position in a map
     */
    private void respawnPlayersToNewCoordinate(Map<PlayerIndex, Coordinate> respawnPos_logical) {
        respawnPos_logical.keySet().forEach(playerIndex -> {
            Coordinate currCoordinate = respawnPos_logical.get(playerIndex);
            Coordinate coordinateOnWayCard = POSITION_OF_BUTTON_ON_WAY_CARD[playerIndex.ordinal()];
            players.get(playerIndex).setManaged(false);
            gridPanesOnWayCards[currCoordinate.getX() + OFFSET_WAY_CARD][currCoordinate.getY() + OFFSET_WAY_CARD].add(players.get(playerIndex), coordinateOnWayCard.getX(), coordinateOnWayCard.getY());
            players.get(playerIndex).setTranslateX(0);
            players.get(playerIndex).setTranslateY(0);
            players.get(playerIndex).setManaged(true);

        });
    }

    /**
     * Will set the free card text
     */
    public void setFreeCardText() {
        freeCardText.setText(FREE_CARD_TEXT);
    }

    /**
     * Sets the image view and the grid pane of the way card to new position
     *
     * @param iv   - image view of the way card
     * @param grid - grid pane on the way card
     * @param x_fx - the new position
     * @param y_fx - the new position
     */
    private void setWayCardTo(ImageView iv, GridPane grid, int x_fx, int y_fx) {
        setGridIndicesAndResetTranslate(iv, grid, x_fx, y_fx);
        imageViews[x_fx][y_fx] = iv;
        gridPanesOnWayCards[x_fx][y_fx] = grid;
    }

    /**
     * Will set the new free card, to the free card position
     *
     * @param iv   - image view of the new free card
     * @param grid - grid pane of the new free card
     */
    private void setNewFreeCard(ImageView iv, GridPane grid) {
        setGridIndicesAndResetTranslate(iv, grid, X_FREE_CARD, Y_FREE_CARD);
        imgVwFreeCard = iv;
        grdPnOnFreeCard = grid;
        imgVwFreeCard.toFront();
        grdPnOnFreeCard.toFront();
    }

    /**
     * Will set the new position in the grid pane, after removing it from the old
     * grid pane position
     *
     * @param iv   - image of the way card
     * @param grid - the grid on the way card
     * @param x_fx - the new x position of the grid pane cell
     * @param y_fx - the new y position of the grid pane cell
     */
    private void setGridIndicesAndResetTranslate(ImageView iv, GridPane grid, int x_fx, int y_fx) {
        iv.setManaged(false);
        grid.setManaged(false);
        GridPane.setColumnIndex(iv, x_fx);
        GridPane.setRowIndex(iv, y_fx);
        GridPane.setColumnIndex(grid, x_fx);
        GridPane.setRowIndex(grid, y_fx);
        iv.setTranslateX(0);
        iv.setTranslateY(0);
        grid.setTranslateX(0);
        grid.setTranslateY(0);
        iv.setManaged(true);
        grid.setManaged(true);
    }

    @Override
    public void showFreeCard(WayCard freeCard) {
        this.imgVwFreeCard.setImage(IMAGES_OF_WAY_CARDS[freeCard.getWayType().ordinal()]);
        rotate(this.imgVwFreeCard, freeCard.getRotation());
        if (freeCard.getTreasure() != Treasure.NONE) {
            showTreasureCard(freeCard, grdPnOnFreeCard);
        }
    }

    @Override
    public void rotateFreeCard(WayCard freeCard) {
        // don't draw treasure multiple.
        if (freeCard.getTreasure() != Treasure.NONE) {
            removeTreasureCard(grdPnOnFreeCard);
        }
        this.showFreeCard(freeCard);
    }

    @Override
    public void showWayCard(WayCard wayCard, int x_logical, int y_logical) {
        ImageView img = imageViews[x_logical + OFFSET_WAY_CARD][y_logical + OFFSET_WAY_CARD];
        img.setImage(IMAGES_OF_WAY_CARDS[wayCard.getWayType().ordinal()]);
        rotate(img, wayCard.getRotation());

        if (wayCard.getTreasure() != Treasure.NONE) {
            showTreasureCard(wayCard, gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][y_logical + OFFSET_WAY_CARD]);
        }
    }

    @Override
    public void initializeArrows() {

        // first row with arrow
        for (int i = 2; i < 7; i = i + 2) {
            imageViews[i][0].setImage(ARROW);
            rotate(imageViews[i][0], Rotation.NINETY);
        }

        // last row with arrow
        for (int i = 2; i < 7; i = i + 2) {
            // this.grdPnGame.add(pane, 8, i);
            imageViews[i][8].setImage(ARROW);
            rotate(imageViews[i][8], Rotation.TWO_HUNDRED_SEVENTY);

        }

        // first col with arrows
        for (int i = 2; i < 7; i = i + 2) {
            imageViews[0][i].setImage(ARROW);
            rotate(imageViews[0][i], Rotation.ZERO);
        }

        // last col with arrows
        for (int i = 2; i < 7; i = i + 2) {
            imageViews[8][i].setImage(ARROW);
            rotate(imageViews[8][i], Rotation.HUNDRED_EIGHTY);
        }
    }

    @Override
    public void initializeAndShowPlayer(Player player) {
        int x = player.getPosition().getX() + OFFSET_WAY_CARD;
        int y = player.getPosition().getY() + OFFSET_WAY_CARD;
        GridPane grid = gridPanesOnWayCards[x][y];
        Button button = new Button();
        button.getStyleClass().add(STYLE_CLASS_CSS_BUTTON[player.getPlayerIndex().ordinal()]);
        button.prefHeightProperty().bind(grdPnGame.widthProperty().divide(SIZE_GRID_GAME_FIELD * SIZE_GRID_ON_WAY_CARD + MARGIN_TREASURE));
        button.prefWidthProperty().bind(grdPnGame.widthProperty().divide(SIZE_GRID_GAME_FIELD * SIZE_GRID_ON_WAY_CARD + MARGIN_TREASURE));
        Coordinate positionOnWayCard = POSITION_OF_BUTTON_ON_WAY_CARD[player.getPlayerIndex().ordinal()];
        grid.add(button, positionOnWayCard.getX(), positionOnWayCard.getY());
        button.toFront();
        players.put(player.getPlayerIndex(), button);
        guiDisplay.initNameAndTreasure(player.getPlayerIndex(), player.getName(), player.getTreasures().size());

    }

    /**
     * Adds parallel transition to a given parallel transition.
     * Allows any desired number of nodes to put in this transition.
     * But all with the same direction. Useful for animate a whole row or column.
     *
     * @param transition - add the new transition to this given transition
     * @param distanceX  - the distance x, which a node shall move
     * @param distanceY  - the distance y, which a node shall move
     * @param duration   - a duration
     * @param nodes      - any desired number of nodes
     * @return a parallel transition
     */
    private ParallelTransition addParallelAnimationTo(ParallelTransition transition, double distanceX, double distanceY, Duration duration, Node... nodes) {
        for (int i = 0; i < nodes.length; i++) {
            TranslateTransition trans = new TranslateTransition(duration, nodes[i]);
            trans.byXProperty().set(distanceX);
            trans.byYProperty().set(distanceY);
            transition.getChildren().add(trans);
        }
        return transition;
    }

    @Override
    public void animatePushHorizontalLeftToRightAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {
        /*
        Animation from free card cell to clicked arrow cell
         */
        isResizeWindowAllowed(false);
        SequentialTransition allCombined = new SequentialTransition();

        final double cellWidth = (grdPnGame.getWidth() / SIZE_GRID_GAME_FIELD);
        double distanceX = -SIZE_PLAY_FIELD * cellWidth;
        double distanceY = (y_logical + OFFSET_WAY_CARD - Y_FREE_CARD) * cellWidth;

        // multiply the animation duration of the free way card, to new position with the multiply factor, because of the distance
        ParallelTransition moveFreeWayCardToClickedCell = addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwFreeCard, grdPnOnFreeCard);

        // now move the complete row.
        distanceX = cellWidth;
        distanceY = 0;
        ParallelTransition moveTheRow = addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                ANIMATION_DURATION_WAY_CARD, imgVwFreeCard, grdPnOnFreeCard);

        for (int i = X_LAST_WAY_CARD; i > X_FIRST_WAY_CARD - 1; i--) {
            ImageView iv = imageViews[i][y_logical + OFFSET_WAY_CARD];
            GridPane grid = gridPanesOnWayCards[i][y_logical + OFFSET_WAY_CARD];
            // no multiply needed because of the distance between the neighbors
            addParallelAnimationTo(moveTheRow, cellWidth, 0,
                    ANIMATION_DURATION_WAY_CARD, iv, grid);
        }

        // Set managed already false
        // move the new free card to his cell.
        ImageView imgVwNewFreeCard = imageViews[X_LAST_WAY_CARD][y_logical + OFFSET_WAY_CARD];
        GridPane grdPnNewFreeCard = gridPanesOnWayCards[X_LAST_WAY_CARD][y_logical + OFFSET_WAY_CARD];

        // the order is important here.
        // this is for the animation, so the child is not moving behind a way card
        imgVwNewFreeCard.toFront();
        imgVwFreeCard.toFront();
        grdPnNewFreeCard.toFront();
        grdPnOnFreeCard.toFront();


        // very important: push the panes to front, so they do not animate behind other way cards
        distanceX = cellWidth;
        distanceY = (Y_FREE_CARD - (y_logical + OFFSET_WAY_CARD)) * cellWidth;


        ParallelTransition moveTheNewWayCardToCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwNewFreeCard, grdPnNewFreeCard);

        allCombined.getChildren().add(moveFreeWayCardToClickedCell);
        allCombined.getChildren().add(moveTheRow);
        allCombined.getChildren().add(moveTheNewWayCardToCell);

        if (!respawnCoordinates.isEmpty()) {
            ParallelTransition movePlayerToNewPosition = new ParallelTransition();
            respawnCoordinates.keySet().forEach(index -> {
                double distancePlayerX = -cellWidth * (X_FREE_CARD - OFFSET_WAY_CARD);
                double distancePlayerY = ((y_logical + OFFSET_WAY_CARD) - Y_FREE_CARD) * cellWidth;
                addParallelAnimationTo(movePlayerToNewPosition, distancePlayerX, distancePlayerY,
                        // multiply the because of the distance of respawn
                        ANIMATION_DURATION_PLAYER.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), players.get(index));
            });
            allCombined.getChildren().add(movePlayerToNewPosition);
        }

        setArrowsHorizontalVisibility(y_logical, false);

        allCombined.onFinishedProperty().set(actionEvent -> {
            isResizeWindowAllowed(true);
            JavaFXGameGUI.this.sortHorizontalLeftToRight(y_logical);
            setArrowHorizontalVisibilityLeft(y_logical, true);
            if (!respawnCoordinates.isEmpty()) {
                this.respawnPlayersToNewCoordinate(respawnCoordinates);
            }
            gameState.setStateTwoCheckTreasureRespawn();

        });

        allCombined.play();
    }

    @Override
    public void animatePushHorizontalRightToLeftAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

        isResizeWindowAllowed(false);
        // Animation from free card cell to clicked arrow cell
        SequentialTransition allCombined = new SequentialTransition();
        final double cellWidth = grdPnGame.getWidth() / SIZE_GRID_GAME_FIELD;
        double distanceX = -cellWidth;
        double distanceY = (y_logical + OFFSET_WAY_CARD - Y_FREE_CARD) * cellWidth;
        ParallelTransition moveFreeWayCardToClickedCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwFreeCard, grdPnOnFreeCard);

        // now move the complete row.
        distanceX = -cellWidth;
        distanceY = 0;
        ParallelTransition moveTheRow = addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                ANIMATION_DURATION_WAY_CARD, imgVwFreeCard, grdPnOnFreeCard);

        for (int i = X_LAST_WAY_CARD; i > X_FIRST_WAY_CARD - 1; i--) {
            ImageView iv = imageViews[i][y_logical + OFFSET_WAY_CARD];
            GridPane grid = gridPanesOnWayCards[i][y_logical + OFFSET_WAY_CARD];

            addParallelAnimationTo(moveTheRow, -cellWidth, 0,
                    ANIMATION_DURATION_WAY_CARD, iv, grid);
        }

        // set manged is false because of for - loop.
        // move the new free card to his cell.
        ImageView imgVwNewFreeCard = imageViews[X_FIRST_WAY_CARD][y_logical + OFFSET_WAY_CARD];
        GridPane grdPnNewFreeCard = gridPanesOnWayCards[X_FIRST_WAY_CARD][y_logical + OFFSET_WAY_CARD];

        // the order is important here.
        // this is for the animation, so the child is not moving behind a way card
        imgVwFreeCard.toFront();
        grdPnOnFreeCard.toFront();
        imgVwNewFreeCard.toFront();
        grdPnNewFreeCard.toFront();

        // here it is not necessary to push the way card  to front
        // animation to front
        distanceX = cellWidth * SIZE_PLAY_FIELD;
        distanceY = (Y_FREE_CARD - (y_logical + OFFSET_WAY_CARD)) * cellWidth;

        ParallelTransition moveTheNewWayCardToCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwNewFreeCard, grdPnNewFreeCard);


        // move the new free card to his cell.
        allCombined.getChildren().add(moveFreeWayCardToClickedCell);
        allCombined.getChildren().add(moveTheRow);
        allCombined.getChildren().add(moveTheNewWayCardToCell);

        // respawn player if there
        if (!respawnCoordinates.isEmpty()) {
            ParallelTransition movePlayerToNewPosition = new ParallelTransition();
            respawnCoordinates.keySet().forEach(index -> {
                double distancePlayerX = -cellWidth * 2;
                double distancePlayerY = ((y_logical + OFFSET_WAY_CARD) - Y_FREE_CARD) * cellWidth;

                addParallelAnimationTo(movePlayerToNewPosition, distancePlayerX, distancePlayerY,
                        ANIMATION_DURATION_PLAYER.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), players.get(index));
            });
            allCombined.getChildren().add(movePlayerToNewPosition);
        }

        // make arrow disappear
        setArrowsHorizontalVisibility(y_logical, false);

        allCombined.onFinishedProperty().set(actionEvent -> {
            JavaFXGameGUI.this.sortHorizontalRightToLeft(y_logical);
            setArrowHorizontalVisibilityRight(y_logical, true);
            if (!respawnCoordinates.isEmpty()) {
                this.respawnPlayersToNewCoordinate(respawnCoordinates);
            }
            isResizeWindowAllowed(true);
            gameState.setStateTwoCheckTreasureRespawn();
        });
        allCombined.play();
    }

    @Override
    public void animatePushVerticalUpToDownAndRespawnPlayers(int x_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {
        isResizeWindowAllowed(false);
        // Animation from free card cell to clicked arrow cell
        SequentialTransition allCombined = new SequentialTransition();
        final double cellWidth = grdPnGame.getWidth() / SIZE_GRID_GAME_FIELD;

        double distanceX = (x_logical + OFFSET_WAY_CARD - X_FREE_CARD) * cellWidth;
        double distanceY = -Y_FREE_CARD * cellWidth;
        ParallelTransition moveFreeWayCardToClickedCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwFreeCard, grdPnOnFreeCard);

        // now move the complete column.
        distanceX = 0;
        distanceY = cellWidth;
        ParallelTransition moveTheRow = addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                ANIMATION_DURATION_WAY_CARD, imgVwFreeCard, grdPnOnFreeCard);

        // add all nodes of the column, to a parallel transition
        for (int i = Y_FIRST_WAY_CARD; i < Y_LAST_WAY_CARD + 1; i++) {
            ImageView iv = imageViews[x_logical + OFFSET_WAY_CARD][i];
            GridPane grid = gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][i];
            addParallelAnimationTo(moveTheRow, 0, cellWidth,
                    ANIMATION_DURATION_WAY_CARD, iv, grid);
        }

        // set manged is false because of for - loop.
        // move the new free card to his cell.
        ImageView imgVwNewFreeCard = imageViews[x_logical + OFFSET_WAY_CARD][Y_LAST_WAY_CARD];
        GridPane grdPnNewFreeCard = gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][Y_LAST_WAY_CARD];

        // the order is important here.
        // this is for the animation, so the child is not moving behind a way card
        imgVwFreeCard.toFront();
        grdPnOnFreeCard.toFront();
        imgVwNewFreeCard.toFront();
        grdPnNewFreeCard.toFront();


        distanceX = (X_FREE_CARD - (x_logical + OFFSET_WAY_CARD)) * cellWidth;
        distanceY = -Y_FREE_CARD * cellWidth;

        ParallelTransition moveTheNewWayCardToCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwNewFreeCard, grdPnNewFreeCard);

        // move the new free card to his cell.
        allCombined.getChildren().add(moveFreeWayCardToClickedCell);
        allCombined.getChildren().add(moveTheRow);
        allCombined.getChildren().add(moveTheNewWayCardToCell);

        // handle respawn players, also animated
        if (!respawnCoordinates.isEmpty()) {
            ParallelTransition movePlayerToNewPosition = new ParallelTransition();
            respawnCoordinates.keySet().forEach(index -> {
                double distancePlayerX = -(X_FREE_CARD - (x_logical + OFFSET_WAY_CARD)) * cellWidth;
                double distancePlayerY = -(Y_LAST_WAY_CARD - Y_FREE_CARD) * cellWidth;
                addParallelAnimationTo(movePlayerToNewPosition, distancePlayerX, distancePlayerY,
                        ANIMATION_DURATION_PLAYER.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), players.get(index));
            });
            allCombined.getChildren().add(movePlayerToNewPosition);
        }

        // make arrow disappear
        setArrowsVerticalVisibility(x_logical, false);
        allCombined.onFinishedProperty().set(actionEvent -> {
            JavaFXGameGUI.this.sortVerticalFromUpToDown(x_logical);
            setArrowVerticalVisibilityUp(x_logical, true);
            if (!respawnCoordinates.isEmpty()) {
                this.respawnPlayersToNewCoordinate(respawnCoordinates);
            }
            isResizeWindowAllowed(true);

            // set the new state
            gameState.setStateTwoCheckTreasureRespawn();
        });
        allCombined.play();
    }

    @Override
    public void animatePushVerticalDownToUpAndRespawnPlayers(int logical_x, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

        isResizeWindowAllowed(false);
        // Animation from old free card cell to clicked arrow cell
        SequentialTransition allCombined = new SequentialTransition();

        final double cellWidth = grdPnGame.getWidth() / SIZE_GRID_GAME_FIELD;

        double distanceX = (logical_x + OFFSET_WAY_CARD - X_FREE_CARD) * cellWidth;
        double distanceY = Y_FREE_CARD * cellWidth;
        ParallelTransition moveFreeWayCardToClickedCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwFreeCard, grdPnOnFreeCard);

        // now move the complete column.
        distanceX = 0;
        distanceY = -cellWidth;
        // free card is not in iv and grid array, so do it once special
        ParallelTransition moveTheRow = addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                ANIMATION_DURATION_WAY_CARD, imgVwFreeCard, grdPnOnFreeCard);

        // move the whole column
        for (int i = Y_FIRST_WAY_CARD; i < Y_LAST_WAY_CARD + 1; i++) {
            ImageView iv = imageViews[logical_x + OFFSET_WAY_CARD][i];
            GridPane grid = gridPanesOnWayCards[logical_x + OFFSET_WAY_CARD][i];
            addParallelAnimationTo(moveTheRow, 0, distanceY,
                    ANIMATION_DURATION_WAY_CARD, iv, grid);
        }

        // set manged is false because of for - loop.
        // move the new free card to his cell.
        ImageView imgVwNewFreeCard = imageViews[logical_x + OFFSET_WAY_CARD][Y_FIRST_WAY_CARD];
        GridPane grdPnNewFreeCard = gridPanesOnWayCards[logical_x + OFFSET_WAY_CARD][Y_FIRST_WAY_CARD];

        // the order is important here.
        // this is for the animation, so the child is not moving behind a way card
        imgVwFreeCard.toFront();
        grdPnOnFreeCard.toFront();
        imgVwNewFreeCard.toFront();
        grdPnNewFreeCard.toFront();

        distanceX = (X_FREE_CARD - (logical_x + OFFSET_WAY_CARD)) * cellWidth;
        distanceY = Y_FREE_CARD * cellWidth;

        // move the new free card to his cell
        ParallelTransition moveTheNewWayCardToCell =
                addParallelAnimationTo(new ParallelTransition(), distanceX, distanceY,
                        ANIMATION_DURATION_WAY_CARD.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), imgVwNewFreeCard, grdPnNewFreeCard);

        // move the new free card to his cell.
        allCombined.getChildren().add(moveFreeWayCardToClickedCell);
        allCombined.getChildren().add(moveTheRow);
        allCombined.getChildren().add(moveTheNewWayCardToCell);

        // handle the respawn player, also with animation
        if (!respawnCoordinates.isEmpty()) {
            ParallelTransition movePlayerToNewPosition = new ParallelTransition();
            respawnCoordinates.keySet().forEach(index -> {
                double distancePlayerX = -(X_FREE_CARD - (logical_x + OFFSET_WAY_CARD)) * cellWidth;
                double distancePlayerY = (Y_LAST_WAY_CARD - Y_FREE_CARD) * cellWidth;
                addParallelAnimationTo(movePlayerToNewPosition, distancePlayerX, distancePlayerY,
                        ANIMATION_DURATION_PLAYER.multiply(MULTIPLY_FACTOR_RESPAWN_AND_FREE_WAY_CARD), players.get(index));
            });
            allCombined.getChildren().add(movePlayerToNewPosition);
        }

        // make arrow disappear
        setArrowsVerticalVisibility(logical_x, false);
        allCombined.onFinishedProperty().set(actionEvent -> {
            JavaFXGameGUI.this.sortVerticalDownToUp(logical_x);
            setArrowVerticalVisibilityDown(logical_x, true);
            if (!respawnCoordinates.isEmpty()) {
                this.respawnPlayersToNewCoordinate(respawnCoordinates);
            }
            isResizeWindowAllowed(true);
            gameState.setStateTwoCheckTreasureRespawn();
        });
        allCombined.play();
    }

    /**
     * Sets the visibility of parallel arrows
     *
     * @param y_logical - logical position add off set
     * @param isVisible - flag
     */
    private void setArrowsHorizontalVisibility(int y_logical, boolean isVisible) {
        setArrowHorizontalVisibilityLeft(y_logical, isVisible);
        setArrowHorizontalVisibilityRight(y_logical, isVisible);
    }

    /**
     * Sets the visibility left, to a given position
     *
     * @param y_logical - add off set to get real position
     * @param isVisible - flag
     */
    private void setArrowHorizontalVisibilityLeft(int y_logical, boolean isVisible) {
        imageViews[X_FIRST_ARROW][y_logical + OFFSET_WAY_CARD].setVisible(isVisible);
    }

    /**
     * Sets the visibility right, to a given position
     *
     * @param y_logical - add off set to get real position
     * @param isVisible - flag
     */
    private void setArrowHorizontalVisibilityRight(int y_logical, boolean isVisible) {
        imageViews[X_LAST_ARROW][y_logical + OFFSET_WAY_CARD].setVisible(isVisible);
    }

    /**
     * Sets the visibility of parallel arrows
     *
     * @param x_logical - logical position add off set
     * @param isVisible - flag
     */

    private void setArrowsVerticalVisibility(int x_logical, boolean isVisible) {
        setArrowVerticalVisibilityDown(x_logical, isVisible);
        setArrowVerticalVisibilityUp(x_logical, isVisible);
    }

    /**
     * Sets the visibility of the arrow
     *
     * @param x_logical - add off set to get real x coordinate
     * @param isVisible - is visible
     */
    private void setArrowVerticalVisibilityDown(int x_logical, boolean isVisible) {
        imageViews[x_logical + OFFSET_WAY_CARD][Y_LAST_ARROW].setVisible(isVisible);
    }

    /**
     * Sets the visibility of the arrow
     *
     * @param x_logical - add off set to get real x coordinate
     * @param isVisible - is visible
     */
    private void setArrowVerticalVisibilityUp(int x_logical, boolean isVisible) {
        imageViews[x_logical + OFFSET_WAY_CARD][Y_FIRST_ARROW].setVisible(isVisible);
    }

    /**
     * Sort a row from right to left
     * Its possible to fusion: sortHorizontalRightToLeft and sortHorizontalLeftToRight, but than, it is not so good
     * to understand. Because of this here are two separate methods.
     *
     * @param y_logical - the row to sort, add offset to get the coordinate
     */
    private void sortHorizontalRightToLeft(int y_logical) {
        ImageView lastCard = imageViews[X_FIRST_WAY_CARD][y_logical + OFFSET_WAY_CARD];
        GridPane lastGrid = gridPanesOnWayCards[X_FIRST_WAY_CARD][y_logical + OFFSET_WAY_CARD];

        for (int i = X_FIRST_WAY_CARD; i < X_LAST_WAY_CARD; i++) {
            setWayCardTo(imageViews[i + 1][y_logical + OFFSET_WAY_CARD], gridPanesOnWayCards[i + 1][y_logical + OFFSET_WAY_CARD], i, y_logical + OFFSET_WAY_CARD);
        }

        setWayCardTo(imgVwFreeCard, grdPnOnFreeCard, X_LAST_WAY_CARD, y_logical + OFFSET_WAY_CARD);
        setNewFreeCard(lastCard, lastGrid);
    }

    /**
     * Sort a row from left to right.
     * It is possible to fusion: sortHorizontalRightToLeft and sortHorizontalLeftToRight, but than, it is not so good
     * to understand. Because of this here are two separate methods.
     *
     * @param y_logical - the row to sort, add offset to get the real position
     */
    private void sortHorizontalLeftToRight(int y_logical) {
        ImageView lastCard = imageViews[X_LAST_WAY_CARD][y_logical + OFFSET_WAY_CARD];
        GridPane lastGrid = gridPanesOnWayCards[X_LAST_WAY_CARD][y_logical + OFFSET_WAY_CARD];

        for (int i = X_LAST_WAY_CARD; i > X_FIRST_WAY_CARD; i--) {
            setWayCardTo(imageViews[i - 1][y_logical + OFFSET_WAY_CARD], gridPanesOnWayCards[i - 1][y_logical + OFFSET_WAY_CARD], i, y_logical + OFFSET_WAY_CARD);
        }
        setWayCardTo(imgVwFreeCard, grdPnOnFreeCard, X_FIRST_WAY_CARD, y_logical + OFFSET_WAY_CARD);
        setNewFreeCard(lastCard, lastGrid);
    }

    /**
     * Sorts a column up left to down.
     * It is possible to fusion: ortVerticalFromUpToDown and sortVerticalFromDownToUp, but than, it is not so good
     * to understand. Because of this here are two separate methods.
     *
     * @param x_logical - the row to sort, add offset to get the x coordinate
     */

    private void sortVerticalFromUpToDown(int x_logical) {
        ImageView lastCard = imageViews[x_logical + OFFSET_WAY_CARD][Y_LAST_WAY_CARD];
        GridPane lastGrid = gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][Y_LAST_WAY_CARD];

        for (int i = Y_LAST_WAY_CARD; i > Y_FIRST_WAY_CARD; i--) {
            setWayCardTo(imageViews[x_logical + OFFSET_WAY_CARD][i - 1], gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][i - 1], x_logical + OFFSET_WAY_CARD, i);
        }
        setWayCardTo(imgVwFreeCard, grdPnOnFreeCard, x_logical + OFFSET_WAY_CARD, Y_FIRST_WAY_CARD);
        setNewFreeCard(lastCard, lastGrid);
    }

    /**
     * Sorts a column up left to down
     * It is possible to fusion: ortVerticalFromUpToDown and sortVerticalFromDownToUp, but than, it is not so good
     * to understand. Because of this here are two separate methods.
     *
     * @param x_logical - the row to sort, add off get the real x coordinate.
     */
    private void sortVerticalDownToUp(int x_logical) {
        ImageView lastCard = imageViews[x_logical + OFFSET_WAY_CARD][Y_FIRST_WAY_CARD];
        GridPane lastGrid = gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][Y_FIRST_WAY_CARD];

        for (int i = Y_FIRST_WAY_CARD; i < Y_LAST_WAY_CARD; i++) {
            setWayCardTo(imageViews[x_logical + OFFSET_WAY_CARD][i + 1], gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][i + 1], x_logical + OFFSET_WAY_CARD, i);
        }
        setWayCardTo(imgVwFreeCard, grdPnOnFreeCard, x_logical + OFFSET_WAY_CARD, Y_LAST_WAY_CARD);
        setNewFreeCard(lastCard, lastGrid);
    }

    /**
     * Will rotate the image to a given rotation
     *
     * @param img      - image view
     * @param rotation - set rotation angle
     */
    private void rotate(ImageView img, Rotation rotation) {
        int i = 0;
        double angle = 0;
        while (i < rotation.ordinal()) {
            angle += 90;
            i++;
        }
        img.setRotate(angle);
    }

    /**
     * Shows the treasure of a way card, it will create a new image view,
     * which will added to the given grid, at a fixed position
     *
     * @param wayCard - way card with specific treasure in it
     * @param grid    - the grid where the treasure shall be shown
     */
    private void showTreasureCard(WayCard wayCard, GridPane grid) {
        ImageView treasure = new ImageView(IMAGES_TREASURES[wayCard.getTreasure().ordinal()]);
        rotate(treasure, wayCard.getRotation());
        GridPane.setMargin(treasure, TREASURE_INSET);
        treasure.fitWidthProperty().bind(grdPnGame.widthProperty().divide(SIZE_GRID_GAME_FIELD * SIZE_GRID_ON_WAY_CARD + MARGIN_TREASURE));
        treasure.fitHeightProperty().bind(grdPnGame.heightProperty().divide(SIZE_GRID_GAME_FIELD * SIZE_GRID_ON_WAY_CARD + MARGIN_TREASURE));
        grid.add(treasure, POSITION_TREASURE_IN_GRID, POSITION_TREASURE_IN_GRID);
    }

    /**
     * Removes the treasure imageview, which is in the grid pane.
     *
     * @param grid - the grid, which contains the image view of the treasure, which shall get removed
     */
    private void removeTreasureCard(GridPane grid) {
        grid.getChildren().remove(getChildrenInPaneAt(grid, POSITION_TREASURE_IN_GRID, POSITION_TREASURE_IN_GRID).get(0));
    }

    @Override
    public void removeTreasureCardUpdateTreasuresLeft(int x_logical, int y_logical, PlayerIndex index, int treasuresLeft) {
        removeTreasureCard(gridPanesOnWayCards[x_logical + OFFSET_WAY_CARD][y_logical + OFFSET_WAY_CARD]);
        guiDisplay.updateDisplayTreasuresLeft(index, treasuresLeft);
    }

    @Override
    public void displayShowCurrentPlayer(Player player) {
        guiDisplay.updateDisplayCurrentTurn(player.getPlayerIndex());
    }


    @Override
    public void deleteWayCardImage(int logical_x, int logical_y, boolean hasTreasure) {
        int x = logical_x + OFFSET_WAY_CARD;
        int y = logical_y + OFFSET_WAY_CARD;
        if (hasTreasure) {
            removeTreasureCard(gridPanesOnWayCards[x][y]);
        }
    }

    @Override
    public void deleteFreeCardImage(boolean hasTreasure) {
        if (hasTreasure) {
            removeTreasureCard(grdPnOnFreeCard);
        }
    }


    @Override
    public void removePlayer(PlayerIndex index, Coordinate pos_logical) {
        int scene_x = pos_logical.getX() + OFFSET_WAY_CARD;
        int scene_y = pos_logical.getY() + OFFSET_WAY_CARD;
        Button player = players.get(index);
        player.setManaged(false);
        gridPanesOnWayCards[scene_x][scene_y].getChildren().remove(player);
        players.remove(player);
        guiDisplay.remove(index);
    }

    @Override
    public void displayHasWon(PlayerIndex playerIndex) {
        guiDisplay.showWinner(playerIndex);
    }

    @Override
    public void displayActionPushCard(PlayerIndex index) {
        guiDisplay.displayPushCard(index);

    }

    @Override
    public void displayActionMovePlayer(PlayerIndex index) {
        guiDisplay.displayMovePlayer(index);
    }

    @Override
    public void displayError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_TITLE);
        alert.setHeaderText(ERROR_ACTION);
        if (exception instanceof IllegalInputException) {
            alert.setContentText(exception.getMessage());
        } else {
            alert.setContentText("UNKNOWN ERROR: PLEASE START NEW");
        }
        alert.showAndWait();
    }

    @Override
    public void textAndButtonsToDefault() {
        guiDisplay.invisibleButtons();
        guiDisplay.invisibleActionText();
    }

    @Override
    public void highLightFreeWayCard() {
        setEffectOnCard(grdPnOnFreeCard, imgVwFreeCard, SHADOW_NEXT_TREASURE);
    }

    @Override
    public void highLightDisableFreeWayCard() {
        setEffectOnCard(grdPnOnFreeCard, imgVwFreeCard, NONE_EFFECT);
    }

    @Override
    public void disableAllHighLighting() {
        for (int x_scene = X_FIRST_WAY_CARD; x_scene <= X_LAST_WAY_CARD; x_scene++) {
            for (int y_scene = Y_FIRST_WAY_CARD; y_scene <= Y_LAST_WAY_CARD; y_scene++) {
                GridPane grdPn = gridPanesOnWayCards[x_scene][y_scene];
                ImageView imgVw = imageViews[x_scene][y_scene];
                setEffectOnCard(grdPn, imgVw, NONE_EFFECT);
            }
        }
        setEffectOnCard(grdPnOnFreeCard, imgVwFreeCard, NONE_EFFECT);
    }

    @Override
    public void setArrowVisibility(Direction dir, Coordinate pos_blocked, boolean isVisible) {
        switch (dir) {
            case UP:
                setArrowVerticalVisibilityDown(pos_blocked.getX(), isVisible);
                break;
            case RIGHT:
                setArrowHorizontalVisibilityLeft(pos_blocked.getY(), isVisible);
                break;
            case DOWN:
                setArrowVerticalVisibilityUp(pos_blocked.getX(), isVisible);
                break;
            case LEFT:
                setArrowHorizontalVisibilityRight(pos_blocked.getY(), isVisible);
                break;
        }
    }

    @Override
    public void showBtnStartGame() {
        guiDisplay.showBtnStartGame();
    }


    /**
     * List of all children in the given pane, at the given position
     *
     * @param pane - given pane
     * @param x    - position
     * @param y    - position
     * @return list with all children at position
     */
    public List<Node> getChildrenInPaneAt(Pane pane, int x, int y) {
        ObservableList<Node> paneChildren = pane.getChildren();
        LinkedList<Node> list = new LinkedList<>();

        for (int i = 0; i < paneChildren.size(); i++) {
            if (GridPane.getColumnIndex(paneChildren.get(i)) != null &&
                    GridPane.getRowIndex(paneChildren.get(i)) != null &&
                    GridPane.getColumnIndex(paneChildren.get(i)) == x && GridPane.getRowIndex(paneChildren.get(i)) == y) {
                list.add(paneChildren.get(i));
            }
        }
        return list;
    }

    /**
     * Set the permission to resize the stage window
     *
     * @param isAllowed - is resize allowed
     */
    private void isResizeWindowAllowed(boolean isAllowed) {
        if (isAllowed) {

            double mindWidth = 1300;
            double ratio = 1.5;
            double minHeight = mindWidth / ratio;

            currentStage.setMinWidth(mindWidth);
            currentStage.setMinHeight(minHeight);
            currentStage.setMaxHeight(Double.MAX_VALUE);
            currentStage.setMaxWidth(Double.MAX_VALUE);

        } else {
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            currentStage.setMinWidth(width);
            currentStage.setMaxWidth(width);

            currentStage.setMinHeight(height);
            currentStage.setMaxHeight(height);
        }
    }
}
