/*
 * GUI der Pfadfindunggsanwendung (Beleg 3)
 */
package de.sickred.uni.navi;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.*;

public class GUI extends JFrame{
    final Canvas c;
    Graph g;
    final GUI f;
    static Dialog_KnotSettings knotsettings;
    
    /**
     * GUI der Pfadfindunggsanwendung (Beleg 3)
     */   
    public GUI() {
         
        /*  Initialisierung der GUI */
        super("Pfadfindungs- bzw. Navigationssystem");
        this.setVisible(true);
        this.setSize(800, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));    
        this.setJMenuBar(new MenuBarr(this));
        this.f = this;
        
        /* Erzeugen des Canvas für den Graphen  */
        c = new Canvas();
        c.setSize(this.getWidth(), this.getHeight() - 150);
        
        /* Erzeugen des Graphen */
        g = new Graph(this);
        
        /*  Listener an Canvas binden   */
        c.addMouseListener(new CanvasClickListener(this));
        c.addMouseMotionListener(new CanvasMouseAdapter(this));         
        
        /*  Canvas, Pfadfindungsmenü und Grapherzeugungsmenü einfügen   */
        this.add(c);        
        this.add(getPathFindingOptions());
        this.add(getRandomFieldOptions());
        this.validate();
        
        /*  ComponentListener an JFrame zur Behandlung der Fenstergrössenveränderung binden */
        this.getRootPane().addComponentListener(new ComponentListener(){            
           
            public void componentResized(ComponentEvent e) {    
                
                c.setSize(f.getWidth(), f.getHeight() - 150);                
                f.validate();
                
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    g.paintKnots();
                }).start();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                
            }

            @Override
            public void componentShown(ComponentEvent e) {
               
            }

