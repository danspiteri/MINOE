 /*
 * GapsPanel2.java
 *
 * Created on February 22, 2008, 1:15 AM
 * 
 * Screen 2 of 3
 * Provides a JPanel GUI interface for rendering a JTable.
 * The table is partially editable, allowing for certain cells
 * to be specified by the user as a link in an ecosystem model.
 * 
 */
package minoe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * 
 * @author  Dan Spiteri
 */
public class GapsPanel2 extends javax.swing.JPanel {

    private CustomLinkageTableModel defaultTableModel;
    private String[] columnNames;
    private JTable table;
    private GapsContainer container;
    private final int PADDING = 0;

    /** Creates new form GapsPanel2
     * @param container 
     */
    public GapsPanel2(GapsContainer container) {
        this.defaultTableModel = new CustomLinkageTableModel(5,5,this.PADDING);
        initComponents();

        this.container = container;

        final TableCellRenderer customRenderer = new CustomRenderer();
        this.table = new JTable(){
            @Override
          public TableCellRenderer getCellRenderer(int row, int column) {
             return customRenderer;
           }        
        };
        table.setModel(defaultTableModel);
        table.setGridColor(Color.GRAY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));

        this.scrollPane.setViewportView(table);
    }

    /**
     * Create a table the size of the data, disallowing edits of certain fields.
     * Run each time the user visits this screen from the previous screen.
     * @param rowcount 
     * @param columncount 
     * @param padding 
     */
    public void reInit(int rowcount, final int columncount, final int padding) {
        this.defaultTableModel = new CustomLinkageTableModel(rowcount, columncount, padding);
        table.setModel(defaultTableModel);      
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);

    } // end method

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
        
        int padding = this.PADDING; // add extra columns in the table to fill the scrollpane
        int numberofcolumns = columnNames.length + padding;
        reInit(columnNames.length, numberofcolumns, padding);

        // Set row count = number of terms.
        this.defaultTableModel.setRowCount(columnNames.length);

        // Shift all elements right by one because column 1 will
        // be a mirror of the heading.
        String[] columnTitles = new String[numberofcolumns + 1];
        for (int i = 0; i <= numberofcolumns; i++) {
            if (i == 0) {
                columnTitles[i] = "";
            } else if (i <= (numberofcolumns - padding)){
                columnTitles[i] = columnNames[i - 1];
            } else {
                columnTitles[i] = "";
            }

        }

        // Set the column titles.
        this.defaultTableModel.setColumnIdentifiers(columnTitles);

        // Set first column values
        for (int row = 0; row < columnNames.length; row++) {
            //int row=j+1;
            String value = columnNames[row];
            this.defaultTableModel.setValueAt(value, row, 0); //value, row, column
        }

        // Populate uneditable cells with a --- value.
        for (int row = 0; row < this.defaultTableModel.getRowCount(); row++) {
            for (int col = 0; col < this.defaultTableModel.getColumnCount(); col++) {        
                boolean b = this.defaultTableModel.isCellEditable(row, col);
                if (b == false && col > 0) {
                    this.defaultTableModel.setValueAt("---", row, col);
                }
            }
        }
        // Adjust column widths       
        this.autoFitColumns();

        this.countLabel.setText("Term Count: " + this.columnNames.length);
        container.enablePrevious(true);
        revalidate();
    }

    /**
     * Populates the linkages table given an array.
     * Should be called after the column names are set.
     * @param linkages
     */
    public void setLinkages(String[][] linkages) {
        for (int row = 0; row < this.defaultTableModel.getRowCount(); row++) {
            for (int col = 1; col < this.defaultTableModel.getColumnCount(); col++) {
                boolean b = this.defaultTableModel.isCellEditable(row, col);
                if (b) {
                    String linkage = linkages[row][col - 1];
                    this.defaultTableModel.setValueAt(linkage, row, col);
                }

            }
        }
    }

    /** 
     * Returns the table data in a two-dimensional floating-point array. 
     * @return tableData float
     */
    public float[][] getTableData() {
        float[][] tableData =
                new float[this.defaultTableModel.getRowCount()][this.defaultTableModel.getColumnCount()];

        for (int row = 0; row < this.defaultTableModel.getRowCount(); row++) {
            for (int col = 1; col < this.defaultTableModel.getColumnCount(); col++) {
                if (this.defaultTableModel.isCellEditable(row, col)) {
                    // Retrieve cell value
                    if (this.defaultTableModel.containsValidData(row, col) == false){
                        JOptionPane.showMessageDialog(this.container, "Please enter a valid value in all cells.", "Missing Required Data", JOptionPane.WARNING_MESSAGE);
                        return null;
                    } else {
                        tableData[row][col] = this.defaultTableModel.getFloatValue(row, col);
                    }

                }
            }
        }

        return tableData;
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public void highlightCell(int row, int col) {
        TableColumn tc = this.table.getColumnModel().getColumn(col);
        tc.setCellRenderer(new RedCellRenderer());
        this.table.changeSelection(row, col, true, true);
    }

    public void clearHighlighting(int row, int col) {
        TableColumn tc = this.table.getColumnModel().getColumn(col);
        tc.setCellRenderer(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        titleLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        buttonPanel = new javax.swing.JPanel();
        zeroFillButton = new javax.swing.JButton();
        oneFillButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        optionBox = new javax.swing.JComboBox();
        labelPanel = new javax.swing.JPanel();
        countLabel = new javax.swing.JLabel();
        scrollPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();

        setPreferredSize(new java.awt.Dimension(425, 300));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        contentPanel.setMaximumSize(new java.awt.Dimension(32767, 50));
        contentPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        contentPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        titleLabel3.setFont(new java.awt.Font("Arial", 1, 11));
        titleLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleLabel3.setText(org.openide.util.NbBundle.getMessage(GapsPanel2.class, "GapsPanel2.titleLabel3.text")); // NOI18N
        contentPanel.add(titleLabel3);

        add(contentPanel);

        jSeparator1.setForeground(new java.awt.Color(51, 51, 51));
        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator1.setPreferredSize(new java.awt.Dimension(420, 2));
        add(jSeparator1);

        buttonPanel.setMaximumSize(new java.awt.Dimension(32767, 45));
        buttonPanel.setPreferredSize(new java.awt.Dimension(420, 40));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        zeroFillButton.setFont(new java.awt.Font("Dialog", 0, 12));
        zeroFillButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel2.class, "GapsPanel2.zeroFillButton.text")); // NOI18N
        zeroFillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroFillButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(zeroFillButton);

        oneFillButton.setFont(new java.awt.Font("Dialog", 0, 12));
        oneFillButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel2.class, "GapsPanel2.oneFillButton.text")); // NOI18N
        oneFillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneFillButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(oneFillButton);

        saveButton.setFont(new java.awt.Font("Dialog", 0, 12));
        saveButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel2.class, "GapsPanel2.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        optionBox.setFont(new java.awt.Font("Dialog", 0, 12));
        optionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Table Options...", "Auto Fit Columns", "Compact Columns" }));
        optionBox.setPreferredSize(new java.awt.Dimension(120, 22));
        optionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionBoxActionPerformed(evt);
            }
        });
        buttonPanel.add(optionBox);

        add(buttonPanel);

        labelPanel.setMaximumSize(new java.awt.Dimension(32767, 25));
        labelPanel.setPreferredSize(new java.awt.Dimension(200, 20));
        labelPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        countLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        countLabel.setText(org.openide.util.NbBundle.getMessage(GapsPanel2.class, "GapsPanel2.countLabel.text")); // NOI18N
        labelPanel.add(countLabel);

        add(labelPanel);

        scrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        scrollPanel.setLayout(new javax.swing.BoxLayout(scrollPanel, javax.swing.BoxLayout.LINE_AXIS));

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setAutoscrolls(true);
        scrollPane.setMaximumSize(new java.awt.Dimension(3000, 3000));
        scrollPanel.add(scrollPane);

        add(scrollPanel);
    }// </editor-fold>//GEN-END:initComponents
    private void oneFillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneFillButtonActionPerformed
        for (int row = 0; row < this.defaultTableModel.getRowCount(); row++) {
            for (int col = 0; col < this.defaultTableModel.getColumnCount(); col++) {
                boolean b = this.defaultTableModel.isCellEditable(row, col);
                if (b) {
                    this.defaultTableModel.setValueAt("1", row, col);
                }
            }
        }
}//GEN-LAST:event_oneFillButtonActionPerformed

    private void zeroFillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeroFillButtonActionPerformed
        for (int row = 0; row < this.defaultTableModel.getRowCount(); row++) {
            for (int col = 0; col < this.defaultTableModel.getColumnCount(); col++) {
                boolean b = this.defaultTableModel.isCellEditable(row, col);
                if (b) {
                    this.defaultTableModel.setValueAt("0", row, col);
                }
            }
        }
    }//GEN-LAST:event_zeroFillButtonActionPerformed

    /**
     * Save this ecosystem model as a modified pipe-delimited file, with extension .ebm.
     * @param evt
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        String modelName = "";
        String fileName = "";
        boolean canceled = false;
        while (true) {
            modelName = JOptionPane.showInputDialog(null, "Enter the name of this ecosystem model:  ", "Save", JOptionPane.DEFAULT_OPTION);
            fileName = modelName + ".ebm";

            try {
                // User pressed cancel, leaves the entire method.
                if (modelName == null) {
                    canceled = true;
                    break;
                }
                // Validate naming convention.
                Pattern p = Pattern.compile("\\w*");
                Matcher m = p.matcher(modelName);
                boolean isMatch = m.matches();
                if (!isMatch || modelName.equals("") == true) {
                    throw new javax.naming.InvalidNameException();
                }

                // see if file exists already
                String curDirName = System.getProperty("user.dir");
                String saveFolder = curDirName + "/save";
                File curDirHandle = new File(saveFolder);
                // save ecosystem model to the save folder if it exists
                if(!curDirHandle.isDirectory() || !curDirHandle.canWrite()){
                    curDirHandle = new File(curDirName);
                    if(!curDirHandle.isDirectory() || !curDirHandle.canWrite()){
                        throw new java.io.FileNotFoundException();
                    }
                }

                File[] rootContents = curDirHandle.listFiles();
                // label for the break statement
                search:
                for (File file : rootContents) {
                    if (file.getName().equals(fileName) == true) {
                        int overwrite = JOptionPane.showConfirmDialog(null, "A model by this name already exists.  Do you wish to overwrite it?", "Save As", JOptionPane.YES_NO_OPTION);
                        if (overwrite == 0) {
                            break search;
                        } else {
                            throw new java.io.IOException();
                        }
                    }
                }

                this.container.modelName = modelName;
                this.container.setTitle(this.container.defaultTitle + " - " + modelName);

                File outputFile = new File(curDirHandle + "/" + fileName);
                FileWriter fstream = new FileWriter(outputFile);
                BufferedWriter bw = new BufferedWriter(fstream);

                // build output string
                StringBuffer sb = new StringBuffer();
                // first row is the model name.
                sb.append(modelName);
                sb.append("\n");

                //second row is the column headers
                String[] colnames = this.getColumnNames();
                for (String column : colnames) {
                    sb.append(column);
                    sb.append("|");
                }
                sb.append("\n");

                //remainder of the file is linkages data
                float[][] tabledata = this.getTableData();
                int tableCols = this.table.getColumnCount();
                int tableRows = this.table.getRowCount();
                for (int row = 0;
                        row < tableRows;
                        row++) {
                    String rowtext = "";
                    for (int col = 1; col < tableCols; col++) {
                        rowtext += tabledata[row][col] + "|";
                    }
                    sb.append(rowtext);
                    sb.append("\n");
                }

                String outString = sb.toString();
                bw.write(outString);

                bw.close();
                break;
            } catch (InvalidNameException ex) {
                JOptionPane.showMessageDialog(this.container, "Please use letters, numbers or underscores in the name.", "Naming Exception", JOptionPane.WARNING_MESSAGE);
            } catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(this.container, "The save folder is not found or cannot be written to.", "Cannot Save", JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this.container, "Please choose a new name.", "File already exists", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex){
                // nothing
            }

        }//end while

        if (!canceled) {
            JOptionPane.showMessageDialog(this.container, "Your Ecosystem model has been successfully saved to " + fileName + ".", "File Saved", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.autoFitColumns();
    }
    
    private void compactButtonActionPerformed(java.awt.event.ActionEvent evt){
       this.compactColumns();
    }

    private void optionBoxActionPerformed(java.awt.event.ActionEvent evt){
        String selectedOption = (String) this.optionBox.getSelectedItem();
        if (selectedOption.equals("Auto Fit Columns")){
            this.autoFitColumns();
        }
        if (selectedOption.equals("Compact Columns")){
            this.compactColumns();
        }
        if (selectedOption.equals("Increase Font Size")){
            this.increaseFontSize();
        }
        this.optionBox.setSelectedIndex(0);
    }
    
    public void autoFitColumns(){
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
               } 
               header.setResizingColumn(column); // this line is very important 
               column.setWidth(width+table.getIntercellSpacing().width);             
            
        }
        revalidate();        
    }
    
    public void compactColumns(){
        // Adjust column widths
        TableColumn column = null;
        for (int j = 0; j < table.getColumnCount(); j++) {
            column = table.getColumnModel().getColumn(j);
            column.setWidth(40);                         
        }
        revalidate();
        
    }

    public void increaseFontSize(){
        TableColumn column = null;
        for (int j = 0; j < table.getColumnCount(); j++) {
            column = table.getColumnModel().getColumn(j);
            column.setWidth(40);
        }        
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel countLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JButton oneFillButton;
    private javax.swing.JComboBox optionBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel scrollPanel;
    private javax.swing.JLabel titleLabel3;
    private javax.swing.JButton zeroFillButton;
    // End of variables declaration//GEN-END:variables
    static class RedCellRenderer extends DefaultTableCellRenderer {

        public RedCellRenderer() {

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(Color.RED);

            return cell;
            
        }
        
    }
    
    static class CustomRenderer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);        

            Font defaultFont = new Font(Font.DIALOG, Font.PLAIN, 12);
            cell.setFont(defaultFont);
            cell.setForeground(Color.BLACK);
            cell.setBackground(Color.WHITE);
            
            // Set hover text            
            Object rowTitleObj = table.getValueAt(row, 0);
            String rowTitle = (String) rowTitleObj;
            String colTitle = table.getModel().getColumnName(column);
            
            this.setToolTipText(rowTitle + " - " + colTitle);
            
            DefaultTableModel dtm = (DefaultTableModel) table.getModel();
            CustomLinkageTableModel cltm = (CustomLinkageTableModel) dtm;
            
            if (value != null && cltm.isCellEditable(row, column)){
                Pattern pattern = Pattern.compile("[^0-9\\.]");
                Matcher matcher = pattern.matcher(value.toString());
                if(matcher.find()){
                    cell.setBackground(Color.RED);
                    this.setToolTipText("Value must be 0, 1 or a decimal number");
                }
            }
            return cell;
            
        }
    }

    public static class CustomLinkageTableModel extends DefaultTableModel{
       
        int padding = 0; // default
        int columncount = 0; // default  
        
        public CustomLinkageTableModel(int rows, int columns, int padding){
            this.columncount = columns;
            this.padding = padding;
        }
        
        public Float getFloatValue(int row, int column){
            Object o = this.getValueAt(row, column);
            if (o == null){
                return null;
            }
            String s = (String) o;
            if (s.length()==0){
                return null;
            }
            return Float.valueOf(s);
        }
        
        @Override
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2 || row >= col - 1 || col > (columncount - padding)) {
                return false;
            } else {
                return true;
            }

        }
        
        /**
         * Check that all cells contain numbers or decimal numbers.
         * @param row
         * @param column
         * @return
         */
        public boolean containsValidData(int row, int column){
            Object o = this.getValueAt(row, column);
            if (o == null){
                return false;
            }
            String s = (String) o;
            if (s.length()==0){
                return false;
            }
            if (this.isCellEditable(row, column)){
                Pattern pattern = Pattern.compile("[^0-9\\.]");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    return false;
                }
            }
            return true;            
        }
    }//end class CustomLinkageTableModel    
}//end class GapsPanel2
