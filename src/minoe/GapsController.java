package minoe;

import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * This class receives input from the Gaps Container and
 * manages the data according to the current panel.
 */

public class GapsController {
    
    private GapsWizardChooser gapsChooser;
    
    private int currentPanel;
    
    private final int _PANEL_1 = 1;
    private final int _PANEL_2 = 2;
    private final int _PANEL_3 = 3;
    private final int _PANEL_4 = 4;
    private final int _PANEL_5 = 5;
    
    private float[][] panel2data;
    // Store reference to calling gui component.
    private GapsContainer container;
    
    // Default Constructor.
    public GapsController(){        
        
    }  
    public GapsController(GapsContainer gc){        
        this.container = gc;
    }
    
    /** 
     * Processes data from each screen/jpanel.
     * @return boolean True on no errors. 
     */
    public boolean processPanelData(){
        boolean returnVal = false;
        switch (this.currentPanel){
            case _PANEL_1: 
                String[] panel1Data = container.gapsPanel1.getListData();
                container.gapsPanel2.setColumnNames(panel1Data);
                returnVal = true;
                break;
            case _PANEL_2:
                panel2data = container.gapsPanel2.getTableData();              
                if (panel2data == null || panel2data.length == 0){
                    returnVal = false;
                } else{
                    returnVal = true;
                    break;
                }
                break;
            case _PANEL_3:
                Vector<SearchCriteria> searchCriteria = container.gapsPanel3.getSearchCriteria();
                if(searchCriteria == null || searchCriteria.size() == 0){
                    JOptionPane.showMessageDialog(null, "Please create at least one group.");
                    returnVal = false;
                }else{
                    returnVal = true;
                    container.showResults(panel2data, searchCriteria);
                }
                break;
            default: returnVal = false;
        }
        return returnVal;
    }
    
    /** 
     * Process next button action.
     * If the data check returns true then the next panel is displayed.
     * @return boolean Moves to next screen/jpanel on true.
     */  
    public boolean processNext(){
        boolean returnVal = false;
        switch (this.currentPanel){
            case _PANEL_1: 
                if (processPanelData()){
                    returnVal = true;
                    setCurrentPanel(_PANEL_2);
                    container.updateSteps(_PANEL_2);
                    break;
                } 
                break;
            case _PANEL_2:
                if (processPanelData()){
                    returnVal = true;
                    setCurrentPanel(_PANEL_3);
                    container.updateSteps(_PANEL_3);
                    break;
                } 
                break;
            case _PANEL_3:
                if (processPanelData()){
                    break;
                }

                break;
        }
        return returnVal;  
    }
    
    /** Process previous button action. 
     *  @return boolean Condition to return to prior screen.
     */
    public boolean processPrevious(){
        boolean returnVal = false;
        switch (this.currentPanel){
            case _PANEL_2:
                    int c = JOptionPane.showConfirmDialog(null, "Warning:  Going back will reset the linkages on this screen.  Do you wish to go back?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (c==0){
                        returnVal = true;
                        setCurrentPanel(_PANEL_1);
                        container.updateSteps(_PANEL_1);
                    } else {
                        returnVal = false;
                    }
                    break;
            case _PANEL_3: 
                    returnVal = true;
                    setCurrentPanel(_PANEL_2);
                    container.updateSteps(_PANEL_2);
                    break;
            case _PANEL_4: 
                    returnVal = true;
                    setCurrentPanel(_PANEL_3);
                    container.updateSteps(_PANEL_3);
                    break; 
        }
        return returnVal;
    }
    
    // Set Methods
    public void setCurrentPanel(int panel){
        this.currentPanel = panel;      
    }  
    public void setContainer(GapsContainer container){
        this.container = container;
    }    
    public void setChooser(GapsWizardChooser gwc){
        this.gapsChooser = gwc;
    }
    
    // Get Methods
    public int getCurrentPanel(){
        return this.currentPanel;
    }
 
}
