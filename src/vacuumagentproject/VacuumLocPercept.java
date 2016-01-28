/*
 * @author Marietta E. Cameron
 * @version 1.0
 * @since Janaury 11, 2016
 * 
 */

package vacuumagentproject;


public class VacuumLocPercept extends VacuumPercept {
   int row;
   int col; 
   VacuumLocPercept(Status s, int t, int r, int c){
        currentStatus = s;
        currentTime = t;
        row = r;
        col = c;
    }
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
    @Override
    public String toString(){        
        String result = String.format("[%d, %s, (%d,%d)]", currentTime, currentStatus, col, row);
        return result;
    }//toString
}//VacuumLocPercept
