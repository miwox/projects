package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class
 * @author Miwand Baraksaie inf104162
 */
public class Main extends Application {

    /**
     * Start method, if DEV is inactive, it will start the menu controller.
     * @param primaryStage the stage
     * @throws Exception thrown
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // dev is activated
        if(UserInterfaceGameController.GET_DEV()) {
            FXMLLoader loaderGameController = new FXMLLoader();
            loaderGameController.setLocation(getClass().getResource("UserInterfaceGame.fxml"));
            Parent root  =  loaderGameController.load();
            UserInterfaceGameController gameController = loaderGameController.getController();
            Scene scene = new Scene(root, 10, 10);
            scene.getStylesheets().add(getClass().getResource("MapStyle.css").toExternalForm());
            Stage stage = new Stage();
            double mindWidth = 1300;
            double ratio = 1.5;
            double minHeight = mindWidth/ratio;
            stage.setScene(scene);
            stage.setMinWidth(mindWidth);
            stage.setMinHeight(minHeight);
            stage.show();
            gameController.initialize(null, null);
        }

        else {
            Parent root = FXMLLoader.load(getClass().getResource("UserInterfaceMenu.fxml"));
            primaryStage.setTitle("Labyrinth");
            Scene scene = new Scene(root, 950, 1000);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(950);
            primaryStage.setMaxWidth(950);
            primaryStage.setMaxHeight(1000);
            primaryStage.setMaxHeight(1000);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
