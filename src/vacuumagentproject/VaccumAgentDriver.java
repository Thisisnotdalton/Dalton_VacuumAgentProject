package vacuumagentproject;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author Marietta E. Cameron
 */
public class VaccumAgentDriver {

    static int maxWidth = 3;  //maxWidth of floor
    static int maxHeight = 3; //maxHeight of floor
    static int agentX = 0;    //initial position of agent
    static int agentY = 0;
    static double dirtProb = 1.0;
    static int maxTimeSteps = -1;
    static double targetPerformance = 1.5;

    static boolean useVisualizer = false;
    static boolean setAgentLocRandomly = true;
    static String[] geometry = null;
    static char[][] initialFloor;
    static VacuumAgent agent = new VacuumAgent();

    static VacuumEnvironment.PerceptType usePercept = VacuumEnvironment.PerceptType.BUMP;
    static LinkedList<VacuumTrace> trace;

    /**
     * an enum that packages all command line options together. User can use -h
     * option to print all available options.
     */
    static enum OPTIONS {
        h("          : display help info. All other options ignored."),
        d("w h       : Set environment's maxWidth to integer w and maxHeight to integer h."),
        a("x y       : Set agent's initial floor position to first location starting at x,y in row-major order. "),
        p("prob      : Set probability of a clean square becoming dirty to double prob."),
        P("m         : Set target performance to double m."),
        g("filename  : Create environment's floor using the pattern given in FILE filename. Has higher precedence than option -d."),
        t("n         : Set simulation's maxTime steps to n."),
        B("          : Use BUMP percept for agent simulation"),
        L("          : Use LOCATION percept for agent simulation"),
        v("          : Use visualizer"),
        A("className : Implement agent using className.class");
        String helpString;

        OPTIONS(String hStr) {
            helpString = hStr;
        }

        String getHelpString() {
            return helpString;
        }
    }

    /**
     * Prints the command line options.
     */
    static void usage() {
        System.out.println("Command line options for VacuumAgentDriver:");
        for (OPTIONS opt : OPTIONS.values()) {
            System.out.printf("\t -%s %s\n", opt, opt.getHelpString());
        }
        System.exit(0);
    }//usage

    /**
     *
     * @param args command line arguments
     * @param pos position of current option requiring integer parameters
     * @param intCount the number of integer parameters to parse
     * @return the intCount integer parameters for the command line option in
     * position pos in args.
     */
    static int[] checkInts(String[] args, int pos, int intCount) {
        int[] result = new int[intCount];
        if ((pos + intCount) < args.length) {
            try {
                for (int i = 1; i <= intCount; i++) {
                    result[i - 1] = Integer.parseInt(args[pos + i]);
                }

            } catch (Exception e) {
                result = null;
                System.out.printf("Error: argument(s) should be integer(s) for option %s\n",
                        args[pos]);
            }
        } else {
            System.err.printf("Error:  Missing arguments for option %s\n", args[pos]);
        }
        return result;
    }//checkInts

    /**
     *
     * @param args command line arguments
     * @param pos position of current option requiring double parameters
     * @param doubleCount the number of double parameters to parse
     * @return the doubleCount double parameters for the command line option in
     * position pos in args.
     */
    static double[] checkDoubles(String[] args, int pos, int doubleCount) {
        double[] result = new double[doubleCount];
        if ((pos + doubleCount) < args.length) {
            try {
                for (int i = 1; i <= doubleCount; i++) {
                    result[i - 1] = Double.parseDouble(args[pos + i]);
                }

            } catch (Exception e) {
                result = null;
                System.out.printf("Error: argument(s) should be double(s) for option %s\n",
                        args[pos]);
            }
        } else {
            System.err.printf("Error:  Missing arguments for option %s\n", args[pos]);
        }
        return result;
    }

    /**
     *
     * @param geometryFile a file that contains the floor geometry to be used in
     * simulation
     * @throws IOException
     */
    static void readGeometryFile(BufferedReader geometryFile) throws IOException {
        LinkedList<String> stringList = new LinkedList<String>();
        while (geometryFile.ready()) {
            stringList.add(geometryFile.readLine());
        }
        geometry = new String[stringList.size()];
        int i = 0;
        for (String s : stringList) {
            geometry[i++] = s;
        }

    }

