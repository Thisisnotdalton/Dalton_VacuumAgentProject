/*
 * @author Marietta E. Cameron
 * @version 1.0
 * @since January 11, 2016
 * 
 */

package vacuumagentproject;


public class VacuumPercept {
    Status currentStatus;
    int currentTime;
    
    VacuumPercept(){
        currentStatus = Status.OBSTACLE;
        currentTime = 0;
    }
    VacuumPercept(Status s, int r, int c){
        currentStatus = s;
    }
    Status dirtSensor(){
        return currentStatus;
    }
    int clock(){
        return currentTime;
    }
}//class VacuumPercept
