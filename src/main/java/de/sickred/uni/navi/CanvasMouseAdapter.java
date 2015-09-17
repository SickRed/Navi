/*
 * Listener für Mousevents auf dem Canvas auf dem Knoten gezeichnet werden
 * Behandelt: DragAndDrop via Linksklick (Knoten verschieben) DragAndDrop via Rechtsklick (Kante erstellen)
 */
package de.sickred.uni.navi;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.SwingUtilities;

public class CanvasMouseAdapter extends MouseMotionAdapter {

    private Knoten dragKnot;    /*  Knoten der gredraggt wird   */
    private Knoten connectKnotStart;    /*  Knoten von dem eine Verbindung gezogen wird */
    private Knoten connectKnotEnd;  /*  Knoten mit dem verbunden wird   */
    private Integer lineEndX, lineEndY; /*  Letztes Ende der Zeichnung der Verbindungslinie  */
    private final GUI gui;  /*  GUI des Adapters    */
    
    public CanvasMouseAdapter(GUI gui) {
        
        this.gui = gui;
    }
    
    /*
    * Listener für Mousevents auf dem Canvas auf dem Knoten gezeichnet werden
    * Behandelt: DragAndDrop via Linksklick (Knoten verschieben) DragAndDrop via Rechtsklick (Kante erstellen)
    */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        /*  Angeklickten Knoten initialisieren , abbruch wenn kein Knoten angeklickt */
        Knoten clickedKnot;
        Graphics2D g2d = (Graphics2D) gui.c.getGraphics();
        
        /*  Linksklick = Drag Knoten    */
        if(SwingUtilities.isLeftMouseButton(e) && (!gui.g.getKnot(e.getX(), e.getY()).isEmpty() || dragKnot != null)){
            if(dragKnot == null)
                clickedKnot = gui.g.getKnot(e.getX(), e.getY()).iterator().next();
            else
                clickedKnot = dragKnot;
            if(clickedKnot != null){

                this.dragKnot = clickedKnot;
                clickedKnot.clear(g2d);
                clickedKnot.setPostionCenter(e.getX(), e.getY()); 
                clickedKnot.setColor(Knoten.DRAGCOLOR);
                gui.g.paintKnots();
            }
            
            /*  Knoteninfo aktualisieren falls geöffnet */
            if(GUI.knotsettings != null)
                GUI.knotsettings.refresh();
        }
        
        /*  Rechtsklick = Verbinde Knoten   */
        if(SwingUtilities.isRightMouseButton(e)){
            
             /*  Neue Verbindung */
            if(connectKnotStart == null && !gui.g.getKnot(e.getX(), e.getY()).isEmpty()){
                
                connectKnotStart = gui.g.getKnot(e.getX(), e.getY()).iterator().next();
            }            
            
            /*  Verbindung ist über Knoten */
            if(connectKnotStart != null && !gui.g.getKnot(e.getX(), e.getY()).isEmpty()){
                
                for(Knoten k : gui.g.getKnot(e.getX(), e.getY())){
                    if(!k.equals(connectKnotStart)){
                        
                        connectKnotEnd = k;
                        connectKnotEnd.setColor(Knoten.CONNECTCOLOR);
                        connectKnotEnd.paint(g2d);                        
                        break;
                    }
                }
            }
            
            /*  Endknoten resetten falls Maus nicht mehr darüber    */
            if(connectKnotEnd != null && (gui.g.getKnot(e.getX(), e.getY()).isEmpty() || !gui.g.getKnot(e.getX(), e.getY()).contains(connectKnotEnd))){
            
                connectKnotEnd.setColor(Knoten.DEFAULTCIRCLECOLOR);
                connectKnotEnd.paint(g2d);
                connectKnotEnd = null;                
            }
            
            /*  Zeichnen der Linie  */
            if(connectKnotStart != null){
                
                if(lineEndX != null && lineEndY != null){
                    
                    g2d.setColor(g2d.getBackground());
                    g2d.drawLine(connectKnotStart.getX(), connectKnotStart.getY(), lineEndX, lineEndY);
                    lineEndX = null;
                    lineEndY = null;
                    gui.g.paintKnots();
                }
                g2d.setColor(Knoten.CONNECTIONLINECOLOR);
                if(connectKnotEnd != null){
                    g2d.drawLine(connectKnotStart.getX(), connectKnotStart.getY(), connectKnotEnd.getX(), connectKnotEnd.getY());
                    lineEndX = connectKnotEnd.getX();
                    lineEndY = connectKnotEnd.getY();
                }
                else{
                    g2d.drawLine(connectKnotStart.getX(), connectKnotStart.getY(), e.getX(), e.getY());
                    lineEndX = e.getX();
                    lineEndY = e.getY();
                }
            }
        }        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        Graphics2D g2d = (Graphics2D) gui.c.getGraphics();
        /*  Abbruch des Drags, falls Knoten ausgewählt ist und Maus nicht mehr über Knoten hovered  */
        if( dragKnot != null && (gui.g.getKnot(e.getX(), e.getY()).isEmpty() || !gui.g.getKnot(e.getX(), e.getY()).contains(dragKnot))){
                        
            dragKnot.setColor(Knoten.DEFAULTCIRCLECOLOR);
            dragKnot.paint(g2d);
            dragKnot = null;
        }
        
        /*  Linie entfernen falls angefangen    */
        if(lineEndX != null && lineEndY != null){
                    
            g2d.setColor(g2d.getBackground());
            g2d.drawLine(connectKnotStart.getX(), connectKnotStart.getY(), lineEndX, lineEndY);
            lineEndX = null;
            lineEndY = null;
        }
        
        /*  Knoten verbinden oder resetten falls via rechtsklick verbunden    */
        if(connectKnotEnd != null){
            
            if(gui.g.getKnot(e.getX(), e.getY()).contains(connectKnotEnd)){
                Dialog_Distance dialog_Distance = new Dialog_Distance(gui.f, gui.g, connectKnotStart, connectKnotEnd);
                connectKnotEnd.setColor(Knoten.DEFAULTCIRCLECOLOR);
                connectKnotEnd.paint(g2d);
            }
            else{
                
                connectKnotEnd.setColor(Knoten.DEFAULTCIRCLECOLOR);
                               
            }
            gui.g.paintKnots(); 
            connectKnotEnd = null;
            
        }
        
        /*  Startpunk für Verbindung resetten   */
        connectKnotStart = null;
    }
}
