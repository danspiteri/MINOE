
package minoe;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.apache.lucene.index.CorruptIndexException;

/**
 * Displays a window of documents organized in a tree and links to view the document contents.
 * 
 * @author  Dan Spiteri
 */
public class DocumentListWindow extends javax.swing.JInternalFrame {

    Globals globals;

    /** 
     * Creates new form DocumentListWindow.  
     * Displays a list of all the files that co-occur between two terms.
     * @param termA
     * @param termB
     * @param filter
     * @param gl
     */
    public DocumentListWindow(String termA, String termB, SearchCriteria criteria, final Globals gl) {
        this.globals = gl;
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        
        MetaDataController metaData = gl.getMetaDataController();
        MatrixController mc = new MatrixController(gl, metaData);

        // Search the index
        Hashtable<String, Integer> fileList = mc.getCooccurenceDocNames(termA, termB, criteria);

        List<Map.Entry> list = new ArrayList<Map.Entry>(fileList.entrySet());
        // sort according to value (custom comparitor sorts by value rather than key)
        Collections.sort(list, new Comparator<Map.Entry>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                Integer i1 = (Integer) e1.getValue();
                Integer i2 = (Integer) e2.getValue();
                return i2.compareTo(i1);
            }
        });

        int numfiles = fileList.size();
        // Count total amount of hits
        int numhits = 0;
        for (Integer integer : fileList.values()) {
            numhits += integer.intValue();
        }

        this.setTitle(termA + " - " + termB + " (Showing "+numhits+" matches across "+numfiles+" documents).");

        // create the root tree node
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(fileList.size() + " Result(s)");

        // go through each file name and get the description and path.
        for (Map.Entry entry : list) {
            String fileName = entry.getKey().toString();
            SearchFiles searchFiles = this.globals.getSearchFiles();
            String filePath = null;
            try {
                filePath = this.globals.getBaseLocation() + searchFiles.getPath(fileName);
            } catch (CorruptIndexException ex) {
                JOptionPane.showMessageDialog(this, "Error reading index: " + ex.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error getting file information: " + ex.toString());
            } 
            int filecount = Integer.valueOf(entry.getValue().toString());
            String docDescription = metaData.getInfoFromFileName(fileName, metaData.documentTitles);
            MyTreeObject treeObject = new MyTreeObject(fileName, filePath, docDescription, filecount, termA, termB);
            top.add(new DefaultMutableTreeNode(treeObject));

            /*
            // See if category exists already
            if (categories.containsKey(docDescription)){
                Hashtable categoryContents = (Hashtable) categories.get(docDescription);
                categoryContents.put(filePath, filePath);
            } else {
                Hashtable categoryContents = new Hashtable();
                categoryContents.put(filePath, filePath);
                categories.put(docDescription, categoryContents);
            }
             */
        }

        /*
        // Adjust category descriptions, sort, add to tree
        DefaultMutableTreeNode[] catArray = new DefaultMutableTreeNode[categories.size()];
        Hashtable categorySizes = new Hashtable();
        Enumeration<String> enumeration = categories.keys();
        int i = 0;
        while (enumeration.hasMoreElements()){
            String categoryTitle = (String) enumeration.nextElement();
            Hashtable children = (Hashtable) categories.get(categoryTitle);
            int childcount = children.size();
            String newCategoryTitle = categoryTitle + " (" + childcount + ")";
            categorySizes.put(newCategoryTitle, childcount);
            DefaultMutableTreeNode category = new DefaultMutableTreeNode(newCategoryTitle);
            Enumeration keys = children.keys();
            while (keys.hasMoreElements()){
                category.add(new DefaultMutableTreeNode(children.get(keys.nextElement() )));
            }
            catArray[i] = category;
            //top.add(category);
            i++;
        }
                
        // sort the array/categories
        for (int j = 0; j < catArray.length; j++) {
            DefaultMutableTreeNode nodeA = catArray[j];
            String nodeNameA = (String) nodeA.getUserObject();
            Integer catSizeA = (Integer) categorySizes.get(nodeNameA);
            int sizea = catSizeA.intValue();
            int max = sizea;
            int maxindex = j;
            for (int k = j+1; k < catArray.length; k++){
                DefaultMutableTreeNode nodeB = catArray[k];
                String nodeNameB = (String) nodeB.getUserObject();
                Integer catSizeB = (Integer) categorySizes.get(nodeNameB);
                int sizeb = catSizeB.intValue();
                if (sizeb > max){
                    max = sizeb;
                    maxindex = k;
                }
            }
            // if a greater count was found then swap
            if (maxindex != j){
                DefaultMutableTreeNode placeHolder = catArray[j];
                catArray[j] = catArray[maxindex];
                catArray[maxindex] = placeHolder;
            }     
        }

        // finally add the treenodes in order
        for (int l = 0; l < catArray.length; l++) {
            DefaultMutableTreeNode n = catArray[l];
            top.add(n);
        }
        */
                
        final JTree tree = new JTree(top);
        tree.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        MouseAdapter treeAdapter = createTreeAdapter(tree, gl);
        tree.addMouseListener(treeAdapter);

        JScrollPane scrollPane = new JScrollPane(tree);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Size the frame.
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = getWidth();
        int height = getHeight();
        // attempt to center the screen on the page
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);

        //5. Show it.
        gl.parentOwner.add(this);
        setVisible(true);
    
    }

    /**
     * If you have a list of documents and counts to populate use this method.
     * @param fileList
     * @param gl
     */
    public DocumentListWindow(Hashtable<String, Integer> fileList, final Globals gl, SearchCriteria criteria, String termA, String termB) {
        this.globals = gl;
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        MetaDataController metaData = gl.getMetaDataController();
        List<Map.Entry> list = new ArrayList<Map.Entry>(fileList.entrySet());

        // sort according to value (custom comparitor sorts by value rather than key)
        Collections.sort(list, new Comparator<Map.Entry>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                Integer i1 = (Integer) e1.getValue();
                Integer i2 = (Integer) e2.getValue();
                return i2.compareTo(i1);
            }
        });

        int numfiles = fileList.size();
        // Count total amount of hits
        int numhits = 0;
        for (Integer integer : fileList.values()) {
            numhits += integer.intValue();
        }

        if(criteria != null){
            this.setTitle(criteria.toString() + " (Showing "+numhits+" matches across "+numfiles+" documents).");
        }
        // create the root tree node
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(fileList.size() + " Result(s)");

        // go through each file name and get the description and path.
        for (Map.Entry entry : list) {
            String fileName = entry.getKey().toString();
            SearchFiles searchFiles = this.globals.getSearchFiles();
            String filePath = null;
            try {
                filePath = searchFiles.getPath(fileName);
            } catch (CorruptIndexException ex) {
            } catch (IOException ex) {
            }
            int filecount = Integer.valueOf(entry.getValue().toString());
            String docDescription = metaData.getInfoFromFileName(fileName, metaData.documentTitles);
            MyTreeObject treeObject = new MyTreeObject(fileName, filePath, docDescription, filecount, termA, termB);
            top.add(new DefaultMutableTreeNode(treeObject));
        }


        final JTree tree = new JTree(top);
        tree.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        MouseAdapter treeAdapter = createTreeAdapter(tree, gl);
        tree.addMouseListener(treeAdapter);

        JScrollPane scrollPane = new JScrollPane(tree);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Size the frame.
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = getWidth();
        int height = getHeight();
        // attempt to center the screen on the page
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);

        // Show it.
        gl.parentOwner.add(this);
        setVisible(true);

    }

    /**
     * Generates a list of documents in a window organized by parent document,
     * given an array of section names.
     * @param fileNames
     * @param windowTitle
     * @param gl
     * 
     */
    public DocumentListWindow(String fileNames[], String windowTitle, final Globals gl){
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.globals = gl;

        // Create components and put them in the frame.
        MetaDataController metaData = gl.getMetaDataController();

        Hashtable categories = new Hashtable(); // Hashtable of hashtables

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(fileNames.length + " Result(s)");
        for (int i = 0; i < fileNames.length; i++) {
            String filePath = fileNames[i];
            String fileName = fileNames[i].substring(filePath.lastIndexOf("\\")+1, filePath.length());
            String docDescription = metaData.getInfoFromFileName(fileName, metaData.documentTitles);
            // See if category exists already
            if (categories.containsKey(docDescription)){
                Hashtable categoryContents = (Hashtable) categories.get(docDescription);
                categoryContents.put(filePath, filePath);
            } else {
                Hashtable categoryContents = new Hashtable();
                categoryContents.put(filePath, filePath);
                categories.put(docDescription, categoryContents);
            }

        }

        // Adjust category descriptions, sort, add to tree
        DefaultMutableTreeNode[] catArray = new DefaultMutableTreeNode[categories.size()];
        Hashtable categorySizes = new Hashtable();
        Enumeration enumeration = categories.keys();
        int i = 0;
        while (enumeration.hasMoreElements()){
            String categoryTitle = (String) enumeration.nextElement();
            Hashtable children = (Hashtable) categories.get(categoryTitle);
            int childcount = children.size();
            String newCategoryTitle = categoryTitle + " (" + childcount + ")";
            categorySizes.put(newCategoryTitle, childcount);
            DefaultMutableTreeNode category = new DefaultMutableTreeNode(newCategoryTitle);
            Enumeration keys = children.keys();
            while (keys.hasMoreElements()){
                category.add(new DefaultMutableTreeNode(children.get(keys.nextElement() )));
            }
            catArray[i] = category;
            //top.add(category);
            i++;
        }

        // sort the array/categories
        for (int j = 0; j < catArray.length; j++) {
            DefaultMutableTreeNode nodeA = catArray[j];
            String nodeNameA = (String) nodeA.getUserObject();
            Integer catSizeA = (Integer) categorySizes.get(nodeNameA);
            int sizea = catSizeA.intValue();
            int max = sizea;
            int maxindex = j;
            for (int k = j+1; k < catArray.length; k++){
                DefaultMutableTreeNode nodeB = catArray[k];
                String nodeNameB = (String) nodeB.getUserObject();
                Integer catSizeB = (Integer) categorySizes.get(nodeNameB);
                int sizeb = catSizeB.intValue();
                if (sizeb > max){
                    max = sizeb;
                    maxindex = k;
                }
            }
            // if a greater count was found then swap
            if (maxindex != j){
                DefaultMutableTreeNode placeHolder = catArray[j];
                catArray[j] = catArray[maxindex];
                catArray[maxindex] = placeHolder;
            }
        }

        // finally add the treenodes in order
        for (int l = 0; l < catArray.length; l++) {
            DefaultMutableTreeNode n = catArray[l];
            top.add(n);
        }


        final JTree tree = new JTree(top);
        tree.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Another double-click handler for opening a window containing the file contents.
        MouseAdapter treeAdapter = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2 ){
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                    if (clickedNode == null){
                        return;
                    }
                    String fileToRead = "";
                    if (clickedNode.isLeaf()) {
                        try {
                            fileToRead = (String) clickedNode.getUserObject();
                            String fileContents = gl.openFile(fileToRead);

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

                            }
                        catch (IOException ex) {
                                //Exceptions.printStackTrace(ex);
                                JOptionPane.showMessageDialog(gl.parentOwner, "Unable to open the file: " + fileToRead);
                        }
                    }
                }
            }


        };

        tree.addMouseListener(treeAdapter);
        JScrollPane scrollPane = new JScrollPane(tree);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Size the frame.
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = getWidth();
        int height = getHeight();
        // attempt to center the screen on the page
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);

        //5. Show it.
        gl.parentOwner.add(this);
        setVisible(true);


    }

    /**
     * Another double-click handler for opening a window containing the file contents.
     * @param tree
     * @param gl
     * @return
     */
    public MouseAdapter createTreeAdapter(final JTree tree, final Globals gl){
        MouseAdapter treeAdapter = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2 ){
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                    if (clickedNode == null){
                        return;
                    }
                    String fileToRead = "";
                    if (clickedNode.isLeaf()) {
                        try {
                            MyTreeObject treeObj = (MyTreeObject) clickedNode.getUserObject();
                            
                            // get the absolute path
                            fileToRead = treeObj.filePath;

                            // open the file in a new window
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            String fileContents = gl.openFile(fileToRead);
                            String s = treeObj.getSearchString();
                            FileContentsWindow fileContentsWindow = new FileContentsWindow(fileToRead, fileContents, s);
                            globals.parentOwner.add(fileContentsWindow);
                            fileContentsWindow.setFocusable(true);
                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            try {
                                fileContentsWindow.setSelected(true);
                            } catch (PropertyVetoException ex) {
                            }
                        } catch (IOException ex) {
                            //Exceptions.printStackTrace(ex);
                            JOptionPane.showMessageDialog(null, "Unable to open the file: " + fileToRead);
                        }
                    }
                }
            }
        };
        return treeAdapter;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    class MyTreeObject{
        String fileName;
        String filePath;
        String description;
        String termA;
        String termB;
        int count;

        public MyTreeObject(String name, String path, String desc, int c, String a, String b){
            this.fileName = name;
            this.filePath = path;
            this.count = c;
            this.description = desc;
            this.termA = a;
            this.termB = b;
        }

        public String getSearchString(){
            if(termA == null || termB == null){
                return null;
            } else if (termA.equalsIgnoreCase(termB)){
                return termA;  // if both terms are the same, then just return the first term.
            } else{
                return termA + " " + termB;
            }
        }

        @Override
        public String toString(){
            return description + " (" + count + ")";
        }
    }
}
