package NPEv1;

import battlecode.common.*;
import java.util.*;

public class Builder extends BaseRobot {
      
 
    public int totalTower=0;
    public int totalLab=0;
    public int totalMove=0;
    public int spawnX = 0;
    public int spawnY = 0;
    public int dist_threshhold;
    public MapLocation myTower;
    public MapLocation myLab;

    @SuppressWarnings("unused")
    public Builder(RobotController rc){
	super(rc);
        MapLocation currentLocation = rc.getLocation();
        spawnX = currentLocation.x;
        spawnY = currentLocation.y;
	dist_threshhold = 10;
    }

    public void play() {
        MapLocation me = rc.getLocation();
        Direction dir = directions[rng.nextInt(directions.length)];
        int distance=0;

        if (me.x > spawnX)
		distance+= (me.x - spawnX);
        else distance+= (spawnX - me.x);

	if (me.y > spawnY)
		distance+= (me.y - spawnY);
	else distance+= (spawnY - me.y);

        try{
           
            if (rc.canMove(dir)) 
            {
                rc.move(dir);
		totalMove++;
            }
	 System.out.println("building a TOWER");
/* v1 no repair 
	  int radius = rc.getType().actionRadiusSquared;
          Team myteam = rc.getTeam();
          RobotInfo[] mybots = rc.senseNearbyRobots(radius, myteam);

         if(mybots.length > 0)
	{
	   for (int i=mybots.length-1;i-->0;)
	   {
           	if(rc.canRepair(mybots[i].location))
		  rc.repair(mybots[i].location);     
	   }
	}
*/	 
	 if(rng.nextBoolean())
	{

	   if ((totalTower <1) && (distance>dist_threshhold)) 
	   {
	       rc.setIndicatorString("Trying to build a TOWER");
               if (rc.canBuildRobot(RobotType.WATCHTOWER, dir)) {
		totalTower++;
                rc.buildRobot(RobotType.WATCHTOWER, dir);
                }
	    }
	 }else {
            if((totalLab<1) && (distance>dist_threshhold))
	    {                 
	       rc.setIndicatorString("Trying to build a Lab");
               if (rc.canBuildRobot(RobotType.LABORATORY, dir)) {
                System.out.println("building a LAB real");
		totalLab++;
                rc.buildRobot(RobotType.LABORATORY, dir);
              }
	    }
	   }
         } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

         }  catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

        }
  }
}
