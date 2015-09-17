/*
 * LICENSE
 *
        The MIT License (MIT)

        Copyright (c) 2013 SickRed (https://github.com/SickRed/)

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in
        all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
        THE SOFTWARE.

 */

package de.sickred.uni.navi;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

public class Graph {

    private ArrayList <Knoten> knots;   /*  Knoten des Graphen  */
    private final GUI gui;  /*  GUI auf der der Graph dargestellt wird  */
    
    /**
     * Erzeuge leeren Graphen für GUI
     * @param gui GUI auf der der Graph dargestellt wird
     */
    Graph(GUI gui){
        this.gui = gui;
        this.knots = new ArrayList<>();
    }
    
    /**
     * Erzeuge gefüllten Graphen für GUI
     * @param gui GUI auf der der Graph dargestellt wird
     * @param knots Knoten die in Graphen eingetragen werden sollen
     */
    Graph(ArrayList<Knoten> knots, GUI gui){
        
        this.gui = gui;
        this.knots = knots;
    }
        
    /**
     * Knoten auf GUI Zeichnen
     */
    void paintKnots(){
        Graphics2D g = (Graphics2D)gui.c.getGraphics();
        for(Knoten k : knots){
            k.paintConnections(g);            
        }
        for(Knoten k : knots){
            k.paint(g);            
        }
        gui.validate();
    }

    /**
     * Knoten zu Graphen hinzufügen
     * @param knot Knoten zum Hinzufügen
     */
    void addKnot(Knoten knot) {       
        knots.add(knot);
    }
    
    /**
     * Knoten des Graphen
     * @return Knoten des Graphen als ArrayList
     */
    ArrayList<Knoten> getKnots(){
        return knots;
    }
    
    /**
     * Komplette Knoten des Graphen ersetzen
     * @param knots neue Knoten des Graphen
     */
    void setKnots (ArrayList<Knoten> knots){
        this.knots = knots;
    }
    
    /**
     * Knoten an Koordinaten bestimmen
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return Knoten an Koordinate, null falls kein Knoten an Koordinate
     */
    ArrayList<Knoten> getKnot(int x, int y){
        
        ArrayList<Knoten> actualknots = new ArrayList<>();
        for(Knoten k : this.knots)
            if(k.isInsideKnot(x, y))
                actualknots.add(k);
        
        return actualknots;
    }    

    /**
     * Knoten von Graph entfernen
     * @param k zu entfernender Knoten
     */
    void removeKnot(Knoten k) {
       if(knots.contains(k))
           knots.remove(k);
       removeAllLinks(k);
    }
    
    /**
     * Alle Verbindungen eines Knotens entfernen
     * @param k betroffener Knoten
     */
    void removeAllLinks(Knoten k){
        for(Knoten k2 : knots)
            k2.removeLink(k);
    }

    /**
     * Alle Knoten von Zeichenbereich entfernen
     */
    void clear() {
        Graphics2D g = (Graphics2D)gui.c.getGraphics();
        for(Knoten k : knots)
            k.clear(g);
    }
    
