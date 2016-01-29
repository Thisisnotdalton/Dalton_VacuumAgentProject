/*
 * @author Dalton Nickerson
 * 
 */
package vacuumagentproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DmnRandomAgent extends VacuumAgent {

    public static int n = 2;
    public static String outString = "";
    private static PrintWriter pw;
    private static String outFileName="DmnReflexOutputFile";
    public static int gridSize;
    public static boolean isMain = false;
    private static int minSize = 3, maxSize = 25;
    private static int simulations = 1000;
    private static double[] results;

    public static void main(String[] args) {
        isMain = true;
        results = new double[(maxSize - minSize)+1];
        for (int i = 0; i < results.length; i++) {
            results[i] = 0;
        }
        String paramString = "-t 7000000 -A DmnReflexModelAgent -d ";
        for (gridSize = minSize; gridSize <= maxSize; gridSize++) {
            for (int simNum = 0; simNum < simulations; simNum++) {
                VaccumAgentDriver.main((paramString + gridSize + " " + gridSize).toString().split(" "));
            }
        }
        try {
            pw = new PrintWriter(outFileName+"X.txt");
            for (int i = 0; i < results.length; i++) {
                pw.printf("%d\n",i+minSize);
            }
            pw.close();
            pw = new PrintWriter(outFileName+"Y.txt");
            for (int i = 0; i < results.length; i++) {
                pw.printf("%.5f\n",results[i]);
            }
            pw.close();
        } catch (IOException e) {
            //System.out.println("Error initializing PrintWriter for output file " + outFileName);
        }
    }

    public DmnRandomAgent() {
        this("DmnReflexOutputFile.txt");
    }

    public DmnRandomAgent(String outFileName) {
        this.outFileName = outFileName;
        actionList = VacuumAction.values();
    }

    public static void outputPerformance(int testSize, double rating) {
        ////System.out.print(perf);
        //outLines.add(perf);
        results[(testSize-minSize)]+=(100*rating)/simulations;
        
        
    }

    public VacuumAction getAction(VacuumPercept percept) {

        if (percept.currentStatus == Status.DIRTY) {
            return VacuumAction.SUCK;
        } else {//choose a move randomly
            Random gen = new Random();

            int index = gen.nextInt(actionList.length); //randomly select an action

            if (percept.getClass().getName().equals("VacuumBumpPercept")) {
                VacuumBumpPercept bumpPercept = (VacuumBumpPercept) percept;
                while (!actionList[index].isAMove() && !bumpPercept.willBump(actionList[index])) {
                    index = (index + 1) % actionList.length;
                }
            } else {
                while (!actionList[index].isAMove()) {
                    index = (index + 1) % actionList.length;
                }
            }
            return actionList[index];
        }
    }

}
