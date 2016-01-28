/**
 *
 * @author Marietta E. Cameron
 * @since January 11, 2016
 */
package vacuumagentproject;


public enum VacuumAction {
       LEFT(-1,0, true), FORWARD(0,-1, true), RIGHT(1,0,true), BACK(0,1,true), SUCK(0,0,false),
       STOP(0,0,false);
       /**
        * 
        * @param dxInit  -- amount to move on x-axis
        * @param dyInit  -- amount to move on y-axis
        * @param move -- boolean to indicate where this action is a move
        */
       VacuumAction(int dxInit, int dyInit, boolean move){
           dx = dxInit; dy = dyInit;
           movement = move;
           pos = 0;
       }//constructor
       
       private static final int MOVE_COUNT = 4;
       private final int dx,dy, pos;
       private final boolean movement;
       /**
        * 
        * @return amount to move vertically 
        */
       int getRowMove(){
           return dy;
       }//getRowMove
       /**
        * 
        * @return amount to move horizontally 
        */
       int getColMove(){
           return dx;
       }//
       /**
        * 
        * @return  true if action is a move false otherwise  
        */
       boolean isAMove(){
           return movement;
       }//isAMove
       /**
        * 
        * @return  the number of moves in this enumeration 
        */
       static public int numOfMoves(){
           return MOVE_COUNT;
       }//numOfMoves
}//VacuumAction