    /**
     * Kürzeste Route zwischen zwei Knoten bestimmmen
     * @param startKnot Startknoten
     * @param targetKnot Zielknoten
     * @return Strecke als ArrayList (Ziel hat Index 0), null falls keine Strecke gefunden werden kann
     */
    ArrayList<Knoten> getRouteShortest(Knoten startKnot, Knoten targetKnot){
        
        System.out.println("Suche nach dem kürzesten Weg");
        /*  Abbruch falls keine Knoten gespeichert oder Ziel/ Start keine Verbindungen hat  */
        if(knots.isEmpty() || targetKnot.getLinks().isEmpty() || startKnot.getLinks().isEmpty())
            return null;      
        
        /*  Initialisieren   */
        ArrayList<Knoten> list = new ArrayList<>();
        ArrayList<LinkClass> reachable = new ArrayList<>();
        ArrayList<LinkClass> chosen = new ArrayList<>();        
        LinkClass startLink = new LinkClass(startKnot, startKnot, 0);
        
        /*  Suche der Strecke   */
        boolean found = false;
        while(!found){     
            
            chosen.add(startLink);  
            System.out.println("Chosen: " + chosen.get(chosen.size() - 1).target.getName() + " from " +  chosen.get(chosen.size() - 1).start.getName() + " D: " + chosen.get(chosen.size() - 1).distance);
            
            /*  Von aktuellem Punkt Verbindungen suchen und in Liste einfügen   */
            for(Knoten k : startLink.target.getLinks().keySet()){
                boolean newLink = true;
                for (LinkClass aReachable : reachable)
                    if (aReachable.target.equals(k)) {

                        newLink = false;
                        if (aReachable.distance > startLink.distance + k.getDistance(startLink.target)) {

                            aReachable.start = startLink.target;
                            aReachable.distance = startLink.distance + k.getDistance(startLink.target);
                        }
                    }
                if(newLink)
                    for(LinkClass l : chosen)
                        if(l.target.equals(k))
                            newLink = false;
                if(newLink)        
                    reachable.add(new LinkClass(startLink.target, k, startLink.distance + startLink.target.getDistance(k)));        
            }
            
            /*  Nächsten erreichbaren Punkt aus Liste wählen und auswählen */
            LinkClass nearest = null;
            for(LinkClass l : reachable)
                 if(nearest == null || l.distance < nearest.distance)
                     nearest = l;            
            startLink = nearest;
            reachable.remove(startLink);
            
            /*  Abbruch falls keine Optionen mehr   */
            if(nearest == null)
                return null;
            
            /*  Abbruch falls Ziel erreicht */
            if(nearest.target.equals(targetKnot)){
                chosen.add(nearest);
                found = true;
            }            
        }
        
        /*  Backtracing */
        for(int i = chosen.size() - 1; i >= 0; i--){
            if(chosen.get(i).target.equals(targetKnot)){
                
                list.add(chosen.get(i).target);
                targetKnot = chosen.get(i).start;               
            }           
        }      
        return list;
    }
    
    /**
     * Wegfindung via Breitensuche in Graphen
     * @param start Startknoten
     * @param target Zielknoten
     * @return Strecke als ArrayList (Ziel hat Index 0), null falls keine Strecke gefunden werden kann
     */
    ArrayList<Knoten> getRouteBreadthFirst(Knoten start, Knoten target){
        
        System.out.println("Breitensuche");
        /*  Abbruch falls keine Knoten gespeichert oder Ziel/ Start keine Verbindungen hat  */
        if(knots.isEmpty() || target.getLinks().isEmpty() || start.getLinks().isEmpty())
            return null;        
        
        /*  Initialisieren  */
        ArrayList<Knoten> list = new ArrayList<>();
        Knoten[][] reachable = new Knoten[knots.size()][2];
        int countReachables = 1;
        int countChecked = 0;
        Knoten actual = start;   
        reachable[0][0] = start;
        reachable[0][1] = start;
        boolean found = false;
        
        /*  Suche der Strecke   */
        while(!found  && actual != null){
                        
            /*  Erreichbare Punkte zu Liste hinzufügen  */
            for(Knoten link : actual.getLinks().keySet()){
                                  
                if(link.equals(target))
                    found = true;
                boolean alreadyfound = false;
                for (Knoten[] aReachable : reachable)
                    if (aReachable[0] == null)
                        break;
                    else if (aReachable[0].equals(link))
                        alreadyfound = true;
                if(!alreadyfound){
                    System.out.println("New Link: " + link.getName() + " from: " + actual.getName());
                    reachable[countReachables][0] = link;
                    reachable[countReachables++][1] = actual;                            
                }                
            } 
            /*  Auswahl des neuen Knotens FIRST IN FIRST OUT    */
            if(reachable.length <= countChecked || reachable[countChecked][0] == null)
                break;
            else
                actual = reachable[countChecked++][0];
        }
        
        if(!found)
            return null;
        
        /*  Backtracing */
        for(int i = reachable.length - 1; i >= 0; i--)
            if(reachable[i][0] == target){
                list.add(target);
                target = reachable[i][1];
            }                          
        return list;
    }
    