    /**
     * Parses and set parameters according to the command line options in args.
     *
     * @see Options
     * @param args commandLine arguments
     */
    static void parseCommandLine(String[] args) {
        int argPos = 0;
        boolean parseError = false;
        int[] intResult;
        double[] doubleResult;

        while ((argPos < args.length) && !parseError) {
            if (args[argPos].charAt(0) == '-') {
                switch (OPTIONS.valueOf(args[argPos].substring(1))) {
                    case h:
                        usage();
                        break;
                    case d:
                        intResult = checkInts(args, argPos, 2);
                        if (intResult == null) {
                            parseError = true;
                        } else {
                            maxWidth = intResult[0];
                            maxHeight = intResult[1];
                        }
                        argPos += 2;
                        break;
                    case a:
                        intResult = checkInts(args, argPos, 2);
                        if (intResult == null) {
                            parseError = true;
                        } else {
                            agentX = intResult[0];
                            agentY = intResult[1];
                        }
                        argPos += 2;
                        setAgentLocRandomly = false;
                        break;
                    case A:
                        try {

                            Class genericClass = Class.forName("vacuumagentproject." + args[argPos + 1]);
                            agent = (VacuumAgent) genericClass.newInstance();
                        } catch (ClassNotFoundException e) {
                            System.out.printf("Error: Class %s not found.  %s\n", args[argPos + 1], e);
                            parseError = true;
                        } catch (InstantiationException e) {
                            System.out.printf("Error: Unable to instantiate class %s.  %s\n", args[argPos + 1], e);
                            parseError = true;
                        } catch (IllegalAccessException e) {
                            System.out.printf("Error: Unable to access class %s. %s\n", args[argPos + 1], e);
                            parseError = true;
                        }
                        argPos++;
                        break;
                    case t:
                        intResult = checkInts(args, argPos, 1);
                        if (intResult == null) {
                            parseError = true;
                        } else {
                            maxTimeSteps = intResult[0];
                        }
                        argPos++;
                        break;
                    case p:
                        doubleResult = checkDoubles(args, argPos, 1);
                        if (doubleResult == null) {
                            parseError = true;
                        } else {
                            dirtProb = doubleResult[0];
                        }
                        argPos++;
                        break;
                    case P:
                        doubleResult = checkDoubles(args, argPos, 1);
                        if (doubleResult == null) {
                            parseError = true;
                        } else {
                            targetPerformance = doubleResult[0];
                        }
                        argPos++;
                        break;
                    case v:
                        useVisualizer = true;
                        break;
                    case B:
                        usePercept = VacuumEnvironment.PerceptType.BUMP;
                        break;
                    case L:
                        usePercept = VacuumEnvironment.PerceptType.LOCATION;
                        break;
                    case g:
                        if ((argPos + 1) < args.length) {
                            try {
                                BufferedReader geometryFile = new BufferedReader(new FileReader(args[argPos + 1]));
                                readGeometryFile(geometryFile);
                            } catch (Exception e) {
                                System.out.printf("Error:  File open error for %s\n", args[argPos + 1]);
                                parseError = true;
                            }
                        } else {
                            System.out.printf("Error:  Missing arguments for option %s\n", args[argPos]);
                            parseError = true;
                        }
                        argPos++;
                        break;

                }

            } else {
                System.out.printf("Error:  Invalid argument '%s'. \n",
                        args[argPos]);
                parseError = true;
            }
            argPos++;
        }
        if (parseError) {
            System.out.println("Error:  Invalid commandline options");
            usage();
            System.exit(-1);
        }

    }

    /**
     * Schedule a job for the event-dispatching thread. Creating and showing
     * this application's GUI.
     */
    public static void visualize() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VacuumFrame visFrame = new VacuumFrame(initialFloor, trace, 600, 600);
                visFrame.setResizable(true);

                visFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Display Window
                visFrame.pack();
                visFrame.setVisible(true);
            }
        });
    }//visualize

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //parse and set parameters using command line args
        parseCommandLine(args);
        if (setAgentLocRandomly) {

            Random gen = new Random();
            if (geometry == null) {
                agentX = gen.nextInt(maxWidth);
                agentY = gen.nextInt(maxHeight);
            } else {
                agentX = gen.nextInt(geometry[0].length());
                agentY = gen.nextInt(geometry.length);
            }
        }//if seting agent's location randomly

        //initialize environment
        VacuumEnvironment vEnviron;
        if (geometry == null) {
            vEnviron = new VacuumEnvironment(maxWidth, maxHeight, dirtProb, agent, agentX, agentY);
        } else {
            vEnviron = new VacuumEnvironment(geometry, agent, agentX, agentY);
        }

        if (useVisualizer) {
            initialFloor = vEnviron.getFloor(false);
        } else {
            System.out.println("Initial Environment:");
            System.out.println(vEnviron);
        }
        int timeSteps = vEnviron.simulate(maxTimeSteps, targetPerformance, usePercept);
        trace = vEnviron.getTrace();
        if (useVisualizer) {
            System.out.println("Visualizer on");
            visualize();
        } else {
            System.out.println("Final Environment:");
            System.out.println(vEnviron);

            int count = 1;
            for (VacuumTrace entry : trace) {
                System.out.printf("%d: %s", count++, entry);
            }
        }//visualizer off
    }//main    
}//VacuumAgentDriver 
