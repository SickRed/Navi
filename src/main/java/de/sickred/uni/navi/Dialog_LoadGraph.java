/*
 * Dialog zum Laden eines gespeicherten Graphen
 */
package de.sickred.uni.navi;
import java.awt.Graphics2D;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.beans.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import javax.swing.JDialog;

public class Dialog_LoadGraph extends JDialog implements ActionListener, PropertyChangeListener{

    private final String OKSTRING = "Laden";
    private final String CANCELSTRING = "Abbrechen";
    private final String ONLYLETTERSALLOWEDSTRING = ">>>> Nur Zahlen und Buchstaben erlaubt!";
    private final String TOOMANYLETTERSSTRING = ">>>> Nur 20 Zeichen erlaubt!";
    private final String MESSAGE = "Bitte gib einen Dateinamen ein";   /* Meldung an den Nutzer   */
    private final String FILENOTFOUND = "Datei nicht gefunden oder Datei korrupt";
    private String typedText;   /*  Usereingabe */
    private JTextField textField;   /*  Textfeld für Namenseingaben   */
    private JOptionPane optionPane; /*  OptionPane des Dialogs  */     
    private final GUI gui;  /*  GUI die den Dialog ausgelöst hat    */
    
    /**
     * Dialog zum Laden eines gespeicherten Graphen
     * @param gui GUI die den Graph darstellen soll
     */
    public Dialog_LoadGraph(GUI gui) {
        
        /*  Initialisierung des Dialogs */
        super(gui.f, true);             
        this.setTitle("Laden"); 
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setSize(500, 180);        
        
        this.gui = gui;
        
        /*  Erzeugen des Textfelds für den Namen der Datei   */
        this.textField = new JTextField(10);
        
        /*  Erzeugen des optionPane für Eintragung bzw. Abbruch der Eintragung  */
        Object[] options = {OKSTRING, CANCELSTRING};
        Object[] array = {MESSAGE, textField};         
        this.optionPane = new JOptionPane(array,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION, null, options, options[0]); 
        this.setContentPane(optionPane); 
        
        /*  Gleichbehandlung des Fensterschließens wie einen Abbruch des Dialogs    */
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {               
                    optionPane.setValue(JOptionPane.CLOSED_OPTION);
            }
        });
        
        /*  Textfeld fokusieren default */
        this.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                textField.requestFocusInWindow();
            }
        });
        
        textField.addActionListener(this);
        optionPane.addPropertyChangeListener(this);
        this.setVisible(true);
    }
 
    /**
     * Behandlung Drücken der Entertaste wie OK Klicken
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        optionPane.setValue(OKSTRING);
    }
 
    /**
     * Laden des Graphens
     * @param e PropertyChangeEvent Benutzereingabe
     */ 
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
 
        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();
 
            if (value == JOptionPane.UNINITIALIZED_VALUE) 
                return;
            
            /*  OK - Button */
            if (OKSTRING.equals(value)) {
                
                typedText = textField.getText();
                /*  Korrekte Eingabe    */    
                if (((Pattern.compile("^[0-9a-zA-Z ]+$")).matcher(typedText).find() && typedText.length() <= 20)) {
                    
                    Graph g2 = SaveLoad.loadGraph(typedText, gui);
                    if(g2 == null){
                        Object[] array = {MESSAGE,FILENOTFOUND, textField};
                        optionPane.setMessage(array);
                        textField.selectAll();
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        typedText = null;
                        textField.requestFocusInWindow();
                        return;
                    }
                        
                    gui.clear();
                    gui.g = g2;
                    gui.g.paintKnots();
                    Knoten.setIncrement(gui.g.getHighestKnotId() + 1);
                    this.dispose();  
                } 
                /*  Falsche Eingabe */
                else {
                   
                    if(typedText.length() > 20){
                         Object[] array = {MESSAGE,TOOMANYLETTERSSTRING, textField};
                         optionPane.setMessage(array);
                    } 
                    else{
                        Object[] array = {MESSAGE,ONLYLETTERSALLOWEDSTRING, textField};
                        optionPane.setMessage(array);
                    }                    
                    textField.selectAll();
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    typedText = null;
                    textField.requestFocusInWindow();
                }                
            } 
            /*  Abbrechen - Button  */
            else {
                
               this.dispose();
            }
        }
    }
}
