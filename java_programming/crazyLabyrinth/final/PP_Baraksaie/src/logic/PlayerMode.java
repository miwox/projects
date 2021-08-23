package logic;

/**
 * Enum for the player modes
 * @author Miwand Baraksaie inf104162
 */
public enum PlayerMode {
    NONE("Not involved"), HUMAN("Human"), COMPUTER_1("Normal AI"), COMPUTER_2("Advanced AI");
    String s;

    /**
     * Constructor
     * @param s - name
     */
    PlayerMode(String s) {
    this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
