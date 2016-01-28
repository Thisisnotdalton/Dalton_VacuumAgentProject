/**
 *
 * @author Marietta E. Cameron
 * @since January 11, 2016
 */
package vacuumagentproject;

import java.util.Random;


public class VacuumAgent {
    static final int ACTION_COUNT = VacuumAction.numOfMoves();
    int[] actionCounts = new int[VacuumAction.numOfMoves()];
    static VacuumAction[] actionList;
    
   
    /**
     * 
     */
    VacuumAgent(){
         for (int i=0; i<ACTION_COUNT; i++)
            actionCounts[i] = 0;
         actionList = VacuumAction.values();
    }//constructor

    public VacuumAction getAction(VacuumPercept percept){
        if (percept instanceof VacuumLocPercept)
            return getActionRandomReflex((VacuumLocPercept)percept);
        else           
            return getActionRandomReflex((VacuumBumpPercept) percept);           
    }
    /**
     * 
     * @param percept -- a location percept
     * @return VacuumAction.SUCK if current location is dirty else returns a random move 
     */
    private VacuumAction getActionRandomReflex(VacuumLocPercept percept){
        if (percept.currentStatus == Status.DIRTY)
            return VacuumAction.SUCK;
        else {//choose a move randomly
            Random gen = new Random();
            
            int index = gen.nextInt(actionList.length); //randomly select an action
            while (!actionList[index].isAMove())//sequentially look for a move
                index = (index+1) % actionList.length;
            return actionList[index];
        }//if current status is clean
        
    }//getActionRandomReflex
   /**
    * 
    * @param percept -- a percept that simulates Bump sensors
    * @return VacuumAction.SUCK if current location is dirty else returns a random move 
    */ 
   private VacuumAction getActionRandomReflex(VacuumBumpPercept percept){
         if (percept.currentStatus == Status.DIRTY)
            return VacuumAction.SUCK;
        else {//choose a move randomly
            Random gen = new Random();
            actionList = VacuumAction.values();
            int index = gen.nextInt(actionList.length);
            while (!actionList[index].isAMove() && !percept.willBump(actionList[index]))
                index = (index+1) % actionList.length;
            return actionList[index];
        }
    }//getActionRandomReflex   
}//VacuumAgent
