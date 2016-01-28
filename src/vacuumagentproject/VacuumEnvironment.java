/*
 * @author Marietta E. Cameron
 * @version 2.0
 * @since January 11, 2016
 * 
 */
package vacuumagentproject;

import java.util.*;
import java.util.Random;

public class VacuumEnvironment {

    static private int maxWidth = 10; //maxWidth of floor geometry
    static private int maxHeight = 10; //maxHeight of floor geometry
    static private double cleanWeight = 0.75;
    static private double dirtyProbability = 0.1;
    static Status[][] floor;
    static private int initDirtSqCount = 0;
    static private int initSqCount = 0;
    static int targetTime;
    LinkedList<VacuumTrace> trace = new LinkedList<VacuumTrace>();
    VacuumEnvAgent agent = null;

    /**
     * Initializes a rectangular floor pattern with outer border.
     *
     *
     * @param dirtyProb
     * @param initAgent
     * @param row
     * @param col
     */
    public VacuumEnvironment(double dirtyProb, VacuumAgent initAgent, int row, int col) {
        floor = new Status[maxHeight][maxWidth];
        dirtyProbability = dirtyProb;
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                floor[r][c] = Status.CLEAN;
            }
        }//initialize each cell to clean
        for (int c = 0; c < maxWidth; c++) {
            floor[0][c] = Status.OBSTACLE;
            floor[maxHeight - 1][c] = Status.OBSTACLE;
        }//create top and bottom borders

        for (int r = 1; r < maxHeight - 1; r++) {
            floor[r][0] = Status.OBSTACLE;
            floor[r][maxWidth - 1] = Status.OBSTACLE;
        }//create left and right borders

        makeDirty(dirtyProbability);
        computeSquareCount();
        agent = new VacuumEnvAgent(initAgent, row, col);
        targetTime = 2 * initSqCount + 1;
        System.out.printf("Target Time = %d\n", targetTime);
    }//constructor

    /**
     * Initializes a rectangular floor pattern with given width and height and
     * places an agent in the environment a the given location. If the location
     * is an obstacle the agent is placed at the next open location in row-major
     * order.
     *
     * @param width maximum width of floor geometry including border
     * @param height maximum height of floor geometry including border
     * @param dirtyProb probability that a clean square becomes dirty
     * @param initAgent agent designed to clean floor
     * @param x x-coor (col) of the location in which to place agent
     * @param y y-coor (row) of the location in which to place agent
     *
     */
    public VacuumEnvironment(int width, int height, double dirtyProb, VacuumAgent initAgent, int x, int y) {
        maxHeight = height + 2;
        maxWidth = width + 2;
        dirtyProbability = dirtyProb;
        floor = new Status[maxHeight][maxWidth];
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                floor[r][c] = Status.CLEAN;
            }
        }
        for (int c = 0; c < maxWidth; c++) {
            floor[0][c] = Status.OBSTACLE;
            floor[maxHeight - 1][c] = Status.OBSTACLE;
        }
        for (int r = 1; r < maxHeight - 1; r++) {
            floor[r][0] = Status.OBSTACLE;
            floor[r][maxWidth - 1] = Status.OBSTACLE;
        }

        makeDirty(dirtyProbability);
        computeSquareCount();
        agent = new VacuumEnvAgent(initAgent, y, x);
        targetTime = 2 * initSqCount + 1;
        System.out.printf("Target Time = %d\n", targetTime);

    }

    /**
     * Creates a floor pattern using geometry and places an agent in the
     * environment at the given location. If the location is an obstacle the
     * agent is placed at the next open location in row-major order.
     *
     * @param geometry contains geometry of the floor. - a clean square, D a
     * dirty square, any other character is a border
     * @param initAgent agent designed to clean floor
     * @param x x-coor (col) of the location in which to place agent
     * @param y y-coor (row) of the location in which to place agent
     */
    VacuumEnvironment(String[] geometry, VacuumAgent initAgent, int x, int y) {
        //adding to dimensions to allow obstacle padding.
        maxHeight = geometry.length + 2;
        maxWidth = geometry[0].length();
        for (int r = 1; r < geometry.length; r++) {
            if (geometry[r].length() > maxWidth) {
                maxWidth = geometry[r].length();
            }
        }
        maxWidth += 2;
        floor = new Status[maxHeight][maxWidth];
        for (int c = 0; c < maxWidth; c++) {
            floor[0][c] = Status.OBSTACLE;
            floor[maxHeight - 1][c] = Status.OBSTACLE;
        }
        for (int r = 1; r < maxHeight - 1; r++) {
            floor[r][0] = Status.OBSTACLE;
            floor[r][maxWidth - 1] = Status.OBSTACLE;
        }
        for (int gRow = 0; gRow < geometry.length; gRow++) {
            for (int gCol = 0; gCol < geometry[gRow].length(); gCol++) {
                switch (geometry[gRow].charAt(gCol)) {
                    case 'D':
                    case 'd':
                        floor[gRow + 1][gCol + 1] = Status.DIRTY;
                        break;
                    case ' ':
                    case '-':
                        floor[gRow + 1][gCol + 1] = Status.CLEAN;
                        break;
                    default:
                        floor[gRow + 1][gCol + 1] = Status.OBSTACLE;
                        break;
                }
            }
            for (int c = geometry[gRow].length() + 1; c < maxWidth; c++) {
                floor[gRow + 1][c] = Status.OBSTACLE;
            }
        }
        computeSquareCount();
        agent = new VacuumEnvAgent(initAgent, y, x);
        targetTime = 2 * initSqCount + 1;
        System.out.printf("Target Time = %d\n", targetTime);
    }//VacuumEnvironment

    /**
     * Changes a clean square to dirty according to probability prob.
     *
     * @param prob -- probability of an CLEAN square becoming DIRTY
     */
    public final void makeDirty(double prob) {
        int threshold = (int) (prob * 100 + 0.5);
        Random gen = new Random();
        int currentInt;
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                if (floor[r][c] == Status.CLEAN) {
                    currentInt = gen.nextInt(100);
                    if (currentInt < threshold) {
                        floor[r][c] = Status.DIRTY;
                    } else {
                        floor[r][c] = Status.CLEAN;
                    }
                }
            }
        }
    }

    /**
     * sets the value of initSqCount, initDirtSqCount
     */
    private void computeSquareCount() {
        initSqCount = 0;
        initDirtSqCount = 0;
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                if (floor[r][c] != Status.OBSTACLE) {
                    initSqCount++;
                }
                if (floor[r][c] == Status.DIRTY) {
                    initDirtSqCount++;
                }
            }
        }
        try {
            if (initSqCount == 0) {
                throw new Exception("computeSquareCount: no open squares in floor geometry.");
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1000);
        }
    }

    /**
     * @param geometryOnly indicates if geometry only is to be returned.
     * @return if geometryOnly is true, the floor is return as an completely
     * clean floor. Each cell of the 2D array contains either the
     * Status.OBSTACLE character or the Status.CLEAN character Otherwise, the
     * character representation of the floor is returned.
     */
    public char[][] getFloor(boolean geometryOnly) {
        char[][] grid = new char[maxHeight][maxWidth];
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                if (floor[r][c] == Status.OBSTACLE) {
                    grid[r][c] = Status.OBSTACLE.getChar();
                } else {
                    if (geometryOnly) {
                        grid[r][c] = Status.CLEAN.getChar();
                    } else {
                        grid[r][c] = floor[r][c].getChar();
                    }
                }
            }
        }
        return grid;
    }//getGeometry

    /**
     *
     * @param col
     * @param row
     * @return the status of the floor at location col, row. If col or row is
     * outside the floor dimensions, getStatus returns Status.OBSTACLE.
     */
    public Status getStatus(int col, int row) {
        if ((row < 0) || (row > maxWidth) || (col < 0) || (col > maxWidth)) {
            return Status.OBSTACLE;
        } else {
            return floor[row][col];
        }
    }

    /**
     *
     * @param row
     * @param col
     * @return an array of boolean indicating whether or not a given action will
     * cause agent to enter a cell containing an obstacle.
     */
    private boolean[] bumpNeighbors(int row, int col) {
        boolean[] result = new boolean[VacuumAction.values().length];
        for (VacuumAction a : VacuumAction.values()) {
            result[a.ordinal()] = (floor[row + a.getRowMove()][col + a.getColMove()] == Status.OBSTACLE);
        }
        return result;
    }

    /**
     * Simulates the vacuum agent until one of the following occurs: 1.) The
     * agent returns a STOP action 2.) maxTimeSteps have completed 3.) The
     * performance measure has reached the performTarget
     *
     * @param maxTimeSteps Number of time steps to simulate vacuum agent
     * @param performTarget Target for the performance measure
     * @param ptype The type of percept agent is to receive
     * @return the number of time steps performed by the simulation
     */
    public int simulate(int maxTimeSteps, double performTarget, PerceptType ptype) {
        int currentTime = -1;
        boolean performReached = false;
        int performTargetCount = 0, maxPTargetCount = 1;
        if (maxTimeSteps <= 0) {
            maxTimeSteps = 4 * targetTime;
        }
        trace.add(new VacuumTrace(null, null, agent.row, agent.col, floor[agent.row][agent.col], 0.0));
        while ((currentTime < maxTimeSteps) && !performReached) {
            int r = agent.row, c = agent.col;
            if (ptype == PerceptType.LOCATION) {
                agent.performAction(new VacuumLocPercept(floor[r][c], currentTime, r, c), maxTimeSteps);
            } else {//ptype == PerceptType.BUMP
                agent.performAction(new VacuumBumpPercept(floor[r][c], currentTime, bumpNeighbors(r, c)), maxTimeSteps);
            }
            if (trace.getLast().action() == VacuumAction.STOP) {
                performReached = true;
            }
            if (trace.getLast().performMeasure() > performTarget) {
                performTargetCount++;
                if (performTargetCount > maxPTargetCount) {
                    performReached = true;
                }
            }
            if (currentTime >= maxTimeSteps) {

                if (DmnRandomAgent.isMain) {
                    DmnRandomAgent.outputPerformance(DmnRandomAgent.gridSize, performance(currentTime, maxTimeSteps));
                }

                return currentTime;
            }
            if ((currentTime % 1000000) == 0 && currentTime != 0) {
                System.out.println("Still working.\t" + currentTime);
            }

            currentTime++;
        }
        if (DmnRandomAgent.isMain) {
            DmnRandomAgent.outputPerformance(DmnRandomAgent.gridSize, performance(currentTime, maxTimeSteps));
        }
        return currentTime;
    }

    /**
     *
     * @return a LinkedList of trace entries of a simulation
     */
    public LinkedList<VacuumTrace> getTrace() {
        return trace;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String result = "";
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                if ((r == agent.row) && (c == agent.col)) {
                    result += "A ";
                } else {
                    result += floor[r][c].getChar() + " ";
                }
            }
            result += "\n";
        }
        return result;
    }

    public double performance(int currentTime, int maxTimeSteps) {
        long dirtSqCount = 0;
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxWidth; c++) {
                if (floor[r][c] == Status.DIRTY) {
                    dirtSqCount++;
                }
            }
        }
        double cleanMeasure;
        if (initDirtSqCount == 0) {
            cleanMeasure = 1.0;
        } else {
            cleanMeasure = 1.0 - (dirtSqCount) / (double) (initDirtSqCount);
        }

        return cleanMeasure * (cleanWeight + (1.0 - cleanWeight) * ((maxTimeSteps - (currentTime - targetTime)) / (double) maxTimeSteps));
    }

    class VacuumEnvAgent {

        VacuumAgent vAgent;
        int row, col;  //location of agent known to simulation but unknown to testAgent

        long updateCount = 0;

        VacuumEnvAgent(VacuumAgent initAgent, int r, int c) {
            vAgent = initAgent;
            if ((r < 0) || (c < 0) || (r > maxHeight) || (c > maxWidth)) {
                r = 0;
                c = 0;
            }
            while (floor[r][c] == Status.OBSTACLE) {
                c++;
                if (c >= maxWidth) {
                    c = 0;
                    r++;
                }
                if (r >= maxHeight) {
                    r = 0;
                }
            }
            row = r;
            col = c;
        }

        /**
         *
         * @param action performs the appropriate action on the environment and
         * updates the performance statistics
         */
        void performAction(VacuumPercept percept, int maxTimeSteps) {
            VacuumAction action = vAgent.getAction(percept);

            updateCount++;
            if (action != VacuumAction.STOP) {
                if (action == VacuumAction.SUCK) {
                    floor[row][col] = Status.CLEAN;
                } else {
                    int newRow = row + action.getRowMove();
                    int newCol = col + action.getColMove();
                    if (floor[newRow][newCol] != Status.OBSTACLE) { //update position only when not entering an OBSTACLE
                        row = newRow;
                        col = newCol;
                    }
                }
            }
            boolean add = trace.add(new VacuumTrace(percept, action, row, col, floor[row][col],
                    performance(percept.currentTime, maxTimeSteps)));
        }

    }//class 

    public enum PerceptType {

        LOCATION,
        BUMP
    }
}//class VacuumEnvironment
