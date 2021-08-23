package logic;

import java.util.Random;

/**
 * Enum for rotations,
 * @author miwand baraksaie inf104162
 */
public enum Rotation {
    ZERO, NINETY, HUNDRED_EIGHTY, TWO_HUNDRED_SEVENTY;

    /**
     * Get a random rotation
     * @return a random rotation
     */
    public static Rotation getRandomRotation(){
        return Rotation.values()[new Random().nextInt(Rotation.values().length)];
    }
}
