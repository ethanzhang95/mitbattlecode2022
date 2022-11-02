package NPEv1;

import battlecode.common.*;

import java.awt.*;
import java.util.*;

public class Soldier extends BaseRobot {
    public int intialDirection = (int)(Math.random()*3) - 1;
    public int spawnX = 0;
    public int spawnY = 0;
    public boolean foundOpponent = false;
    public int archonCount = rc.getArchonCount();
    public boolean foundArchon = false;
    @SuppressWarnings("unused")
    public Soldier(RobotController rc){
	super(rc);
   }

    public void play() {
        // Increase turn count
        turnCount++;
        // If first turn of bot
        if (turnCount == 1) {
            MapLocation currentLocation = rc.getLocation();
            spawnX = currentLocation.x;
            spawnY = currentLocation.y;
        }

        try {
            // Try to attack someone
            int radius = rc.getType().actionRadiusSquared;
            Team opponent = rc.getTeam().opponent();
            RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

            Arrays.sort(enemies, new Comparator<RobotInfo>() {
                @Override
                public int compare(RobotInfo o1, RobotInfo o2){
                    if (o1.getType().equals(o2.getType())){
                        if (o1.health == o2.health) {
                            return rc.getLocation().distanceSquaredTo(o1.location) - rc.getLocation().distanceSquaredTo(o2.location);
                        } else return o1.health - o2.health;
                    } else {
                        RobotType type1 = o1.getType();
                        RobotType type2 = o2.getType();
                        int value1 = type1 == RobotType.SOLDIER ? 0:1;
                        int value2 = type2 == RobotType.SOLDIER ? 0:1;

                        try {
                            if (!foundArchon) {
                                if (type1 == RobotType.ARCHON) {
                                    recordArchon(o1);
                                    foundArchon = true;
                                }
                                if (type2 == RobotType.ARCHON) {
                                    recordArchon(o2);
                                    foundArchon = true;
                                }
                            }
                        } catch(GameActionException e) {

                        }
                        return value1 - value2;
                    }
                }
            });

            if (enemies.length > 0) {
                foundOpponent = true;
                MapLocation toAttack = enemies[0].location;
                boolean isArchon = enemies[0].type == RobotType.ARCHON;
                if (rc.canAttack(toAttack)) {
                    rc.attack(toAttack);
                }
                // Notify that this archon is dead
                if (!rc.isLocationOccupied(toAttack) && isArchon) {
                    for (int i = 0; i<archonCount; i++) {
                        int value = rc.readSharedArray(i);
                        if (value%100 == toAttack.y && value/100%100 == toAttack.x) {
                            rc.writeSharedArray(i, 40000 + value % 10000);
                            break;
                        }
                    }
                }

                astar(toAttack);
            }  else if (!foundOpponent){
                /*
                // Also try to move randomly.
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {

                    MapLocation nloc = rc.adjacentLocation(dir);
                    if (!rc.isLocationOccupied(nloc)) {
                        rc.move(dir);
                        System.out.println("I moved!");
                    }
                }
                 */

                Direction dir = null;
                switch(intialDirection){
                    case -1:
                        if (rc.getMapWidth()-1 - spawnX > spawnX)
                            dir = Direction.EAST;
                        else
                            dir = Direction.WEST;
                        break;
                    case 0:
                        // 180 rotation case
                        MapLocation potentialArchon = new MapLocation(rc.getMapWidth()-1 - spawnX, rc.getMapHeight()-1 - spawnY);
                        astar(potentialArchon);
                        break;
                    case 1:
                        if (rc.getMapHeight()-1 - spawnY > spawnY)
                            dir = Direction.NORTH;
                        else
                            dir = Direction.SOUTH;
                        break;
                    default:
                        // head towards a found archon location or random direction
                        int value = 0;
                        boolean moved = false;
                        for (int i = 0; i<archonCount; i++){
                            value = rc.readSharedArray(i);
                            if (value < 40000 && value >= 10000) {
                                MapLocation archonLocation = new MapLocation((value/100)%100, value%100);
                                astar(archonLocation);
                                moved = true;
                            }
                        }
                        if (!moved){
                            dir = directions[rng.nextInt(directions.length)];
                        }
                }

                // Current location
                MapLocation currentLocation = rc.getLocation();

                if (dir == null) {
                    return;
                } else if (!rc.onTheMap(currentLocation.add(dir))){
                    // Don't head in the inital direction anymore
                    intialDirection = 2;
                }else if (rc.canMove(dir)) {
                    //rc.setIndicatorString(dir.toString());
                    rc.move(dir);
                    System.out.println("I moved!");
                } else {
                    // Try to move randomly
                    dir = directions[rng.nextInt(directions.length)];
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                        System.out.println("I moved!");
                    }
                }
            } else {
                // Try to move randomly
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    System.out.println("I moved!");
                }
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

    // Return true if new archon, false if already found
    public boolean recordArchon(RobotInfo robotInfo) throws GameActionException{
        MapLocation location = robotInfo.location;
        int x = location.x;
        int y = location.y;
        int value = 100 * location.x + location.y;
        if (intialDirection == -1) {
            value += 10000;
        } else if (intialDirection == 0) {
            value += 30000;
        } else {
            value += 20000;
        }
        for (int i = 0; i<archonCount; i++){
            int arrayValue = rc.readSharedArray(i);
            if (value == arrayValue) {
                return false;
            } else if (arrayValue == 0){
                rc.writeSharedArray(i, value);
                return true;
            }
        }
        return false;
    }
}
