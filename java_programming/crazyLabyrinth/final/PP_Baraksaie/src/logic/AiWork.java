package logic;

import java.util.LinkedList;

/**
 * The AI work class, which collects the best options, for pushing the free card and moving the player.
 * Also allows setting priority lists
 * @author Miwand Baraksaie inf104162
 */
public class
AiWork {

    /**
     * Player coordinates to go, after pushing the free card
     * index x element of this list maps to index to the other list
     */
    private final LinkedList<Coordinate> playerToGoCoordinatesAfterPush;

    /**
     * Rotations of the pushed free card
     * index x element of this list maps to index to the other list
     */
    private final LinkedList<Rotation> rotations;

    /**
     * Logical push coordinates of the free card
     * index x element of this list maps to index to the other list
     */
    private final LinkedList<Coordinate> pushCoordinates;

    /**
     * The distance to target
     */
    private int distance;


    /**
     * Which option get used
     */
    private Integer usedOption;

    /**
     * Constructor, initialise all lists
     */
    public AiWork(){
        playerToGoCoordinatesAfterPush = new LinkedList<>();
        rotations = new LinkedList<>();
        pushCoordinates = new LinkedList<>();
        distance = Integer.MAX_VALUE;

        usedOption = null;
    }

    /**
     * Clears the lists, after finding a better options
     */
    private void clearLists(){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        playerToGoCoordinatesAfterPush.clear();
        rotations.clear();
        pushCoordinates.clear();
        distance = Integer.MAX_VALUE;

        usedOption = null;
    }

    /**
     * After finding a better option, the new distance and clears the old (worse) options
     * @param distance - the new distance
     */
    public void setNewDistance(int distance){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        clearLists();
        this.distance = distance;
    }


    /**
     * Add values to the list, all have the same distance to the target.
     * Added values, get added in the front of list, because they have a high priority
     * @param posPush - position of the pushed card
     * @param rotFreeCard - rotation of the pushed card
     * @param posPlayerToGo - player position to go, after pushing the free card
     */
    public void addNewValues(Coordinate posPush, Rotation rotFreeCard, Coordinate posPlayerToGo){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        pushCoordinates.add(posPush);
        rotations.add(rotFreeCard);
        playerToGoCoordinatesAfterPush.add(posPlayerToGo);
    }

    /**
     * Get the list of the player to go coordinates after pushing
     * @return list with player to go coordinates
     */
    public LinkedList<Coordinate> getPlayerToGoCoordinatesAfterPush(){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        return playerToGoCoordinatesAfterPush;
    }

    /**
     * Get the list of rotations of the pushed free card
     * @return list with the rotations of pushed free card
     */
    public LinkedList<Rotation> getRotationList(){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        return rotations;
    }

    /**
     * Get the push coordinates of the free way card
     * @return list with the pushed coordinates of the free way card
     */
    public LinkedList<Coordinate> getPushCoordinates(){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        return pushCoordinates;
    }

    /**
     * Get the distance of the player to go coordinates and the next target,
     * all options in the list have the same distance
     * @return distance to the next target
     */
    public int getDistance() {
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        return distance;
    }

    public int getNumberOfOptions(){
        assert rotations.size() == playerToGoCoordinatesAfterPush.size();
        assert rotations.size() == pushCoordinates.size();

        return  rotations.size();
    }

    /**
     * Set the used option, this is necessary for moving the player
     * @param option - used option
     */
    public void setUsedOption(Integer option){
        usedOption = option;
    }

    /**
     * When the options in the AI are not used, return false
     * @return is an option of AI used
     */
    public boolean getUsedCoordinateFlag(){
        return usedOption != null;
    }


    /**
     * Return the used option
     * @return used option
     */
    public int getUsedOption() {
        assert usedOption != null;
        return usedOption;
    }
}
