/*
 * @author Marietta E. Cameron
 * @version 2.0
 * @since January 11, 2016
 * 
 */
package vacuumagentproject;

public class VacuumTrace {

    private final VacuumPercept percept;
    private final VacuumAction action;
    private final int agentRow;
    private final int agentCol;
    private final Status newStatus;
    private final double performMeasure;

    VacuumTrace(VacuumPercept p, VacuumAction a, int r, int c, Status s, double m) {
        percept = p;
        action = a;
        agentRow = r;
        agentCol = c;
        newStatus = s;
        performMeasure = m;
    }//constructor

    @Override
    public String toString() {
        return String.format("%s %s (%d,%d), %1.2f\n", percept, action, agentCol, agentRow,
                performMeasure);
    }//toString

    //accessor methods
    public int agentRow() {
        return agentRow;
    }

    public int agentCol() {
        return agentCol;
    }

    public double performMeasure() {
        return performMeasure;
    }

    public Status newStatus() {
        return newStatus;
    }

    public VacuumAction action() {
        return action;
    }

    public VacuumPercept percept() {
        return percept;
    }
}//VacuumTrace
