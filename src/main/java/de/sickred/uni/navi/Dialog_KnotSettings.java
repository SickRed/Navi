/*
 * Frame zur Anzeige und Änderung der Eigenschaften eines Knotens
 */

package de.sickred.uni.navi;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

class Dialog_KnotSettings extends JFrame {
 
    private final JFrame f; /*  Dieser Frame    */
    private final Canvas c;  /*  Canvas auf dem Knoten gezeichnet wird   */   
    private final Knoten k; /*  Zu behandelner Knoten   */
    private final Graph g;  /*  Graph der Knoten    */
    private JTextField nameTextField, sizeTextField; /*    Textfeld für die Entfernungseingabe   */    
    private JLabel nameLabel, coreLabel, sizeLabel, linkInstructionLabel;    /*  Labels */
    private JPanel corePanel, linkPanel, linkLabelPanel;   /*  Panels  */
    private JButton acceptButton, deleteButton;   /*  Buttons  */
    private HashMap <JTextField, Knoten> linkTextFields;
    private HashSet <Component> comps;
    
    /**
     * Constructor des Knoten-Eigenschafte-Frames
     * @param c Canvas auf dem der Knoten gezeichnet wird
     * @param g Graph zu dem der Knoten gehört
     * @param k Knoten dessen Eigenschaften behandelt werden sollen
     */
    public Dialog_KnotSettings(final Canvas c, final Graph g,  final Knoten k) {
        
        /*  Initialisierung des Frame */
        super("Konteneigenschaften"); 
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setSize(300, 600);        
        
        this.g = g;
        this.k = k;
        this.c = c;
        this.linkTextFields = new HashMap<>();
        this.comps = new HashSet<>();
        this.f = this;
        
        this.refresh();
        this.setAlwaysOnTop(true);
        this.setVisible(true);
        
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {               
                    GUI.knotsettings = null;
            }
        });
    }

    /**
     * Inhalt des Frames aktualisieren (Beispielsweise wenn Eigenschaften des Knotens geändert wurden)
     */
    void refresh() {
        
        for(Component c : comps)
            this.remove(c);
        
        /*  Layout  */
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();
        
        /*  Coreoptionen    */
        this.corePanel = new JPanel(gridbag);
        corePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        this.coreLabel = new JLabel("Parameter", SwingConstants.CENTER);
        this.coreLabel.setPreferredSize(new Dimension(this.getWidth(), 40));
        this.add(coreLabel);
        comps.add(coreLabel);
        
        /*  Feld für Namen des Knotens  */        
        this.nameLabel = new JLabel("Name: ");
        con.gridx = 0; con.gridy = 0;
        gridbag.setConstraints(this.nameLabel, con);
        this.corePanel.add(this.nameLabel);
        this.nameTextField = new JTextField(k.getName());
        this.nameTextField.setPreferredSize(new Dimension(150, 20));
        con.gridx = 1; con.gridy = 0;
        gridbag.setConstraints(this.nameTextField, con);
        this.corePanel.add(this.nameTextField);
        
        /*  Feld für Größe  */
        this.sizeLabel = new JLabel("Größe: ");
        con.gridx = 0; con.gridy = 1;
        gridbag.setConstraints(this.sizeLabel, con);
        this.corePanel.add(this.sizeLabel);
        this.sizeTextField = new JTextField(""+k.getSize());
        this.sizeTextField.setPreferredSize(new Dimension(150, 20));
        con.gridx = 1; con.gridy = 1;
        gridbag.setConstraints(this.sizeTextField, con);
        this.corePanel.add(this.sizeTextField);
        
        this.add(corePanel);        
        comps.add(corePanel);
        /*  Verknüpfungen   */
        if(!k.getLinks().isEmpty()){
            
            this.linkPanel = new JPanel(gridbag);
            linkPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
            this.linkLabelPanel = new JPanel(new BorderLayout());
            this.linkLabelPanel.add(new JLabel("Verknüpfungen", SwingConstants.CENTER), BorderLayout.CENTER);
            this.linkLabelPanel.add(new JLabel("(Negative Distanzen entfernen die Verbindung)", SwingConstants.CENTER), BorderLayout.SOUTH);
            this.linkLabelPanel.setPreferredSize(new Dimension(this.getWidth(), 40));
            this.add(linkLabelPanel);
            comps.add(linkLabelPanel);

            /*  Auflistung der Verknüpfung in JPanel    */
            con.gridx = 0; con.gridy = 0;
            JLabel knotname = new JLabel("Name");
            gridbag.setConstraints(knotname, con);
            this.linkPanel.add(knotname);
            con.gridx = 1; con.gridy = 0;
            JLabel distanceLabel = new JLabel("Entfernung");
            gridbag.setConstraints(distanceLabel, con);
            this.linkPanel.add(distanceLabel);
            
            int count = 1;
            for(Knoten link : k.getLinks().keySet()){

                JLabel name = new JLabel(link.getName());
                if(name.getText().equals(""))
                    name.setText("ID " + link.getId());
                con.gridx = 0; con.gridy = count;
                gridbag.setConstraints(name, con);
                this.linkPanel.add(name);

                String distanceString = k.getLinks().get(link) + "";
                if(distanceString.equals("null"))
                    distanceString = (int)k.getDistance(link) + " (A)";
                JTextField distance = new JTextField(distanceString);
                distance.setPreferredSize(new Dimension(150, 20));
                con.gridx = 1; con.gridy = count++;
                gridbag.setConstraints(distance, con);
                this.linkPanel.add(distance);
                this.linkTextFields.put(distance, link);
            }
            this.add(linkPanel);
            comps.add(linkPanel);
        }
        
        /*  Button zum Akzeptieren der Änderungen   */        
        this.acceptButton = new JButton("Änderungen übernehmen");
        this.acceptButton.setPreferredSize(new Dimension(this.getWidth(), 35));
        this.acceptButton.addActionListener(e -> {

            /*  Falsche Usereingaben abfangen   */
            if(!(Pattern.compile("^[0-9a-zA-Z ]{0,20}$")).matcher(nameTextField.getText()).find() && !nameTextField.getText().equals("") && !nameTextField.getText().equals(Knoten.DEFAULTNAME + k.getId())){
                JOptionPane.showMessageDialog(f, "Der Name des Knotens muss alphanumerisch sein (Max. 20 Zeichen)");
                return;
            }
            if(!(Pattern.compile("^[0-9]{1,2}$")).matcher(sizeTextField.getText()).find()){
                JOptionPane.showMessageDialog(f, "Die Größe des Knoten darf nur aus Zahlen bestehen (Max. 99)");
                return;
            }
            if(!k.getName().equals(nameTextField.getText()) && g.getKnotByName(nameTextField.getText()) != null){
                JOptionPane.showMessageDialog(f, "Dieser Name ist bereits registriert");
                return;
            }
            if(nameTextField.getText().equals(""))
                nameTextField.setText(Knoten.DEFAULTNAME + k.getId());

            HashMap<Knoten, Integer> liste = new HashMap<>();
            for( JTextField tf : linkTextFields.keySet()){

                if(tf.getText().contains("(A)"))
                    liste.put(linkTextFields.get(tf), null);
                else if((Pattern.compile("^[0-9]+$")).matcher(tf.getText()).find())
                    liste.put(linkTextFields.get(tf), Integer.parseInt(tf.getText()));
                else if((Pattern.compile("^-[0-9]+$")).matcher(tf.getText()).find())
                    linkTextFields.get(tf).removeLink(k);
                else{
                    JOptionPane.showMessageDialog(f, "Falsche Eingabe (Zahl erwartet): "+ tf.getText());
                    return;
                }
            }

            /*  Links der anderen Knoten aktualisieren  */
            for( Knoten link : liste.keySet() )
                link.addLink(k, liste.get(link));


            Graphics2D g2d = (Graphics2D) c.getGraphics();
            k.clear(g2d);
            k.setName(nameTextField.getText());
            k.setSize(Integer.parseInt(sizeTextField.getText()));
            k.setLinks(liste);
            g.paintKnots();
            f.dispose();
        });
        this.add(acceptButton);    
        comps.add(acceptButton);
        
         /*  Button zum Löschen des Knotens   */
        this.deleteButton = new JButton("Diesen Knoten löschen");
        this.deleteButton.setPreferredSize(new Dimension(this.getWidth(), 25));
        this.deleteButton.addActionListener(e -> {
            g.removeKnot(k);
            k.clear((Graphics2D) c.getGraphics());
            g.paintKnots();
            f.dispose();
        });
        this.add(deleteButton);
        comps.add(deleteButton);
                
        this.setSize(this.getWidth(), 270 + (k.getLinks().size() + 1)*20);
        this.validate();
    }
 
}
