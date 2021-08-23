package logic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Parser for reading json files
 * @author Miwand Baraksaie inf104162
 */
public class ParserJSON {

    /**
     * Loads a json data from file handler and build a state.
     * @param url - path
     * @return a game state factory
     * @throws FileNotFoundException - Exception.
     */
    public static GameStateFactory loadGameState(String url) throws IllegalInputException {
        try {
            Gson gson = new Gson();
            FileReader reader =  new FileReader(url);
            GameStateFactory savedState = gson.fromJson(reader, GameStateFactory.class);
            return savedState;
        }
        catch (Exception e){
            throw new IllegalInputException("Syntax Error or FileNotFound");
        }
    }

    public static void saveGame(String path, GameStateFactory state) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(path)) {
            gson.toJson(state, fileWriter);
            fileWriter.flush();
        }
    }
}