            @Override
            public void componentHidden(ComponentEvent e) {
               
            }
        });             
    }   

    /**
     * Erzeugung des Pfandfindungsmenüs
     * @return Pfandfindungsmenü
     */
    private JPanel getPathFindingOptions() {
        
        /*  Initialisieren  */
        JPanel panel = new JPanel(new FlowLayout());
        JLabel startLabel = new JLabel("Startknoten:");
        JLabel targetLabel = new JLabel("Zielknoten:");
        final JTextField startText = new JTextField("");
        final JTextField targetText = new JTextField("");
        JButton path1Butt = new JButton("Breitensuche");
        JButton path2Butt = new JButton("Tiefensuche");
        JButton path3Butt = new JButton("Kürzester Weg");
        
        startText.setPreferredSize(new Dimension(100, 20));
        targetText.setPreferredSize(new Dimension(100, 20));
        
        /*  Breitensuche- Button    */
        path1Butt.addActionListener(e -> {

            int distance = 0;
            /* Knoten normalisieren    */
            Graphics2D g2d = (Graphics2D) c.getGraphics();
            for(Knoten k : g.getKnots()){
                k.setColor(Knoten.DEFAULTCIRCLECOLOR);
                k.paintConnections(g2d);
                k.paint(g2d);
            }
            /*  Falsche Usereingaben    */
            Knoten start = g.getKnotByName(startText.getText());
            Knoten target = g.getKnotByName(targetText.getText());
            if(start == null){
                JOptionPane.showMessageDialog(f, "'" + startText.getText() + "' ist kein registrierter Knoten");
                return;
            }
            if(target == null){
                JOptionPane.showMessageDialog(f, "'" + targetText.getText() + "' ist kein registrierter Knoten");
                return;
            }

            /*  Strecke suchen  */
            ArrayList<Knoten> l = g.getRouteBreadthFirst(start , target);
            if(l == null){
                JOptionPane.showMessageDialog(f, "Konnte keine Strecke finden");
                return;
            }

            /*Strecke darstellen    */
            for(int i = l.size() - 1; i >= 0; i--){

                if(i -1 >= 0){
                    distance += l.get(i).getDistance(l.get(i-1));
                    g2d.setColor(Color.ORANGE);
                    g2d.drawLine(l.get(i).getX(), l.get(i).getY(), l.get(i-1).getX(), l.get(i-1).getY());
                }

                l.get(i).setColor(Color.ORANGE);
                l.get(i).paint(g2d);
            }
            JOptionPane.showMessageDialog(f, "Länge der Strecke: " + distance);
        });
        /*  Tiefensuche- Button    */
        path2Butt.addActionListener(e -> {

            int distance = 0;
            /* Knoten normalisieren    */
            Graphics2D g2d = (Graphics2D) c.getGraphics();
            for(Knoten k : g.getKnots()){
                k.setColor(Knoten.DEFAULTCIRCLECOLOR);
                k.paintConnections(g2d);
                k.paint(g2d);
            }
            /*  Falsche Usereingaben    */
            Knoten start = g.getKnotByName(startText.getText());
            Knoten target = g.getKnotByName(targetText.getText());
            if(start == null){
                JOptionPane.showMessageDialog(f, "'" + startText.getText() + "' ist kein registrierter Knoten");
                return;
            }
            if(target == null){
                JOptionPane.showMessageDialog(f, "'" + targetText.getText() + "' ist kein registrierter Knoten");
                return;
            }
            /*  Strecke suchen  */
            ArrayList<Knoten> l = g.getRouteDepthFirst(start , target);
            if(l == null){
                JOptionPane.showMessageDialog(f, "Konnte keine Strecke finden");
                return;
            }
            /*Strecke darstellen    */
            for(int i = l.size() - 1; i >= 0; i--){

                if(i -1 >= 0){
                    distance += l.get(i).getDistance(l.get(i-1));
                    g2d.setColor(Color.ORANGE);
                    g2d.drawLine(l.get(i).getX(), l.get(i).getY(), l.get(i-1).getX(), l.get(i-1).getY());
                }

                l.get(i).setColor(Color.ORANGE);
                l.get(i).paint(g2d);
            }
            JOptionPane.showMessageDialog(f, "Länge der Strecke: " + distance);
        });
        path3Butt.addActionListener(e -> {

            int distance = 0;
         /* Knoten normalisieren    */
            Graphics2D g2d = (Graphics2D) c.getGraphics();
            for(Knoten k : g.getKnots()){
                k.setColor(Knoten.DEFAULTCIRCLECOLOR);
                k.paintConnections(g2d);
                k.paint(g2d);
            }

            /*  Falsche Usereingaben    */
            Knoten start = g.getKnotByName(startText.getText());
            Knoten target = g.getKnotByName(targetText.getText());
            if(start == null){
                JOptionPane.showMessageDialog(f, "'" + startText.getText() + "' ist kein registrierter Knoten");
                return;
            }
            if(target == null){
                JOptionPane.showMessageDialog(f, "'" + targetText.getText() + "' ist kein registrierter Knoten");
                return;
            }
            /*  Strecke suchen  */
            ArrayList<Knoten> l = g.getRouteShortest(start , target);
            if(l == null){
                JOptionPane.showMessageDialog(f, "Konnte keine Strecke finden");
                return;
            }
            /*Strecke darstellen    */
            for(int i = l.size() - 1; i >= 0; i--){

                if(i -1 >= 0){
                    distance += l.get(i).getDistance(l.get(i-1));
                    g2d.setColor(Color.ORANGE);
                    g2d.drawLine(l.get(i).getX(), l.get(i).getY(), l.get(i-1).getX(), l.get(i-1).getY());
                }

                l.get(i).setColor(Color.ORANGE);
                l.get(i).paint(g2d);
            }
            JOptionPane.showMessageDialog(f, "Länge der Strecke: " + distance);

        });
        
        /*  Komponenten hinzufügen  */
        panel.add(startLabel);
        panel.add(startText);
        panel.add(targetLabel);
        panel.add(targetText);
        panel.add(path1Butt);
        panel.add(path2Butt);
        panel.add(path3Butt);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        
        return panel;
    }

    /**
     * Zufälligen Graphen erzeugen - Menü
     * @return Graphenmenü als JPanel
     */
    private JPanel getRandomFieldOptions(){
        
        /*  Inititalisierung    */
        JPanel panel = new JPanel(new FlowLayout());
        JLabel knotsLabel = new JLabel("Knotenzahl:");
        JLabel edgesLabel = new JLabel("Kantenzahl:");
        final JTextField knotsText = new JTextField("");
        final JTextField edgesText = new JTextField("");
        JButton generateButt = new JButton("Zufälligen Graphen erzeugen");
        
        knotsText.setPreferredSize(new Dimension(100, 20));
        edgesText.setPreferredSize(new Dimension(100, 20));
        
        /*  Button zum Generieren eines Graphen */
        generateButt.addActionListener(e -> {

            /*  Falsche Usereigaben */
            if(!(Pattern.compile("^[0-9]+$")).matcher(knotsText.getText()).find()){
                JOptionPane.showMessageDialog(f, "Anzahl der Knoten muss eine Zahl sein");
                return;
            }
            if(!(Pattern.compile("^[0-9]+$")).matcher(edgesText.getText()).find()){
                JOptionPane.showMessageDialog(f, "Anzahl der Kanten muss eine Zahl sein");
                return;
            }

            /*  Initialisierung */
            Random r = new Random();
            ArrayList <Knoten> knots = new ArrayList<>();
            int knotsToGenerate = Integer.parseInt(knotsText.getText());
            int edgesToGenerate = Integer.parseInt(edgesText.getText());
            int maxEdges = Graph.getMaxAmountOfEdges(knotsToGenerate);
            int generatedEdges = 0;

            /*  Zu viele Kanten */
            if(edgesToGenerate > maxEdges){
                JOptionPane.showMessageDialog(f, "Ein Graph mit " + knotsToGenerate + " Knoten kann nur " + maxEdges + " Kanten haben");
                return;
            }

            /*  Knoten resetten und zufällig neue erzeugen */
            Knoten.resetIncrement();
            for(int i = 0; i < knotsToGenerate; i++)
                knots.add(new Knoten(null, r.nextInt(c.getWidth() - Knoten.DEFAULTSIZE) + Knoten.DEFAULTSIZE/2, r.nextInt(c.getHeight() - Knoten.DEFAULTSIZE) + Knoten.DEFAULTSIZE/2));

            /*  Kanten erzeugen */
            while(generatedEdges < edgesToGenerate){

                Knoten start = knots.get(r.nextInt(knots.size()));
                Knoten target = knots.get(r.nextInt(knots.size()));
                if(start.equals(target) || start.isLinked(target))
                    continue;

                start.addLink(target, null);
                target.addLink(start,null);
                generatedEdges++;
            }
            /*  Knoten neu zeichnen */
            g.clear();
            g = new Graph(knots, f);
            g.paintKnots();
        });
        
        /*  Komponenten hinzufügen  */
        panel.add(knotsLabel);
        panel.add(knotsText);
        panel.add(edgesLabel);
        panel.add(edgesText);
        panel.add(generateButt);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        return panel;
    }
    
    /*  Zeichenbereich reinigen */
    void clear() {
        g.clear();
        g.setKnots(new ArrayList<>());
    }
    
    
}
