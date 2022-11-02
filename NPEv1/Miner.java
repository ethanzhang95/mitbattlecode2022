package NPEv1;

import battlecode.common.*;
import java.util.Random;
import java.util.*;

public class Miner extends BaseRobot {
    
    @SuppressWarnings("unused")
    int w = rc.getMapWidth();
    int h = rc.getMapHeight();
    boolean[][] visited = new boolean[w][h];
    int count = 0;
    public Miner(RobotController rc){
	super(rc);
   }
   public void play() {
        // Try to mine on squares around us.
        MapLocation me = rc.getLocation();
        if(visited[me.x][me.y] == false)
        {
            visited[me.x][me.y] = true;
        }
      try{
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation)) {
                    rc.mineLead(mineLocation);
                }
            } 
        }

        //MapLocation[] nearby = rc.senseNearbyLocationsWithLead(1);
        MapLocation[] nearby = rc.senseNearbyLocationsWithLead(100);
        int nx; int ny; int diffx; int diffy;
        for(int i=0; i<nearby.length; i++)
        {
            nx = nearby[i].x; ny = nearby[i].y;
            diffx = nx-(me.x); diffy = ny-(me.y);
            //if(diffx == 1 && diffy == 1)
            if(diffx > 0 && diffy > 0)
            {
                if(rc.canMove(directions[1]))
                    rc.move(directions[1]);
            }
            else if(diffx > 0 && diffy == 0)
            {
                if(rc.canMove(directions[2]))
                    rc.move(directions[2]);
            }
            else if(diffx < 0 && diffy == 0)
            {
                if(rc.canMove(directions[6]))
                    rc.move(directions[6]);
            }
            else if(diffx < 0 && diffy < 0)
            {
                if(rc.canMove(directions[5]))
                    rc.move(directions[5]);
            }
            else if(diffx == 0 && diffy < 0)
            {
                if(rc.canMove(directions[4]))
                    rc.move(directions[4]);
            }
            else if(diffx < 0 && diffy > 0)
            {
                if(rc.canMove(directions[7]))
                    rc.move(directions[7]);
            }
            else if(diffx > 0 && diffy < 0)
            {
                if(rc.canMove(directions[3]))
                    rc.move(directions[3]);
            }
            else if(diffx == 0 && diffy > 0)
            {
                if(rc.canMove(directions[0]))
                    rc.move(directions[0]);
            }
        }
        if(nearby.length == 0)
        {
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) 
            {
                rc.move(dir);
                System.out.println("I moved!");
            }
        }

     } catch (Exception e){
          e.printStackTrace();
     }
   }
}
