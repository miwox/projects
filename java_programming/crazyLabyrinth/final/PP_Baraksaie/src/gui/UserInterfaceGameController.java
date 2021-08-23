package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * This is the main controller. Before using the method initialise: You have to
 * set the SINGLETON factory! If you do not, you will get null pointer exceptions.
 * @author Miwand Baraksaie inf104162
 */
public class UserInterfaceGameController {

    private static final int NUMBER_OF_PLAYER = 4;
    private static final int OFFSET_WAY_CARDS = 1;
    private static final int GRID_PANE_ON_CARD = 3;
    private static final int GRID_PANE_CONSTRAINT_HEIGHT_WIDTH = 100;
    private static final int INDEX_FIRST_FIELD = 0;
    private static final int INDEX_LAST_FIELD = 8;
    /**
     * For developing set this to true. So you can start immediately the game, without the menu
     */
    private static boolean DEV = false;
    private static boolean isKeyPressed = false;
    /**
     *
     * the size of grid pane in the fxml file is 10x10
     * we initialise only a 9x9 imageview array
     * the empty cells above, below, left and right from an arrow they are null
     *
     */
    private static final int GRID_PANE_SIZE_GAME_FIELD = 9;
    private static final int Y_FREE_CARD_ON_GUI = 4;

    @FXML
    public Button btnNewGame;
    @FXML
    public Button btnCloseGame;
    @FXML
    public Label lblAction;
    @FXML
    public MenuItem mnItmSaveGame;
    @FXML
    public MenuItem mnItmCloseGame;
    @FXML
    public Button btnStartGame;
    @FXML
    public MenuItem mnItmPlayerFast;
    @FXML
    public MenuItem mnItmPlayerSlow;
    @FXML
    public MenuItem mnItmPlayerNone;

    /**
     * Display out controls
     */
    @FXML
    private VBox vBoxDisplayOut;

    @FXML
    private Label lblPlayerOne;
    @FXML
    private Label lblPlayerTwo;
    @FXML
    private Label lblPlayerThree;
    @FXML
    private Label lblPlayerFour;
    @FXML
    private Label lblPlayerTwoTreasureLeft;
    @FXML
    private Label lblCurrentPlayer;
    @FXML
    private Label lblPlayerFourTreasureLeft;
    @FXML
    private Label lblThreeTreasureLeft;
    @FXML
    private Label lblPlayerOneTreasureLeft;

    /**
     * For developing, start directly the game without the start menu
     *
     * @return which mode is on?
     */
    public static boolean GET_DEV() {
        return DEV;
    }

    /**
     * Game field controls
     */
    @FXML
    private HBox hBoxWrappingVBox;
    @FXML
    private VBox vBoxWrappingGrdPn;
    @FXML
    private GridPane grdPnGame;
    @FXML
    private BorderPane brdrPnRoot;
    @FXML
    private MenuItem mnItmPushFast;
    @FXML
    private MenuItem mnItmPushSlow;
    @FXML
    private MenuItem mnItmPushNone;
    @FXML
    private MenuItem mnItmLoad;
    @FXML
    private MenuItem mnItmNewGame;

    /**
     * Class instances
     */
    private ChoiceBox[] choiceBoxesForPlayerMode;
    private ImageView[][] imageViews;
    private GridPane[][] gridPanesOnWayCards;
    private TextField[] textFieldsPlayerNames;
    private GridPane gridOnFreeCard;
    private ImageView imgVwFreeCard;
    private GameField logic;

    /**
     * Singleton factory. To open this controller from another controller,
     * you have to set the necessary information,
     * before use the method initialise
     */
    private enum INPUT_FROM_MENU_CONTROLLER {
        SINGLETON;
        private Integer counterPageIndex;
        private ChoiceBox[] choiceBoxes;
        private TextField[] textFields;

        private void setCounterPageIndex(Integer counter) {
            counterPageIndex = counter;
        }

