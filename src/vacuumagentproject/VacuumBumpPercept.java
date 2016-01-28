/**
 *
 * @author Marietta E. Cameron
 * @since January 11, 2016
 */
package vacuumagentproject;

public class VacuumBumpPercept extends VacuumPercept{
    boolean[] willBumpStorage; 
    VacuumBumpPercept(Status s, int t, boolean[] bStorage){
        currentStatus = s;
        currentTime = t;
        willBumpStorage = new boolean[bStorage.length];
        System.arraycopy(bStorage, 0, willBumpStorage, 0, willBumpStorage.length);
    }
    boolean willBump(VacuumAction action){
        
        return willBumpStorage[action.ordinal()];
    }
    
    @Override
    public String toString(){
        String result;
        boolean firstTime = true;
        result = currentTime + ", "  +currentStatus.toString() + ", obstaclesAt(";
        for (VacuumAction a : VacuumAction.values())
            if (willBump(a)){
                if (firstTime)
                    firstTime=false;
                else 
                    result +=" ";
                result += a.toString();
            }
         if (firstTime)
             result += "None";
         result +=")";
        return String.format("[%s]", result);
    }
}
