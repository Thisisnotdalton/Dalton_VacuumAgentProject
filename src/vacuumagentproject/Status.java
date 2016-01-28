/**
 *
 * @author Marietta E. Cameron
 * @since January 11, 2016
 */
package vacuumagentproject;

/**
 * Status enumeration indicates the possible status for each square unit in
 * VacuumEnvironment's floor.
 */
public enum Status {
    /**
     *  Floor cell contains an obstacle. In the ascii representation this is represented
     *  by the char 'X'
     */
    OBSTACLE('X'),
    /**
     *  Floor cell is considered dirty. In the ascii representation this is represented
     *  by the char 'D'
     */
    DIRTY('D'),
    /**
     *  Floor cell is considered clean. In the ascii representation this is represented
     *  by the char '-'
     */
    CLEAN('-');
    private final char charRep; //char that would be used in an array or string rep
 
    /**
     * 
     * @return the char representation of this Status value 
     */
    public char getChar() {
        return charRep;
    }//getChar
    
    /**
     * 
     * @param initChar -- the char value for this enum value 
     */
    Status(char initChar) {
        charRep = initChar;
    }//Status
}//enum Status

