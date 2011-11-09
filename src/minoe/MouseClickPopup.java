package minoe;

import minoe.ResultsFrame.CustomListTableModel;
import minoe.ResultsFrame.CustomTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.JPopupMenu;

    
    
/**
 * For handling table cell double-clicks to display DocumentListWindow.
 * Only activate on "isEditable()" cells.
 * @author Dan Spiteri
 */
    public class MouseClickPopup extends MouseAdapter implements ActionListener{
        JTable table;
        SearchCriteria criteria;
        Vector<SearchCriteria> searchCriteria;
        
        public Globals globals;
        public String ACTION_VIEW_LAWS = "View Laws";
        public String ACTION_AGENCY_GRAPH = "View Agency Graph";
        
        String termA = "";
        String termB = "";
        
        static final int LIST_TABLE = 1;
        static final int NORMAL_TABLE = 0;
        int type = NORMAL_TABLE;  // for determining the source of the double click
                                  // and what action to take.
        
        // From the results page.
        public MouseClickPopup(JTable table, SearchCriteria criteria, int type, Globals globals){
            this.type = type;
            this.table = table;
            this.criteria = criteria;
            this.globals = globals;
        }
                
        @Override
        public void mouseClicked(MouseEvent e) {
            int button = e.getButton();
            
            // Gather table information on double click or alt click.
            if (e.getClickCount() == 2 || button > 1){
                
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (this.type == LIST_TABLE){
                   CustomListTableModel model = (CustomListTableModel) table.getModel();
                   if (model.isEditable(row, col) == false){
                       return;
                   }
                }else{
                   CustomTableModel model = (CustomTableModel) table.getModel();
                   if (model.isEditable(row, col) == false){
                       return;
                   }
                }
                
                Object objCellContent;
                try{
                   objCellContent = table.getValueAt(row,col);
                } catch(Exception exception){
                    javax.swing.JOptionPane.showMessageDialog(null, exception.toString());
                   return;
                }
                if (objCellContent instanceof Integer){
                    if (this.type == LIST_TABLE){
                      String[] terms = ((String) table.getValueAt(row, 0)).split(" - ");   
                      termA = terms[0];
                      termB = terms[1];  
                    }else{
                      termA = (String) table.getValueAt(row, 0);
                      termB = table.getColumnName(col);
                    }
                }
                
                if (e.getClickCount() == 2){
                    // Display laws on double-click
                    new DocumentListWindow(termA, termB, criteria, globals);
                } else if (button > 1){
                    
                    // Show popup menu on alt click
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item;

                    item = new JMenuItem("View Laws");
                    item.setActionCommand(ACTION_VIEW_LAWS);
                    item.addActionListener(this);
                    menu.add(item);

                    item = new JMenuItem("View Agency Graph");
                    item.setActionCommand(ACTION_AGENCY_GRAPH);
                    item.addActionListener(this);
                    menu.add(item);
                    menu.show(table.getParent(), e.getX(), e.getY());
                }
            }
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(ACTION_VIEW_LAWS)){
                // Display list of laws/granules.
                globals.setGlobalWaitCursor();
                new DocumentListWindow(termA, termB, criteria, globals);
                globals.setGlobalDefaultCursor();
            } else if (e.getActionCommand().equals(ACTION_AGENCY_GRAPH)){
                // Display the graph window.
                globals.setGlobalWaitCursor();
                new AgencyGraph(globals, termA, termB, criteria);
                globals.setGlobalDefaultCursor();
            }
        }
        
        

    }
    
