package NPEv1;

import battlecode.common.*;
import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {


    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        BaseRobot robot;
	int turnCount=0;

        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");
        
        switch (rc.getType()) {
                    case ARCHON:
                         robot = new Archon(rc); 
                         break;
                    case MINER:  
                         robot = new Miner(rc); 
                         break;
                    case SOLDIER:
		    default:    
                         robot=new Soldier(rc); 
                         break;
                    case LABORATORY: // Examplefuncsplayer doesn't use any of these robot types below.
                         robot=new Lab(rc);
 			 break;
		    case WATCHTOWER: // You might want to give them a try!
		         robot=new Tower(rc);
			 break;
                    case BUILDER:
			 robot=new Builder(rc);
                         break; 
                   //case SAGE:       break;
                }

       
        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.
      
            System.out.println("Age: " + turnCount + "; Location: " + rc.getLocation());
            turnCount++;
	    

               robot.play();
                Clock.yield();
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

}
