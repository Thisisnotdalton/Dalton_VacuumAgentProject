/**
 *
 * @author Marietta E. Cameron
 * @since 2011-12-20
 */
package vacuumagentproject;


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class VacuumCanvas extends JPanel {
    char [][] floor;  
    VacuumTrace current;
    int agentCol, agentRow;
    int maxFloorWidth;
    int maxFloorHeight;
    int tileWidth=3, tileHeight=3;
    Color cleanColor = new Color(255, 255, 200);
    Color obstacleColor = new Color(0, 0,0);
    Color dirtColor = new Color(250,200,100);
    Color agentColor = new Color(255,0,0);
   
     VacuumCanvas(char[][] pFloor, VacuumTrace currentTrace, int width, int height){
         floor = pFloor;         
         current = currentTrace; 
         maxFloorHeight = floor.length;
         maxFloorWidth = floor[0].length;
         tileWidth = width / maxFloorWidth;
         tileHeight =(int)( height / (double)maxFloorHeight * maxFloorWidth/(double)maxFloorHeight);
         if (tileWidth < 1)
             tileWidth = 1;
         if (tileHeight < 1)
             tileHeight = 1;
         setSize(maxFloorWidth*tileWidth, maxFloorHeight*tileHeight);
         
    }//constructor
    public void setCurrentState(VacuumTrace traceEntry, char[][] currentFloor){
        current = traceEntry;
        floor = currentFloor;
    }//setCurrentState
   
    int floor2CanvasX(int col){
         return col*tileWidth;
    }//floor2CanvasX
    
    int floor2CanvasY(int row){
         return row*tileHeight;
    }//floor2CanvasY
    
    void drawFloorTile(Graphics g, int col, int row){
       switch (floor[row][col]){ 
           case '-' : g.setColor(cleanColor);break;
           case 'D' : g.setColor(dirtColor); break;
           case 'X' : g.setColor(obstacleColor); break;
       }       
       g.fillRect(floor2CanvasX(col), floor2CanvasY(row), tileWidth, tileHeight);
    }//drawFloorTile    

    @Override
    public void paintComponent(Graphics g){
         tileWidth = this.getWidth() / maxFloorWidth;
         tileHeight = this.getHeight() / maxFloorHeight;
         if (tileWidth < 1)
             tileWidth = 1;
         if (tileHeight < 1)
             tileHeight = 1;
           //drawFloor
           for (int r=0; r<maxFloorHeight; r++)
              for (int c=0; c<maxFloorWidth; c++)
                  drawFloorTile(g, c, r);
           if (current != null){
              //drawAgent
              g.setColor(agentColor);
              g.fillOval(floor2CanvasX(current.agentCol()), floor2CanvasY(current.agentRow()), tileWidth, tileWidth);
           }          
    }//paintComponent     
}//VacuumCanvas
