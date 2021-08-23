package logic;

/**
 * Class to handle input exception
 */
public class IllegalInputException extends Exception{
    /**
     * Constructor
     * @param text - text on gui
     */
    public IllegalInputException(String text) {
        super(text);
    }
}
