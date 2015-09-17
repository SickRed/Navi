/*
 *  Menüleiste für Navi-GUI
 * 
 */
package de.sickred.uni.navi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBarr extends JMenuBar implements ActionListener {
    private final GUI gui;

    /**
     * Menüleiste für eine Pfadfindungsanwendung
     * @param gui GUI in die die Menüleiste eingepflegt wird
     */
    MenuBarr(final GUI gui){
        
        /*  Initialisieren  */
        this.gui = gui;
        JMenu menuFile = new JMenu("Datei");
        JMenuItem menuItemNew = new JMenuItem("Neu");
        JMenuItem menuItemLoad = new JMenuItem("Laden");        
        JMenuItem menuItemSave = new JMenuItem("Speichern"); 
        JMenuItem menuItemExit = new JMenuItem("Beenden");
        
        /*  Neuer Graph */
        menuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemNew.addActionListener(evt -> {
            gui.clear();
            gui.g = new Graph(gui);
            Knoten.resetIncrement();
        });
        menuFile.add(menuItemNew);        
        
        /*  Graph laden */
        menuItemLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menuItemLoad.addActionListener(evt -> new Dialog_LoadGraph(gui));
        menuFile.add(menuItemLoad);

        /*  Graph speichern  */
        menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSave.addActionListener(evt -> new Dialog_SaveGraph(gui.f, gui.g));
        menuFile.add(menuItemSave);
        
        /*  Programm beenden    */
        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        menuItemExit.addActionListener(evt -> gui.dispose());
        menuFile.add(menuItemExit);
        
        this.add(menuFile);
        
    }        
   
    @Override
    public void actionPerformed(ActionEvent e) {        
    }

}
