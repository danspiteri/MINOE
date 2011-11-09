/*
 * FilterPanel.java
 *
 */

package minoe;

import java.awt.Cursor;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Displays a GUI panel of filters.
 * @author Daniel Spiteri
 */
public class FilterPanel extends javax.swing.JPanel {

    private DefaultListModel yearListModel;
    private DefaultListModel locationListModel;
    private DefaultListModel doctypeListModel;
    private DefaultListModel otherListModel;

    private Vector<String> selectedFiles; // the current list of files displayed

    public String collectionName;

    private Globals globals;
    private DefaultListModel documentListModel;
    private int displayedcount;
    private int selectedcount;

    private StatusFrame sf;

    /** Creates new form FilterPanel */
    public FilterPanel() {
        this.yearListModel = new DefaultListModel();
        this.locationListModel = new DefaultListModel();
        this.doctypeListModel = new DefaultListModel();
        this.otherListModel = new DefaultListModel();
        initComponents();
        resetPanelContents();
        populateDocList(null);
    }

    public FilterPanel(String title, Vector<String> tags, Globals g){
        this.yearListModel = new DefaultListModel();
        this.locationListModel = new DefaultListModel();
        this.doctypeListModel = new DefaultListModel();
        this.otherListModel = new DefaultListModel();
        this.globals = g;
        initComponents();
        this.collectionName = title;
        this.setTitle(title);
        resetPanelContents();
        populateDocList(null);
    }

    public void disableTermDistance(){
        this.wordDistanceTextField.setEnabled(false);
        this.wordDistanceLabel.setEnabled(false);
    }

    public String getTermDistance(){
        return this.wordDistanceTextField.getText();
    }

