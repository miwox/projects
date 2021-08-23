package logic;
import org.junit.Test;

import static org.junit.Assert.*;

public class WayTypeTest {

    @Test
    public void testWayCardI_ZeroRotation(){
        WayCard test0 = new WayCard(WayType.I, Rotation.ZERO, null);

        assertEquals(false, test0.isLeftPossibleToGo());
        assertEquals(true, test0.isUpPossibleToGo());
        assertEquals(false, test0.isRightPossibleToGo());
        assertEquals(true, test0.isDownPossibleToGo());

    }

    @Test
    public void testWayCardI_NinetyRotation(){
        WayCard test90 = new WayCard(WayType.I, Rotation.NINETY, null);

        assertEquals(true, test90.isLeftPossibleToGo());
        assertEquals(false, test90.isUpPossibleToGo());
        assertEquals(true, test90.isRightPossibleToGo());
        assertEquals(false, test90.isDownPossibleToGo());

    }

    @Test
    public void testWayCardI_180Rotation(){
        WayCard test180 = new WayCard(WayType.I, Rotation.HUNDRED_EIGHTY, null);
        assertEquals(false, test180.isLeftPossibleToGo());
        assertEquals(true, test180.isUpPossibleToGo());
        assertEquals(false, test180.isRightPossibleToGo());
        assertEquals(true, test180.isDownPossibleToGo());
    }

    @Test
    public void testWayCardI_TwoHundredSeventyRotation(){
        WayCard test270 = new WayCard(WayType.I, Rotation.TWO_HUNDRED_SEVENTY, null);

        assertEquals(true, test270.isLeftPossibleToGo());
        assertEquals(false, test270.isUpPossibleToGo());
        assertEquals(true, test270.isRightPossibleToGo());
        assertEquals(false, test270.isDownPossibleToGo());

    }

    @Test
    public void testWayCardL_ZeroRotation(){
        WayCard test0 = new WayCard(WayType.L, Rotation.ZERO, null);

        assertEquals(false, test0.isLeftPossibleToGo());
        assertEquals(true, test0.isUpPossibleToGo());
        assertEquals(true, test0.isRightPossibleToGo());
        assertEquals(false, test0.isDownPossibleToGo());

    }

    @Test
    public void testWayCardL_NinetyRotation(){
        WayCard test90 = new WayCard(WayType.L, Rotation.NINETY, null);

        assertEquals(false, test90.isLeftPossibleToGo());
        assertEquals(false, test90.isUpPossibleToGo());
        assertEquals(true, test90.isRightPossibleToGo());
        assertEquals(true, test90.isDownPossibleToGo());

    }

    @Test
    public void testWayCardL_180Rotation(){
        WayCard test180 = new WayCard(WayType.L, Rotation.HUNDRED_EIGHTY, null);
        assertEquals(true, test180.isLeftPossibleToGo());
        assertEquals(false, test180.isUpPossibleToGo());
        assertEquals(false, test180.isRightPossibleToGo());
        assertEquals(true, test180.isDownPossibleToGo());
    }

    @Test
    public void testWayCardL_TwoHundredSeventyRotation(){
        WayCard test270 = new WayCard(WayType.L, Rotation.TWO_HUNDRED_SEVENTY, null);
        assertEquals(true, test270.isLeftPossibleToGo());
        assertEquals(true, test270.isUpPossibleToGo());
        assertEquals(false, test270.isRightPossibleToGo());
        assertEquals(false, test270.isDownPossibleToGo());

    }

    @Test
    public void testWayCardT_ZeroRotation(){
        WayCard test0 = new WayCard(WayType.T, Rotation.ZERO, null);

        assertEquals(true, test0.isLeftPossibleToGo());
        assertEquals(false, test0.isUpPossibleToGo());
        assertEquals(true, test0.isRightPossibleToGo());
        assertEquals(true, test0.isDownPossibleToGo());
    }

    @Test
    public void testWayCardT_NinetyRotation(){
        WayCard test90 = new WayCard(WayType.T, Rotation.NINETY, null);

        assertEquals(true, test90.isLeftPossibleToGo());
        assertEquals(true, test90.isUpPossibleToGo());
        assertEquals(false, test90.isRightPossibleToGo());
        assertEquals(true, test90.isDownPossibleToGo());
    }