        /**
         * Set the choice boxes. Which player is selected and what kind of mode is the player playing
         *
         * @param cBoxes - no null and length is 4, there are 4 players
         */
        private void setChoiceBoxes(ChoiceBox[] cBoxes) {
            assert cBoxes != null;
            assert cBoxes.length == NUMBER_OF_PLAYER;
            choiceBoxes = cBoxes;
        }

        /**
         * Set the text field of the player names
         *
         * @param txtFields text fields of the player
         */
        private void setTextFields(TextField[] txtFields) {
            assert txtFields != null;
            assert txtFields.length == NUMBER_OF_PLAYER;

            textFields = txtFields;
        }

        /**
         * Very important you have to add 1 on it.
         * The page index begins at 0, when it shows 1.
         *
         * @return get amount of treasures, selected from the players
         */
        private Integer getCounterPageIndex() {
            if (counterPageIndex == null) {
                throw new RuntimeException();
            }
            return counterPageIndex;
        }

        /**
         * Get the choice boxes with the selected mode.
         *
         * @return choice boxes
         */
        private ChoiceBox[] getChoiceBoxes() {
            if (choiceBoxes == null) {
                throw new RuntimeException();
            }
            return choiceBoxes;
        }

        /**
         * Get the text field of the player names
         *
         * @return text fields
         */
        private TextField[] getTextFields() {
            if (textFields == null) {
                throw new RuntimeException();
            }
            return textFields;
        }
    }

    /**
     * Singleton
     */
    private static final INPUT_FROM_MENU_CONTROLLER INPUT_FROM_MENU_CONTROLLER_ = INPUT_FROM_MENU_CONTROLLER.SINGLETON;

    /**
     * Get called from the menu controller, so provide the necessary information which
     * are needed to init the game field.
     *
     * @param choiceBoxes - selected modes from other controller
     */
    void setChoiceBoxes(ChoiceBox[] choiceBoxes) {
        INPUT_FROM_MENU_CONTROLLER_.setChoiceBoxes(choiceBoxes);
    }

    /**
     * Get called from the menu controller, so provide the necessary information which
     * are needed to init the game field.
     *
     * @param textFields - entered names of the players from the other controller
     */
    void setTextFields(TextField[] textFields) {
        INPUT_FROM_MENU_CONTROLLER_.setTextFields(textFields);
    }

    /**
     * Get called from the menu controller, so provide the necessary information which
     * are needed to init the game field.
     *
     * @param counterPage - page index, important begins at 0 when it shows 1.
     */
    void setCounterPage(int counterPage) {
        INPUT_FROM_MENU_CONTROLLER_.setCounterPageIndex(counterPage);
    }

