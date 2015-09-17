/*
 * Dialog zu Festsetzung der Distanz zwischen zwei Knoten
 */

package de.sickred.uni.navi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

class Dialog_Distance extends JDialog implements ActionListener, PropertyChangeListener {
    
    private final String OKSTRING = "Eintragen";
    private final String CANCELSTRING = "Abbrechen";
    private final String ONLYNUMBERSALLOWEDSTRING = ">>>> Nur Zahlen erlaubt!";
    private final String MESSAGE = "Bitte lege die Distanz zwischen den Knoten fest oder lass das Feld leer \nfalls die Entfernung der Punkte dynamisch bestimmt werden soll";   /* Meldung an den Nutzer   */
    private final Graph g;  /*  Graph der Knoten    */
    private final Knoten connectKnotStart;  /*  Knoten 1    */
    private final Knoten connectKnotEnd;    /*  Knoten 2    */
    private JTextField textField; /*Textfeld für die Entfernungseingabe   */
    private String typedText;   /*  Usereingabe */
    private JOptionPane optionPane; /*  OptionPane des Dialogs  */     

    public Dialog_Distance(JFrame f, Graph g, Knoten connectKnotStart, Knoten connectKnotEnd) {
        
        /*  Initialisierung des Dialogs */
        super(f, true);             
        this.setTitle("Distanz der Knoten"); 
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setSize(500, 180);        
        
        this.g = g;
        this.connectKnotStart = connectKnotStart;
        this.connectKnotEnd = connectKnotEnd;
        
        /*  Erzeugen des Textfelds für die Entfernung    */
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
            @Override
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
     * Eintragung der Verknüpfung in den Graphen
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
                if ((Pattern.compile("^[0-9]+$")).matcher(typedText).find()) {
                    
                    connectKnotStart.addLink(connectKnotEnd, Integer.parseInt(typedText));
                    connectKnotEnd.addLink(connectKnotStart, Integer.parseInt(typedText));
                    this.dispose();                    
                } 
                else {
                   
                    if(typedText.equals("")){
                       connectKnotStart.addLink(connectKnotEnd, null);       
                       connectKnotEnd.addLink(connectKnotStart, null);
                       this.dispose();
                    } 
                    else{
                        Object[] array = {MESSAGE,ONLYNUMBERSALLOWEDSTRING, textField};
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
