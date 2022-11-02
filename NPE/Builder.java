package NPE;

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
    public int turns = 0;
    public int repairinterval = 0;
    public MapLocation newMe = null;
    public int intialDirection = 0; 

    @SuppressWarnings("unused")
    public Builder(RobotController rc){
	super(rc);
        MapLocation currentLocation = rc.getLocation();
        spawnX = currentLocation.x;
        spawnY = currentLocation.y;
	dist_threshhold = 5;
	intialDirection = (int)(Math.random()*3)-1;
    }

    public void play() {
        MapLocation me = rc.getLocation();
        Direction dir = directions[rng.nextInt(directions.length)];
        int distance=0;
	int newX=0, newY=0;

	turns++;
        repairinterval++;
        
	try{
           
	 //System.out.println("building a TOWER");
	  int radius = rc.getType().actionRadiusSquared;
          Team myteam = rc.getTeam();
	
	  if (turns==1){
                if((rc.getMapWidth() - spawnX - 10) > spawnX)
                        newX=spawnX+8;
                else
                        newX=spawnX-8;

                if((rc.getMapHeight() - spawnY - 10) > spawnY)
                        newY=spawnY+8;
                else newY=spawnY-8;

                // move away from the archon to build other buildings.
  
	         switch(intialDirection){
                    case 0:
                        // 180 rotation case
			newMe = new MapLocation(newX, newY);
                        break;
                     case -1:
                     case 1:
			newMe = new MapLocation(spawnX, spawnY); //next to Archon to keep repairing it
                        break;
	        }
		System.out.println("Builder intialDirection: " + intialDirection); 
	 }

	 if(newMe!=null && turns<8)
	 {   
		 System.out.println("builder moving away from spawn, x:y" + newX + " "+ newY + "current:" + me.x + " "+ me.y);
			noPathing(newMe);
	 }
	 
	 if(rng.nextBoolean())
	 {
	   if ((totalTower <1)) 
	   {
	       rc.setIndicatorString("Trying to build a TOWER");
               if (rc.canBuildRobot(RobotType.WATCHTOWER, dir)) {
		totalTower++;
                rc.buildRobot(RobotType.WATCHTOWER, dir);
		MapLocation tloc=rc.adjacentLocation(dir);
		while(rc.canRepair(tloc))
		   rc.repair(tloc);
                }
	    }
	 }else {
            if((totalLab<1))
	    {                 
	       rc.setIndicatorString("Trying to build a Lab");
               if (rc.canBuildRobot(RobotType.LABORATORY, dir)) {
                //System.out.println("building a LAB real");
		totalLab++;
                rc.buildRobot(RobotType.LABORATORY, dir);
		MapLocation tloc=rc.adjacentLocation(dir);
                while(rc.canRepair(tloc))
                   rc.repair(tloc);
                }
              }
	}
	
	if(repairinterval==5)
	{
        	RobotInfo[] mybots = rc.senseNearbyRobots(radius, myteam);
	        repairinterval=0;

        	 if(mybots.length > 0)
        	{
           	   for (int i=mybots.length-1;i-->0;)
           	   {
                     if(rc.canRepair(mybots[i].location))
                       rc.repair(mybots[i].location);
                     if(rc.canMutate(mybots[i].location))
                       rc.mutate(mybots[i].location);
           	  }
        	}
	}
       } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                //System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

         } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                //System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

        }
  }
}
