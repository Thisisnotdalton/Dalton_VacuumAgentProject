/**
 *
 * @author Marietta E. Cameron
 * @since January 11, 2016
 */
package vacuumagentproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class VacuumFrame extends JFrame implements ActionListener{
    static final long animationDELAY = 35L;
    VacuumCanvas vFloor;
    char[][] initFloor;
    char[][] floor;
    VacuumTrace current;
    int currentTime=-1;    
    LinkedList<VacuumTrace> trace;
    ListIterator<VacuumTrace> it;
    
    Timer runTimer, stopTimer;
     
    JLabel timeContents;
    JLabel agentLocContents;
    JLabel perceptContents;
    JLabel actionContents;
    JLabel performContents;
    
    VacuumFrame(char[][] iFloor, LinkedList<VacuumTrace> iTrace, int width, int height){
         setPreferredSize(new Dimension(width, height));
         initFloor = iFloor;
         floor = new char[initFloor.length][initFloor[0].length];
         resetFloor();
         trace = iTrace;
         it = trace.listIterator();
         current = it.next();
        
         vFloor = new VacuumCanvas(floor, current, (int)(width*0.8),(int)(height*0.9));                            
         getContentPane().add(vFloor, BorderLayout.CENTER);
         
         
         
         //Layout for eastern section of content Pane
         //Eastern(Right Section) --> Time Step, Agent Location, Performance, Button Panel 
         JPanel eastPanel = new JPanel(new GridLayout(7,1));
         eastPanel.setPreferredSize(new Dimension((int)(0.2*width),(int)(height*0.9)));
         
         Color textColor = Color.BLUE;
         JLabel timeLabel = new JLabel("Time:");
         timeContents = new JLabel("");
         timeContents.setForeground(textColor);
         JLabel agentLocation = new JLabel("Agent Location: ");
         agentLocContents = new JLabel(String.format("(%d, %d)", current.agentCol(), current.agentRow()));
         agentLocContents.setForeground(textColor);
         JLabel performLabel = new JLabel("Performance: ");
         performContents = new JLabel("");
         performContents.setForeground(textColor);
         
         eastPanel.add(timeLabel); eastPanel.add(timeContents);
         eastPanel.add(agentLocation); eastPanel.add(agentLocContents);    
         eastPanel.add(performLabel); eastPanel.add(performContents);
                                    
         //Button Panel
         JPanel buttonPanel = new JPanel(new GridLayout(4,1));
         JButton resetButton   = new JButton("Reset");
         JButton runButton     = new JButton("Run");
         JButton backButton    = new JButton("Back");
         JButton forwardButton = new JButton ("Forward");
                 
         buttonPanel.add(resetButton);
         buttonPanel.add(runButton);
         buttonPanel.add(backButton);
         buttonPanel.add(forwardButton);
         eastPanel.add(buttonPanel);
         
         getContentPane().add(eastPanel, BorderLayout.EAST);
         
         
         JPanel southPanel = new JPanel(new GridBagLayout());
         southPanel.setPreferredSize(new Dimension((int)(width),(int)(height*0.1)));
         JLabel perceptLabel = new JLabel("Percept: ");
         perceptContents = new JLabel("");
         perceptContents.setForeground(textColor);
         JLabel actionLabel = new JLabel("Action: ");
         actionContents = new JLabel("");
         actionContents.setForeground(textColor);
        
         southPanel.add(perceptLabel);
         southPanel.add(perceptContents);
         perceptContents.setPreferredSize(new Dimension(400,15));
         southPanel.add(actionLabel);
         southPanel.add(actionContents);
         getContentPane().add(southPanel, BorderLayout.SOUTH);
         
         setStats(currentTime);
         
        
         resetButton.addActionListener(this);
         resetButton.setActionCommand("Reset");         
         runButton.addActionListener(this);
         runButton.setActionCommand("Run");         
         backButton.addActionListener(this);
         backButton.setActionCommand("Back");         
         forwardButton.addActionListener(this);
         forwardButton.setActionCommand("Forward");
      
    }//constructor
    
    public final void resetFloor(){      
        for (int r=0; r<initFloor.length; r++)
           System.arraycopy(initFloor[r], 0, floor[r], 0, initFloor[0].length);
    }//resetFloor 
    
    public void setTraceTimeStep(int timeStep){    
        currentTime = timeStep;
        it = trace.listIterator(timeStep+1);
        current = it.next();       
    }//setTraceTimeStep
   
    public void setNextTrace(){
        if (it.hasNext()){
          current = it.next();
          floor[current.agentRow()][current.agentCol()] = current.newStatus().getChar();
          currentTime++;
        }  
    }//setNextTrace
    
    public void setPrevTrace(){
        if (it.hasPrevious()){            
            current = it.previous();
            floor[current.agentRow()][current.agentCol()] = current.newStatus().getChar();            
            currentTime--;
        }    
    }//setPrevTrace
    
    final public void setStats(int timeStep){
       timeContents.setText(String.format("%5d/%d (%d)", timeStep, trace.size()-1,VacuumEnvironment.targetTime));
       agentLocContents.setText(String.format("(%d, %d)", 
               current.agentCol(), current.agentRow()));
       perceptContents.setText(String.format("%s", current.percept()));
       actionContents.setText(String.format("%s", current.action()));
       performContents.setText(String.format("%1.5f", current.performMeasure()));
    }//setStats
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (runTimer != null) runTimer.cancel();
        if (stopTimer != null) stopTimer.cancel();
        JButton eventObject = (JButton)event.getSource();
        String actionStr = event.getActionCommand();
        if (actionStr.equals("Run")){
           setTraceTimeStep(-1);
           currentTime = -1;
           resetFloor();                   
           runTimer = new Timer() ;
           stopTimer = new Timer();
           int timeSteps = trace.size();  //number of time steps for this trace
           stopTimer.schedule(new TimerAction(runTimer, eventObject), animationDELAY*timeSteps);
           runTimer.schedule(new TimerAction(trace.size(), eventObject), 0L, animationDELAY);
           eventObject.setText("Stop");
           eventObject.setActionCommand("Stop");                 
        }
        else if (actionStr.equals("Back")){
             setPrevTrace();
             setStats(currentTime);
             vFloor.setCurrentState(current, floor);
             vFloor.repaint();
        }
        else if (actionStr.equals("Forward")){
             setNextTrace();
             setStats(currentTime);
             vFloor.setCurrentState(current, floor);
             vFloor.repaint();
        }
        else if (actionStr.equals("Reset")){
            currentTime = -1;
            setTraceTimeStep(currentTime);         
            resetFloor();
            setStats(currentTime);
            vFloor.setCurrentState(current, floor);
            vFloor.repaint();           
        }
        else if (actionStr.equals("Stop")){
            runTimer.cancel();
            eventObject.setText("Run");
            eventObject.setActionCommand("Run"); 
        }
    }//actionPerformed
    
    
    class TimerAction extends TimerTask  {
        int stopTime;
        JButton runButton;
        Timer timerToCancel = null;
        TimerAction (int sTime, JButton rButton){
            stopTime = sTime;
            runButton = rButton;
        }
        TimerAction(Timer sTimer, JButton rButton){
            timerToCancel = sTimer;
            runButton = rButton;
        }
        @Override
        public void run() {
            if (timerToCancel == null){
                if (currentTime >= stopTime){
                    runButton.setText("Run");
                    runButton.setActionCommand("Run");
                    this.cancel();

                }    
                else {
                 setNextTrace();
                 setStats(currentTime);
                 vFloor.setCurrentState(current, floor);
                 vFloor.repaint();
                }
            }
            else {
                runButton.setText("Run");
                runButton.setActionCommand("Run");
                stopTimer.cancel();
            }
        }//run
    }//VacuumFrame
    
    
}