    /**
     * Wegfindung via Tiefensuche in Graphen
     * @param start Startknoten
     * @param target Zielknoten
     * @return Strecke als ArrayList (Ziel hat Index 0), null falls keine Strecke gefunden werden kann
     */
    ArrayList<Knoten> getRouteDepthFirst(Knoten start, Knoten target){
        
        System.out.println("Tiefensuche");
        /*  Abbruch falls keine Knoten gespeichert oder Ziel/ Start keine Verbindungen hat  */
        if(knots.isEmpty() || target.getLinks().isEmpty() || start.getLinks().isEmpty())
            return null;    
        
        /*  Initialisierung */
        ArrayList<Knoten> list = new ArrayList<>();
        Knoten[][] reachable = new Knoten[knots.size()][2];
        int countReachables = 1;
        int positionInArray = 1;
        Knoten actual = start;   
        reachable[0][0] = start;
        reachable[0][1] = start;
        boolean found = false;  
        
        /*  Suche der Strecke   */
        while(!found){
                  
            /*  Erreichbare Punkte zu Liste hinzufügen  */
            for(Knoten link : actual.getLinks().keySet()){
                                  
                if(link.equals(target))
                    found = true;
                boolean alreadyfound = false;
                for (Knoten[] aReachable : reachable)
                    if (aReachable[0] == null)
                        break;
                    else if (aReachable[0].equals(link))
                        alreadyfound = true;
                if(!alreadyfound){
                    System.out.println("New Link: " + link.getName() + " from: " + actual.getName());
                    reachable[countReachables][0] = link;
                    reachable[countReachables++][1] = actual;   
                    positionInArray = countReachables;                    
                } 
            } 
            /*  Auswahl des neuen Knotens FIRST IN LAST OUT    */
            if(reachable.length < positionInArray || positionInArray < 1 || reachable[positionInArray - 1][0] == null)
                break;
            else
                actual = reachable[--positionInArray][0];
        }
        
        if(!found)
            return null;
        
        /*  Backtracing */
        for(int i = reachable.length - 1; i >= 0; i--)
            if(reachable[i][0] == target){
                list.add(target);
                target = reachable[i][1];
            }                          
        return list;
    }
      
    /**
     * Zufälligen Knoten aus Graphen
     * @return Zufälligen Knoten aus Graphen 
     */
    Knoten getRandomKnotFromGraph(){
        
        if(knots.isEmpty())
            return null;
        int count = 0;       
        Knoten[] list = new Knoten[knots.size()];        
        for(Knoten k : knots)
            list[count++] = k;
        Random r = new Random();
        return list[r.nextInt(list.length)];                
    }
    
    /**
     * Knoten des Graphen an Hand des Namens holen
     * @param name Name des Knoten
     * @return Knoten oder Null falls kein noten mit diesem Namen gespeichert
     */
    Knoten getKnotByName(String name){
        for(Knoten k : knots)
            if(k.getName().equals(name))
                return k;
        return null;
    }
    
    /**
     * Anzahl der maximalen Kanten berechnen
     * @param amountofknots Anzahl der Knoten
     * @return Anzahl der maximalen Kanten
     */
    static int getMaxAmountOfEdges(int amountofknots){
        
        int amount = 0;
        for(int i = 0; i < amountofknots; i++){
            amount+= i;
        }
        return amount;
    }
    
    /**
     * Höchste ID der Knoten im Graphen bestimmen
     * @return höchste ID
     */
    int getHighestKnotId(){
        
        int high = 0;
        for(Knoten k : knots)
            if(k.getId() > high)
                high = k.getId();
        return high;
    }
    
    /**
     * Klasse zur verwaltung der Verbindungen bei der Suche nach dem kürzesten Weg
     */
    class LinkClass {
        Knoten start;
        Knoten target;
        double distance;

        public LinkClass(Knoten start, Knoten target, double distance) {

            this.start = start;
            this.target = target;
            this.distance = distance;
        }


    }
}