    /**
     * Get called from the other controller.
     * Before calling this make sure, that you set the necessary information by using the setters of this class.
     * When we remove the arguments url and resourceBundle we got exception, and it's not possible to get this controller.
     *
     * @param url            - not used, do not remove: exception
     * @param resourceBundle - not used, do not remove: exception
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        brdrPnRoot.setOnKeyPressed(handleKeyEventPressedDown());
        brdrPnRoot.setOnKeyReleased(handleKeyEventReleased());
        hBoxWrappingVBox.setVisible(true);
        vBoxWrappingGrdPn.prefWidthProperty().bind(hBoxWrappingVBox.heightProperty());
        vBoxDisplayOut.prefWidthProperty().bind(vBoxWrappingGrdPn.heightProperty().divide(1.5));
        grdPnGame.prefWidthProperty().bind(vBoxWrappingGrdPn.widthProperty());
        grdPnGame.prefHeightProperty().bind(vBoxWrappingGrdPn.widthProperty());
        imageViews = new ImageView[GRID_PANE_SIZE_GAME_FIELD][GRID_PANE_SIZE_GAME_FIELD];
        gridPanesOnWayCards = new GridPane[GRID_PANE_SIZE_GAME_FIELD][GRID_PANE_SIZE_GAME_FIELD];

        Stage oldStage = (Stage) brdrPnRoot.getScene().getWindow();
        // The gameplay with cards begins at x=1 and y=1, the offset are for the arrows. Or they are empty.
        for (int y = OFFSET_WAY_CARDS; y < GRID_PANE_SIZE_GAME_FIELD - OFFSET_WAY_CARDS; y++) {
            for (int x = OFFSET_WAY_CARDS; x < GRID_PANE_SIZE_GAME_FIELD - OFFSET_WAY_CARDS; x++) {
                initGridCellWithImgVwAndGrdPn(x, y);
            }
        }

        initializeGameFieldArrow();
        initializeGameFieldEmpty();

        // Initialize the free card;
        gridOnFreeCard = createGridPaneSquare(GRID_PANE_ON_CARD);
        gridOnFreeCard.setGridLinesVisible(false);
        imgVwFreeCard = createImageViewBoundToGridPaneCell();
        grdPnGame.add(imgVwFreeCard, GRID_PANE_SIZE_GAME_FIELD, Y_FREE_CARD_ON_GUI);
        grdPnGame.add(gridOnFreeCard, GRID_PANE_SIZE_GAME_FIELD, Y_FREE_CARD_ON_GUI);
        Label freeCardText = new Label();
        GridPane.setValignment(freeCardText, VPos.BOTTOM);
        GridPane.setHalignment(freeCardText, HPos.CENTER);
        freeCardText.setFont(Font.font("Helvetica", 15));

        grdPnGame.add(freeCardText, GRID_PANE_SIZE_GAME_FIELD, 3);
        GuiDisplay guiDisplay = new GuiDisplay(
                lblCurrentPlayer,
                lblPlayerOne,
                lblPlayerTwo,
                lblPlayerThree,
                lblPlayerFour,
                lblPlayerOneTreasureLeft,
                lblPlayerTwoTreasureLeft,
                lblThreeTreasureLeft,
                lblPlayerFourTreasureLeft,
                lblAction,
                btnNewGame,
                btnCloseGame,
                btnStartGame);
        JavaFXGameGUI gui = new JavaFXGameGUI(
                oldStage,
                grdPnGame,
                imageViews,
                imgVwFreeCard,
                freeCardText,
                gridPanesOnWayCards,
                gridOnFreeCard,
                guiDisplay);

        gui.setFreeCardText();
        gui.initializeArrows();

        // for developing the game, set the logic your own
        if (DEV) {
            this.logic = new GameField(gui, 1, new PlayerMode[]{PlayerMode.COMPUTER_1}, new String[]{"advanced"}, new TextLogger());
        } else {
            // get input from the other controller
            choiceBoxesForPlayerMode = INPUT_FROM_MENU_CONTROLLER_.getChoiceBoxes();
            textFieldsPlayerNames = INPUT_FROM_MENU_CONTROLLER_.getTextFields();
            PlayerMode[] playerModes = new PlayerMode[NUMBER_OF_PLAYER];
            String[] playerNames = new String[NUMBER_OF_PLAYER];
            for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
                playerModes[i] = (PlayerMode) choiceBoxesForPlayerMode[i].getSelectionModel().getSelectedItem();
                playerNames[i] = textFieldsPlayerNames[i].getText();
            }
            int treasureEachPlayer = INPUT_FROM_MENU_CONTROLLER_.getCounterPageIndex() + 1;
            logic = new GameField(gui, treasureEachPlayer, playerModes, playerNames, new TextLogger());
        }
        mnItmPushNone.setOnAction(actionEvent -> logic.setDurationForPushCardGui(0));
        mnItmPushFast.setOnAction(actionEvent -> logic.setDurationForPushCardGui(1));
        mnItmPushSlow.setOnAction(actionEvent -> logic.setDurationForPushCardGui(2));
        mnItmPlayerNone.setOnAction(actionEvent -> logic.setDurationForPlayer(0));
        mnItmPlayerFast.setOnAction(actionEvent -> logic.setDurationForPlayer(1));
        mnItmPlayerSlow.setOnAction(actionEvent -> logic.setDurationForPlayer(2));

        mnItmLoad.setOnAction(this::handleLoadGame);
        mnItmNewGame.setOnAction(this::handleStartNewGame);
        btnNewGame.setOnAction(this::handleStartNewGame);
        btnCloseGame.setOnAction(this::handleCloseGame);
        mnItmCloseGame.setOnAction(this::handleCloseGame);
        mnItmSaveGame.setOnAction(this::handleSaveGame);
        btnStartGame.setOnAction(this::handleBtnStartGame);
    }

    /**
     * Handle button start game
     * @param actionEvent not used
     */
    private void handleBtnStartGame(ActionEvent actionEvent) {
        btnStartGame.setVisible(false);
        logic.startGame();
    }


