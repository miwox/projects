package logic;

/**
 * Coordinate class, represents an element in Z²,
 * is used in the logic and in the gui
 * @author Miwand Baraksaie inf104162
 */
public class Coordinate {

    private static final int DISTANCE_NEIGHBOR = 1;

    /**
     * Class components
     */
    private int x;
    private int y;

    /**
     * Constructor
     * @param x - position
     * @param y - position
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor
     * @param c - to copy coordinate
     */
    public Coordinate(Coordinate c){
        this.x = c.x;
        this.y = c.y;
    }

    /**
     * Checks equality of a given x,y element
     * @param x - element to check
     * @param y - element to check
     * @return is it equal
     */
    public boolean isEqual(int x, int y){
        return this.x == x && this.y == y;
    }

    /**
     * Getter the x position
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Getter the y position
     * @return y
     */
    public int getY(){
        return y;
    }

    /**
     * Setter the x position
     * @param x - element to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setter the y position
     * @param y - element to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Calculates the distance between two coordinates.
     * Calculates |distance_horizontal| + |distance_vertical|
     * @param coordinate - distance to calculate
     * @return distance
     */
    public int distance(Coordinate coordinate){
        return (int) (Math.sqrt(Math.pow(x - coordinate.x, 2)) + Math.sqrt(Math.pow(y - coordinate.y, 2)));
    }

    /**
     * Check if the position is diagonal to target
     * @param posTarget - target
     * @return is diagonal
     */
    public boolean isDiagonal(Coordinate posTarget) {
        int distanceX = this.x - posTarget.x;
        int distanceY = this.y - posTarget.y;
        boolean isDiagonal = (int) Math.pow(distanceX, 2) == DISTANCE_NEIGHBOR && (int) Math.pow(distanceY, 2) == DISTANCE_NEIGHBOR;

        return isDiagonal;

    }

    /**
     * Is only equal when the coordinates have the same x and y values
     * @param o - object to check
     * @return equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public String toString(){
        return "X: " + x + " Y: " + y;
    }
}
