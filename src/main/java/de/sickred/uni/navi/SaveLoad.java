/*
 * Klasse zur Behandlung des Ladens und Speicherns eines Graphen
 */

package de.sickred.uni.navi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class SaveLoad {
    
    /**
     * Graph speichern
     * @param name Name der Datei
     * @param g Graph
     */
    static void saveGraph(String name, Graph g){
        
        String path = name + ".txt";
        File f = new File(path);
        if (!f.exists()) {

            try {
                if(!f.createNewFile())
                    System.err.println("Fehler beim Erstellen der Speicher-Datei ("+path+")");
            } catch (IOException ex) {
                System.err.println("Fehler beim Erstellen der Speicher-Datei ("+path+"): " + ex.getMessage());
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            for (Knoten knot : g.getKnots()) {
                
                out.write("KNOT:" +knot.getId() + ":" + knot.getName() + ":" + knot.getX() + ":" + knot.getY()); 
                if(!knot.getLinks().isEmpty()){                    
                    out.write(":");
                    Iterator<Knoten> linksIt = knot.getLinks().keySet().iterator();
                    while(linksIt.hasNext()){                        
                        Knoten l = linksIt.next();
                        out.write(l.getId() + "_" + knot.getLinks().get(l));
                        if(linksIt.hasNext())
                            out.write(",");
                    }
                }               
                out.newLine();
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            System.err.println("Fehler beim Speichern der Speicher-Datei ("+path+"): " + ex.getMessage());
        }            
    }
    
    /**
     * Graph laden
     * @param name Name der Datei
     * @param gui GUI auf dem der Graph dargestellt wird
     * @return loaded graph
     */
    static Graph loadGraph(String name, GUI gui){
        
        HashMap<String, Knoten> linkstrings = new HashMap<>();
        HashMap<Integer, Knoten> linksIDs = new HashMap<>();
        String path = name + ".txt";
        Graph g = new Graph(gui);
        String strLine;
        BufferedReader br;
        DataInputStream in;
        FileInputStream fstream;
        int count = 0;
        
        try {
            File f = new File(path);
            if (!f.exists())
                return null;

            fstream = new FileInputStream(path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));           

            while ((strLine = br.readLine()) != null){
                
                String[] split = strLine.split(":");
                if(!split[0].equals("KNOT")){
                    System.err.println("Kein Knoten");
                    continue;
                }    
                int id = Integer.parseInt(split[1]);
                String knotname = split[2];
                int x = Integer.parseInt(split[3]);
                int y = Integer.parseInt(split[4]);                
                
                Knoten k = new Knoten(knotname, x, y);
                if(split.length > 5)
                    linkstrings.put(split[5], k);
                
                linksIDs.put(id, k);
                g.addKnot(k);
            }      
            in.close();
            
            for(String s : linkstrings.keySet()){
                
                for(String idString : s.split(",")){
                    
                    int id = Integer.parseInt(idString.split("_")[0]);
                    Integer distance = null;
                    if(!idString.split("_")[1].equals("null"))
                        distance = Integer.parseInt(idString.split("_")[1]);
                    if(linksIDs.containsKey(id))
                        linkstrings.get(s).addLink(linksIDs.get(id), distance);
                }
                    
            }
            
        } catch (Exception ex) {
             System.err.println("Fehler beim Laden des Graphen ("+path+"): " + ex.getMessage());
             return null;
        }	
        return g;
    }
    
    
}
