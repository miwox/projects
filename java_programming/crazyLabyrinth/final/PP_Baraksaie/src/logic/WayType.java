package logic;

/**
 * Enum for way types, also return the possible directions to move the card
 */
public enum WayType {
    /**
     * enums
     */
        I, L, T;

    /**
     * Will return the default direction to go, when rotation is 0
     * @param type - type
     * @return boolean array
     */
    public static boolean[] possibleDirections(WayType type){
            boolean[] ret;
            switch (type){
                case I: ret = new boolean[]{false, true, false, true}; break;
                case L: ret = new boolean[]{false, true, true, false}; break;
                case T: ret = new boolean[]{true, false, true, true}; break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            return ret;
        }
}

