package NPEv2;

import battlecode.common.*;
import java.util.Random;

public class Lab extends BaseRobot {
      
    
    @SuppressWarnings("unused")
    public Lab(RobotController rc){
	super(rc);
   }
   public void play() {
        MapLocation me = rc.getLocation();

	try {
	  if(rc.canTransmute())
	  {
	     rc.transmute();
	   }
	} catch (GameActionException e) {
           System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
       }
   }
}
