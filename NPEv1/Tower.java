package NPEv1;

import battlecode.common.*;
import java.util.*;

public class Tower extends BaseRobot {
      
    @SuppressWarnings("unused")
    public Tower(RobotController rc){
	super(rc);
   }
   public void play() {
        MapLocation me = rc.getLocation();
        int   radius =  rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        
        Arrays.sort(enemies, new Comparator<RobotInfo>() {
            @Override
            public int compare(RobotInfo o1, RobotInfo o2) {
                if (o1.getType().equals(o2.getType())){
                    if (o1.health == o2.health) {
                        return rc.getLocation().distanceSquaredTo(o1.location) - rc.getLocation().distanceSquaredTo(o2.location);
                    } else return o1.health - o2.health;
                } else {
                    int type1 = o1.getType() == RobotType.SOLDIER ? 0:1;
                    int type2 = o2.getType() == RobotType.SOLDIER ? 0:1;
                    return  type1 - type2;
                }
            }
        });
	
	try {

	System.out.println("Tower: finding enemies--"+enemies.length);

        if(rc.getMode()==RobotMode.PORTABLE)
	{
	   if(rc.canTransform())
		rc.transform(); 
	}

        if (enemies.length > 0) {
	    for (int i=enemies.length; i-->0;)
            { MapLocation toAttack = enemies[i].location;
              if (rc.canAttack(toAttack)) {
		System.out.println("Tower: attacking enemies approaching");
                rc.attack(toAttack);
               }
	    }
         }
	   // Also try to move randomly.
 /*         Direction dir = directions[rng.nextInt(directions.length)];
          if (rc.canMove(dir)) {
               rc.move(dir);
                System.out.println("I moved!");
          } */
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