    @Test
    public void testWayCardT_HundredEightyRotation(){
        WayCard test90 = new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, null);

        assertEquals(true, test90.isLeftPossibleToGo());
        assertEquals(true, test90.isUpPossibleToGo());
        assertEquals(true, test90.isRightPossibleToGo());
        assertEquals(false, test90.isDownPossibleToGo());
        assertEquals(Rotation.HUNDRED_EIGHTY, test90.getRotation());
    }

    @Test
    public void testWayCardT_TwoHundredSeventyRotation(){
        WayCard test90 = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, null);

        assertEquals(false, test90.isLeftPossibleToGo());
        assertEquals(true, test90.isUpPossibleToGo());
        assertEquals(true, test90.isRightPossibleToGo());
        assertEquals(true, test90.isDownPossibleToGo());
    }


    @Test
    public void testWayCardT_TwoHundredSeventyRotationThenRotate(){
        WayCard test90 = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, null);
        test90.rotateClockWise();

        assertEquals(true, test90.isLeftPossibleToGo());
        assertEquals(false, test90.isUpPossibleToGo());
        assertEquals(true, test90.isRightPossibleToGo());
        assertEquals(true, test90.isDownPossibleToGo());
        assertEquals(Rotation.ZERO, test90.getRotation());
    }

    @Test
    public void testWayCardI_RotateIt4Times(){
        WayCard test = new WayCard(WayType.I, Rotation.ZERO, null);
        test.rotateClockWise();
        test.rotateClockWise();
        assertEquals(Rotation.HUNDRED_EIGHTY, test.getRotation());

    }

    @Test
    public void testWayCardI_SetRotateTimes1(){
        WayCard test = new WayCard(WayType.I, Rotation.ZERO, null);
        test.setRotation(Rotation.NINETY);
        assertEquals(Rotation.NINETY, test.getRotation());
        assertTrue(test.isRightPossibleToGo());
        assertTrue(test.isLeftPossibleToGo());
        assertFalse(test.isUpPossibleToGo());
        assertFalse(test.isDownPossibleToGo());

    }

    @Test
    public void testWayCardI_SetRotateTimes2(){
        WayCard test = new WayCard(WayType.T, Rotation.ZERO, null);
        test.setRotation(Rotation.NINETY);
        assertEquals(Rotation.NINETY, test.getRotation());
        assertFalse(test.isRightPossibleToGo());
        assertTrue(test.isLeftPossibleToGo());
        assertTrue(test.isUpPossibleToGo());
        assertTrue(test.isDownPossibleToGo());
    }

    @Test
    public void testWayCardI_SetRotateTimes3(){
        WayCard test = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, null);
        test.setRotation(Rotation.NINETY);
        assertEquals(Rotation.NINETY, test.getRotation());
        assertFalse(test.isRightPossibleToGo());
        assertTrue(test.isLeftPossibleToGo());
        assertTrue(test.isUpPossibleToGo());
        assertTrue(test.isDownPossibleToGo());
    }

    @Test
    public void testWayCardI_SetRotateTimes4(){
        WayCard test = new WayCard(WayType.T, Rotation.NINETY, null);
        test.setRotation(Rotation.NINETY);
        assertEquals(Rotation.NINETY, test.getRotation());
        assertFalse(test.isRightPossibleToGo());
        assertTrue(test.isLeftPossibleToGo());
        assertTrue(test.isUpPossibleToGo());
        assertTrue(test.isDownPossibleToGo());
    }

    @Test
    public void testWayCardI_SetRotateTimes5(){
        WayCard test = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, null);
        test.setRotation(Rotation.NINETY);
        assertEquals(Rotation.NINETY, test.getRotation());
        assertFalse(test.isRightPossibleToGo());
        assertTrue(test.isLeftPossibleToGo());
        assertTrue(test.isUpPossibleToGo());
        assertTrue(test.isDownPossibleToGo());
    }




}
