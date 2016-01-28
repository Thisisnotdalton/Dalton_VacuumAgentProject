/*
 * @author Dalton Nickerson
 * 
 */
package vacuumagentproject;

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

    private int x, y;

    /**
     * A private Vertex class for path finding and representing the graph.
     */
    private class Vertex {

        //a label to differentiate between vertices
        private int label;
        //if we have seen this vertex before
        private boolean visited;
        //adjacent vertices
        private Edge[] adjacent;
        //the minimum distance to this Vertex
        private double distance; //we use a double here for its infinity value

        public Vertex(int label) {
            this.label = label;
            reset();
        }

        /**
         * Reset the values used for Dijkstra's algorithm.
         */
        public void reset() {
            visited = false;
            distance = Double.POSITIVE_INFINITY;
        }

        public int compareTo(Vertex other) {
            return Double.compare(distance, other.distance);
        }
    }

    private class Edge {

        public final Vertex target;

        public Edge(Vertex target) {
            this.target = target;
        }
    }

    
    
    
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

        unknown(false), obstacle(true, false), empty(false, false), clean(false, true);

        /**
         * Create a new MapEntry based on the current state of another MapEntry.
         *
         * @param other the MapEntry to copy.
         */
        MapEntry(MapEntry other) {
            state = other.getState();
        }

        /**
         * Create a new MapEntry which is checked if checked is true, otherwise
         * everything about the tile is unknown.
         *
         * @param checked
         */
        MapEntry(boolean checked) {
            state = (byte) (checked ? 1 : 0);
        }

        /**
         * Create a tile in which we have checked the tile and at least know if
         * it is an obstacle and potentially if it's clean.
         *
         * @param obstacle whether the tile is obstructed.
         * @param clean whether we know the tile is clean.
         */
        MapEntry(boolean obstacle, boolean clean) {
            state = (byte) (1 + (obstacle ? 2 : 0) + (clean ? 4 : 0));
        }

        //this state of all properties of the tile, i.e. clena, obstacle, etc.
        private byte state;

        /**
         * Return this MapEntry's state.
         *
         * @return this MapEntry's state.
         */
        private byte getState() {
            return state;
        }

        /**
         * Compare equivalence of two MapEntry's by their state.
         *
         * @param other the other MapEntry to check for equivalence.
         * @return true if the MapEntry's have the same state.
         */
        public boolean equals(MapEntry other) {
            return (other.getState() == state);
        }

    }

    public DmnReflexModelAgent() {
        map = new MapEntry[1000][1000];
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
            System.out.println("Cleaning cell.");
            return VacuumAction.SUCK;
        } else {
            //now check each adjacent cell
            checkAdjacentCells(percept);
            //check four basic directions for empty, but not necessarily clean cells
            for (int t = 0; t < 4; t++) {
                System.out.printf("Direction:%d\tX:%d\tY:%d\tMovementChanges:(%d,%d)\n", direction, (movementChanges[direction + 1] + x + map[0].length) % map[0].length, (movementChanges[direction] + y + map.length) % map.length, movementChanges[direction + 1], movementChanges[direction]);
                //if we don't know if this tile is clean
                if (map[(movementChanges[direction] + y + map.length) % map.length][(movementChanges[direction + 1] + x + map[0].length) % map[0].length] == MapEntry.empty) {
                    //Go that way!
                    x += movementChanges[direction + 1];
                    y += movementChanges[direction];
                    System.out.println("Cell is empty");
                    return movements[direction];
                } else {
                    //try turning left
                    System.out.println("Cell is not empty, trying another cell.");
                    turn(true);
                }
            }
            System.out.println("Dead end.");
        }

        return VacuumAction.STOP;
    }
}
