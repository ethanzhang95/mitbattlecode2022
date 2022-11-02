package NPEv2;

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

        try {
            // If first turn of bot
            if (turnCount == 1) {
                MapLocation currentLocation = rc.getLocation();
                for (Direction dir: directions) {
                    MapLocation checkLocation = currentLocation.add(dir);
                    if (rc.onTheMap(checkLocation)) {
                        RobotInfo potentialParent = rc.senseRobotAtLocation(checkLocation);
                        if (potentialParent != null) {
                            if (potentialParent.getType()==RobotType.ARCHON && potentialParent.getTeam()==rc.getTeam()) {
                                currentLocation = currentLocation.add(dir);
                                spawnX = currentLocation.x;
                                spawnY = currentLocation.y;
                            }
                        }
                    }
                }
            }

            rc.setIndicatorString(""+rc.readSharedArray(0)+" "+rc.readSharedArray(1)+" "+rc.readSharedArray(2));

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
                        int value1 = 1;
                        int value2 = 1;

                        switch (type1) {
                            case SOLDIER:
                            case ARCHON:
                            case SAGE:
                            case WATCHTOWER:
                                value1 = 0;
                                break;
                        }
                        switch (type2) {
                            case SOLDIER:
                            case ARCHON:
                            case SAGE:
                            case WATCHTOWER:
                                value2 = 0;
                                break;
                        }

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
                        int archonCountFromArray = value/10000;
                        if (archonCountFromArray != 5 && archonCountFromArray > archonCount) {
                            archonCount = archonCountFromArray;
                        }
                        if (value%100 == toAttack.y && value/100%100 == toAttack.x) {
                            rc.writeSharedArray(i, 50000 + value % 10000);
                            break;
                        }
                    }
                }

                astar(toAttack);
            }  else{// if (!foundOpponent){
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
                MapLocation potentialArchon = null;
                switch(intialDirection){
                    case -1:
                        potentialArchon = new MapLocation(rc.getMapWidth()-1 - spawnX, spawnY);
                        rc.setIndicatorString(""+potentialArchon+" "+intialDirection);
                        astar(potentialArchon);
                        break;
                    case 0:
                        // 180 rotation case
                        potentialArchon = new MapLocation(rc.getMapWidth()-1 - spawnX, rc.getMapHeight()-1 - spawnY);
                        astar(potentialArchon);
                        break;
                    case 1:
                        potentialArchon = new MapLocation(spawnX, rc.getMapHeight()-1 - spawnY);
                        astar(potentialArchon);
                        break;
                    default:
                        // head towards a found archon location or random direction
                        int value = 0;
                        boolean moved = false;
                        for (int i = 0; i<archonCount; i++){
                            value = rc.readSharedArray(i);
                            int archonCountFromArray = value/10000;
                            if (archonCountFromArray != 5 && archonCountFromArray > archonCount) {
                                archonCount = archonCountFromArray;
                            }
                            if (value < 50000 && value >= 10000) {
                                MapLocation archonLocation = new MapLocation((value/100)%100, value%100);
                                if (rc.canSenseLocation(archonLocation)) {
                                    if (rc.senseRobotAtLocation(archonLocation) == null) {
                                        rc.writeSharedArray(i, 50000 + value % 10000);
                                        continue;
                                    }
                                }
                                astar(archonLocation);
                                moved = true;
                            }
                        }
                        if (!moved){
                            //dir = directions[rng.nextInt(directions.length)];
                            astar(new MapLocation(spawnX, spawnY));
                        }
                }
                // Current location
                MapLocation currentLocation = rc.getLocation();
                rc.setIndicatorString(""+intialDirection);
                if (intialDirection != 2 && isNotOpponentArchon(potentialArchon)) {
                    // Don't head in the inital direction anymore
                    intialDirection = 2;
                }else if (dir == null){
                    return;
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
            }/* else {
                // Try to move randomly
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    System.out.println("I moved!");
                }
            }*/
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

    // false if can't check or is opponent archon
    // true if confirmed not opponent archon, or dead opponent archon
    public boolean isNotOpponentArchon(MapLocation potentialArchon) throws GameActionException{
        if (potentialArchon == null) {
            return false;
        }
        if (rc.canSenseLocation(potentialArchon)) {
            RobotInfo potentialArchonInfo = rc.senseRobotAtLocation(potentialArchon);
            if (potentialArchonInfo == null) {
                return true;
            } else if (potentialArchonInfo.getTeam() == rc.getTeam()){
                return true;
            } else {
                return false;
            }
        } else {
            for (int i = 0; i<archonCount; i++) {
                int value = rc.readSharedArray(i);
                int archonCountFromArray = value/10000;
                if (archonCountFromArray != 5 && archonCountFromArray > archonCount) {
                    archonCount = archonCountFromArray;
                }
                rc.setIndicatorString(""+value);
                if (potentialArchon.x == value/100%100 && potentialArchon.y == value%100) {
                    if (value/10000 == 5)
                        return true;
                    else return false;
                }
            }
            return false;
        }
    }

    // Return true if new archon, false if already found
    public boolean recordArchon(RobotInfo robotInfo) throws GameActionException{
        MapLocation location = robotInfo.location;
        int x = location.x;
        int y = location.y;
        int value = 100 * location.x + location.y;
        for (int i = 0; i<archonCount; i++){
            int arrayValue = rc.readSharedArray(i);
            int archonCountFromArray = arrayValue/10000;
            if (archonCountFromArray != 5 && archonCountFromArray > archonCount) {
                archonCount = archonCountFromArray;
            }
            if (value == arrayValue%10000) {
                return false;
            } else if (arrayValue == 0){
                rc.writeSharedArray(i, archonCount*10000 + value);
                return true;
            }
        }
        return false;
    }
}
