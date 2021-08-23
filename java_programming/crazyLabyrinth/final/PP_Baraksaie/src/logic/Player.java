package logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class Player
 * @author Miwand Baraksaie inf104162
 */
public class Player {
    private String name;
    private final PlayerIndex index;
    private final PlayerMode playerMode;
    private Coordinate position;
    private List<Treasure> treasures;

    /**
     * Constructor
     * @param playerIndex - player index
     * @param playerMode  - player mode
     * @param name - name
     * @param coordinate - start position
     */
    public Player(PlayerIndex playerIndex, PlayerMode playerMode, String name, Coordinate coordinate) {
        this.index = playerIndex;
        this.playerMode = playerMode;
        this.name = name;
        this.position = coordinate;
    }

    /**
     * Copy constructor
     * @param copy to copy player
     */
    public Player(Player copy){
        name = copy.getName();
        playerMode = copy.getPlayerMode();
        index = copy.getPlayerIndex();
        position = new Coordinate(copy.getPosition().getX(), copy.getPosition().getY());
        treasures = new ArrayList<>(copy.getTreasures());
    }

    /**
     * Constructor
     * We have possible players. All of them have fixed start positions.
     * @param index - player index
     * @param playerMode - player mode
     * @param position - start position
     */

    Player(PlayerIndex index, PlayerMode playerMode, Coordinate position){
        this.index = index;
        this.playerMode = playerMode;
        this.position = position;
    }

    /**
     * Constructor
     * @param index player index
     * @param playerMode player mode
     * @param position start position
     * @param treasures - treasures
     */
     Player(PlayerIndex index, PlayerMode playerMode, Coordinate position, List<Treasure> treasures){
        this(index, playerMode, position);
        this.addTreasures(treasures);
    }

    /**
     * Add treasures to player
     * @param treasures - treasures to add
     */
    public void addTreasures(List<Treasure> treasures) {
        this.treasures = new LinkedList<>(treasures);
    }

    /**
     * Get treasures
     * @return treasures
     */
    public List<Treasure> getTreasures() {
        return treasures;
    }

    /**
     * Get position
     * @return position
     */
    public Coordinate getPosition(){
        return position;
    }

    /**
     * Get player index
     * @return - player index
     */
    public PlayerIndex getPlayerIndex() {
        return index;
    }

    /**
     * Get player mode
     * @return player mode
     */
    public PlayerMode getPlayerMode(){return playerMode; }

    /**
     * Set position
     * @param newPos position
     */
    public void setPosition(Coordinate newPos){
        position = newPos;
    }

    /**
     * Get next treasure to find
     * @return Treasure
     */
    public Treasure getNextTreasure(){
        assert treasures.size() > 0;
        return treasures.get(0);
    }

    /**
     * Remove first treasure on list
     * @return treasure
     */
    public Treasure removeFirstTreasure() {
        assert treasures.size() > 0;
        return treasures.remove(0);
    }

    /**
     * Has treasure
     * @return boolean
     */
    public boolean hasTreasure(){
        return !treasures.isEmpty();
    }

    /**
     * Finishing with collecting treasures
     * @return boolean
     */
    public boolean isFinishedWithCollectingTreasures(){
        return this.treasures.isEmpty();
    }

    /**
     * Get name of the player.
     * @return name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString(){
        return index + " Name: " + name + " is: " + playerMode;
    }
}
