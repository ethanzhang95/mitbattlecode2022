package NPEv2;

import battlecode.common.*;
import java.util.Random;
import java.util.*;

public class Miner extends BaseRobot {
    
    @SuppressWarnings("unused")
    static Direction exploreDir = null;
    public Miner(RobotController rc){
	    super(rc);
    }
    public void play() {
      try{
        if(exploreDir == null)
        {
            rng.setSeed(rc.getID());
            exploreDir = directions[rng.nextInt(directions.length)];
        }
        rc.setIndicatorString(exploreDir.toString());

        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > 1) {
                    rc.mineLead(mineLocation);
                }
            } 
        }

        int visionRadius = rc.getType().visionRadiusSquared;
        MapLocation[] nearbyLocations = rc.getAllLocationsWithinRadiusSquared(me, visionRadius);

        MapLocation targetLocation = null;
        int distanceToTarget = Integer.MAX_VALUE;

        for(MapLocation tryLocation : nearbyLocations){
            if(rc.senseLead(tryLocation) > 1 || rc.senseGold(tryLocation) > 0)
            {
                int distanceTo = me.distanceSquaredTo(tryLocation);
                if(distanceTo < distanceToTarget)
                { 
                    targetLocation = tryLocation;
                    distanceToTarget = distanceTo;
                }
            }
        }

        if(targetLocation != null) {
            Pathing.walkTowards(rc, targetLocation);
        }
        else
        {
            if(rc.canMove(exploreDir))
            {
                rc.move(exploreDir);
            }
            else if(!rc.onTheMap(rc.getLocation().add(exploreDir)))
            {
                exploreDir = exploreDir.opposite();
            }
        }

        // Also try to move randomly
        Direction dir = directions[rng.nextInt(directions.length)];
        if(rc.canMove(dir)) {
            rc.move(dir);
            System.out.println("I moved!");
        }

     } catch (Exception e){
          e.printStackTrace();
     }
   }
}
