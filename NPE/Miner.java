package NPE;

import battlecode.common.*;
import java.util.Random;
import java.util.*;

public class Miner extends BaseRobot {
    
    @SuppressWarnings("unused")
    static Direction exploreDir = null;
    public Team opponent;
    public int visionRadius;

    public Miner(RobotController rc){
	    super(rc);
	    opponent = rc.getTeam().opponent();
	    visionRadius = rc.getType().visionRadiusSquared;
    }

    public boolean isSafeToMine(MapLocation loc) throws GameActionException  {
        RobotInfo[] enemies = rc.senseNearbyRobots(visionRadius, opponent);
	
  	if(enemies.length >0)
        {
        for(int j=enemies.length-1; j-->0;)
        {
           switch(enemies[j].type){
                case SOLDIER:
                case WATCHTOWER:
		        return false;
                case ARCHON:
			System.out.println("Miner found enemy Archon");
                        recordArchon(enemies[j]);
                        break;
                default:
                        break;
           }
        }
        }

        return true;
    }

   public void runAway() throws GameActionException {
        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(visionRadius,opponent);
        RobotInfo nearestEnemy = null;
	MapLocation me = rc.getLocation();
        int smallestDistSq = 999999;
        for (RobotInfo enemy : visibleEnemies) {
            if (enemy.type == RobotType.SOLDIER || enemy.type==RobotType.WATCHTOWER) {
                int distSq = me.distanceSquaredTo(enemy.location);
                if (distSq < smallestDistSq) {
                    smallestDistSq = distSq;
                    nearestEnemy = enemy;
                }
            }
        }
        if (nearestEnemy == null) return;

        Direction away = nearestEnemy.location.directionTo(me);
        Direction[] dirs = new Direction[] { away, away.rotateLeft(), away.rotateRight(), away.rotateLeft().rotateLeft(), away.rotateRight().rotateRight() };
        Direction flightDir = null;
        for (Direction dir : dirs) {
            if (rc.canMove(dir)) {
                //if (!inEnemyTowerOrHQRange(me.add(dir), enemyTowers)) { // this gets checked twice :(
                    if (isSafeToMine(me.add(dir))) {
                        rc.move(dir);
                        return;
                    } else if (flightDir == null) {
                        flightDir = dir;
                    }
                }
           // }
        }
        if (flightDir != null) {
            rc.move(flightDir);
        }
    }

    public void play() {
      try{
        if(exploreDir == null)
        {
            rng.setSeed(rc.getID());
            exploreDir = directions[rng.nextInt(directions.length)];
        }
        rc.setIndicatorString(exploreDir.toString());
	
	//if (rc.getHealth()<10)
        //      decreaseMiner();  //if unhealthy, remove from the global counter
	MapLocation me = rc.getLocation();
        if(!isSafeToMine(me)){
		System.out.println("Running  away Miner!");
                runAway();
                //return;
        } 

	me = rc.getLocation();
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
            //Pathing.walkTowards(rc, targetLocation);
	    noPathing(targetLocation);
	    //pathfind(targetLocation);
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
