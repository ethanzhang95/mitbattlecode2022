package NPEv1;

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

    int turns=0;

    public Archon(RobotController rc){
	super(rc);
        
        this.myLoc = rc.getLocation();
        this.mapH  = rc.getMapHeight();
        this.mapW  = rc.getMapWidth();
        
   }
   
    public void play() {
       // Pick a direction to build in.
    	int i;    
        Direction dir = directions[rng.nextInt(directions.length)]; 
        	
        try {
 	
	 //TBD: optimize spawn toward some directions and amount of each, especially when ARCHON is in a corner        
	 if(dir==Direction.EAST){
            rc.setIndicatorString("Trying to build a miner");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
		   rc.buildRobot(RobotType.MINER, dir);
                   totalMiner++;
            }
	   } 
	 else if (dir==Direction.WEST)
          {
 	       if (totalBuilder < 15 && totalSoldier > 30) {
               rc.setIndicatorString("Trying to build a builder");
               if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
                rc.buildRobot(RobotType.BUILDER, dir);
                totalBuilder++;
              }
            }
 	   }
	 else {
               rc.setIndicatorString("Trying to build more soldier");
               if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                  rc.buildRobot(RobotType.SOLDIER, dir);
                  totalSoldier++;
               }
          }

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
