package logic;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ComponentTest {

    @Test
    public void testCoordinate(){
        Coordinate same0 = new Coordinate(0,0);
        Coordinate same1 = new Coordinate(0,0);
        Coordinate same2 = new Coordinate(0,0);
        Coordinate different0 = new Coordinate(-1,-1);

        assertTrue(same0.equals(same1));
        assertTrue(same1.equals(same2));
        assertTrue(same2.equals(same0));
        assertFalse(different0.equals(same0));

        List<Coordinate> coordinateList = new LinkedList<>();
        coordinateList.add(same0);

        assertTrue(coordinateList.contains(same0));
        assertTrue(coordinateList.contains(same1));
        assertTrue(coordinateList.contains(same2));
        assertFalse(coordinateList.contains(different0));
    }

    @Test
    public void testWrapping(){
        Coordinate wrapper = new Coordinate(0,2);
        assertEquals(Direction.UP, wrapCoordinate(wrapper));
        assertEquals(1, wrapper.getY());
        wrapper = new Coordinate(4,0);
        assertEquals(Direction.UP, wrapCoordinate(wrapper));
        assertEquals(400, wrapper.getX());
    }

    /**
     * Testing the mapping from controller to logic
     * @param wrapper
     * @return
     */
    private Direction wrapCoordinate(Coordinate wrapper){
        if(wrapper.getX() == 0){
            wrapper.setY(wrapper.getY() - 1);
        } else if (wrapper.getY() == 0){
            wrapper.setX(wrapper.getX() * 100);
        }
        return Direction.UP;
    }

    /**
     * Rotate once
     * @param freeCard
     * @return
     */
    private Coordinate wrapWayCard(WayCard freeCard){
        freeCard.rotateClockWise();
        return  new Coordinate(-1,-1);
    }

    @Test
    public void testWrappingWayCardToChangeRotation(){
        WayCard freeCard = new WayCard(WayType.T, Rotation.ZERO,Treasure.NONE);
        wrapWayCard(freeCard);
        assertEquals(Rotation.NINETY, freeCard.getRotation());
        wrapWayCard(freeCard);
        assertEquals(Rotation.HUNDRED_EIGHTY, freeCard.getRotation());
        wrapWayCard(freeCard);
        assertEquals(Rotation.TWO_HUNDRED_SEVENTY, freeCard.getRotation());
        wrapWayCard(freeCard);
        assertEquals(Rotation.ZERO, freeCard.getRotation());
    }

    @Test
    public void testDistanceBetweenCoordinates(){
        Coordinate test00 = new Coordinate(0,0);
        Coordinate test01 = new Coordinate(0,1);
        Coordinate test02 = new Coordinate(0,2);
        Coordinate test93 = new Coordinate(9,3);

        int distance1 = 1;
        int distance2 = 2;
        int distance0 = 0;
        int distance12 = 12;
        int distance10 = 10;

        assertEquals(distance1, test00.distance(test01));
        assertEquals(distance1, test01.distance(test00));
        assertEquals(distance0, test01.distance(test01));
        assertEquals(distance2, test02.distance(test00));
        assertEquals(distance2, test00.distance(test02));
        assertEquals(distance12, test00.distance(test93));
        assertEquals(distance10, test02.distance(test93));
        assertEquals(distance10, test93.distance(test02));

    }

    @Test
    public void testDiagonalCoordinates(){
        Coordinate test00 = new Coordinate(0,0);
        Coordinate test11 = new Coordinate(1,1);
        assertTrue(test00.isDiagonal(test11));
        Coordinate test01 = new Coordinate(0,1);
        assertFalse(test00.isDiagonal(test00));
        assertFalse(test00.isDiagonal(test01));
    }




}
