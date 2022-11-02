package NPEv2;

import battlecode.common.*;
import java.util.*;

/**
 * Base Player is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public abstract class BaseRobot {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };
    
    RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public BaseRobot(RobotController rc){
       this.rc = rc;
   }
   abstract void play();
   
    public double heuristic(MapLocation start, MapLocation target) throws GameActionException{
        int distance = start.distanceSquaredTo(target);
        // Add a value for the rubble at start
        if (rc.canSenseLocation(start)) {
            distance += rc.senseRubble(start);
        }
        return distance;
    }

    public void astar(MapLocation target) throws GameActionException{
        PQ frontier = new PQ();
        MapLocation start = rc.getLocation();
        if (start.equals(target)){
            return;
        }

        // adds the initial start to explored and frontier
        double f_start = heuristic(start, target);
        ArrayList<MapLocation> path = new ArrayList<>();
        path.add(start);
        // Node(path of map locations to frontier, estimated total path length)
        frontier.push(new Node(path, f_start));
        // Keeps for each map location, keep track of current best {f-value, h-value=g-value+f-value}
        // Also keeps track of which locations have been explored
        HashMap<MapLocation, Double[]> explored = new HashMap<>();
        explored.put(start, new Double[]{f_start, f_start});

        // Only runs the search loop 20 times to conserve bytecode, seems like it's generally enough
        for (int i = 0; i<=20; i++) {
            // If it can't find a possible path
            if (frontier.isEmpty()){
                // try to move randomly.
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    System.out.println("I moved!");
                }
                return;
            }

            // Next location in the frontier (smallest f-value)
            Node s = frontier.pop();
            ArrayList<MapLocation> sPath = (ArrayList<MapLocation>)s.obj;
            MapLocation currentLocation = sPath.get(sPath.size()-1);
            // If the max number of iteration reached OR
            // It found the target
            if (Clock.getBytecodeNum() > 5000 || i == 20 || currentLocation.equals(target)) {
                Direction moveTo = start.directionTo(sPath.get(1));
                if (rc.canMove(moveTo))
                    rc.move(moveTo);
                rc.setIndicatorString(sPath.toString());
                return;
            }

            // Adds each direction to frontier
            for (Direction dir: directions){
                MapLocation child = currentLocation.add(dir);
                if (rc.canSenseLocation(child)){
                    if (!rc.onTheMap(child))
                        continue;
                    if (rc.isLocationOccupied(child))
                        continue;
                } else {
                    if (child.x < 0 || child.y < 0 || child.x >= rc.getMapWidth() || child.y >= rc.getMapHeight())
                        continue;
                }
                double g_child = sPath.size() + 1;
                ArrayList<MapLocation> sPathCopy = (ArrayList<MapLocation>)sPath.clone();
                // Checks if child has already been checked
                if (!explored.containsKey(child)){
                    // Makes the child's node
                    double h_child = heuristic(child, target);
                    double f_child = g_child + h_child;
                    sPathCopy.add(child);
                    Node child_node = new Node(sPathCopy, f_child);
                    // Adds child to be checked
                    frontier.push(child_node);
                    explored.put(child, new Double[]{f_child, h_child});
                // If child hasn't been checked, check if the new f-value is smaller than the old one
                } else if (explored.get(child)[0] > g_child + explored.get(child)[1]){
                    // Makes the child's node
                    double f_child = g_child + explored.get(child)[1];
                    sPathCopy.add(child);
                    Node child_node = new Node(sPathCopy, f_child);
                    // Adds child to be checked
                    frontier.push(child_node);
                    explored.put(child, new Double[]{f_child, explored.get(child)[1]});
                }
            }
        }

    }
}
