/*
 * Knoten eines Graphen
 */
package de.sickred.uni.navi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;

public class Knoten {
    
    /*  Standart Größe des Knotens  */
    public static final int DEFAULTSIZE = 10;
    /*  Standart Farbe des Knotens  */
    public static final Color DEFAULTCIRCLECOLOR = Color.red;
    /*  Standart Größe des Knotennamens  */
    public static final Color DEFAULTTEXTCOLOR = Color.black;
    /*  Standart Farbe des Knotens während er verschoben wird  */
    public static final Color DRAGCOLOR = Color.blue;
    /*  Standart Farbe des Knotens wenn er eine Verbindung bekommt */
    public static final Color CONNECTCOLOR = Color.pink;
    /*  Standart Farbe der Verbindungslinie zwischen zwei Knoten  */
    public static final Color CONNECTIONLINECOLOR = Color.BLACK;
    /*  Prefix des Knotennamens falls kein Name gesetzt wird  */
    public static final String DEFAULTNAME = "K_";
    /*  ID-Increment    */
    private static int increment = 0;
    
    /* ID-Vergabe zurücksetzen  */
    static void resetIncrement(){
        increment = 0;
    }
    
    /**
     * Aktuelle ID-Vergabe auf Wert festsetzen 
     * @param setTo Neuer Startwert der ID-Vergabe
     */
    static void setIncrement(int setTo){
        increment = setTo;
    }
    
    
    private int id; /*  ID  */
    private String name;    /*  Name    */
    private HashMap <Knoten, Integer> links;    /* Verbundene Knoten + Distanz  */
    private int x, y;   /*  Koordinaten */
    private Color circlecolor, textcolor;   /*  Farben  */
    private int size;   /*  Grösse  */ 
    
    /**
     * Knoten erzeugen
     * @param name Name des Knoten
     */
    Knoten (String name){
        
        this.id = increment++;
        this.name = name;
        this.links = new HashMap<>();
        this.circlecolor = DEFAULTCIRCLECOLOR;
        this.textcolor = DEFAULTTEXTCOLOR;
        this.size = DEFAULTSIZE;
    }
    
    /**
     * Knoten an Koordinate erzeugen
     * @param name Name des Knoten
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    Knoten (String name, int x, int y){
        
        this.id = increment++;
        this.name = name;
        if(this.name == null)
            this.name = DEFAULTNAME + this.id;
        this.links = new HashMap<>();
        this.circlecolor = DEFAULTCIRCLECOLOR;
        this.size = DEFAULTSIZE;
        this.x = x;
        this.y = y;
    }
       
    /**
     * Distanz zwischen zwei Knoten entweder durch den abgespeicherten Wert oder falls dieser nicht vorhanden anhand der Distanz zwischen den Knoten bestimmen
     * @param k Knoten mit dem Verglichen werden soll
     * @return Distanz der Knoten
     */
    double getDistance(Knoten k){
        
        if(this.getLinks().containsKey(k) && this.getLinks().get(k) != null)
            return this.getLinks().get(k);
        return Math.sqrt((this.x-k.x)*(this.x - k.x) + (this.y-k.y)*(this.y-k.y));
    }
    
    /**
     * Überprüfung ob Koordinate innerhalb des Knotens
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return true wenn innerhalb des Knotens, false wenn nicht
     */
    boolean isInsideKnot(int x, int y){

        return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) <= size / 2 * size / 2;
    }
    
    /**
     * Position des Knoten ändern
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    void setPostionCenter(int x, int y){
        
        this.x = x;
        this.y = y;
    }
    
    /**
     * Knoten zeichnen
     * @param g Zeichenfläche
     */
    void paint(Graphics2D g){
        
        g.setColor(circlecolor);
        g.fillOval(this.x-this.size/2, this.y-this.size/2, this.size, this.size);
        if(name != null){
            
            g.setFont(new Font("ARIAL", Font.BOLD, 14));
            g.setColor(textcolor);
            g.drawString(name, x - g.getFontMetrics().stringWidth(name)/2, y - this.size/2 );
        }
    }
    
    /**
     * Verbindungslinien zeichnen
     * @param g Zeichenfläche
     */
    void paintConnections(Graphics2D g){
        
        for(Knoten l : links.keySet()){
            
            g.setColor(CONNECTIONLINECOLOR);
            g.drawLine(this.getX(), this.getY(), l.getX(), l.getY());
        }
    }
    
    /**
     * Knoten von Zeichenfläche entfernen
     * @param g Zeichenfläche
     */
    void clear(Graphics2D g){
        
        Color tmpCirc = this.circlecolor;
        Color tmpFont = this.textcolor;
        this.circlecolor = g.getBackground();
        this.textcolor = g.getBackground();
        paint(g);
        
        g.setColor(g.getBackground());
        for(Knoten l : links.keySet()){
            g.drawLine(this.getX(), this.getY(), l.getX(), l.getY());
        }
                
        this.circlecolor = tmpCirc;
        this.textcolor = tmpFont;
    }
    
    /**
     * Farbe des Knoten ändern
     * @param c neue Farbe
     */
    void setColor(Color c){
        this.circlecolor = c;
    }
    
    /**
     * Durchmesser des Knotenpunkts ändern
     * @param size neuer Durchmesser
     */
    void setSize(int size){
        this.size = size;
    }
    
    /**
     * Größe des Knotens
     * @return Größe des Knotens
     */
    int getSize() {
        return this.size;
    }
    
    /**
     * Verbindung zu einem anderen Knoten hinzufügen
     * @param k zu verbindender Knoten
     * @param distance Distanz zwischen den Knoten
     */
    void addLink(Knoten k, Integer distance){          
        
        this.links.put(k, distance);
    }
    
    /**
     * Überprüfung ob dieser Knoten mit einem anderen Verbunden ist
     * @param k anderer Knoten
     * @return true wenn verbunden, false wenn nicht
     */
    boolean isLinked(Knoten k){

        return links.containsKey(k);
    }
    
    /**
     * x-Koordinate des Knotens
     * @return  x-Koordinate des Knotens
     */
    int getX(){
        return this.x;
    }   
    
    /**
     * y-Koordinate des Knotens
     * @return y-Koordinate des Knotens
     */
    int getY(){
        return this.y;
    }
    
    /**
     * Namen des Knotens ändern
     * @param name neuer Name
     */
    void setName(String name){
        this.name = name;
    }
    
    /**
     * Name des Knotens
     */
    String getName() {
        return this.name;
    }
    
    /**
     * mit diesem Knoten verbundene Knoten
     * @return mit diesem Knoten verbundene Knoten
     */
    HashMap<Knoten, Integer> getLinks(){
        return this.links;
    }
    
    /**
     * Alle Verbindungen des Knotens ersetzen
     * @param links neue Verbindungen
     */
    void setLinks(HashMap<Knoten, Integer> links){
        this.links = links;
    }
    
    /**
     * Eine Verbindung zu einem anderen Knoten entfernen
     * @param k Verbundener Knoten der entfernt werden soll
     */
    void removeLink(Knoten k){
        if(links.containsKey(k))
            links.remove(k);
    }
    
    /**
     * ID des Knotens
     * @return ID des Knotens
     */
    int getId(){
        return this.id;
    }

}
