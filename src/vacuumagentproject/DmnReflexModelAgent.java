/*
 * @author Dalton Nickerson
 * 
 */
package vacuumagentproject;

import java.util.LinkedList;

public class DmnReflexModelAgent extends VacuumAgent {

    /*
     This 2d array of map entries will represent the map of the area to be vacuumed.
     The first index will be for the rows (y) and the second will be for the columns (x).
     */
    private MapEntry[][] map;

    //A list of all of the movements we can perform to change position
    private VacuumAction[] movements = {VacuumAction.RIGHT, VacuumAction.BACK, VacuumAction.LEFT, VacuumAction.FORWARD};
    //A sequence of all of the changes that can occur with
    private int[] movementChanges = {0, 1, 0, -1, 0};
    //An integer determining which movement action we are facing
    private int direction;
    //keep track of all the movement actions taken
    private LinkedList<Integer> actionsTaken;
    //keep track of how many unexplored empty tiles are known of
    private int unexploredCount;
    private int x, y;

    /**
     * MapEntry will function as enumeration using a byte to represent the
     * status of a map tile.
     *
     * MapEntry's may be any of the following: * Unknown - Nothing is known
     * about the tile. * Obstacle - The tile is a known obstacle. * Clean - The
     * Tile is known to be clean.
     *
     * These three state properties will be represented as bits in the state
     * byte, as Java usually uses a byte or more per boolean and I thought this
     * usage of booleans as bits in a byte would be more memory efficient.
     *
     * The state of the MapEntry may be represented bitwise as it is below:
     *
     * 00000ABC
     *
     * C is whether we know what the tile is (0 if unknown, otherwise 1) B is
     * whether we know that the tile is an obstacle (1 if obstacle, otherwise 0)
     * A is whether we know that the tile is clean (1 if clean, otherwise 0)
     */
    private enum MapEntry {
        unknown, obstacle, empty, clean;
    }

    public DmnReflexModelAgent() {
        map = new MapEntry[1000][1000];
        unexploredCount = 1;
        actionsTaken = new LinkedList<Integer>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = MapEntry.unknown;
            }
        }
        direction = 0;
    }

    /**
     * Check the four adjacent cells next to the agent and update map for
     * unknown tiles and obstacles.
     *
     * @param percept the bump percept to check for obstacles.
     */
    private void checkAdjacentCells(VacuumBumpPercept percept) {
        for (int t = 0; t < 4; t++) {
            //if we don't know anything about this tile....
            if (map[(movementChanges[t] + y + map.length) % map.length][(movementChanges[t + 1] + x + map[0].length) % map[0].length] == MapEntry.unknown) {
                //check if we will bump into it....
                if (percept.willBump(movements[t])) {
                    //if so...mark obstacle on map
                    map[(movementChanges[t] + y + map.length) % map.length][(movementChanges[t + 1] + x + map[0].length) % map[0].length] = MapEntry.obstacle;
                } else {
                    //otherwise, it's an empty space,but we don't know it's clean yet...we'll check that later
                    map[(movementChanges[t] + y + map.length) % map.length][(movementChanges[t + 1] + x + map[0].length) % map[0].length] = MapEntry.empty;
                    unexploredCount++;
                }
            }
        }
    }

    /**
     * Change the agent's current direction to be 90 degrees relative to it's
     * current direction.
     *
     * @param left whether to turn 90 degrees left or right. Left if true.
     */
    private void turn(boolean left) {
        direction = (direction + (left ? 3 : 1)) % 4;
    }

    public VacuumAction getAction(VacuumPercept percept) {
        return getAction((VacuumBumpPercept) percept);
    }

    /**
     * Determine what movement action this vacuum agent will take based on its
     * reflex bump sensor.
     *
     * @param percept the reflex bump sensor percept.
     * @return the next action this vacuum agent will perform.
     */
    public VacuumAction getAction(VacuumBumpPercept percept) {

        //check current cell for dirt
        if (percept.dirtSensor() == Status.DIRTY) {
            //if the cell is dirty, clean it
            //update our map too
            map[(y + map.length) % map.length][(x + map[0].length) % map[0].length] = MapEntry.clean;
            //System.out.println("Cleaning cell.");
            unexploredCount--;
            return VacuumAction.SUCK;
        } else {
            if (map[(y + map.length) % map.length][(x + map[0].length) % map[0].length] == MapEntry.empty) {

                map[(y + map.length) % map.length][(x + map[0].length) % map[0].length] = MapEntry.clean;
                //System.out.println("This cell was already clean, but we didn't know that...");
                unexploredCount--;
            }
            //now check each adjacent cell
            checkAdjacentCells(percept);
            //check four basic directions for empty, but not necessarily clean cells
            for (int t = 0; t < 4; t++) {
                //System.out.printf("Direction:%d\tX:%d\tY:%d\tMovementChanges:(%d,%d)\n", direction, (movementChanges[direction + 1] + x + map[0].length) % map[0].length, (movementChanges[direction] + y + map.length) % map.length, movementChanges[direction + 1], movementChanges[direction]);
                //if we don't know if this tile is clean
                if (map[(movementChanges[direction] + y + map.length) % map.length][(movementChanges[direction + 1] + x + map[0].length) % map[0].length] == MapEntry.empty) {
                    //Go that way!
                    x += movementChanges[direction + 1];
                    y += movementChanges[direction];
                    //System.out.println("Cell is empty");
                    actionsTaken.addFirst(direction);
                    return movements[direction];
                } else {
                    //try turning left
                    //System.out.println("Cell is not empty, trying another cell.");
                    turn(true);
                }
            }
            if (unexploredCount > 0) {
                //System.out.println("Not sure where to go...backtracking.");
                direction = (2 + actionsTaken.poll()) % 4;
                x += movementChanges[direction + 1];
                y += movementChanges[direction];
                //System.out.println("Cell is empty");
                return movements[direction];
            }
        }
        //System.out.println("Mission Complete?");
        return VacuumAction.STOP;
    }
}
