package gui;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class TestJavaFxGui {





    @Test
    public void testForGetGameFieldUnit(){
        Platform.startup(() ->{
            GridPane gridpane = new GridPane();
            Button btn00 = new Button();
            Button btn01 = new Button();
            Button btn10 = new Button();
            Button btn11 = new Button();
            Button btn111 = new Button();
            gridpane.add(btn00, 0, 0);
            gridpane.add(btn10, 1, 0);
            gridpane.add(btn11, 1, 1);
            gridpane.add(btn111, 0, 0); // Speichern doppelt ab. Und zwar bei 0 0
            gridpane.add(btn01, 0, 1);

        });




    }
}