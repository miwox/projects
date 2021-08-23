package logic;

/**
 * Way Card class it represents a logical way card.
 * There are three different way types I, L, T.
 * See the enum WayType.
 * @author Miwand Baraksaie inf104162
 */
public class WayCard {
    /**
     * Position of the boolean in the boolean array, that
     * we get from the WayType enum. It shows if its possible to the direction
     */
    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;

    private WayType wayType;
    private Treasure treasure;
    private Rotation rotation;

    /**
     * Direction to go,
     * can't be final, because of the rotations
     */
    private boolean left;
    private boolean up;
    private boolean right;
    private boolean down;

    /**
     * Will construct a Way Card to a given way type, rotation, treasure.
     * The constructor will get the way information from way type enum.
     * In Java an enum can have also methods.
     * @param wayType - which way type to construct
     * @param rotation - set the rotation
     * @param treasure - what treasure
     */
    public WayCard(WayType wayType, Rotation rotation, Treasure treasure) {
        assert wayType != null;
        this.rotation = Rotation.ZERO;
        this.treasure = treasure;
        this.wayType = wayType;
        boolean[] direction = WayType.possibleDirections(wayType);
        left = direction[LEFT];
        up = direction[UP];
        right = direction[RIGHT];
        down = direction[DOWN];
        int i = 0;
        while (i < rotation.ordinal()) {
            rotateClockWise();
            i++;
        }
    }

    /**
     * Copy constructor
     * @param copy way card to copy
     */
    public WayCard(WayCard copy){
        wayType = copy.wayType;
        treasure = copy.treasure;
        rotation = copy.getRotation();
        left = copy.left;
        right = copy.right;
        up = copy.up;
        down = copy.down;
    }

    /**
     * Get the way type
     * @return way type
     */
    public WayType getWayType() {
        return this.wayType;
    }

    /**
     * Get the rotation
     * @return rotation
     */
    public Rotation getRotation() {
        return this.rotation;
    }

    /**
     * Get the treasure
     * @return treasure
     */
    public Treasure getTreasure() {
        return this.treasure;
    }

    /**
     * Will set a treasure
      * @param treasure so set
     */
    public void setTreasure(Treasure treasure) {
        this.treasure = treasure;
    }

    /**
     * Will rotate current way card once clockwise
     */
    public void rotateClockWise() {
        boolean remember = down;
        this.down = right;
        this.right = up;
        this.up = left;
        this.left = remember;
        this.rotation = Rotation.values()[(this.rotation.ordinal() + 1) % Rotation.values().length];
    }

    /**
     * Is it possible to go left, from the way type
     * @return boolean
     */
    public boolean isLeftPossibleToGo() {
        return left;
    }

    /**
     * Is it possible to up, from the way type
     * @return boolean
     */
    public boolean isUpPossibleToGo() {
        return up;
    }

    /**
     * Is it possible to go right, from the way type
     * @return boolean
     */
    public boolean isRightPossibleToGo() {
        return right;
    }

    /**
     * Is it possible to go down, from the way type
     * @return boolean
     */
    public boolean isDownPossibleToGo() {
        return down;
    }

    /**
     * Is there a treasure in this way type
     * @return boolean
     */
    public boolean hasTreasure(){
        return treasure.ordinal() > 0;
    }

    /**
     * Will set the curren rotation to a given rotation
     * @param rot - rotation to set
     */
    public void setRotation(Rotation rot){
        while(rotation != rot){
            rotateClockWise();
        }
    }

    /**
     * To String method
     * @return a string
     */
    public String toString() {
        return " {Way Card: " + getWayType() + " Rotation: " + getRotation() + " Treasure: " + getTreasure() + "} ";
    }
}