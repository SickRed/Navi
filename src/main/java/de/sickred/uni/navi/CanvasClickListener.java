/*
 * Listener für Klicks auf den Canvas auf dem die Knoten gezeichnet werden.
 * Behandelt: Linksklick (Knoten Erstellen) und Rechtsklick auf Knoten (Knoteneigenschaften aufrufen)
 */

package de.sickred.uni.navi;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class CanvasClickListener implements MouseListener{

    private final GUI gui;

    public CanvasClickListener(GUI gui) {
        
        this.gui = gui;
    }

    /*
     * Listener für Klicks auf den Canvas auf dem die Knoten gezeichnet werden.
     * Behandelt: Linksklick (Knoten Erstellen) und Rechtsklick auf Knoten (Knoteneigenschaften aufrufen)    * 
     */
    @Override
    public void mouseClicked(MouseEvent e) {        
                
        /*  Linksklick  */
        if(e.getButton() == MouseEvent.BUTTON1){
            Graphics2D g2d = (Graphics2D) gui.c.getGraphics(); 
            if(!gui.g.getKnot(e.getX(), e.getY()).isEmpty())        
                return;        
        
            Dialog_NewKnot d = new Dialog_NewKnot(e.getX(), e.getY(), gui.f, gui.g);
            gui.g.paintKnots();
        }
        /*  Rechtsklick */
        if(e.getButton() == MouseEvent.BUTTON3){
             
            if(gui.g.getKnot(e.getX(), e.getY()).isEmpty())        
                return;        
            if(GUI.knotsettings != null)
                GUI.knotsettings.dispose();
            GUI.knotsettings = new Dialog_KnotSettings(gui.c, gui.g, gui.g.getKnot(e.getX(), e.getY()).iterator().next());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
       
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}
