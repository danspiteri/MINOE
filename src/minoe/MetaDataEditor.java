
package minoe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.lucene.index.CorruptIndexException;

/**
 * Displays a GUI for editing the meta data file.
 * @author Daniel Spiteri
 */
public class MetaDataEditor extends javax.swing.JInternalFrame {

    CustomTableModel tableModel;
    MetaDataController mdc;
    File metaDataFile;
    CustomCellRenderer customCellRenderer;
    Vector<Vector> duplicateTable; // keep track of duplicate elements for the cell renderer
    Globals globals;
    Vector<String> includedFiles = new Vector<String>(); // files that have meta data, populated on loading the editor.
    Vector<String> missingFiles = new Vector<String>(); // files that do not have meta data.


    /** Creates new form MetaDataEditor */
    public MetaDataEditor(MetaDataController controller, File metadata, Globals globals) {
        this.globals = globals;
        this.customCellRenderer = new CustomCellRenderer();
        this.mdc = controller;
        this.metaDataFile = metadata;
        this.tableModel = new CustomTableModel();
        this.duplicateTable = new Vector<Vector>();
        initComponents();

        this.jTable.getTableHeader().setReorderingAllowed(false);
        this.jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        this.loadTableModel(null);
    }


    /**
     * Retrieves the data from the meta data file and loads into the jtable.
     */
    public void loadTableModel(File dataFile){
        try {
            if(dataFile == null){
               dataFile = this.metaDataFile;
            }
            List<String[]> csvData = this.mdc.getFileData(dataFile);
            String[] columnTitles = csvData.get(0);
            Vector<String> colTitlesVec = new Vector<String>();
            colTitlesVec.add("Row #");
            for (String string : columnTitles) {
                colTitlesVec.add(string);
            }

            Vector<Vector> dataVector = new Vector<Vector>();

            // Load the data into the table model, starting from the second line.
            for (int row = 1; row < csvData.size(); row++) {
                String[] lineData = csvData.get(row);
                Vector<String> lineVec = new Vector<String>();
                lineVec.add("" + row);
                for (int col = 0; col < lineData.length; col++) {
                    lineVec.add(lineData[col]);
                    if(col == 0){
                        // keep track of the file names that have meta data.
                        this.includedFiles.add(lineData[col]);
                    }
                }
                dataVector.add(lineVec);
            }

            errorCheck(dataVector, true);
            this.tableModel = new CustomTableModel(dataVector, colTitlesVec);
            this.jTable.setModel(tableModel);
            autoFitColumns(this.jTable);

            checkForMissingFiles();

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString());
        }
    }

    public String errorCheck(Vector<Vector> dataVector, boolean offset){
        String warnings = this.mdc.integrityCheck(dataVector, offset);

        if(warnings.length() > 0){
            showErrorLabel();
        } else {
            hideErrorLabel();
        }

        this.duplicateTable = this.mdc.getIntegrityDuplicates(dataVector, offset);
        revalidate();
        repaint();

        return warnings;
    }

    public void closeWindow(){
        int confirm = JOptionPane.showConfirmDialog(this, "Press OK to leave the editor.  Any changes made since saving will be lost.", "Quit Editing?", JOptionPane.OK_CANCEL_OPTION);
        if(confirm == 0){
           this.dispose();
        }
    }

    /**
     * Resizes the columns to the width of the column data.
     * @param table
     */
    public void autoFitColumns(JTable table){
        // Adjust column widths
        TableColumn column = null;
        for (int j = 0; j < table.getColumnCount(); j++) {
            column = table.getColumnModel().getColumn(j);
            JTableHeader header = table.getTableHeader();

            int rowCount = table.getRowCount();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)header.getDefaultRenderer()
                       .getTableCellRendererComponent(table, column.getIdentifier()
                               , false, false, -1, col).getPreferredSize().getWidth();
            for(int row = 0; row<rowCount; row++){
                   int preferedWidth = (int)table.getCellRenderer(row, col).getTableCellRendererComponent(table,
                           table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                   width = Math.max(width, preferedWidth);
                   width = Math.min(width, 200);
            }
            header.setResizingColumn(column); // this line is very important
            column.setWidth(width+table.getIntercellSpacing().width);

        }
        revalidate();
    }

    /**
     * Loads the data from the meta data file back into the editor.
     */
    public void revertChanges(){
        int confirm = JOptionPane.showConfirmDialog(this, "Undo all changes since saving?", "Revert?", JOptionPane.OK_CANCEL_OPTION);
        if(confirm != 0){
           return;
        }
        loadTableModel(null);
    }

    public void showErrorLabel(){
        this.errorLabel.setVisible(true);
    }
    public void hideErrorLabel(){
        this.errorLabel.setVisible(false);
    }

    public void showFileChooser(){
       JFileChooser chooser = new JFileChooser();
        try{
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnvalue = chooser.showDialog(this, "Import Meta Data");
            if (returnvalue == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String warnings = this.mdc.integrityCheck(file);
                if(warnings.length() > 0){
                    int confirm = JOptionPane.showConfirmDialog(this, warnings + "\n Press OK to continue importing.", "Formatting errors", JOptionPane.OK_CANCEL_OPTION);
                    if(confirm != 0){
                       return;
                    }
                }
                loadTableModel(file);
                JOptionPane.showMessageDialog(this, "The data from " + file.getName() + " has been loaded into the editor.");
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.toString(), "Error opening file/folder: " + e.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks for file names in the index that do not have meta data associated.
     */
    public void checkForMissingFiles(){
        try {
            Vector<String> allFiles = this.globals.getSearchFiles().getAllFileNames();  // all the file names in the index

            // See which files are not in the meta data
            for (String file : allFiles) {
                if(!this.includedFiles.contains(file)){
                    this.missingFiles.add(file);
                }
            }

            // insert the missing file names as new rows
            for (String file : this.missingFiles) {
                addRow(file);
            }

            if(this.missingFiles.size() > 0){
                JOptionPane.showMessageDialog(this, "Warning: there were files found in the collection that do not have meta data associated. \n " +
                        "The names of the files have been inserted as new rows in the editor.  If you do not provide information for these files," +
                        "they will not be included in any searching or analyses.");
            }

        } catch (CorruptIndexException ex) {
            JOptionPane.showMessageDialog(this, "Error checking for missing files: " + ex.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error checking for missing files: " + ex.toString());
        }
    }

    /**
     * Adds a new row to the bottom of the editor table.
     * If a file name is specified, a blank row with the specified 
     * file name value is inserted.
     * @param filename
     */
    public void addRow(String filename){
        Object[] rowData = new Object[this.tableModel.getColumnCount()];
        Vector<Boolean> bVec = new Vector<Boolean>(this.tableModel.getColumnCount());
        for (int i = 0; i < rowData.length; i++) {
            if(i == 0){
                rowData[i] = String.valueOf(jTable.getRowCount() + 1);
            } else if(i == 1 && filename != null){
                rowData[i] = filename;
            } else{
                rowData[i] = "";
            }
            Boolean bObj = new Boolean(false);
            bVec.add(bObj);
        }
        this.duplicateTable.add(bVec);
        this.tableModel.addRow(rowData);
        // scroll to bottom
        this.jTable.scrollRectToVisible(jTable.getCellRect(jTable.getRowCount() - 1, 0, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        addRowButton = new javax.swing.JButton();
        deleteRowButton = new javax.swing.JButton();
        revertButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable(){
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return customCellRenderer;
            }
        };
        bottomPanel = new javax.swing.JPanel();
        errorPanel = new javax.swing.JPanel();
        errorLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        importButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.title")); // NOI18N
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/minoe/pencil.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        topPanel.setBackground(new java.awt.Color(255, 255, 255));
        topPanel.setFont(new java.awt.Font("Arial", 1, 11));
        topPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        topPanel.setMinimumSize(new java.awt.Dimension(800, 40));
        topPanel.setPreferredSize(new java.awt.Dimension(800, 40));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.jLabel2.text")); // NOI18N
        topPanel.add(jLabel2);

        getContentPane().add(topPanel);

        rightPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        rightPanel.setMaximumSize(new java.awt.Dimension(32767, 25));
        rightPanel.setMinimumSize(new java.awt.Dimension(195, 25));
        rightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        addRowButton.setFont(new java.awt.Font("Arial", 0, 11));
        addRowButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.addRowButton.text")); // NOI18N
        addRowButton.setMaximumSize(new java.awt.Dimension(90, 23));
        addRowButton.setMinimumSize(new java.awt.Dimension(90, 23));
        addRowButton.setPreferredSize(new java.awt.Dimension(90, 23));
        addRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowButtonActionPerformed(evt);
            }
        });
        rightPanel.add(addRowButton);

        deleteRowButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        deleteRowButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.deleteRowButton.text")); // NOI18N
        deleteRowButton.setMaximumSize(new java.awt.Dimension(90, 23));
        deleteRowButton.setMinimumSize(new java.awt.Dimension(90, 23));
        deleteRowButton.setPreferredSize(new java.awt.Dimension(90, 23));
        deleteRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRowButtonActionPerformed(evt);
            }
        });
        rightPanel.add(deleteRowButton);

        revertButton.setFont(new java.awt.Font("Arial", 0, 11));
        revertButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.revertButton.text")); // NOI18N
        revertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertButtonActionPerformed(evt);
            }
        });
        rightPanel.add(revertButton);

        getContentPane().add(rightPanel);

        tablePanel.setFont(new java.awt.Font("Arial", 0, 11));
        tablePanel.setLayout(new javax.swing.BoxLayout(tablePanel, javax.swing.BoxLayout.X_AXIS));

        jTable.setFont(new java.awt.Font("Arial", 0, 11));
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.jTable.columnModel.title0")); // NOI18N
        jTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.jTable.columnModel.title1")); // NOI18N
        jTable.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.jTable.columnModel.title2")); // NOI18N
        jTable.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.jTable.columnModel.title3")); // NOI18N

        tablePanel.add(jScrollPane1);

        getContentPane().add(tablePanel);

        bottomPanel.setFont(new java.awt.Font("Arial", 0, 11));
        bottomPanel.setMaximumSize(new java.awt.Dimension(32767, 50));
        bottomPanel.setMinimumSize(new java.awt.Dimension(137, 50));
        bottomPanel.setLayout(new javax.swing.BoxLayout(bottomPanel, javax.swing.BoxLayout.LINE_AXIS));

        errorLabel.setFont(new java.awt.Font("Arial", 1, 12));
        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.errorLabel.text")); // NOI18N
        errorPanel.add(errorLabel);

        bottomPanel.add(errorPanel);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        importButton.setFont(new java.awt.Font("Arial", 0, 12));
        importButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.importButton.text")); // NOI18N
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(importButton);

        saveButton.setFont(new java.awt.Font("Arial", 0, 12));
        saveButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        cancelButton.setFont(new java.awt.Font("Arial", 0, 12));
        cancelButton.setText(org.openide.util.NbBundle.getMessage(MetaDataEditor.class, "MetaDataEditor.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel);

        getContentPane().add(bottomPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        closeWindow();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Save table information to meta data file.
     * @param evt
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Press OK to save changes.", "Save?", JOptionPane.OK_CANCEL_OPTION);
        if(confirm != 0){
           return;
        }
        Vector<Vector> tableData = this.tableModel.getDataVector();
        Vector<String> columnTitles = new Vector<String>();

        // Don't grab info from the first column (start at 1).
        for (int i = 1; i < this.tableModel.getColumnCount(); i++) {
            columnTitles.add(this.tableModel.getColumnName(i));
        }
        
        Vector<Vector> outData = new Vector<Vector>(); // header is first row, followed by data

        for (Vector<String> v : tableData) {
            Vector<String> rowData = new Vector<String>();
            for (int i = 1; i < v.size(); i++) { // don't grab from the first column
                rowData.add(v.get(i));
            }
            outData.add(rowData);
        }

        String warnings = errorCheck(outData, false);
        // see if there are any errors before proceeding
        boolean has_warnings = false;
        if(warnings != null && warnings.length() > 0){
            has_warnings = true;
            int override = JOptionPane.showConfirmDialog(this,
                    "Please fix before proceeding: " + warnings + "\n\nPress OK to bypass and continue saving.",
                    "Errors Found",
                    JOptionPane.OK_CANCEL_OPTION);
            if(override != 0){
               return;
            }
        }
        // add column titles if everything is okay
        outData.insertElementAt(columnTitles, 0);
        try {
            boolean success = this.mdc.writeToMetaDataFile(outData, this.metaDataFile);
            if(success){
                globals.setMetaDataErrorFlag(false);
                if(has_warnings == false){
                    globals.setMetaDataErrorFlag(false);
                    globals.getWorkspace().enableSearchButtons();
                } else{
                    globals.setMetaDataErrorFlag(true);
                    globals.getWorkspace().disableSearchButtons();
                }
               JOptionPane.showMessageDialog(this, "Meta Data has been saved.");
            }else{
               JOptionPane.showMessageDialog(this, "Problem saving meta data.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving to meta data file: " + ex.toString());
        }
        try {
            // reload data into memory
            this.mdc.loadDataFile(this.metaDataFile);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Could not reload the meta data.  Please the restart the application to apply your changes. " + ex.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not reload the meta data.  Please the restart the application to apply your changes. " + ex.toString());
        }
        revalidate();
        repaint();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void revertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertButtonActionPerformed
        revertChanges();
    }//GEN-LAST:event_revertButtonActionPerformed

    private void addRowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowButtonActionPerformed
        addRow(null);
    }//GEN-LAST:event_addRowButtonActionPerformed

    private void deleteRowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRowButtonActionPerformed
        int row_to_delete = this.jTable.getSelectedRow();
        if(row_to_delete < 0){
            // no row selected
            JOptionPane.showMessageDialog(this, "No rows selected.", "", JOptionPane.OK_OPTION);
            return;
        }
        this.tableModel.removeRow(row_to_delete);
        this.duplicateTable.removeElementAt(row_to_delete);
    }//GEN-LAST:event_deleteRowButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
       showFileChooser();
    }//GEN-LAST:event_importButtonActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        closeWindow();
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRowButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteRowButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel errorPanel;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JButton revertButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables


    /**
     * Custom table cell renderer used in the meta data table.
     */
   class CustomCellRenderer extends DefaultTableCellRenderer {
        public CustomCellRenderer() {

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set the column font here also.
            JTableHeader header = table.getTableHeader();
            Font defaultColumnHeaderFont = new Font("Arial", Font.PLAIN, 12);
            header.setFont(defaultColumnHeaderFont);

            Font defaultFont = new Font("Arial", Font.PLAIN, 12);
            cell.setFont(defaultFont);

            cell.setBackground(Color.WHITE);
            cell.setForeground(Color.BLACK);

            // Set the background color if there is a problem
            String sValue = (String) value;
            if ((column == 1 || column == 9 || column == 10 || column == 11) && (sValue == null || sValue.length() == 0)){
                // if the file name is blank
                cell.setBackground(Color.RED);
            }
            if (column == 2 && (sValue == null || sValue.length() == 0)){
                // if the title is blank
                cell.setBackground(Color.YELLOW);
            }
            Vector<Boolean> rVec = duplicateTable.get(row);
            if(rVec != null && column < rVec.size()){
                if(rVec.get(column) == true){
                    // highlight duplicates
                    cell.setBackground(Color.RED);
                }
            }

            if (hasFocus){
                setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.CYAN));
            }

            if (isSelected){
                Font bf = new Font(cell.getFont().toString(), Font.BOLD, cell.getFont().getSize());
                cell.setFont(bf);
            } else {
                Font nf = new Font(cell.getFont().toString(), Font.PLAIN, cell.getFont().getSize());
                cell.setFont(nf);
            }

            // Set hover text
            Object rowTitleObj = table.getValueAt(row, 1);
            String rowTitle = (String) rowTitleObj;
            String colTitle = table.getModel().getColumnName(column);

            this.setToolTipText(rowTitle + " - " + colTitle);

            return cell;
        }
    }

   /**
    * Custom extension of the default table model.
    * Doesn't allow certain columns to be editable.
    */
   class CustomTableModel extends DefaultTableModel{
       public CustomTableModel(){}
       public CustomTableModel(Vector data, Vector columns){
           super(data, columns);
       }
       @Override
       /**
        * Handles the actual ability to enter values in the cell
        */
       public boolean isCellEditable(int row, int col) {
           if(col < 1){
               return false;
           }
           return true;
       }
  }

}
