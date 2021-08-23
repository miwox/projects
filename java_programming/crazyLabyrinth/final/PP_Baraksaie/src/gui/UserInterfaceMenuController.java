package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import logic.PlayerMode;
import logic.Treasure;

import java.io.IOException;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller of the menu, will get called first.
 * Will also set the information of the controller to game controller
 * @author Miwand Baraksaie inf104162
 */
public class UserInterfaceMenuController implements Initializable {

    @FXML
    private ChoiceBox chcBxPlayerYellow;
    @FXML
    private AnchorPane anchrPnMenu;
    @FXML
    private ChoiceBox chcBxPlayerGreen;
    @FXML
    private ChoiceBox chcBxPlayerBlue;
    @FXML
    private ChoiceBox chcBxPlayerRed;
    @FXML
    private TextField txtFldPlayerYellow;
    @FXML
    private TextField txtFldPlayerBlue;
    @FXML
    private TextField txtFldPlayerGreen;
    @FXML
    private TextField txtFldPlayerRed;
    @FXML
    private Button btnChooseTreasure;
    @FXML
    private Pagination counterAmountOfTreasure;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnStart;
    
    private static final int NUMBER_OF_PLAYER = 4;
    private final int MIN_LENGTH_PLAYER_NAME = 3;
    ChoiceBox[] choiceBoxesForPlayerMode;
    TextField[] textFieldsPlayerNames;

    /**
     * Get called by start the application.
     * Initialise the buttons and hide necessary buttons
     * @param url - url
     * @param resourceBundle - resource bundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBoxesForPlayerMode = new ChoiceBox[]{chcBxPlayerYellow, chcBxPlayerBlue, chcBxPlayerGreen, chcBxPlayerRed};
        textFieldsPlayerNames = new TextField[]{txtFldPlayerYellow, txtFldPlayerBlue, txtFldPlayerGreen, txtFldPlayerRed};
        // init choice boxes with values
        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            choiceBoxesForPlayerMode[i].getItems().clear();
            choiceBoxesForPlayerMode[i].getItems().addAll(PlayerMode.values());
            choiceBoxesForPlayerMode[i].getSelectionModel().select(PlayerMode.NONE.ordinal());
            choiceBoxesForPlayerMode[i].getSelectionModel().selectedIndexProperty()
                    .addListener(new ChoiceBoxListenerMakeTextFieldVisible(choiceBoxesForPlayerMode[i], textFieldsPlayerNames[i]));
        }
        hideButtons();
    }

    /**
     * Hide some buttons on start
     */
    private void hideButtons() {
        btnStart.setVisible(false);
        btnReset.setVisible(false);
        btnChooseTreasure.setVisible(false);
        counterAmountOfTreasure.setVisible(false);
        for (TextField txtFld : textFieldsPlayerNames) {
            txtFld.setVisible(false);
        }

    }

    /**
     * Get called when you button: Choose Treasure get clicked, gets clicked
     * Check if enough letters for name get entered from player
     * If yes than it will show the start button and reset button
     * User can choose after this amount of treasures or get back
     *
     * @param mouseEvent - unused
     */
    @FXML
    private void handleBtnChooseTreasure(MouseEvent mouseEvent) {
        boolean invalidInput = false;
        int i = 0;
        int numberOfPlayers = 0;
        // check input of the user, at least one player has to be chosen with a min length of string as a name
        while (i < NUMBER_OF_PLAYER && !invalidInput) {
            invalidInput = choiceBoxesForPlayerMode[i].getSelectionModel().getSelectedItem() != PlayerMode.NONE
                    && textFieldsPlayerNames[i].getText().length() < MIN_LENGTH_PLAYER_NAME;

            if(choiceBoxesForPlayerMode[i].getSelectionModel().getSelectedItem() != PlayerMode.NONE){
                numberOfPlayers++;
            }
            i++;
        }
        // make start button and reset  button visible
        if (!invalidInput && numberOfPlayers > 0) {
            btnChooseTreasure.setDisable(true);
            counterAmountOfTreasure.setVisible(true);
            counterAmountOfTreasure.setPageCount((Treasure.values().length - 1) / numberOfPlayers);
            counterAmountOfTreasure.setCurrentPageIndex((Treasure.values().length - 1) / numberOfPlayers);
            btnStart.setVisible(true);
            btnReset.setVisible(true);
            System.out.println(counterAmountOfTreasure.getCurrentPageIndex());
            i = 0;
            while (i < NUMBER_OF_PLAYER) {
                choiceBoxesForPlayerMode[i].setDisable(true);
                textFieldsPlayerNames[i].setDisable(true);
                i++;
            }
        }
        else {
            activateAlertInvalidInput();
        }
    }

