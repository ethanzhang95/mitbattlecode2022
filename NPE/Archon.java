package NPE;

import battlecode.common.*;
import java.util.Random;

public class Archon extends BaseRobot {
      
    
    @SuppressWarnings("unused")

    Direction myDir;
    MapLocation myLoc;
    public int mapH;
    public int mapW;    
    public int totalSoldier=0;
    public int totalMiner=0;
    public int totalBuilder=0;
    public Team myteam;
    public Team opponent;
    public boolean previousbuildsoldier=false;
    public int turns=0;
    public int recentHealth;
    public int visionRadius;

    public Archon(RobotController rc){
	super(rc);
        
        //this.myLoc = rc.getLocation();
        //this.mapH  = rc.getMapHeight();
        //this.mapW  = rc.getMapWidth();
	this.myteam = rc.getTeam();
	this.opponent = this.myteam.opponent();
	recentHealth=600;       
	visionRadius = rc.getType().visionRadiusSquared;
   }
   
    public void play() {
       // Pick a direction to build in.
    	int i;
        int built=0;    
        Direction dir = directions[rng.nextInt(directions.length)]; 
        	
        try {
	 //turns++;
	 int currHealth=rc.getHealth();
         
         //RobotInfo[] enemies = rc.senseNearbyRobots(visionRadius, opponent);
	
	//if health is down from previous or enemy in sight, get soldier up 
	if(currHealth<recentHealth) //||enemies.length>0)
	{
	     if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                   rc.buildRobot(RobotType.SOLDIER, dir);
                   //totalSoldier++;
		   built=1;
                   previousbuildsoldier = true;
               }
	} 
	else if(dir==Direction.EAST && rc.getTeamLeadAmount(myteam) < rc.getTeamLeadAmount(opponent)+30){
            //rc.setIndicatorString("Trying to build a miner");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
		   rc.buildRobot(RobotType.MINER, dir);
                   //totalMiner++;
	 	   previousbuildsoldier=false;
                   built=1;
            }
	   } 
	 else if (dir==Direction.SOUTH)
          {
 	       if (totalBuilder < 2 && rc.getTeamLeadAmount(myteam)>150) {
               //rc.setIndicatorString("Trying to build a builder");
               if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
                  rc.buildRobot(RobotType.BUILDER, dir);
                  totalBuilder++;
		  previousbuildsoldier=false;
                  built=1;
              }
            }
 	   }
        
	 recentHealth=currHealth;
         if(built==0)
	 {
         //rc.setIndicatorString("Trying to build more soldier");
	 
	 if(rc.getTeamLeadAmount(myteam) < rc.getTeamLeadAmount(opponent)+30  && previousbuildsoldier)
	 {
		if (rc.canBuildRobot(RobotType.MINER, dir)) {
                   rc.buildRobot(RobotType.MINER, dir);
                   //totalMiner++;
		   previousbuildsoldier = false;
 	           }

	 }
         else { 
		if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                   rc.buildRobot(RobotType.SOLDIER, dir);
                   //totalSoldier++;
		   previousbuildsoldier = true;
               }
	    }
         }
	
	 /*
	 //try repair nearby droids, seem to bbe too costly, disabled now
 	 RobotInfo[] mybots = rc.senseNearbyRobots(visionRadius, myteam);
                 if(mybots.length > 0)
                {
                   for (i=mybots.length-1;i-->0;)
                   {
                     if(rc.canRepair(mybots[i].location))
                       rc.repair(mybots[i].location);
                   }
                }
	 */
	 //System.out.println("Consumed Bytecount " + Clock.getBytecodeNum() + "totalsoldier" + totalSoldier + " miner"  + totalMiner);
    }  catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                //System.out.println(rc.getType() + " Exception");
                 e.printStackTrace();

    }  catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
               //System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

    }

  }
}