    /**
     * Handle button save game
     * @param actionEvent not sued
     */
    private void handleSaveGame(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            logic.clickedSaveGame(fileChooser.showSaveDialog(new Stage()).getPath());
        } catch (Exception e) {
            logic.handleErrorJSON(e);
            e.printStackTrace();
        }

    }

    /**
     * Closes the game.
     *
     * @param actionEvent - event
     */
    private void handleCloseGame(ActionEvent actionEvent) {
        Stage oldStage = (Stage) brdrPnRoot.getScene().getWindow();
        oldStage.close();
    }

    /**
     * Handle button start new game.
     * Will open a new scene, and delete the old logic.
     * @param actionEvent - not used
     */
    public void handleStartNewGame(ActionEvent actionEvent) {
        this.logic = null;
        double WIDTH = 950;
        double HEIGHT = 950;
        String NAME = "Labyrinth";
        FXMLLoader loaderMenu = new FXMLLoader();
        loaderMenu.setLocation(getClass().getResource("UserInterfaceMenu.fxml"));
        Parent root = null;
        try {
            root = loaderMenu.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        Stage stage = new Stage();
        stage.setMinWidth(950);
        stage.setMaxWidth(950);
        stage.setMaxHeight(WIDTH);
        stage.setMaxHeight(HEIGHT);
        stage.setScene(scene);
        stage.setTitle(NAME);
        stage.show();
        // close the old stage
        Stage oldStage = (Stage) brdrPnRoot.getScene().getWindow();
        DEV = false;
        oldStage.close();
    }

    /**
     * Init main grid pane with grid pane, which are on the way cards.
     * Also creates an imageview.
     *
     * @param x location
     * @param y location
     */
    private void initGridCellWithImgVwAndGrdPn(int x, int y) {

        gridPanesOnWayCards[x][y] = createGridPaneSquare(GRID_PANE_ON_CARD);
        imageViews[x][y] = createImageViewBoundToGridPaneCell();
        gridPanesOnWayCards[x][y].setGridLinesVisible(false);
        grdPnGame.add(imageViews[x][y], x, y);
        grdPnGame.add(gridPanesOnWayCards[x][y], x, y);
    }

    /**
     * Creates an imageview
     *
     * @return an imageview which in bounded to a grid pane cell
     */
    private ImageView createImageViewBoundToGridPaneCell() {
        ImageView img = new ImageView();
        img.fitWidthProperty().bind(this.grdPnGame.widthProperty().divide(GRID_PANE_SIZE_GAME_FIELD + 1));
        img.fitHeightProperty().bind(this.grdPnGame.heightProperty().divide(GRID_PANE_SIZE_GAME_FIELD + 1));
        return img;
    }

    /**
     * Creates a grid pane with a given size.
     * Also sets the constraints
     *
     * @param gridPaneCell x gridPaneCell grid pane
     * @return
     */
    private GridPane createGridPaneSquare(int gridPaneCell) {
        GridPane gridpane = new GridPane();
        for (int col = 0; col < gridPaneCell; col++) {
            RowConstraints rConstraint = new RowConstraints();
            ColumnConstraints cConstraint = new ColumnConstraints();
            rConstraint.setPercentHeight(GRID_PANE_CONSTRAINT_HEIGHT_WIDTH);
            cConstraint.setPercentWidth(GRID_PANE_CONSTRAINT_HEIGHT_WIDTH);
            gridpane.getRowConstraints().add(rConstraint);
            gridpane.getColumnConstraints().add(cConstraint);
        }

        gridpane.setOnMouseClicked(handleClickedWayCard());
        gridpane.setOnMouseEntered(handleMouseEnteredWayCard());
        gridpane.setOnMouseExited(handleMouseExitedWayCard());
        return gridpane;
    }


    /**
     * Event handler when mouse exited a specific way card
     * @return event handler
     */
    private EventHandler<? super MouseEvent> handleMouseExitedWayCard() {
        return mouseEvent -> {
            int x = GridPane.getColumnIndex((Node) mouseEvent.getSource());
            int y = GridPane.getRowIndex((Node) mouseEvent.getSource());
            logic.mouseExitedGridOnWayCard(x, y);
        };
    }

    /**
     * Handles keyboard input, when is key pressed.
     *
     * @return event handler
     */
    private EventHandler<? super KeyEvent> handleKeyEventPressedDown() {
        return (EventHandler<KeyEvent>) keyEvent -> {
            switch (keyEvent.getCode()) {
                case H:
                    logic.handleKeyBoardPressedH();
                    isKeyPressed = true;
                    break;
                case ESCAPE:
                    handleCloseGame(null); //dummy argument
                    break;
            }
        };
    }

    /**
     * Handles keyboard input, when key is released.
     *
     * @return event handler
     */
    private EventHandler<? super KeyEvent> handleKeyEventReleased() {
        return (EventHandler<KeyEvent>) keyEvent -> {
            if (keyEvent.getCode() == KeyCode.H) {
                logic.handleKeyBoardReleasedH();
                isKeyPressed = false;
            }

        };
    }

    /**
     * Handles when the mouse cursor in entering a grid pane cell.
     *
     * @return event handler
     */
    private EventHandler<? super MouseEvent> handleMouseEnteredWayCard() {
        return mouseEvent -> {
            int x = GridPane.getColumnIndex((Node) mouseEvent.getSource());
            int y = GridPane.getRowIndex((Node) mouseEvent.getSource());
            logic.mouseEnteredGridOnWayCard(x, y);
        };
    }

    /**
     * Handles when an arrows get clicked
     *
     * @return event handler
     */
    private EventHandler<? super MouseEvent> handleClickedWayCard() {
        return mouseEvent -> {
            int x = GridPane.getColumnIndex((Node) mouseEvent.getSource());
            int y = GridPane.getRowIndex((Node) mouseEvent.getSource());
            boolean leftClicked = mouseEvent.getButton() == MouseButton.PRIMARY;
            boolean rightClicked = mouseEvent.getButton() == MouseButton.SECONDARY;
            // do not allow multiple key board or mouse input.
            if (leftClicked && !isKeyPressed) {
                logic.clickedLeftOnWayCard(x, y);
            }
        };
    }

    /**
     * Initialise the grid pane with arrows and init the array of image views.
     */
    private void initializeGameFieldArrow() {
        // first col with arrows
        for (int i = INDEX_FIRST_FIELD + 2; i < INDEX_LAST_FIELD - 1; i = i + 2) {
            imageViews[INDEX_FIRST_FIELD][i] = createImageViewBoundToGridPaneCell();
            imageViews[INDEX_FIRST_FIELD][i].setOnMouseClicked(handleClickedArrow());
            grdPnGame.add(imageViews[INDEX_FIRST_FIELD][i], INDEX_FIRST_FIELD, i);
        }

        // last col with arrow
        for (int i = INDEX_FIRST_FIELD + 2; i < INDEX_LAST_FIELD -1; i = i + 2) {
            // this.grdPnGame.add(pane, 8, i);
            imageViews[INDEX_LAST_FIELD][i] = createImageViewBoundToGridPaneCell();
            imageViews[INDEX_LAST_FIELD][i].setOnMouseClicked(handleClickedArrow());
            grdPnGame.add(imageViews[INDEX_LAST_FIELD][i], INDEX_LAST_FIELD, i);
        }

        // first row with arrow
        for (int i = 2; i < 7; i = i + 2) {
            //  this.grdPnGame.add(pane, i, 0);
            imageViews[i][INDEX_FIRST_FIELD] = createImageViewBoundToGridPaneCell();
            imageViews[i][INDEX_FIRST_FIELD].setOnMouseClicked(handleClickedArrow());
            grdPnGame.add(imageViews[i][INDEX_FIRST_FIELD], i, INDEX_FIRST_FIELD);
        }
        // last row with arrow
        for (int i = 2; i < 7; i = i + 2) {
            //  this.grdPnGame.add(pane, i, 8);
            imageViews[i][INDEX_LAST_FIELD] = createImageViewBoundToGridPaneCell();
            imageViews[i][INDEX_LAST_FIELD].setOnMouseClicked(handleClickedArrow());
            grdPnGame.add(imageViews[i][INDEX_LAST_FIELD], i, INDEX_LAST_FIELD);
        }
    }

    /**
     * Initialise the empty game field units, where  you can put the free card on it
     */
    private void initializeGameFieldEmpty() {
        Pane pane = new Pane();
        this.grdPnGame.add(pane, INDEX_FIRST_FIELD, INDEX_FIRST_FIELD);
        pane = new Pane();
        this.grdPnGame.add(pane, INDEX_LAST_FIELD, INDEX_FIRST_FIELD);
        pane = new Pane();
        this.grdPnGame.add(pane, INDEX_LAST_FIELD, INDEX_LAST_FIELD);

        // vertical parallel
        for (int x = 1; x < INDEX_LAST_FIELD; x += 2) {
            pane = new Pane();
            this.grdPnGame.add(pane, x, INDEX_FIRST_FIELD);
            pane = new Pane();
            this.grdPnGame.add(pane, x, INDEX_LAST_FIELD);
        }

        // horizontal parallel
        for (int y = 1; y < INDEX_LAST_FIELD; y += 2) {
            pane = new Pane();
            this.grdPnGame.add(pane, INDEX_FIRST_FIELD, y);
            pane = new Pane();
            this.grdPnGame.add(pane, INDEX_LAST_FIELD, y);
        }
    }

    /**
     * Handle clicked on arrow
     * @return event handler
     */
    private EventHandler<MouseEvent> handleClickedArrow() {
        return mouseEvent -> {

            int x_controller = GridPane.getColumnIndex(((Node) mouseEvent.getSource()));
            int y_controller = GridPane.getRowIndex(((Node) mouseEvent.getSource()));
            boolean leftClicked = mouseEvent.getButton() == MouseButton.PRIMARY;
            if (leftClicked && !isKeyPressed) {
                logic.clickedLeftOnArrowHandlingCoordinates(x_controller, y_controller);
            }
        };
    }

    /**
     * Handle load game
     * Package private, so we can call it from the menu controller,
     * to load a game
     * @param actionEvent - not used
     */

    void handleLoadGame(ActionEvent actionEvent) {
        File currDir = null;
        try {
            currDir = new File(UserInterfaceGameController.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            //oops... ¯\_(ツ)_/¯
            //guess we won't be opening the dialog in the right directory
        }
        //Step 2: Put it together
        FileChooser fileChooser = new FileChooser();
        if (currDir != null) {
            //ensure the dialog opens in the correct directory
            fileChooser.setInitialDirectory(currDir.getParentFile());
        }
        fileChooser.setTitle("Open saved game file");
        //Step 3: Open the Dialog (set window owner, so nothing in the original window
        //can be changed)
        File selectedFile = fileChooser.showOpenDialog(brdrPnRoot.getScene().getWindow());

        try {
            logic.loadGame(selectedFile.toString());
        } catch (Exception ex) {
            logic.handleErrorJSON(ex);
        }
    }

}