    /**
     * Alert for invalid user input for the names.
     */
    private void activateAlertInvalidInput() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("invalid names");
        alert.setHeaderText("minimum name length is: " + MIN_LENGTH_PLAYER_NAME);
        alert.setContentText("Please check your names!");
        ButtonType btnCancel = new ButtonType("Okay", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnCancel);
        alert.showAndWait();
    }

    /**
     * Get called when reset button will be clicked
     * Reset all elements to default, like initialise.
     *
     * @param mouseEvent - unused
     */
    @FXML
    private void handleBtnReset(MouseEvent mouseEvent) {
        btnReset.setVisible(false);
        btnStart.setVisible(false);
        btnChooseTreasure.setDisable(false);
        counterAmountOfTreasure.setVisible(false);
        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            choiceBoxesForPlayerMode[i].setDisable(false);
            choiceBoxesForPlayerMode[i].getSelectionModel().select(PlayerMode.NONE.ordinal());
            textFieldsPlayerNames[i].setDisable(false);
            textFieldsPlayerNames[i].clear();
        }
    }

    /**
     * Handle button start clicked. Will set the information for the other game controller.
     * And will close the current stage and will open the game field and initialise the new controller.
     * @param mouseEvent unused
     * @throws IOException - may be caused by load the new controller.
     */
    @FXML
    private void handleBtnStart(MouseEvent mouseEvent) throws IOException {
        double WIDTH = 1500;
        double HEIGHT = 1000;
        double MIN_WIDTH = 1000;
        double RATIO = 1.5;
        double MIN_HEIGHT = MIN_WIDTH / RATIO;
        String NAME = "Labyrinth";

        FXMLLoader loaderGameController = new FXMLLoader();
        loaderGameController.setLocation(getClass().getResource("UserInterfaceGame.fxml"));
        Parent root  =  loaderGameController.load();
        UserInterfaceGameController gameController = loaderGameController.getController();
        gameController.setCounterPage(counterAmountOfTreasure.getCurrentPageIndex());
        gameController.setChoiceBoxes(choiceBoxesForPlayerMode);
        gameController.setTextFields(textFieldsPlayerNames);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("MapStyle.css").toExternalForm());
        Stage stage = new Stage();

        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        stage.setTitle(NAME);
        stage.show();
        gameController.initialize(null, null);
        // close the old stage
        Stage oldStage = (Stage) anchrPnMenu.getScene().getWindow();
        oldStage.close();
    }

    /**
     * private Listener class to handle choice box actions
     * It's needed to check the conditions for treasure counter.
     */
    private class ChoiceBoxListenerMakeTextFieldVisible implements ChangeListener<Number> {

        private final ChoiceBox<PlayerMode> ref;
        private final TextField doChange;

        private ChoiceBoxListenerMakeTextFieldVisible(ChoiceBox<PlayerMode> reference, TextField doChange) {
            this.ref = reference;
            this.doChange = doChange;
        }

        /**
         * Get called when the choice box get changed. Will check the other choice boxes and
         * can set the visibility for treasure choose
         * @param observableValue - not used
         * @param oldVal - was value
         * @param newVal - is now value
         */
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
            // set here the new value, otherwise the old value is still selected.
            ref.getSelectionModel().select(PlayerMode.values()[newVal.intValue()]);

            // make text field for player visible
            if (newVal.intValue() != PlayerMode.NONE.ordinal()) {
                doChange.setVisible(true);
            } else {
                doChange.setVisible(false);
                doChange.clear();
            }
            // check new value is non than disable the treasure choose button
            if (newVal.intValue() != PlayerMode.NONE.ordinal()) {
                btnChooseTreasure.setVisible(true);
            } else if (Arrays.stream(choiceBoxesForPlayerMode)
                    .allMatch(x -> x.getSelectionModel().getSelectedItem().equals(PlayerMode.NONE))) {
                btnChooseTreasure.setVisible(false);
            }
        }
    }
}
