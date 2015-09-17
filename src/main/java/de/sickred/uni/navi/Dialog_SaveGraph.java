/*
 * Dialog zum Speichern des Graphen
 */

package de.sickred.uni.navi;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.beans.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import javax.swing.JDialog;

public class Dialog_SaveGraph extends JDialog implements ActionListener, PropertyChangeListener{

    private final String OKSTRING = "Speichern";
    private final String CANCELSTRING = "Abbrechen";
    private final String ONLYLETTERSALLOWEDSTRING = ">>>> Nur Zahlen und Buchstaben erlaubt!";
    private final String TOOMANYLETTERSSTRING = ">>>> Nur 20 Zeichen erlaubt!";
    private final String MESSAGE = "Bitte gib einen Dateinamen ein";   /* Meldung an den Nutzer   */
    private Graph g;    /*  Graph zu neuem Knoten   */
    private String typedText;   /*  Usereingabe */
    private JTextField textField;   /*  Textfeld für Namenseingaben   */
    private JOptionPane optionPane; /*  OptionPane des Dialogs  */     
    
    /**
     * Dialog zum Speichern des Graphen
     * @param f GUI von der der Benutzer aus Speichert
     * @param g Zu speichernder Graph
     */
    public Dialog_SaveGraph(GUI f, Graph g) {
        
        /*  Initialisierung des Dialogs */
        super(f, true);             
        this.setTitle("Speichern"); 
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setSize(500, 180);        
        
        this.g = g;
        
        /*  Erzeugen des Textfelds für den Namen    */
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
     * Speichern des Graphen
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
            
            if (OKSTRING.equals(value)) {
                    typedText = textField.getText();
                if (((Pattern.compile("^[0-9a-zA-Z ]+$")).matcher(typedText).find() && typedText.length() <= 20)) {
                    
                    SaveLoad.saveGraph(typedText, g);
                    this.dispose();
                    
                } else {
                   
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
            } else {
                
               this.dispose();
            }
        }
    }
}
