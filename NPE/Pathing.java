package NPE;

import battlecode.common.*;
import java.util.Random;
import java.util.*;

strictfp class Pathing {
    
    /*
    * The direction that we are trying to use to go around the obstacle
    */
    private static Direction bugDirection = null;
    private static final int ACCEPTABLE_RUBBLE = 25;

    static void walkTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (!rc.isMovementReady()) {
            return;
        }
        MapLocation currentLocation = rc.getLocation();
        //if(currentLocation == target) //this is bad!
        if (currentLocation.equals(target))
            //already at goal
            return;
        Direction d = currentLocation.directionTo(target); 
        if(rc.canMove(d) && !isObstacle(rc, d)) {
            // Ease case of Bug 0
            rc.move(d);
            bugDirection = null;
        }
        else 
        {
            //Hard case of Bug 0
            //have to go around it
            if(bugDirection == null)
            {
                //If we don't know what we're trying to do
                //make something up
                //And what better than the pick as the direction we want to go in
                //best direction towards goal
                bugDirection = d;
            }
            for(int i=0; i<0; i++)
            {
                if(rc.canMove(bugDirection) && !isObstacle(rc, bugDirection))
                {
                    rc.move(bugDirection);
                    bugDirection = bugDirection.rotateLeft();
                    break;
                }
                else
                {
                    bugDirection = bugDirection.rotateRight();
                }
            }
        }
    }

    private static boolean isObstacle(RobotController rc, Direction d) throws GameActionException
    {
        //TODO
        MapLocation adjacentLocation = rc.getLocation().add(d);
        int rubbleOnLocation = rc.senseRubble(adjacentLocation);
        return rubbleOnLocation > ACCEPTABLE_RUBBLE;
    }
}