    /**
     * Populates the list of documents.  Refreshes everytime a filter is selected.
     * Only populate the list of documents pertaining to the selected filters.
     */
    public void populateDocList(SearchCriteria criteria){
        if(this.sf != null){
            sf.setLabel("Populating document list");
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.selectedFiles = new Vector<String>();
        this.documentListModel = new DefaultListModel();
        MetaDataController mdc = this.globals.getMetaDataController();
        List<String> list = null;
        if(criteria == null){
            list = mdc.getDocuments();
        } else{
            list = mdc.getDocumentsBySearchCriteria(criteria);
        }
        // Get document file names and their titles.  Titles should be unique.
        // title => file name
        Hashtable<String, String> doctitles = mdc.getTitlesAndDocuments(list);

        Enumeration<String> e = doctitles.keys();
        ArrayList<String> fileTitles = new ArrayList<String>();
        while(e.hasMoreElements()){
            String key = e.nextElement();
            fileTitles.add(key);
        }
        // alphabetical sort.
        Collections.sort(fileTitles);
        for (String title : fileTitles) {
            this.selectedFiles.add(doctitles.get(title));
            this.documentListModel.addElement(title);
        }
        this.docListComponent.setModel(this.documentListModel);
        this.displayedcount = documentListModel.getSize();
        this.resetDocumentCountLabel();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void resetPanelContents(){
        yearListModel.clear();
        locationListModel.clear();
        doctypeListModel.clear();
        otherListModel.clear();
        yearList.setModel(yearListModel);
        locationList.setModel(locationListModel);
        doctypeList.setModel(doctypeListModel);
        otherList.setModel(otherListModel);
        initYearList();
    }

    public String getCollectionName(){
        return this.collectionName;
    }

    public void initYearList(){
        List<String> years = this.globals.getMetaDataController().getDistinctDocumentYears();
        for (String year : years) {
            this.yearListModel.addElement(year);
        }
    }

    public void setLayoutStyle(String style){
        if(style.equalsIgnoreCase("vertical")){
            northPanel.setLayout(new javax.swing.BoxLayout(northPanel, javax.swing.BoxLayout.Y_AXIS));
            validate();
        }
    }

    @SuppressWarnings("static-access")
    public void setWidth(int w){
        double h = this.getPreferredSize().getHeight();
        this.setPreferredSize(new java.awt.Dimension(w,(int) h));
        validate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        containerPanel = new javax.swing.JPanel();
        northPanel = new javax.swing.JPanel();
        yearPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        yearList = new javax.swing.JList();
        locationPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        locationList = new javax.swing.JList();
        doctypePanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        doctypeList = new javax.swing.JList();
        otherPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        otherList = new javax.swing.JList();
        documentPanel = new javax.swing.JPanel();
        docCountLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        docListComponent = new javax.swing.JList();
        distancePanel = new javax.swing.JPanel();
        wordDistanceLabel = new javax.swing.JLabel();
        wordDistanceTextField = new javax.swing.JTextField();

        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setMaximumSize(new java.awt.Dimension(32783, 32783));
        setMinimumSize(new java.awt.Dimension(150, 200));
        setPreferredSize(new java.awt.Dimension(350, 300));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        containerPanel.setLayout(new javax.swing.BoxLayout(containerPanel, javax.swing.BoxLayout.Y_AXIS));

        northPanel.setMaximumSize(new java.awt.Dimension(131132, 200));
        northPanel.setLayout(new javax.swing.BoxLayout(northPanel, javax.swing.BoxLayout.X_AXIS));

        yearPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.yearPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        yearPanel.setFont(new java.awt.Font("Arial", 0, 11));
        yearPanel.setMinimumSize(new java.awt.Dimension(50, 75));
        yearPanel.setPreferredSize(new java.awt.Dimension(50, 160));
        yearPanel.setLayout(new javax.swing.BoxLayout(yearPanel, javax.swing.BoxLayout.LINE_AXIS));

        yearList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        yearList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                yearListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(yearList);

        yearPanel.add(jScrollPane1);

        northPanel.add(yearPanel);

        locationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.locationPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        locationPanel.setFont(new java.awt.Font("Arial", 0, 11));
        locationPanel.setMinimumSize(new java.awt.Dimension(50, 75));
        locationPanel.setPreferredSize(new java.awt.Dimension(50, 160));
        locationPanel.setLayout(new javax.swing.BoxLayout(locationPanel, javax.swing.BoxLayout.LINE_AXIS));

        locationList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        locationList.setMinimumSize(new java.awt.Dimension(50, 80));
        locationList.setPreferredSize(new java.awt.Dimension(50, 80));
        locationList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                locationListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(locationList);

        locationPanel.add(jScrollPane2);

        northPanel.add(locationPanel);

        doctypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.doctypePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        doctypePanel.setFont(new java.awt.Font("Arial", 0, 11));
        doctypePanel.setMinimumSize(new java.awt.Dimension(50, 75));
        doctypePanel.setPreferredSize(new java.awt.Dimension(50, 160));
        doctypePanel.setLayout(new javax.swing.BoxLayout(doctypePanel, javax.swing.BoxLayout.LINE_AXIS));

        doctypeList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        doctypeList.setMinimumSize(new java.awt.Dimension(50, 80));
        doctypeList.setPreferredSize(new java.awt.Dimension(50, 80));
        doctypeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                doctypeListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(doctypeList);

        doctypePanel.add(jScrollPane3);

        northPanel.add(doctypePanel);

        otherPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.otherPanel.border.title"))); // NOI18N
        otherPanel.setFont(new java.awt.Font("Arial", 0, 11));
        otherPanel.setMinimumSize(new java.awt.Dimension(50, 75));
        otherPanel.setPreferredSize(new java.awt.Dimension(50, 160));
        otherPanel.setLayout(new javax.swing.BoxLayout(otherPanel, javax.swing.BoxLayout.LINE_AXIS));

        otherList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        otherList.setMinimumSize(new java.awt.Dimension(50, 80));
        otherList.setPreferredSize(new java.awt.Dimension(50, 80));
        otherList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                otherListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(otherList);

        otherPanel.add(jScrollPane4);

        northPanel.add(otherPanel);

        containerPanel.add(northPanel);

        documentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.documentPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        documentPanel.setLayout(new javax.swing.BoxLayout(documentPanel, javax.swing.BoxLayout.Y_AXIS));

        docCountLabel.setText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.docCountLabel.text")); // NOI18N
        documentPanel.add(docCountLabel);

        docListComponent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                docListComponentMouseClicked(evt);
            }
        });
        docListComponent.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                docListComponentValueChanged(evt);
            }
        });
        scrollPane.setViewportView(docListComponent);

        documentPanel.add(scrollPane);

        containerPanel.add(documentPanel);

        distancePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.distancePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        distancePanel.setMaximumSize(new java.awt.Dimension(32783, 55));
        distancePanel.setLayout(new javax.swing.BoxLayout(distancePanel, javax.swing.BoxLayout.X_AXIS));

        wordDistanceLabel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        wordDistanceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wordDistanceLabel.setText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.wordDistanceLabel.text")); // NOI18N
        wordDistanceLabel.setMaximumSize(new java.awt.Dimension(100, 15));
        wordDistanceLabel.setMinimumSize(new java.awt.Dimension(100, 15));
        wordDistanceLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        distancePanel.add(wordDistanceLabel);

        wordDistanceTextField.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        wordDistanceTextField.setText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.wordDistanceTextField.text")); // NOI18N
        wordDistanceTextField.setMaximumSize(new java.awt.Dimension(50, 25));
        wordDistanceTextField.setMinimumSize(new java.awt.Dimension(25, 25));
        wordDistanceTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        wordDistanceTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordDistanceTextFieldActionPerformed(evt);
            }
        });
        distancePanel.add(wordDistanceTextField);

        containerPanel.add(distancePanel);

        add(containerPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void resetDocumentCountLabel() {
        this.docCountLabel.setText(this.selectedFiles.size() + " documents showing.");
    }

    private void yearListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_yearListValueChanged
        // Clear out the lists and repopulate
        locationListModel.clear();
        doctypeListModel.clear();
        otherListModel.clear();
        // Populate the location list
        Object[] selectedItems = yearList.getSelectedValues();
        for (Object o : selectedItems) {
            String year = o.toString();
            List<String> list = globals.getMetaDataController().getDistinctDocumentLocations(year);
            for (int i=0; i < list.size(); i++) {
                String location = list.get(i);
                if(locationListModel.contains(location) == false){
                    locationListModel.addElement(location);
                }
            }
        }
        this.populateDocList(this.getSelectedItems());
    }//GEN-LAST:event_yearListValueChanged

    private void locationListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_locationListValueChanged
        doctypeListModel.clear();
        otherListModel.clear();
        // Populate the document type list
        Object[] selectedYears     = yearList.getSelectedValues();
        Object[] selectedLocations = locationList.getSelectedValues();
        for (Object o : selectedYears) {
            String year = o.toString();
            for (Object o2 : selectedLocations) {
                String location = o2.toString();
                List<String> list = globals.getMetaDataController().getDistinctDocumentTypes(year, location);
                for (int i=0; i < list.size(); i++) {
                    String type = list.get(i);
                    if(doctypeListModel.contains(type) == false){
                        doctypeListModel.addElement(type);
                    }
                }
            }
        }
        this.populateDocList(this.getSelectedItems());
    }//GEN-LAST:event_locationListValueChanged

    private void doctypeListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_doctypeListValueChanged
        otherListModel.clear();
        // Populate the document type list
        Object[] selectedYears     = yearList.getSelectedValues();
        Object[] selectedLocations = locationList.getSelectedValues();
        Object[] selectedTypes     = doctypeList.getSelectedValues();
        for (Object o : selectedYears) {
            String year = o.toString();
            for (Object o2 : selectedLocations) {
                String location = o2.toString();
                for (Object o3 : selectedTypes) {
                    String type = o3.toString();
                    List<String> list = globals.getMetaDataController().getDistinctOtherLabels(year, location, type);
                    for (int i=0; i < list.size(); i++) {
                        String label = list.get(i);
                        if(otherListModel.contains(label) == false){
                            otherListModel.addElement(label);
                        }
                    }
                }
            }
        }
        this.populateDocList(this.getSelectedItems());
    }//GEN-LAST:event_doctypeListValueChanged

    /**
     * User double-clicks a file name / description.  Opens a new fileContentsWindow.
     * @param evt
     */
    private void docListComponentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_docListComponentMouseClicked
        if(evt.getClickCount() == 2){
            int selectedIndex = this.docListComponent.getSelectedIndex();
            String fileToRead = this.selectedFiles.get(selectedIndex);
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                SearchFiles searchFiles = this.globals.getSearchFiles();

                // lookup the path
                String filePath = searchFiles.getPath(fileToRead);
                if(filePath == null){
                    throw new IOException("File path not found.");
                }
                String baseLocation = this.globals.baseLocation;
                String fileContents = this.globals.openFile(baseLocation + filePath);

                FileContentsWindow fileContentsWindow = new FileContentsWindow();
                fileContentsWindow.setTitle(fileToRead);
                fileContentsWindow.setTextArea(fileContents);
                fileContentsWindow.pack();
                fileContentsWindow.setVisible(true);
                globals.parentOwner.add(fileContentsWindow);
                fileContentsWindow.setFocusable(true);
                try {
                    fileContentsWindow.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            } catch (IOException ex) {
                //Exceptions.printStackTrace(ex);
                JOptionPane.showMessageDialog(this, "Unable to open the file: " + fileToRead + ". " + ex.getMessage());
                return;
            } finally {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
}//GEN-LAST:event_docListComponentMouseClicked

    private void docListComponentValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_docListComponentValueChanged
        if (evt.getValueIsAdjusting() == false) {
            this.selectedcount = this.docListComponent.getSelectedValues().length;



            this.resetDocumentCountLabel();
        }
}//GEN-LAST:event_docListComponentValueChanged

    private void otherListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_otherListValueChanged
       this.populateDocList(this.getSelectedItems());
    }//GEN-LAST:event_otherListValueChanged

    private void wordDistanceTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordDistanceTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_wordDistanceTextFieldActionPerformed

    // Sets the title of the panel's border.
    public void setTitle(String title){
        if(title == null){
            return;
        }
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, 
                title + " (ctrl+click to select)",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new java.awt.Font("Arial", 0, 11))); // NOI18N
    }
    public void setYearList(Vector<String> v){
        for (String tag : v) {
            this.yearListModel.addElement(tag);
        }
    }
    public void setLocationList(Vector<String> v){
        for (String tag : v) {
            this.locationListModel.addElement(tag);
        }
    }
    public void setDocTypeList(Vector<String> v){
        for (String tag : v) {
            this.doctypeListModel.addElement(tag);
        }
    }
    public void setOtherList(Vector<String> v){
        for (String tag : v) {
            this.otherListModel.addElement(tag);
        }
    }

    /**
     * Sets the selected items according to the search criteria passed in.
     * @param criteria
     */
    public void setSelectedItems(SearchCriteria criteria){
        Vector<String> yearVec = criteria.getYears();
        for (String y : yearVec) {
            if(yearListModel.contains(y)){
               yearList.setSelectedValue(y, false);
            }
        }
        Vector<String> locVec = criteria.getLocations();
        for (String l : locVec) {
            if(locationListModel.contains(l)){
               locationList.setSelectedValue(l, false);
            }
        }
        Vector<String> typeVec = criteria.getTypes();
        for (String t : typeVec) {
            if(doctypeListModel.contains(t)){
               doctypeList.setSelectedValue(t, false);
            }
        }
        Vector<String> labelsVec = criteria.getLabels();
        for (String l : labelsVec) {
            if(otherListModel.contains(l)){
               otherList.setSelectedValue(l, false);
            }
        }
        this.wordDistanceTextField.setText(String.valueOf(criteria.getSlop()));
    }
    /**
     * Returns each item selected from each panel.
     * @return
     */
    public SearchCriteria getSelectedItems(){
        SearchCriteria criteria = new SearchCriteria();
        Object[] selectedYears     = yearList.getSelectedValues();
        Object[] selectedLocations = locationList.getSelectedValues();
        Object[] selectedTypes     = doctypeList.getSelectedValues();
        Object[] selectedLabels    = otherList.getSelectedValues();
        Vector<Vector> items = new Vector();
        Vector<String> years = new Vector();
        for (Object o : selectedYears) {
            years.add(o.toString());
            criteria.addYear(o.toString());
        }
        items.add(years);
        Vector<String> locations = new Vector();
        for (Object o : selectedLocations) {
            locations.add(o.toString());
            criteria.addLocation(o.toString());
        }
        items.add(locations);
        Vector<String> types = new Vector();
        for (Object o : selectedTypes) {
            types.add(o.toString());
            criteria.addType(o.toString());
        }
        items.add(types);
        Vector<String> labels = new Vector();
        for (Object o : selectedLabels) {
            labels.add(o.toString());
            criteria.addLabel(o.toString());
        }
        items.add(labels);
        
        // See if documents are selected
        int[] selectedDocs = this.docListComponent.getSelectedIndices();
        for (int i : selectedDocs) {
            String file = this.selectedFiles.get(i);
            criteria.addDocument(file);
        }

        // set slop (term distance)
        criteria.setSlop(this.getTermDistance());

        return criteria;
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel containerPanel;
    private javax.swing.JPanel distancePanel;
    private javax.swing.JLabel docCountLabel;
    private javax.swing.JList docListComponent;
    private javax.swing.JList doctypeList;
    private javax.swing.JPanel doctypePanel;
    private javax.swing.JPanel documentPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList locationList;
    private javax.swing.JPanel locationPanel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JList otherList;
    private javax.swing.JPanel otherPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel wordDistanceLabel;
    private javax.swing.JTextField wordDistanceTextField;
    private javax.swing.JList yearList;
    private javax.swing.JPanel yearPanel;
    // End of variables declaration//GEN-END:variables



    /**
     * Controls the mouse-click events that occur on each list component.
     * Populates each list depending on the id.
     */
    class MyListSelectionHandler implements ListSelectionListener{

        String id;

        public MyListSelectionHandler(String id){
            this.id = id;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(id.equalsIgnoreCase("year")){
                // Clear out the lists and repopulate
                locationListModel.clear();
                doctypeListModel.clear();
                otherListModel.clear();
                // Populate the location list
                String[] selectedItems = (String[]) yearList.getSelectedValues();
                for (String year : selectedItems) {
                    List<String> list = globals.getMetaDataController().getDistinctDocumentLocations(year);
                    for (int i=0; i < list.size(); i++) {
                        String location = list.get(i);
                        locationListModel.addElement(location);
                    }
                }
            } else if(id.equalsIgnoreCase("location")){
                doctypeListModel.clear();
                otherListModel.clear();
                // Populate the document type list
                String[] selectedYears = (String[]) yearList.getSelectedValues();
                String[] selectedLocations = (String[]) locationList.getSelectedValues();
                for (String year : selectedYears) {
                    for (String location : selectedLocations) {
                        List<String> list = globals.getMetaDataController().getDistinctDocumentTypes(year, location);
                        for (int i=0; i < list.size(); i++) {
                            String type = list.get(i);
                            doctypeListModel.addElement(type);
                        }
                    }
                }
            } else if(id.equalsIgnoreCase("type")){
                otherListModel.clear();
                // Populate the document type list
                String[] selectedYears     = (String[]) yearList.getSelectedValues();
                String[] selectedLocations = (String[]) locationList.getSelectedValues();
                String[] selectedTypes     = (String[]) doctypeList.getSelectedValues();
                for (String year : selectedYears) {
                    for (String location : selectedLocations) {
                        for (String type : selectedTypes) {
                            List<String> list = globals.getMetaDataController().getDistinctOtherLabels(year, location, type);
                            for (int i=0; i < list.size(); i++) {
                                String label = list.get(i);
                                otherListModel.addElement(label);
                            }
                        }
                    }
                }
            }
        }// end valueChanged()
    }// end MyListSelectionHandler

    /**
     * Populate the document list.
     */
    class PopulateWorker extends SwingWorker<Void, Void>{

        StatusFrame statusFrame;
        
        public PopulateWorker(StatusFrame statusFrame){
            this.statusFrame = new StatusFrame(true);
        }

        @Override
        protected Void doInBackground() {
            populateDocList(getSelectedItems());
            return null;
        }
        @Override
        public void done() {
            if (statusFrame != null){
                statusFrame.dispose();
            }
        }
    }

}// end FilterPanel
